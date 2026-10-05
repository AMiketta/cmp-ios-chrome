rootProject.name = "cmp-ios-chrome"

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

val androidFlag: String? = providers.gradleProperty("cmpioschrome.android").orNull
val sdkFromLocalProps: String? = file("local.properties").takeIf { it.exists() }
    ?.readLines()?.firstOrNull { it.startsWith("sdk.dir=") }?.substringAfter("=")
val sdkDir: String? = sdkFromLocalProps
    ?: System.getenv("ANDROID_HOME")
    ?: System.getenv("ANDROID_SDK_ROOT")
val androidEnabled = when (androidFlag) {
    "true" -> true
    "false" -> false
    else -> sdkDir != null && file(sdkDir).isDirectory
}
gradle.extra["cmpioschrome.androidEnabled"] = androidEnabled

val iosFlag: String? = providers.gradleProperty("cmpioschrome.ios").orNull
val isMac = System.getProperty("os.name").lowercase().contains("mac")
gradle.extra["cmpioschrome.iosEnabled"] = when (iosFlag) {
    "true" -> true
    "false" -> false
    else -> isMac
}

include(":library")
include(":sample")
