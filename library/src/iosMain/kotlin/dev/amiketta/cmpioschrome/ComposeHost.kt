package dev.amiketta.cmpioschrome

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

/**
 * Direction 2 — host any Compose UI inside UIKit / SwiftUI.
 *
 * Call from Kotlin `iosMain` (or export a thin factory from your app module), then wrap the
 * returned [UIViewController] with the Swift helpers in `swift/CmpIosChrome/ComposeScreen.swift`.
 *
 * Content is wrapped in [Modifier.fillMaxSize] so nested hosts are not measured 0×0.
 *
 * Example (app `iosMain`):
 * ```
 * fun HomeScreenController(): UIViewController =
 *     composeScreenController { HomeScreen() }
 * ```
 */
fun composeScreenController(
    content: @Composable () -> Unit,
): UIViewController = ComposeUIViewController {
    Box(Modifier.fillMaxSize()) {
        content()
    }
}
