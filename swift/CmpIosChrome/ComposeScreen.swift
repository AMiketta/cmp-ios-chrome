import SwiftUI
import UIKit

/// Direction 2: drop-in SwiftUI wrapper around any Kotlin `ComposeUIViewController` factory.
///
/// Copy this file into your Xcode target (or SPM package) and point [factory] at an exported
/// Kotlin function, e.g. `SampleAppKt.ComposeOnlyViewController()`.
public struct ComposeScreen: UIViewControllerRepresentable {
    public let factory: () -> UIViewController

    public init(factory: @escaping () -> UIViewController) {
        self.factory = factory
    }

    public func makeUIViewController(context: Context) -> UIViewController {
        factory()
    }

    public func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        // Compose owns its own recomposition; nothing to sync for the MVP.
    }
}

public extension ComposeScreen {
    /// Convenience SwiftUI `View` so you can write `ComposeScreenView { … }` in a `TabView` / `NavigationStack`.
    struct ViewHost: View {
        private let factory: () -> UIViewController

        public init(factory: @escaping () -> UIViewController) {
            self.factory = factory
        }

        public var body: some View {
            ComposeScreen(factory: factory)
                .ignoresSafeArea(.keyboard) // Compose handles IME; avoid double insets
        }
    }
}

/// Typealias for call-sites that prefer a short name.
public typealias ComposeScreenView = ComposeScreen.ViewHost
