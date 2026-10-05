package dev.amiketta.cmpioschrome

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Material3 NavigationBar default container height (dp). */
private val NavigationBarContainerHeight = 80.dp

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
    val barHeight = NavigationBarContainerHeight + bottomInset

    // Same contract as iOS: content is edge-to-edge; bar overlays; LocalTabBarOverlap
    // tells shared screens how much to pad scrollables / controls.
    Box(modifier.fillMaxSize()) {
        CompositionLocalProvider(LocalTabBarOverlap provides barHeight) {
            Box(Modifier.fillMaxSize()) {
                tabs[index].content()
            }
        }
        NavigationBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            windowInsets = WindowInsets.navigationBars.only(WindowInsetsSides.Bottom),
        ) {
            tabs.forEachIndexed { i, tab ->
                NavigationBarItem(
                    selected = i == index,
                    onClick = { onSelectedIndexChange(i) },
                    icon = {
                        // No SF Symbols off-iOS; title doubles as label.
                        Box(Modifier, contentAlignment = Alignment.Center) {
                            Text(tab.title.take(1))
                        }
                    },
                    label = { Text(tab.title) },
                )
            }
        }
    }
}
