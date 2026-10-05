# Swift helpers (`CmpIosChrome`)

**Direction 2** — use Compose Multiplatform screens inside Xcode / SwiftUI without hand-rolling `UIViewControllerRepresentable` each time.

1. Build/export your KMP framework (sample: `SampleApp`).
2. Copy `CmpIosChrome/ComposeScreen.swift` into the iOS app target.
3. Call a Kotlin factory that returns `UIViewController` (from `composeScreenController { … }` or `ComposeUIViewController { … }`).

```swift
import SwiftUI

struct ContentView: View {
    var body: some View {
        ComposeScreenView {
            SampleAppKt.ComposeOnlyViewController()
        }
    }
}
```

Or embed inside a native `TabView` / `NavigationStack` while keeping individual screens in Compose.
