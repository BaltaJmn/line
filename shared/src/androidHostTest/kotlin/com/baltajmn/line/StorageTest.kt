package com.baltajmn.line

import com.baltajmn.line.data.LineRepository
import com.baltajmn.line.data.Storage
import com.baltajmn.line.model.JournalFile
import com.baltajmn.line.model.LineEntry
import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

// 2. Atomic writes, and a file that cannot be read is never overwritten.
class StorageTest {
    private lateinit var dir: File

    private val good = """{"version":1,"entries":{"2027-01-17":{"text":"kept"}}}"""

    @BeforeTest
    fun setUp() {
        dir = createTempDirectory("purl").toFile()
        Storage.rootOverride = dir
    }

    @AfterTest
    fun tearDown() {
        Storage.rootOverride = null
        dir.deleteRecursively()
    }

    @Test
    fun halfWrittenTempAndUnreadableMainFallBackToTheBackup() {
        File(dir, "entries.tmp.json").writeText("{\"version\":1,\"entr")
        File(dir, "entries.json").writeText("not json")
        File(dir, "entries.bak.json").writeText(good)

        LineRepository.load()

        assertEquals("kept", LineRepository.journal["2027-01-17"]?.text)
        assertFalse(LineRepository.corrupt)
        assertEquals(good, File(dir, "entries.json").readText())
        assertEquals(good, File(dir, "entries.bak.json").readText())
    }

    @Test
    fun twoUnreadableFilesAreQuarantinedUntouched() {
        File(dir, "entries.json").writeText("garbage one")
        File(dir, "entries.bak.json").writeText("garbage two")

        LineRepository.load()

        assertTrue(LineRepository.corrupt)
        assertTrue(LineRepository.journal.isEmpty())
        assertFalse(File(dir, "entries.json").exists())
        assertFalse(File(dir, "entries.bak.json").exists())
        val moved = File(dir, "corrupt").listFiles().orEmpty()
        assertEquals(setOf("garbage one", "garbage two"), moved.map { it.readText() }.toSet())
        assertTrue(moved.any { it.name.endsWith(".bak.json") })
    }

    @Test
    fun noFilesIsAFreshDiary() {
        LineRepository.load()
        assertFalse(LineRepository.corrupt)
        assertTrue(LineRepository.journal.isEmpty())
        assertFalse(File(dir, "entries.json").exists())
    }

    @Test
    fun writeRotatesTheBackupAndLeavesNoTemp() {
        Storage.write("first")
        Storage.write("second")
        assertEquals("second", Storage.read())
        assertEquals("first", Storage.readPrevious())
        assertFalse(File(dir, "entries.tmp.json").exists())
    }

    @Test
    fun orphanPhotosAndImportLeftoversAreSweptOnLoad() {
        File(dir, "entries.json").writeText(
            """{"version":1,"entries":{"2027-01-17":{"text":"x","photo":"p-00000001.jpg"}}}""",
        )
        Storage.writePhoto("p-00000001.jpg", byteArrayOf(1))
        Storage.writePhoto("p-deadbeef.jpg", byteArrayOf(2))
        File(dir, "import").apply { mkdirs() }.resolve("p-cafebabe.jpg").writeBytes(byteArrayOf(3))

        LineRepository.load()

        assertEquals(listOf("p-00000001.jpg"), Storage.listPhotos())
        assertTrue(File(dir, "import").list().orEmpty().isEmpty())
    }

    @Test
    fun theFileIsWrittenInDateOrder() {
        val text = LineRepository.encode(
            JournalFile(entries = mapOf("2027-02-01" to LineEntry("b"), "2026-01-01" to LineEntry("a"))),
        )
        assertTrue(text.indexOf("2026-01-01") < text.indexOf("2027-02-01"))
    }
}
