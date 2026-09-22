import SwiftUI
import WidgetKit

/// Pro. The year as written or not written: the grid is painted from the 366 characters of
/// widget.json, which carry no text at all (docs/pantallas.md 11.2).
struct YearEntry: TimelineEntry {
    let date: Date
    let state: LineState?
}

struct YearProvider: TimelineProvider {
    func placeholder(in context: Context) -> YearEntry { YearEntry(date: .now, state: nil) }

    func getSnapshot(in context: Context, completion: @escaping (YearEntry) -> Void) {
        completion(entry(at: .now))
    }

    /// Now and the next 03:00, like the today widget: the grid gains a day at the same cutoff.
    func getTimeline(in context: Context, completion: @escaping (Timeline<YearEntry>) -> Void) {
        let cutoff = LineStore.nextCutoff(after: .now)
        completion(Timeline(entries: [entry(at: .now), entry(at: cutoff)], policy: .after(cutoff)))
    }

    private func entry(at instant: Date) -> YearEntry {
        guard let state = LineStore.read() else { return YearEntry(date: instant, state: nil) }
        let day = LineStore.logicalDay(instant)
        return YearEntry(
            date: instant,
            state: LineStore.view(state, on: LineStore.key(day), year: LineStore.year(day))
        )
    }
}

private struct YearGrid: View {
    let days: String?
    let year: Int
    let today: Date
    let cover: Color

    private static let months = 12
    private static let daysInMonth = 31

    var body: some View {
        Canvas { context, size in
            let gap: CGFloat = 2
            let side = min(
                (size.width - CGFloat(Self.daysInMonth - 1) * gap) / CGFloat(Self.daysInMonth),
                (size.height - CGFloat(Self.months - 1) * gap) / CGFloat(Self.months)
            )
            let step = side + gap
            let left = (size.width - (step * CGFloat(Self.daysInMonth) - gap)) / 2
            let top = (size.height - (step * CGFloat(Self.months) - gap)) / 2
            var calendar = Calendar(identifier: .gregorian)
            calendar.timeZone = .current
            let marks = Array(days ?? "")

            for month in 1...Self.months {
                for day in 1...Self.daysInMonth {
                    var parts = DateComponents()
                    parts.year = year
                    parts.month = month
                    parts.day = day
                    // A day the month does not have comes back as another month: skip it.
                    guard let date = calendar.date(from: parts),
                          calendar.component(.day, from: date) == day,
                          let ordinal = calendar.ordinality(of: .day, in: .year, for: date)
                    else { continue }

                    let box = CGRect(
                        x: left + step * CGFloat(day - 1),
                        y: top + step * CGFloat(month - 1),
                        width: side,
                        height: side
                    )
                    let cell = Path(roundedRect: box, cornerRadius: side / 5)
                    if ordinal <= marks.count, marks[ordinal - 1] == "1" {
                        context.fill(cell, with: .color(cover))
                    } else if date > today {
                        context.stroke(cell, with: .color(.primary.opacity(0.08)), lineWidth: 1)
                    } else {
                        context.stroke(cell, with: .color(.primary.opacity(0.18)), lineWidth: 1)
                    }
                }
            }
        }
    }
}

private struct YearView: View {
    @Environment(\.widgetFamily) private var family
    let entry: YearEntry

    private var pro: Bool { entry.state?.pro == true }
    private var year: Int { entry.state?.year ?? LineStore.year(LineStore.logicalDay(entry.date)) }
    private var written: Int { entry.state?.days.filter { $0 == "1" }.count ?? 0 }

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            HStack {
                Text(String(year))
                    .font(.system(size: family == .systemLarge ? 20 : 13, weight: .medium))
                Spacer()
                if pro {
                    Text(L.yearCount(written, year))
                        .font(.system(size: 12))
                        .foregroundStyle(.secondary)
                }
            }
            ZStack {
                YearGrid(
                    days: pro ? entry.state?.days : nil,
                    year: year,
                    today: LineStore.logicalDay(entry.date),
                    cover: LineStore.coverColor(entry.state?.cover ?? "sage")
                )
                if !pro {
                    VStack(spacing: 2) {
                        Text(L.proTitle).font(.system(size: 13, weight: .semibold))
                        Text(L.unlock).font(.system(size: 12)).foregroundStyle(.secondary)
                    }
                }
            }
        }
        // A locked grid that opened the year screen would explain nothing: it sells Pro instead.
        .widgetURL(URL(string: pro ? "com.baltajmn.line://year" : "com.baltajmn.line://pro"))
        .containerBackground(for: .widget) { Color("WidgetBackground") }
    }
}

struct LineYearWidget: Widget {
    var body: some WidgetConfiguration {
        StaticConfiguration(kind: "LineYearWidget", provider: YearProvider()) { entry in
            YearView(entry: entry)
        }
        // Literals, not L: AppIntents and the widget gallery read these at build time.
        .configurationDisplayName("The year")
        .description("Your year, day by day")
        .supportedFamilies([.systemMedium, .systemLarge])
    }
}
