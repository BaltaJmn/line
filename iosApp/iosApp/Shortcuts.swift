import AppIntents
import Shared

/// Writing without opening the app (docs/tecnico.md 12.2). Swift only carries the words across:
/// the diary is written by Kotlin, so no Swift code ever has to know the shape of entries.json.
/// Literals in English on purpose: AppIntents reads them at build time, and the translations live in
/// <lang>.lproj/Localizable.strings and AppShortcuts.strings under the same English key.
struct WriteLineIntent: AppIntent {
    static var title: LocalizedStringResource = "Write today's line"
    static var description = IntentDescription("Adds a line to today in your diary.")
    // The phone has to be unlocked: a diary must not take dictation from whoever holds it.
    static var authenticationPolicy: IntentAuthenticationPolicy = .requiresAuthentication

    @Parameter(title: "Line", requestValueDialog: "What do you want to write?")
    var line: String

    @MainActor
    func perform() async throws -> some IntentResult & ProvidesDialog {
        let result = try await LineBridge.shared.dictate(text: line)
        // Kotlin enums reach Swift lower cased whole, and a suspend result arrives optional.
        if result == DictateResult.needspro {
            return .result(dialog: "Dictating a line is part of Purl Pro. Open the app to see it.")
        }
        return .result(dialog: "Saved in your diary.")
    }
}

struct LineShortcuts: AppShortcutsProvider {
    static var appShortcuts: [AppShortcut] {
        AppShortcut(
            intent: WriteLineIntent(),
            phrases: [
                "Write my line in \(.applicationName)",
                "Add a line to \(.applicationName)",
            ],
            shortTitle: "Write today's line",
            systemImageName: "square.and.pencil"
        )
    }
}
