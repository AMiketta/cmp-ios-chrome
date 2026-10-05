package dev.amiketta.cmpioschrome.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import dev.amiketta.cmpioschrome.IosChromeTab
import dev.amiketta.cmpioschrome.composeScreenController
import dev.amiketta.cmpioschrome.createNativeTabBarController
import platform.UIKit.UIViewController

/**
 * Direction 1 — window root is a native [platform.UIKit.UITabBarController] (recommended on iOS).
 */
fun MainViewController(): UIViewController = createNativeTabBarController(
    tabs = listOf(
        IosChromeTab("Home", "house") { TabLabel("Home") },
        IosChromeTab("Search", "magnifyingglass") { TabLabel("Search") },
        IosChromeTab("Profile", "person") { TabLabel("Profile") },
    ),
)

/**
 * Direction 2 — single Compose tree for SwiftUI via `swift/CmpIosChrome/ComposeScreen.swift`.
 */
fun ComposeOnlyViewController(): UIViewController =
    composeScreenController { SampleApp() }

/** Full [SampleApp] (uses [dev.amiketta.cmpioschrome.NativeTabBar] Composable) in one host. */
fun SampleComposeViewController(): UIViewController =
    ComposeUIViewController { SampleApp() }

@Composable
private fun TabLabel(name: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("$name (native tab + Compose)")
    }
}
