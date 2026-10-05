package dev.amiketta.cmpioschrome

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
actual fun NativeTabBar(
    tabs: List<IosChromeTab>,
    modifier: Modifier,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
) {
    require(tabs.isNotEmpty()) { "NativeTabBar needs at least one tab" }
    val index = selectedIndex.coerceIn(0, tabs.lastIndex)

    Column(modifier = modifier.fillMaxSize()) {
        Box(Modifier.weight(1f).fillMaxWidth()) {
            tabs[index].content()
        }
        NavigationBar(modifier = Modifier.fillMaxWidth()) {
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
