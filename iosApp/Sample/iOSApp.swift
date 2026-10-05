import SwiftUI
import UIKit

@main
struct iOSApp: App {
    var body: some Scene {
        WindowGroup {
            // Primary: one Compose tree + NativeTabBar (UITabBar overlay).
            // Advanced alternative (isolated Compose trees per tab, window-root only):
            //   SampleAppKt.NativeTabBarControllerRoot()
            // Direction 2 (SwiftUI host):
            //   ComposeScreenView { SampleAppKt.ComposeOnlyViewController() }
            ComposeRootRepresentable()
                .ignoresSafeArea()
        }
    }
}

/// Hosts Kotlin `MainViewController()` — ComposeUIViewController { SampleApp() }.
private struct ComposeRootRepresentable: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        SampleAppKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
