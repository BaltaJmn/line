package com.baltajmn.line

import com.baltajmn.line.data.ImportFailed
import com.baltajmn.line.data.ImportProblem
import com.baltajmn.line.data.mergeMood
import com.baltajmn.line.data.readBackup
import com.baltajmn.line.data.readMoodBackup
import com.baltajmn.line.i18n.S
import com.baltajmn.line.model.LineEntry
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/** docs/tecnico.md 4.5 and 12.5. */
class MoodImportTest {

    private fun source(text: String): (Int) -> ByteArray? {
        var bytes: ByteArray? = text.encodeToByteArray()
        return { _ -> bytes.also { bytes = null } }
    }

    private val backup = """
        {"app":"mood","store":{"moods":[],"entries":[
          {"date":"2026-01-17","moodId":"m4","note":"Paseo por el rio","tags":["Mi madre","#sol"]},
          {"date":"2026-01-18","moodId":"m2","note":"   "},
          {"date":"2026-01-19","moodId":"m3","note":"Ya escrito aqui"},
          {"date":"no-date","moodId":"m3","note":"x"}
        ]},"photos":{"m4.jpg":"AAAA"}}
    """.trimIndent()

    @Test
    fun takesNotesWithTextAndTheirTagsAndNothingElse() {
        val notes = readMoodBackup(source(backup))
        assertEquals(setOf("2026-01-17", "2026-01-19"), notes.keys)
        assertEquals(LineEntry("Paseo por el rio", tags = listOf("mi-madre", "sol")), notes["2026-01-17"])
    }

    @Test
    fun neverTouchesADayThatHasALine() {
        val device = mapOf("2026-01-19" to LineEntry("Mio", late = true))
        val r = mergeMood(device, readMoodBackup(source(backup)))
        assertEquals(LineEntry("Mio", late = true), r.journal["2026-01-19"])
        assertEquals(false, r.journal.getValue("2026-01-17").late)
        assertEquals(1, r.added)
        assertEquals(1, r.same)
    }

    @Test
    fun refusesWhatIsNotMoodTraker() {
        val purl = """{"version":1,"entries":{"2026-01-17":{"text":"a"}}}"""
        assertEquals(ImportProblem.MoodNotBackup, assertFailsWith<ImportFailed> { readMoodBackup(source(purl)) }.problem)
        assertEquals(ImportProblem.MoodNotBackup, assertFailsWith<ImportFailed> { readMoodBackup(source("hola")) }.problem)
        val empty = """{"app":"mood","store":{"entries":[{"date":"2026-01-17","moodId":"m1","note":""}]}}"""
        assertEquals(ImportProblem.Empty, assertFailsWith<ImportFailed> { readMoodBackup(source(empty)) }.problem)
    }

    @Test
    fun aPurlBackupKeepsItsTags() {
        val purl = """{"version":1,"entries":{"2026-01-17":{"text":"a","tags":["cena","Cena","#viaje"]}}}"""
        assertEquals(listOf("cena", "viaje"), readBackup(source(purl)).journal.getValue("2026-01-17").tags)
    }

    @Test
    fun theCountSaysBothHalvesOnlyWhenBothHappen() {
        val lang = S.lang
        S.lang = "es"
        assertEquals(
            "Entran 120 notas de MoodTraker. 8 días ya tenían línea y se quedan como están.",
            S.importMoodCount(120, 8),
        )
        assertEquals("Entra 1 nota de MoodTraker.", S.importMoodCount(1, 0))
        S.lang = lang
    }
}
