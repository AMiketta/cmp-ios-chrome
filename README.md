# cmp-ios-chrome

Kotlin Multiplatform helpers to bridge **Compose Multiplatform** and **native iOS UI** — in **both directions**.

| Direction | What | Where |
|-----------|------|--------|
| **1. Native chrome → Compose** | Real `UITabBar` (SF Symbols, system look / Liquid Glass) as an **overlay** on one Compose tree; content stays edge-to-edge | `:library` → `NativeTabBar`, `LocalTabBarOverlap` |
| **2. Compose → Xcode / SwiftUI** | Drop CMP screens into SwiftUI/UIKit without hand-writing `UIViewControllerRepresentable` each time | `composeScreenController` + `swift/CmpIosChrome/ComposeScreen.swift` |

License: MIT · Package: `dev.amiketta.cmpioschrome`

---

## Deutsch

### Zweck
Compose Multiplatform zeichnet UI plattformübergreifend — eine Tabbar fühlt sich daher oft **nicht** wie UIKit an. Gleichzeitig will man in Xcode oft einzelne Compose-Screens in SwiftUI einhängen, ohne jedes Mal Boilerplate zu tippen. Diese Lib skizziert beide Schnitte:

1. **Chrome nativ, Content Compose** (Tabbar als Overlay; später Nav/Sheets).
2. **Compose-Screens in Xcode** über fertige Swift-Hüllen.

### Richtung 1 — API (empfohlen: Overlay)

```kotlin
NativeTabBar(selectedIndex = selected, onSelectedIndexChange = { selected = it }) {
    tab(title = "Home", systemImage = "house") { HomeScreen() }
    tab(title = "Search", systemImage = "magnifyingglass") { SearchScreen() }
}
```

- **iOS:** echte `UITabBar` über `UIKitView` (`placedAsOverlay`, `NonCooperative`), Inhalt in **einer** Compose-Komposition mit `Modifier.fillMaxSize()` — Karte/Listen laufen unter der transluzenten Leiste durch.
- **Android / Desktop:** Material3-`NavigationBar` mit demselben Overlay-Vertrag.
- **`LocalTabBarOverlap`:** Höhe der überdeckenden Leiste (inkl. Home-Indicator / Nav-Inset). Scrollbare Inhalte und Controls damit padden, Hintergründe weiter edge-to-edge zeichnen.

```kotlin
val overlap = LocalTabBarOverlap.current
LazyColumn(contentPadding = PaddingValues(bottom = overlap + 16.dp)) { … }
```

**Sauberste iOS-Einbindung** als Window-Root mit dem Overlay-Composable:

```kotlin
fun MainViewController(): UIViewController =
    ComposeUIViewController { SampleApp() } // SampleApp nutzt NativeTabBar
```

### Fallstricke: `UITabBarController` in Compose nesten

`createNativeTabBarController(...)` bleibt für den **Window-Root**-Pfad (UIKit besitzt den Tab-Wechsel; modernes `UITab`-API). **Nicht** als Kind in Compose einbetten:

| Problem | Wirkung |
|---------|---------|
| `UIKitViewController` misst oft **0×0** | Weißer / leerer Screen |
| Pro Tab eigener `ComposeUIViewController` | Getrennte Compose-Bäume → Theme, CompositionLocals, ImageLoader, Nav-State **nicht** geteilt |

Wenn du den Controller-Pfad brauchst: nur als `UIWindow.rootViewController`, und tab-lokale Provider selbst setzen. Für App-Chrome in Compose: **`NativeTabBar`-Overlay**.

### Richtung 2 — API
Kotlin (`iosMain`):
```kotlin
fun HomeScreenController(): UIViewController =
    composeScreenController { HomeScreen() } // wrappt fillMaxSize
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
- Tab-Listen in der Overlay-Factory einmalig erfasst (Chrome als stabile Struktur behandeln).
- iOS-**Link**/Xcode nur auf **macOS**; Sample Deployment Target: **iOS 18** (wegen `UITab` im optionalen Controller-Pfad).
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

### Direction 1 — `NativeTabBar` (overlay, recommended)
Expect/actual Composable (+ DSL). On iOS a real `UITabBar` overlays **one** Compose composition (`fillMaxSize`, edge-to-edge under the translucent bar). Other targets use Material3 `NavigationBar` with the same overlay + [`LocalTabBarOverlap`](library/src/commonMain/kotlin/dev/amiketta/cmpioschrome/LocalTabBarOverlap.kt) contract so shared padding works.

```kotlin
val overlap = LocalTabBarOverlap.current
// Draw map/list full-bleed; pad scrollables / controls by `overlap`.
```

Prefer:
```kotlin
ComposeUIViewController { SampleApp() } // uses NativeTabBar overlay
```

### Pitfall: nesting `UITabBarController` in Compose
`createNativeTabBarController` is an **advanced / window-root** helper. Embedded via `UIKitViewController` it often measures **0×0** (blank screen), and each tab is a **separate** Compose tree (no shared Theme / CompositionLocals). Use the overlay `NativeTabBar` instead unless UIKit must own the window root.

### Direction 2 — `ComposeScreen`
- Kotlin: `composeScreenController { … }` → `UIViewController` (content wrapped in `Box(Modifier.fillMaxSize())`)
- Swift: copy `swift/CmpIosChrome/ComposeScreen.swift` → `ComposeScreen` / `ComposeScreenView`

### Limits
Chrome only; state stays in Compose; iOS link requires macOS + iOS 18 for the optional `UITab` controller path; Swift helpers are templates (not SPM yet).

### Build
```bash
./gradlew :library:compileKotlinDesktop :sample:compileKotlinDesktop \
  -Pcmpioschrome.android=false -Pcmpioschrome.ios=false
# iOS klib metadata (no link) on any host:
./gradlew :library:compileKotlinIosSimulatorArm64 -Pcmpioschrome.ios=true
```
