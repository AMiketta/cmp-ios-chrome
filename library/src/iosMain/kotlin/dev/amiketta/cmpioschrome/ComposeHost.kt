package dev.amiketta.cmpioschrome

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

/**
 * Direction 2 — host any Compose UI inside UIKit / SwiftUI.
 *
 * Call from Kotlin `iosMain` (or export a thin factory from your app module), then wrap the
 * returned [UIViewController] with the Swift helpers in `swift/CmpIosChrome/ComposeScreen.swift`.
 *
 * Example (app `iosMain`):
 * ```
 * fun HomeScreenController(): UIViewController =
 *     composeScreenController { HomeScreen() }
 * ```
 */
fun composeScreenController(
    content: @Composable () -> Unit,
): UIViewController = ComposeUIViewController { content() }
