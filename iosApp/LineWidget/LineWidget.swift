import SwiftUI
import WidgetKit

/// The mirror of `Strings.kt` for the extension, which cannot reach Kotlin (docs/textos.md 11).
enum L {
    private static var code: String {
        let tag = Locale.preferredLanguages.first ?? "en"
        return String(tag.prefix(2))
    }

    private static func t(_ en: String, _ es: String, _ pt: String, _ de: String, _ fr: String) -> String {
        switch code {
        case "es": return es
        case "pt": return pt
        case "de": return de
        case "fr": return fr
        default: return en
        }
    }

    static var written: String { t("Written", "Escrita", "Escrita", "Geschrieben", "Écrite") }
    static var notWritten: String {
        t("Not written yet", "Por escribir", "Por escrever", "Noch nicht geschrieben", "À écrire")
    }
    static var memory: String {
        t("There's a memory", "Hay recuerdo", "Há uma lembrança", "Es gibt eine Erinnerung", "Il y a un souvenir")
    }
    static var proTitle: String { "Purl Pro" }
    static var unlock: String {
        t("Tap to turn it on", "Toca para activarlo", "Toque para ativar", "Tippen zum Aktivieren",
          "Touche pour l'activer")
    }

    private static var weekdays: [String] {
        t("Mon, Tue, Wed, Thu, Fri, Sat, Sun", "lun, mar, mié, jue, vie, sáb, dom",
          "seg, ter, qua, qui, sex, sáb, dom", "Mo, Di, Mi, Do, Fr, Sa, So",
          "lun, mar, mer, jeu, ven, sam, dim").components(separatedBy: ", ")
    }

    private static var months: [String] {
        t("Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec",
          "ene, feb, mar, abr, may, jun, jul, ago, sept, oct, nov, dic",
          "jan, fev, mar, abr, mai, jun, jul, ago, set, out, nov, dez",
          "Jan., Feb., März, Apr., Mai, Juni, Juli, Aug., Sept., Okt., Nov., Dez.",
          "janv., févr., mars, avr., mai, juin, juil., août, sept., oct., nov., déc.")
            .components(separatedBy: ", ")
    }

    /// SAT 17 JAN, the same shape as `S.widgetDate`.
    static func date(_ day: Date) -> String {
        var calendar = Calendar(identifier: .gregorian)
        calendar.timeZone = .current
        // Calendar counts weekdays from Sunday; the app counts from Monday.
        let weekday = (calendar.component(.weekday, from: day) + 5) % 7
        let month = calendar.component(.month, from: day) - 1
        let number = calendar.component(.day, from: day)
        let name = months[month].replacingOccurrences(of: ".", with: "")
        return "\(weekdays[weekday]) \(number) \(name)".uppercased()
    }
}

struct TodayEntry: TimelineEntry {
    let date: Date
    let state: LineState?
}

struct TodayProvider: TimelineProvider {
    func placeholder(in context: Context) -> TodayEntry { TodayEntry(date: .now, state: nil) }

    func getSnapshot(in context: Context, completion: @escaping (TodayEntry) -> Void) {
        completion(entry(at: .now))
    }

    /// Now and the next 03:00, each already seen through the logical day it belongs to, and then
    /// ask to be woken at that cutoff. A widget must be right on its own overnight.
    func getTimeline(in context: Context, completion: @escaping (Timeline<TodayEntry>) -> Void) {
        let cutoff = LineStore.nextCutoff(after: .now)
        let timeline = Timeline(
            entries: [entry(at: .now), entry(at: cutoff)],
            policy: .after(cutoff)
        )
        completion(timeline)
    }

    private func entry(at instant: Date) -> TodayEntry {
        guard let state = LineStore.read() else { return TodayEntry(date: instant, state: nil) }
        let day = LineStore.logicalDay(instant)
        return TodayEntry(
            date: instant,
            state: LineStore.view(state, on: LineStore.key(day), year: LineStore.year(day))
        )
    }
}

