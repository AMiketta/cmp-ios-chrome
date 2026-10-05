package dev.amiketta.cmpioschrome.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
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
                    TabPane("Home")
                }
                tab(title = "Search", systemImage = "magnifyingglass") {
                    TabPane("Search")
                }
                tab(title = "Profile", systemImage = "person") {
                    TabPane("Profile")
                }
            }
        }
    }
}

@Composable
private fun TabPane(name: String) {
    Box(
        Modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text("$name tab (Compose content)")
    }
}
