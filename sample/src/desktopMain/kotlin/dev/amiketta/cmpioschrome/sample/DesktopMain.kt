package dev.amiketta.cmpioschrome.sample

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "cmp-ios-chrome sample") {
        SampleApp()
    }
}