private struct Dot: View {
    let size: CGFloat
    let filled: Bool
    let cover: Color

    var body: some View {
        Group {
            if filled {
                Circle().fill(cover)
            } else {
                Circle().stroke(Color.primary.opacity(0.15), lineWidth: 2)
            }
        }
        .frame(width: size, height: size)
    }
}

private struct TodayBody: View {
    let entry: TodayEntry

    private var cover: Color { LineStore.coverColor(entry.state?.cover ?? "sage") }
    private var written: Bool { entry.state?.written == true }

    var body: some View {
        VStack(spacing: 8) {
            Text(L.date(LineStore.logicalDay(entry.date)))
                .font(.system(size: 12, weight: .medium))
                .foregroundStyle(.secondary)
            Dot(size: 28, filled: written, cover: cover)
            Text(written ? L.written : L.notWritten)
                .font(.system(size: 15, weight: .medium))
                .multilineTextAlignment(.center)
            if entry.state?.memory == true {
                HStack(spacing: 6) {
                    Dot(size: 6, filled: true, cover: cover)
                    Text(L.memory).font(.system(size: 12)).foregroundStyle(.secondary)
                }
            }
        }
    }
}

private struct TodayView: View {
    @Environment(\.widgetFamily) private var family
    let entry: TodayEntry

    private var cover: Color { LineStore.coverColor(entry.state?.cover ?? "sage") }
    private var written: Bool { entry.state?.written == true }

    var body: some View {
        // SwiftUI redacts the text on its own when the user asks for it, and it stays redacted even
        // though there is nothing here to hide.
        content
            .widgetURL(URL(string: "com.baltajmn.line://today"))
            .containerBackground(for: .widget) { background }
    }

    @ViewBuilder private var background: some View {
        switch family {
        // The accessory families are painted by the system and must stay transparent.
        case .accessoryCircular, .accessoryRectangular: Color.clear
        default: Color("WidgetBackground")
        }
    }

    @ViewBuilder private var content: some View {
        switch family {
        case .accessoryCircular:
            if entry.state?.pro == true {
                ZStack {
                    Dot(size: 26, filled: written, cover: .primary)
                    if entry.state?.memory == true { Circle().fill(Color.primary).frame(width: 5, height: 5) }
                }
            } else {
                Image(systemName: "lock")
            }
        case .accessoryRectangular:
            if entry.state?.pro == true {
                VStack(alignment: .leading, spacing: 1) {
                    Text(L.date(LineStore.logicalDay(entry.date))).font(.system(size: 12, weight: .medium))
                    Text(written ? L.written : L.notWritten).font(.system(size: 13, weight: .semibold))
                    if entry.state?.memory == true { Text(L.memory).font(.system(size: 12)) }
                }
            } else {
                VStack(alignment: .leading, spacing: 1) {
                    Text(L.proTitle).font(.system(size: 13, weight: .semibold))
                    Text(L.unlock).font(.system(size: 12))
                }
            }
        case .systemMedium:
            HStack(spacing: 16) {
                Text(L.date(LineStore.logicalDay(entry.date)))
                    .font(.system(size: 17, weight: .medium))
                Spacer()
                TodayBody(entry: entry)
            }
        default:
            TodayBody(entry: entry)
        }
    }
}

struct LineTodayWidget: Widget {
    var body: some WidgetConfiguration {
        StaticConfiguration(kind: "LineTodayWidget", provider: TodayProvider()) { entry in
            TodayView(entry: entry)
        }
        // Literals, not L: AppIntents and the widget gallery read these at build time.
        .configurationDisplayName("Today")
        .description("Whether today has a line and whether there's a memory")
        .supportedFamilies([.systemSmall, .systemMedium, .accessoryCircular, .accessoryRectangular])
    }
}

@main
struct LineWidgetBundle: WidgetBundle {
    var body: some Widget {
        LineTodayWidget()
    }
}
