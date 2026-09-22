package com.baltajmn.line.data

import com.baltajmn.line.model.Journal
import com.baltajmn.line.model.LineEntry
import com.baltajmn.line.model.fold
import kotlinx.datetime.LocalDate

/**
 * Free text over the diary already in memory: no index and no database. A diary of thirty years is
 * eleven thousand short lines, and folding them all costs less than keeping an index honest
 * (docs/tecnico.md 6.5).
 */
fun search(j: Journal, query: String): List<Pair<LocalDate, LineEntry>> {
    val q = fold(query.trim())
    if (q.isEmpty()) return emptyList()
    return j.filter { (_, e) -> fold(e.text).contains(q) }
        .map { (k, e) -> LocalDate.parse(k) to e }
        .sortedByDescending { it.first }
}
