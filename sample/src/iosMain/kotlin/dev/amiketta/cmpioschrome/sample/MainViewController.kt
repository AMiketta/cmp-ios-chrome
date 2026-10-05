package dev.amiketta.cmpioschrome.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import dev.amiketta.cmpioschrome.IosChromeTab
import dev.amiketta.cmpioschrome.LocalTabBarOverlap
import dev.amiketta.cmpioschrome.composeScreenController
import dev.amiketta.cmpioschrome.createNativeTabBarController
import platform.UIKit.UIViewController

/**
 * Primary iOS entry: **one** Compose composition with [NativeTabBar] overlay
 * (UITabBar via UIKitView). Theme / CompositionLocals / [LocalTabBarOverlap] are shared.
 */
fun MainViewController(): UIViewController =
    ComposeUIViewController { SampleApp() }

/**
 * Direction 2 — single Compose tree for SwiftUI via `swift/CmpIosChrome/ComposeScreen.swift`.
 */
fun ComposeOnlyViewController(): UIViewController =
    composeScreenController { SampleApp() }

/** Same as [MainViewController]; kept for call-site clarity in docs / Swift. */
fun SampleComposeViewController(): UIViewController =
    ComposeUIViewController { SampleApp() }

/**
 * Advanced / secondary demo: window-root [createNativeTabBarController].
 *
 * **Warning:** each tab is a separate Compose tree (no shared Theme / CompositionLocals
 * across tabs). Nesting this controller *inside* Compose often measures 0×0 — use only
 * as the UIWindow root, or prefer [MainViewController] / [NativeTabBar] overlay.
 */
fun NativeTabBarControllerRoot(): UIViewController = createNativeTabBarController(
    tabs = listOf(
        IosChromeTab("Home", "house") { IsolatedTabLabel("Home") },
        IosChromeTab("Search", "magnifyingglass") { IsolatedTabLabel("Search") },
        IosChromeTab("Profile", "person") { IsolatedTabLabel("Profile") },
    ),
)

@Composable
private fun IsolatedTabLabel(name: String) {
    // LocalTabBarOverlap is 0.dp here — separate tree, no NativeTabBar provider.
    val overlap = LocalTabBarOverlap.current
    Box(
        Modifier.fillMaxSize().padding(24.dp).padding(bottom = overlap),
        contentAlignment = Alignment.Center,
    ) {
        Text("$name (UITabBarController root — isolated Compose tree)")
    }
}
