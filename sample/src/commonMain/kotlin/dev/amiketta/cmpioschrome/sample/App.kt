package dev.amiketta.cmpioschrome.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.amiketta.cmpioschrome.LocalTabBarOverlap
import dev.amiketta.cmpioschrome.NativeTabBar

@Composable
fun SampleApp() {
    MaterialTheme {
        Surface(Modifier.fillMaxSize()) {
            var selected by remember { mutableStateOf(0) }
            NativeTabBar(
                selectedIndex = selected,
                onSelectedIndexChange = { selected = it },
            ) {
                tab(title = "Home", systemImage = "house") {
                    // Map-like full-bleed background under the translucent bar.
                    MapDemoPane()
                }
                tab(title = "Search", systemImage = "magnifyingglass") {
                    ListDemoPane(title = "Search")
                }
                tab(title = "Profile", systemImage = "person") {
                    ListDemoPane(title = "Profile")
                }
            }
        }
    }
}

/**
 * Demonstrates edge-to-edge content: the gradient fills the screen (runs under the tab bar),
 * while the label is padded by [LocalTabBarOverlap] so it stays readable above the bar.
 */
@Composable
private fun MapDemoPane() {
    val overlap = LocalTabBarOverlap.current
    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF1B5E20), Color(0xFF81C784), Color(0xFFE8F5E9)),
                    ),
                ),
        )
        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = overlap + 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                "Home (map under translucent bar)",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF1B5E20),
            )
            Text(
                "LocalTabBarOverlap = $overlap",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF2E7D32),
            )
        }
    }
}

/**
 * List that scrolls under the bar; [LocalTabBarOverlap] is applied as bottom contentPadding
 * so the last rows remain tappable above the chrome.
 */
@Composable
private fun ListDemoPane(title: String) {
    val overlap = LocalTabBarOverlap.current
    val rows = remember { (1..40).map { "$title item #$it" } }
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 24.dp,
            end = 24.dp,
            top = 24.dp,
            bottom = overlap + 16.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                "$title tab — scroll under bar",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Bottom contentPadding includes LocalTabBarOverlap ($overlap)",
                style = MaterialTheme.typography.bodySmall,
            )
        }
        items(rows) { row ->
            Surface(
                tonalElevation = 1.dp,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(row, Modifier.padding(16.dp))
            }
        }
    }
}
