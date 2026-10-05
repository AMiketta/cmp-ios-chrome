import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import org.gradle.api.plugins.ExtensionAware

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

val androidEnabled = gradle.extra["cmpioschrome.androidEnabled"] as Boolean
val iosEnabled = gradle.extra["cmpioschrome.iosEnabled"] as Boolean

if (androidEnabled) apply(plugin = libs.plugins.android.kmp.library.get().pluginId)

if (androidEnabled) {
    (kotlin as ExtensionAware).extensions.configure<KotlinMultiplatformAndroidLibraryExtension> {
        namespace = "dev.amiketta.cmpioschrome"
        compileSdk = 37
        minSdk = 24
    }
}

kotlin {
    jvm("desktop")

    if (iosEnabled) {
        listOf(iosArm64(), iosSimulatorArm64()).forEach {
            it.binaries.framework {
                baseName = "CmpIosChrome"
                isStatic = true
            }
        }
    }

    sourceSets {
        val commonMain = getByName("commonMain")
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        val nonIosMain = create("nonIosMain") {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.compose.material3)
            }
        }
        getByName("desktopMain").dependsOn(nonIosMain)
        if (androidEnabled) {
            getByName("androidMain").dependsOn(nonIosMain)
        }

        if (iosEnabled) {
            val iosMain = create("iosMain") {
                dependsOn(commonMain)
            }
            getByName("iosArm64Main").dependsOn(iosMain)
            getByName("iosSimulatorArm64Main").dependsOn(iosMain)
        }
    }
}
