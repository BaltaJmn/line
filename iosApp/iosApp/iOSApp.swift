import SwiftUI
import WidgetKit
import Shared

@main
struct iOSApp: App {
    @Environment(\.scenePhase) private var scenePhase
    @Environment(\.colorScheme) private var colorScheme

    init() {
        // WidgetCenter is Swift's; Kotlin writes widget.json and asks through here.
        LineBridge.shared.reloadWidgets = { WidgetCenter.shared.reloadAllTimelines() }
    }

    var body: some Scene {
        WindowGroup {
            // The system takes the task switcher picture before Compose could repaint, so the cover
            // is painted here, in the window Swift owns (docs/tecnico.md 7).
            ZStack {
                ContentView()
                if scenePhase != .active && LineBridge.shared.isLockOn() {
                    Color(colorScheme == .dark ? UIColor(red: 0.09, green: 0.082, blue: 0.059, alpha: 1)
                                               : UIColor(red: 0.984, green: 0.973, blue: 0.953, alpha: 1))
                        .ignoresSafeArea()
                }
            }
            .onOpenURL { LineBridge.shared.open(url: $0.absoluteString) }
            .onChange(of: scenePhase, initial: true) { _, phase in
                if phase == .active { Self.checkMemoryWidget() }
            }
        }
    }

    /// The memory widget is the consent to put a line in widget.json, so Kotlin has to know whether
    /// it is placed (docs/tecnico.md 12.3). Asked on every return: it may have been added meanwhile.
    private static func checkMemoryWidget() {
        WidgetCenter.shared.getCurrentConfigurations { result in
            guard case .success(let widgets) = result else { return }
            let placed = widgets.contains { $0.kind == "LineMemoryWidget" }
            DispatchQueue.main.async { LineBridge.shared.setMemoryWidgetPlaced(placed: placed) }
        }
    }
}
