package com.baltajmn.line

import com.baltajmn.line.data.journalMarkdown
import com.baltajmn.line.data.merge
import com.baltajmn.line.data.search
import com.baltajmn.line.model.JournalFile
import com.baltajmn.line.model.JournalJson
import com.baltajmn.line.model.LineEntry
import com.baltajmn.line.model.TAG_MAX
import com.baltajmn.line.model.addTag
import com.baltajmn.line.model.codePointCount
import com.baltajmn.line.model.normalizeTag
import com.baltajmn.line.model.tagSuggestions
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

/** docs/tecnico.md 12.6, and the three places a tag has to show up: search, merge and journal.md. */
class TagsTest {

    @Test
    fun normalisationHasOneSpellingPerTag() {
        assertEquals("mi-madre", normalizeTag("  #Mi   madre "))
        assertEquals("viaje", normalizeTag("#viaje"))
        assertNull(normalizeTag(" # "))
        assertEquals(TAG_MAX, normalizeTag("a".repeat(40))!!.codePointCount())
    }

    @Test
    fun addingSkipsRepeatsAndStopsAtFive() {
        assertEquals(listOf("cena"), addTag(listOf("cena"), "#Cena"))
        val five = listOf("a", "b", "c", "d", "e")
        assertEquals(five, addTag(five, "f"))
        assertEquals(listOf("a", "b"), addTag(listOf("a"), "B"))
    }

    @Test
    fun suggestionsAreTheMostUsedMinusTheOnesAlreadyOn() {
        val j = mapOf(
            "2026-01-01" to LineEntry("x", tags = listOf("cena", "amigos")),
            "2026-01-02" to LineEntry("x", tags = listOf("cena")),
            "2026-01-03" to LineEntry("x", tags = listOf("viaje", "cena", "amigos")),
        )
        assertEquals(listOf("cena", "amigos", "viaje"), tagSuggestions(j, emptyList()))
        assertEquals(listOf("amigos", "viaje"), tagSuggestions(j, listOf("cena")))
    }

    @Test
    fun searchFindsATagEvenWhenTheTextDoesNotSayIt() {
        val j = mapOf("2026-01-01" to LineEntry("Cena larga", tags = listOf("amigos")))
        assertEquals(1, search(j, "amigos").size)
    }

    @Test
    fun mergeJoinsTagsThisPhoneFirst() {
        val device = mapOf("2026-01-01" to LineEntry("a", tags = listOf("cena", "amigos")))
        val incoming = mapOf("2026-01-01" to LineEntry("a", tags = listOf("viaje", "cena")))
        val r = merge(device, incoming)
        assertEquals(listOf("cena", "amigos", "viaje"), r.journal.getValue("2026-01-01").tags)
        assertEquals(1, r.joined)
    }

    @Test
    fun theMarkdownPutsTagsUnderTheText() {
        val j = mapOf("2027-02-03" to LineEntry("Cena con los de la facultad.", tags = listOf("amigos", "cena")))
        assertEquals("# Purl\n\n## 2027-02-03\nCena con los de la facultad.\n#amigos #cena\n", journalMarkdown(j))
    }

    @Test
    fun anOldFileWithoutTagsReadsAndAnEmptyListIsNotWritten() {
        val old = JournalJson.decodeFromString<JournalFile>("""{"version":1,"entries":{"2026-01-01":{"text":"a"}}}""")
        assertEquals(emptyList(), old.entries.getValue("2026-01-01").tags)
        assertFalse(JournalJson.encodeToString(old).contains("tags"))
    }
}
