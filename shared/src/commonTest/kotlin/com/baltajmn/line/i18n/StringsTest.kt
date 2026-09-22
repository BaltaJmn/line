package com.baltajmn.line.i18n

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.LocalDate

/** The examples of docs/textos.md, in the five languages. */
class StringsTest {
    private val saved = S.lang
    private val jan17 = LocalDate(2026, 1, 17)

    @AfterTest
    fun restore() {
        S.lang = saved
    }

    private fun each(check: (String) -> String, en: String, es: String, pt: String, de: String, fr: String) {
        listOf("en" to en, "es" to es, "pt" to pt, "de" to de, "fr" to fr).forEach { (code, expected) ->
            S.lang = code
            assertEquals(expected, check(code), code)
        }
    }

    @Test
    fun fallsBackToEnglish() {
        assertEquals("es", normalizeLanguage("es-ES"))
        assertEquals("en", normalizeLanguage("it"))
        assertEquals("en", normalizeLanguage(""))
    }

    @Test
    fun dates() {
        each(
            { S.longDate(jan17) },
            "Saturday, January 17", "Sábado, 17 de enero", "Sábado, 17 de janeiro", "Samstag, 17. Januar",
            "Samedi 17 janvier",
        )
        each(
            { S.longDateWithYear(LocalDate(2026, 1, 20)) },
            "Tuesday, January 20, 2026", "Martes, 20 de enero de 2026", "Terça-feira, 20 de janeiro de 2026",
            "Dienstag, 20. Januar 2026", "Mardi 20 janvier 2026",
        )
        each(
            { S.abbrDateWithYear(LocalDate(2027, 1, 17)) },
            "Jan 17, 2027", "17 ene 2027", "17 jan 2027", "17. Jan. 2027", "17 janv. 2027",
        )
        each({ S.widgetDate(jan17) }, "SAT 17 JAN", "SÁB 17 ENE", "SÁB 17 JAN", "SA 17 JAN", "SAM 17 JANV")
        each(
            { S.returnsOn(LocalDate(2028, 1, 17)) },
            "This page will come back on January 17, 2028.", "Esta página volverá el 17 de enero de 2028.",
            "Esta página vai voltar em 17 de janeiro de 2028.", "Diese Seite kommt am 17. Januar 2028 wieder.",
            "Cette page reviendra le 17 janvier 2028.",
        )
        each(
            { S.a11yDay(jan17, written = false) },
            "January 17, not written", "17 de enero, sin escribir", "17 de janeiro, sem escrever",
            "17. Januar, nicht geschrieben", "17 janvier, pas écrite",
        )
    }

    @Test
    fun plurals() {
        each(
            { S.pastYearLabel(2026, 1) + " / " + S.pastYearLabel(2025, 2) },
            "2026, a year ago / 2025, 2 years ago", "2026, hace un año / 2025, hace 2 años",
            "2026, há um ano / 2025, há 2 anos", "2026, vor einem Jahr / 2025, vor 2 Jahren",
            "2026, il y a un an / 2025, il y a 2 ans",
        )
        each(
            { S.yearCount(1, 2027) + " / " + S.yearCount(212, 2027) },
            "1 line in 2027 / 212 lines in 2027", "1 línea en 2027 / 212 líneas en 2027",
            "1 linha em 2027 / 212 linhas em 2027", "1 Zeile in 2027 / 212 Zeilen in 2027",
            "1 ligne en 2027 / 212 lignes en 2027",
        )
    }

    @Test
    fun importSummaryJoinsTheParts() {
        each(
            { S.importSummary(12, 3, 40) },
            "The backup brings 12 new days, 3 that join with yours and 40 that are the same. Nothing gets deleted.",
            "La copia trae 12 días nuevos, 3 que se juntan con los tuyos y 40 iguales. No se borra nada.",
            "A cópia traz 12 dias novos, 3 que se juntam aos seus e 40 iguais. Nada é apagado.",
            "Die Sicherung bringt 12 neue Tage, 3, die sich mit deinen verbinden, und 40 gleiche. Es wird nichts gelöscht.",
            "La copie apporte 12 nouveaux jours, 3 qui se joignent aux tiens et 40 identiques. Rien n'est supprimé.",
        )
        S.lang = "es"
        assertEquals("La copia trae 1 día nuevo. No se borra nada.", S.importSummary(1, 0, 0))
        assertEquals("La copia trae 2 días nuevos y 1 igual. No se borra nada.", S.importSummary(2, 0, 1))
        S.lang = "de"
        assertEquals(
            "Die Sicherung bringt 1 neuen Tag und 2, die sich mit deinen verbinden. Es wird nichts gelöscht.",
            S.importSummary(1, 2, 0),
        )
    }

    @Test
    fun coversAndHours() {
        S.lang = "es"
        assertEquals("salvia", S.coverName("sage"))
        assertEquals("salvia", S.coverName("unknown"))
        assertEquals("lila", S.coverName("lilac"))
        assertEquals("A las 09:05", S.reminderAt(9, 5))
        assertEquals(12, S.monthNames().size)
        assertEquals(7, S.weekdayShort().size)
    }
}
