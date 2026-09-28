import SwiftUI
import WidgetKit

/// Pro, and the one surface that shows words of the diary: the line of a year ago. Placing it is
/// the consent; the app only writes `line` into widget.json while it is placed, with Pro and with
/// the lock off (docs/tecnico.md 12.3). Everything else it can say without a word of the diary.
struct MemoryEntry: TimelineEntry {
    let date: Date
    let state: LineState?
}

struct MemoryProvider: TimelineProvider {
    func placeholder(in context: Context) -> MemoryEntry { MemoryEntry(date: .now, state: nil) }

    func getSnapshot(in context: Context, completion: @escaping (MemoryEntry) -> Void) {
        completion(entry(at: .now))
    }

    /// Now and the next 03:00: at the cutoff the widget switches to `lineNext` on its own.
    func getTimeline(in context: Context, completion: @escaping (Timeline<MemoryEntry>) -> Void) {
        let cutoff = LineStore.nextCutoff(after: .now)
        completion(Timeline(entries: [entry(at: .now), entry(at: cutoff)], policy: .after(cutoff)))
    }

    private func entry(at instant: Date) -> MemoryEntry {
        guard let state = LineStore.read() else { return MemoryEntry(date: instant, state: nil) }
        let day = LineStore.logicalDay(instant)
        return MemoryEntry(
            date: instant,
            state: LineStore.view(state, on: LineStore.key(day), year: LineStore.year(day))
        )
    }
}

private struct MemoryView: View {
    let entry: MemoryEntry

    private var pro: Bool { entry.state?.pro == true }
    private var locked: Bool { entry.state?.locked == true }
    private var line: String? { pro && !locked ? entry.state?.line : nil }

    var body: some View {
        content
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: line == nil ? .center : .topLeading)
            .widgetURL(URL(string: !pro ? "com.baltajmn.line://pro"
                : line != nil ? "com.baltajmn.line://memory" : "com.baltajmn.line://today"))
            .containerBackground(for: .widget) { Color("WidgetBackground") }
    }

    @ViewBuilder private var content: some View {
        if !pro {
            VStack(spacing: 2) {
                Text(L.proTitle).font(.system(size: 13, weight: .medium))
                Text(L.unlock).font(.system(size: 12)).foregroundStyle(.secondary)
            }
            .multilineTextAlignment(.center)
        } else if locked {
            Text(L.locked).font(.system(size: 13)).foregroundStyle(.secondary)
        } else if let line {
            VStack(alignment: .leading, spacing: 6) {
                Text(L.memoryLabel(LineStore.year(LineStore.logicalDay(entry.date)) - 1).uppercased())
                    .font(.system(size: 11, weight: .medium))
                    .foregroundStyle(.secondary)
                // Redacted on the lock screen and in StandBy: a home screen is not a public place,
                // those two are.
                Text(line)
                    .font(.custom("Literata-Regular", size: 15))
                    .lineLimit(5)
                    .truncationMode(.tail)
                    .privacySensitive()
            }
        } else {
            Text(L.noMemory)
                .font(.system(size: 13))
                .foregroundStyle(.secondary)
                .multilineTextAlignment(.center)
        }
    }
}

struct LineMemoryWidget: Widget {
    var body: some WidgetConfiguration {
        StaticConfiguration(kind: "LineMemoryWidget", provider: MemoryProvider()) { entry in
            MemoryView(entry: entry)
        }
        // Literals, not L: the widget gallery reads these at build time.
        .configurationDisplayName("Memory")
        .description("What you wrote a year ago")
        .supportedFamilies([.systemSmall, .systemMedium])
    }
}
