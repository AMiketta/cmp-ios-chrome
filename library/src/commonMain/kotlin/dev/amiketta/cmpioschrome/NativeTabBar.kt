package dev.amiketta.cmpioschrome

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Platform chrome tab bar.
 *
 * - **iOS:** real [platform.UIKit.UITabBar] overlaid on **one** Compose composition
 *   (edge-to-edge content under a translucent system bar). Tab content shares Theme,
 *   CompositionLocals, and other ambient state. See [LocalTabBarOverlap].
 * - **Android / Desktop:** Material3 [androidx.compose.material3.NavigationBar] overlay
 *   with the same [LocalTabBarOverlap] contract so shared padding code works.
 *
 * Prefer this Composable over nesting a [platform.UIKit.UITabBarController] inside
 * Compose (see [createNativeTabBarController] pitfalls on iOS).
 *
 * State and screen content stay in Compose; only the bar itself is native on iOS.
 */
@Composable
expect fun NativeTabBar(
    tabs: List<IosChromeTab>,
    modifier: Modifier = Modifier,
    selectedIndex: Int = 0,
    onSelectedIndexChange: (Int) -> Unit = {},
)

/**
 * DSL overload of [NativeTabBar].
 */
@Composable
fun NativeTabBar(
    modifier: Modifier = Modifier,
    selectedIndex: Int = 0,
    onSelectedIndexChange: (Int) -> Unit = {},
    content: IosTabBarScope.() -> Unit,
) {
    val scope = IosTabBarScopeImpl().apply(content)
    NativeTabBar(
        tabs = scope.tabs.toList(),
        modifier = modifier,
        selectedIndex = selectedIndex,
        onSelectedIndexChange = onSelectedIndexChange,
    )
}
