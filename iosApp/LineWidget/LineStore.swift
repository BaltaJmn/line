import Foundation
import SwiftUI

/// Exactly the shape of `widget.json` (docs/tecnico.md 4.2). There is no model of the diary in
/// Swift, and in v1.0 `line` and `lineNext` never arrive: the file carries no text at all.
struct LineState: Decodable {
    let date: String
    let written: Bool
    let memory: Bool
    let memoryNext: Bool
    let year: Int
    let days: String
    let cover: String
    let pro: Bool
    let line: String?
    let lineNext: String?
}

/// Reads the one file the extension is allowed to see, and nothing else.
enum LineStore {
    static let appGroup = "group.com.baltajmn.line"

    static func read() -> LineState? {
        guard let url = FileManager.default
            .containerURL(forSecurityApplicationGroupIdentifier: appGroup)?
            .appendingPathComponent("widget.json"),
            let data = try? Data(contentsOf: url)
        else { return nil }
        return try? JSONDecoder().decode(LineState.self, from: data)
    }

    /// The same rule as `widgetView` in Kotlin. The widget may wake up days after the app last ran.
    static func view(_ state: LineState, on day: String, year: Int) -> LineState {
        var seen = state
        if state.date != day {
            let yesterday = dayBefore(day)
            seen = LineState(
                date: day,
                written: false,
                memory: state.date == yesterday ? state.memoryNext : false,
                memoryNext: state.memoryNext,
                year: state.year,
                days: state.days,
                cover: state.cover,
                pro: state.pro,
                line: state.date == yesterday ? state.lineNext : nil,
                lineNext: state.lineNext
            )
        }
        guard seen.year != year else { return seen }
        return LineState(
            date: seen.date, written: seen.written, memory: seen.memory, memoryNext: seen.memoryNext,
            year: year, days: String(repeating: "0", count: 366), cover: seen.cover, pro: seen.pro,
            line: seen.line, lineNext: seen.lineNext
        )
    }

    // MARK: - The logical day

    private static var calendar: Calendar {
        var c = Calendar(identifier: .gregorian)
        c.timeZone = .current
        return c
    }

    /// The day ends at 03:00, so anything before that still belongs to the night before.
    static func logicalDay(_ instant: Date) -> Date {
        let c = calendar
        let start = c.startOfDay(for: instant)
        return c.component(.hour, from: instant) < 3 ? c.date(byAdding: .day, value: -1, to: start)! : start
    }

    static func nextCutoff(after instant: Date) -> Date {
        let c = calendar
        let todayAt3 = c.date(bySettingHour: 3, minute: 0, second: 0, of: instant)!
        return todayAt3 > instant ? todayAt3 : c.date(byAdding: .day, value: 1, to: todayAt3)!
    }

    static func key(_ day: Date) -> String {
        let f = DateFormatter()
        f.calendar = calendar
        f.timeZone = .current
        f.locale = Locale(identifier: "en_US_POSIX")
        f.dateFormat = "yyyy-MM-dd"
        return f.string(from: day)
    }

    static func year(_ day: Date) -> Int { calendar.component(.year, from: day) }

    private static func dayBefore(_ key: String) -> String {
        let f = DateFormatter()
        f.locale = Locale(identifier: "en_US_POSIX")
        f.dateFormat = "yyyy-MM-dd"
        guard let day = f.date(from: key) else { return "" }
        return Self.key(calendar.date(byAdding: .day, value: -1, to: day)!)
    }

    // MARK: - The covers, the same eight as the app

    static func coverColor(_ id: String) -> Color {
        switch id {
        case "rose": return Color(red: 0.941, green: 0.686, blue: 0.745)
        case "peach": return Color(red: 0.961, green: 0.765, blue: 0.608)
        case "butter": return Color(red: 0.929, green: 0.863, blue: 0.596)
        case "mint": return Color(red: 0.612, green: 0.827, blue: 0.780)
        case "sky": return Color(red: 0.635, green: 0.765, blue: 0.914)
        case "periwinkle": return Color(red: 0.706, green: 0.722, blue: 0.925)
        case "lilac": return Color(red: 0.851, green: 0.686, blue: 0.902)
        default: return Color(red: 0.714, green: 0.839, blue: 0.671)
        }
    }
}
