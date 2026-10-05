import SwiftUI
import UIKit

@main
struct iOSApp: App {
    var body: some Scene {
        WindowGroup {
            // Direction 1 (default): native UITabBarController as root via Kotlin.
            // Direction 2 alternative:
            // ComposeScreenView { SampleAppKt.ComposeOnlyViewController() }
            ComposeRootRepresentable()
                .ignoresSafeArea()
        }
    }
}

/// Thin representable that hosts Kotlin `MainViewController()` (native tab bar root).
private struct ComposeRootRepresentable: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        SampleAppKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
