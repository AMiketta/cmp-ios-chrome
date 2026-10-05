# cmp-ios-chrome

Kotlin Multiplatform helpers to bridge **Compose Multiplatform** and **native iOS UI** — in **both directions**.

| Direction | What | Where |
|-----------|------|--------|
| **1. Native chrome → Compose** | Real `UITabBarController` / `UITab` (SF Symbols, system look / Liquid Glass) while tab **content** stays Compose | `:library` → `NativeTabBar`, `createNativeTabBarController` |
| **2. Compose → Xcode / SwiftUI** | Drop CMP screens into SwiftUI/UIKit without hand-writing `UIViewControllerRepresentable` each time | `composeScreenController` + `swift/CmpIosChrome/ComposeScreen.swift` |

License: MIT · Package: `dev.amiketta.cmpioschrome`

---

## Deutsch

### Zweck
Compose Multiplatform zeichnet UI plattformübergreifend — eine Tabbar fühlt sich daher oft **nicht** wie UIKit an. Gleichzeitig will man in Xcode oft einzelne Compose-Screens in SwiftUI einhängen, ohne jedes Mal Boilerplate zu tippen. Diese Lib skizziert beide Schnitte:

1. **Chrome nativ, Content Compose** (Tabbar; später Nav/Sheets).
2. **Compose-Screens in Xcode** über fertige Swift-Hüllen.

### Richtung 1 — API
```kotlin
NativeTabBar(selectedIndex = selected, onSelectedIndexChange = { selected = it }) {
    tab(title = "Home", systemImage = "house") { HomeScreen() }
    tab(title = "Search", systemImage = "magnifyingglass") { SearchScreen() }
}
```
- **iOS 18+:** `UITab` + `UITabBarController(tabs:)` (aktuelles Kotlin/Native-UIKit; `UIViewController.tabBarItem` ist in den Bindings nicht mehr exponiert).
- **Android / Desktop:** Material3-`NavigationBar`-Fallback.

Sauberste iOS-Einbindung als **Window-Root**:
```kotlin
fun MainViewController(): UIViewController =
    createNativeTabBarController(tabs = …)
```

### Richtung 2 — API
Kotlin (`iosMain`):
```kotlin
fun HomeScreenController(): UIViewController =
    composeScreenController { HomeScreen() }
```
Swift (Datei aus `swift/CmpIosChrome/` in das Xcode-Target kopieren):
```swift
ComposeScreenView {
    SampleAppKt.ComposeOnlyViewController()
}
```
Damit landen CMP-Views in `TabView` / `NavigationStack` / jedem SwiftUI-Screen, ohne eigene `UIViewControllerRepresentable`-Boilerplate.

### Grenzen
- Nur **Chrome** (Tabbar) ist nativ — Layout/State bleiben Compose.
- Kein vollständiges SwiftUI-in-Kotlin; Swift-Templates liegen neben dem KMP-Modul (`swift/`), noch kein published SPM-Binary.
- Tab-Content-Lambdas werden in der UIKit-Factory einmalig erfasst (Tabs als stabile Chrome behandeln).
- iOS-**Link**/Xcode nur auf **macOS**; Deployment Target Sample: **iOS 18** (wegen `UITab`).
- Linux/CI: Desktop-JVM (+ optional Android); iOS-Klib-Metadaten mit `-Pcmpioschrome.ios=true` möglich, kein Simulator-Link.

### Sample auf dem Mac
```bash
git clone https://github.com/AMiketta/cmp-ios-chrome.git
cd cmp-ios-chrome/iosApp
brew install xcodegen   # falls nötig
xcodegen generate
open CmpIosChromeSample.xcodeproj
```
Xcode baut das Kotlin-Framework per Gradle (`embedAndSignAppleFrameworkForXcode`).  
Desktop-Vorschau (auch Linux): `./gradlew :sample:run`

---

## English

### Purpose
Bridge shared Compose UI and native iOS chrome, plus a ready Swift wrapper so CMP screens drop into Xcode cleanly.

### Direction 1 — `NativeTabBar`
Expect/actual Composable (+ DSL). iOS uses the modern `UITab` API; other targets use Material3 navigation bar so the sample still builds on Linux/CI.

Prefer `createNativeTabBarController(...)` as the iOS window root.

### Direction 2 — `ComposeScreen`
- Kotlin: `composeScreenController { … }` → `UIViewController`
- Swift: copy `swift/CmpIosChrome/ComposeScreen.swift` → `ComposeScreen` / `ComposeScreenView`

### Limits
Chrome only; state stays in Compose; iOS link requires macOS + iOS 18; Swift helpers are templates (not SPM yet).

### Build
```bash
./gradlew :library:compileKotlinDesktop :sample:compileKotlinDesktop \
  -Pcmpioschrome.android=false -Pcmpioschrome.ios=false
# iOS klib metadata (no link) on any host:
./gradlew :library:compileKotlinIosSimulatorArm64 -Pcmpioschrome.ios=true
```
