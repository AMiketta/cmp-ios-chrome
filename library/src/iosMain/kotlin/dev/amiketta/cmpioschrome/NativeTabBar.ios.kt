package dev.amiketta.cmpioschrome

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitInteropInteractionMode
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIImage
import platform.UIKit.UITab
import platform.UIKit.UITabBar
import platform.UIKit.UITabBarAppearance
import platform.UIKit.UITabBarController
import platform.UIKit.UITabBarDelegateProtocol
import platform.UIKit.UITabBarItem
import platform.darwin.NSObject

/**
 * Edge-to-edge iOS chrome: tab content fills the screen in **one** Compose composition;
 * a real UIKit [UITabBar] (SF Symbols, system material / Liquid Glass) sits as a native
 * overlay. How far it covers content is exposed via [LocalTabBarOverlap].
 *
 * Nesting [UITabBarController] inside Compose via [UIKitViewController] is **not** used
 * here — that path measured 0×0 and spawned a separate Compose tree per tab (lost Theme /
 * CompositionLocals). Prefer this overlay, or [createNativeTabBarController] only as the
 * **window root**.
 */
@Composable
actual fun NativeTabBar(
    tabs: List<IosChromeTab>,
    modifier: Modifier,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
) {
    require(tabs.isNotEmpty()) { "NativeTabBar needs at least one tab" }
    val index = selectedIndex.coerceIn(0, tabs.lastIndex)
    val bottomInset =
        WindowInsets.navigationBars.only(WindowInsetsSides.Bottom).asPaddingValues().calculateBottomPadding()
    val barHeight = TAB_BAR_HEIGHT + bottomInset

    Box(modifier.fillMaxSize()) {
        CompositionLocalProvider(LocalTabBarOverlap provides barHeight) {
            Box(Modifier.fillMaxSize()) {
                tabs[index].content()
            }
        }
        IosOverlayTabBar(
            tabs = tabs,
            selectedIndex = index,
            onSelectedIndexChange = onSelectedIndexChange,
            height = barHeight,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

/** Standard UITabBar height without the home-indicator region (pt). */
private val TAB_BAR_HEIGHT = 49.dp

@OptIn(ExperimentalForeignApi::class, ExperimentalComposeUiApi::class)
@Composable
private fun IosOverlayTabBar(
    tabs: List<IosChromeTab>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    height: Dp,
    modifier: Modifier = Modifier,
) {
    val currentOnSelect by rememberUpdatedState(onSelectedIndexChange)
    // UITabBar holds its delegate weakly — keep it in composition.
    // UIKit invokes the delegate on the main thread only on user taps (not when setting
    // selectedItem), so there is no recursion with the update block.
    val delegate = remember {
        TabBarDelegate { idx -> currentOnSelect(idx) }
    }
    val selectedState = rememberUpdatedState(selectedIndex)
    val titlesKey = tabs.map { it.title to it.systemImage }

    UIKitView(
        factory = {
            UITabBar().apply {
                val tabItems = tabs.mapIndexed { i, tab ->
                    UITabBarItem(
                        title = tab.title,
                        image = tab.systemImage?.let { name -> UIImage.systemImageNamed(name) },
                        tag = i.toLong(),
                    )
                }
                setItems(tabItems, animated = false)
                // Always show the translucent system material (also at the "scroll edge",
                // where iOS 15–18 would otherwise use a fully clear background).
                val appearance = UITabBarAppearance().apply { configureWithDefaultBackground() }
                standardAppearance = appearance
                scrollEdgeAppearance = appearance
                this.delegate = delegate
            }
        },
        modifier = modifier.fillMaxWidth().height(height),
        update = { bar ->
            // Rebuild items if tab titles / symbols change (rare for chrome).
            @Suppress("UNUSED_VARIABLE")
            val ignored = titlesKey
            val target = bar.items?.getOrNull(selectedState.value) as? UITabBarItem
            if (target != null && bar.selectedItem != target) {
                bar.selectedItem = target
            }
        },
        // Native overlay above the Compose canvas so content shows through the material.
        // NonCooperative: taps go straight to the bar (no scroll conflict with a tab strip).
        properties = UIKitInteropProperties(
            interactionMode = UIKitInteropInteractionMode.NonCooperative,
            isNativeAccessibilityEnabled = true,
            placedAsOverlay = true,
        ),
    )
}

private class TabBarDelegate(
    private val onSelectIndex: (Int) -> Unit,
) : NSObject(), UITabBarDelegateProtocol {
    override fun tabBar(tabBar: UITabBar, didSelectItem: UITabBarItem) {
        onSelectIndex(didSelectItem.tag.toInt())
    }
}

/**
 * Advanced / window-root path: builds a native [UITabBarController] with one
 * [ComposeUIViewController] per tab (modern [UITab] API, SF Symbols, system chrome).
 *
 * **Pitfalls when nesting inside Compose** (e.g. via `UIKitViewController`):
 * - The hosted controller often measures **0×0** → white/blank screen.
 * - Each tab is a **separate** Compose tree → Theme, CompositionLocals, ImageLoader,
 *   and navigation state are **not** shared across tabs.
 *
 * Prefer [NativeTabBar] (UITabBar overlay in one composition) for in-Compose chrome.
 * Use this factory only as the **UIWindow root** when you intentionally want UIKit to
 * own tab switching and accept isolated Compose trees per tab.
 */
fun createNativeTabBarController(
    tabs: List<IosChromeTab>,
    initialIndex: Int = 0,
    onSelectedIndexChange: (Int) -> Unit = {},
): UITabBarController {
    require(tabs.isNotEmpty()) { "Need at least one tab" }

    val uiTabs = tabs.mapIndexed { index, tab ->
        val identifier = tab.systemImage?.let { "${tab.title}-$it" } ?: "${tab.title}-$index"
        UITab(
            title = tab.title,
            image = tab.systemImage?.let { name -> UIImage.systemImageNamed(name) },
            identifier = identifier,
            viewControllerProvider = {
                // fillMaxSize so hosted screens are not measured 0×0.
                ComposeUIViewController {
                    Box(Modifier.fillMaxSize()) {
                        tab.content()
                    }
                }
            },
        )
    }

    val controller = UITabBarController(tabs = uiTabs)
    controller.selectedIndex = initialIndex.coerceIn(0, tabs.lastIndex).toULong()
    @Suppress("UNUSED_VARIABLE")
    val ignored = onSelectedIndexChange
    return controller
}
