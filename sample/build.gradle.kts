plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

val iosEnabled = gradle.extra["cmpioschrome.iosEnabled"] as Boolean

kotlin {
    jvm("desktop")

    if (iosEnabled) {
        listOf(iosArm64(), iosSimulatorArm64()).forEach {
            it.binaries.framework {
                baseName = "SampleApp"
                isStatic = true
                export(project(":library"))
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":library"))
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
        }
        getByName("desktopMain").dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

compose.desktop {
    application {
        mainClass = "dev.amiketta.cmpioschrome.sample.DesktopMainKt"
    }
}
