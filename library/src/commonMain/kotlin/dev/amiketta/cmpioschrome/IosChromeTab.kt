package dev.amiketta.cmpioschrome

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

/**
 * Describes one tab for [NativeTabBar].
 *
 * @param title Visible tab title.
 * @param systemImage SF Symbol name on iOS (e.g. `"house"`). Ignored or mapped on other platforms.
 * @param content Compose content shown when the tab is selected.
 */
@Immutable
data class IosChromeTab(
    val title: String,
    val systemImage: String? = null,
    val content: @Composable () -> Unit,
)

/**
 * DSL scope for declaring tabs inside [NativeTabBar].
 */
interface IosTabBarScope {
    fun tab(
        title: String,
        systemImage: String? = null,
        content: @Composable () -> Unit,
    )
}

internal class IosTabBarScopeImpl : IosTabBarScope {
    val tabs = mutableListOf<IosChromeTab>()

    override fun tab(
        title: String,
        systemImage: String?,
        content: @Composable () -> Unit,
    ) {
        tabs += IosChromeTab(title = title, systemImage = systemImage, content = content)
    }
}
