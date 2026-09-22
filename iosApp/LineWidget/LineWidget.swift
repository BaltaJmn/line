import SwiftUI
import WidgetKit

// Placeholder so the extension target builds. The real widgets arrive with #21, #34 and #35.
struct TodayEntry: TimelineEntry {
    let date: Date
}

struct TodayProvider: TimelineProvider {
    func placeholder(in context: Context) -> TodayEntry { TodayEntry(date: .now) }
    func getSnapshot(in context: Context, completion: @escaping (TodayEntry) -> Void) {
        completion(TodayEntry(date: .now))
    }
    func getTimeline(in context: Context, completion: @escaping (Timeline<TodayEntry>) -> Void) {
        completion(Timeline(entries: [TodayEntry(date: .now)], policy: .never))
    }
}

struct LineTodayWidget: Widget {
    var body: some WidgetConfiguration {
        StaticConfiguration(kind: "LineTodayWidget", provider: TodayProvider()) { _ in
            Text("Purl").containerBackground(.background, for: .widget)
        }
        .supportedFamilies([.systemSmall])
    }
}

@main
struct LineWidgetBundle: WidgetBundle {
    var body: some Widget {
        LineTodayWidget()
    }
}
