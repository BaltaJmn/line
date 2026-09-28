// El espejo Swift de widgetView y del dia logico no lo cubre ningun test de Kotlin, y la extension
// no tiene target de tests. Se corre a mano cuando se toca LineStore.swift:
//
//   cp tools/check-linestore.swift /tmp/main.swift
//   swiftc -o /tmp/check /tmp/main.swift iosApp/LineWidget/LineStore.swift && /tmp/check
//
// main.swift y no este nombre: con dos ficheros, swiftc solo acepta codigo suelto en main.swift.
//
import Foundation

func day(_ s: String) -> Date {
    let f = DateFormatter()
    f.locale = Locale(identifier: "en_US_POSIX")
    f.timeZone = .current
    f.dateFormat = "yyyy-MM-dd HH:mm"
    return f.date(from: s)!
}

func expect(_ got: String, _ want: String, _ what: String) {
    precondition(got == want, "\(what): got \(got), want \(want)")
    print("ok \(what): \(got)")
}

// 02:59 still belongs to the day before; 03:00 starts the new one (docs/tecnico.md, dia logico).
expect(LineStore.key(LineStore.logicalDay(day("2027-01-17 02:59"))), "2027-01-16", "02:59")
expect(LineStore.key(LineStore.logicalDay(day("2027-01-17 03:00"))), "2027-01-17", "03:00")
expect(LineStore.key(LineStore.nextCutoff(after: day("2027-01-17 02:00"))), "2027-01-17", "cutoff before 3")
expect(LineStore.key(LineStore.nextCutoff(after: day("2027-01-17 10:00"))), "2027-01-18", "cutoff after 3")

let state = LineState(date: "2027-01-17", written: true, memory: false, memoryNext: true,
                      year: 2027, days: String(repeating: "1", count: 366), cover: "sage",
                      pro: false, locked: true, line: nil, lineNext: nil)

// Same day: untouched.
expect(LineStore.view(state, on: "2027-01-17", year: 2027).written.description, "true", "same day")
// Yesterday's state seen today: not written, and the memory is the one announced for today.
let next = LineStore.view(state, on: "2027-01-18", year: 2027)
expect(next.written.description, "false", "stale written")
expect(next.memory.description, "true", "stale memory")
// Two days stale: nothing can be assumed.
expect(LineStore.view(state, on: "2027-01-19", year: 2027).memory.description, "false", "two days stale")
// A new year empties the grid.
let newYear = LineStore.view(state, on: "2028-01-01", year: 2028)
expect(newYear.year.description, "2028", "new year")
expect(newYear.days.contains("1").description, "false", "grid cleared")
// The lock travels through the day change: a widget woken after 03:00 still knows not to quote.
expect(String(describing: LineStore.view(state, on: "2027-01-18", year: 2027).locked), "Optional(true)", "lock kept")
print("all ok")
