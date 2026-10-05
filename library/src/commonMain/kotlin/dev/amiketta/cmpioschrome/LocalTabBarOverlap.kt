package dev.amiketta.cmpioschrome

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Height by which an overlapping tab bar covers the bottom of the screen
 * (including the home-indicator / navigation-bar inset when applicable).
 *
 * Tab screens should draw backgrounds (maps, lists) edge-to-edge, but pad
 * **scrollable content and controls** by this value so nothing sits permanently
 * under the bar — e.g. `LazyColumn` `contentPadding` or a trailing `Spacer` in a
 * `verticalScroll` column.
 *
 * Provided by [NativeTabBar]. Default is `0.dp` when used outside that chrome.
 */
val LocalTabBarOverlap = staticCompositionLocalOf<Dp> { 0.dp }
