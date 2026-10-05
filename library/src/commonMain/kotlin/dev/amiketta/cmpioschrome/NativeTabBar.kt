package dev.amiketta.cmpioschrome

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Platform chrome tab bar.
 *
 * - **iOS:** real [platform.UIKit.UITabBarController] hosting each tab in its own
 *   [androidx.compose.ui.window.ComposeUIViewController].
 * - **Android / Desktop:** Material3 [androidx.compose.material3.NavigationBar] fallback
 *   so samples and shared previews still build.
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
