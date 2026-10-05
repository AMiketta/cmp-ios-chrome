package dev.amiketta.cmpioschrome

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitViewController
import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIImage
import platform.UIKit.UITab
import platform.UIKit.UITabBarController

/**
 * Embeds a native [UITabBarController] (modern [UITab] API). Each tab hosts Compose
 * via [ComposeUIViewController] so content stays shared while chrome is UIKit.
 */
@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun NativeTabBar(
    tabs: List<IosChromeTab>,
    modifier: Modifier,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
) {
    require(tabs.isNotEmpty()) { "NativeTabBar needs at least one tab" }

    val rememberedTabs = remember(tabs.map { it.title to it.systemImage }) { tabs }

    UIKitViewController(
        factory = {
            createNativeTabBarController(
                tabs = rememberedTabs,
                initialIndex = selectedIndex.coerceIn(0, rememberedTabs.lastIndex),
                onSelectedIndexChange = onSelectedIndexChange,
            )
        },
        modifier = modifier,
        update = { controller ->
            val count = (controller.viewControllers?.size ?: 1L).toInt()
            val target = selectedIndex.coerceIn(0, (count - 1).coerceAtLeast(0))
            if (controller.selectedIndex.toInt() != target) {
                controller.selectedIndex = target.toULong()
            }
        },
    )
}

/**
 * Preferred iOS entry when the tab bar should be the **window root**.
 *
 * Uses iOS 18+ [UITab] / `UITabBarController(tabs:)` so SF Symbols and system chrome
 * (incl. Liquid Glass on newer OS versions) work without the removed `UIViewController.tabBarItem`
 * property in current Kotlin/Native UIKit bindings.
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
                ComposeUIViewController { tab.content() }
            },
        )
    }

    val controller = UITabBarController(tabs = uiTabs)
    controller.selectedIndex = initialIndex.coerceIn(0, tabs.lastIndex).toULong()
    @Suppress("UNUSED_VARIABLE")
    val ignored = onSelectedIndexChange
    return controller
}
