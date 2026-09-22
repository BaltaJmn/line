package com.baltajmn.line

import com.baltajmn.line.data.FREE_PHOTO_LIMIT
import com.baltajmn.line.data.LineRepository
import com.baltajmn.line.data.Storage
import com.baltajmn.line.data.freePhotoName
import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.datetime.LocalDate

// Three entries with a photo are free; the fourth is where Pro comes in (docs/tecnico.md 6.13).
class PhotoTest {
    private lateinit var dir: File

    @BeforeTest
    fun setUp() {
        dir = createTempDirectory("purl-photos").toFile()
        Storage.rootOverride = dir
    }

    @AfterTest
    fun tearDown() {
        Storage.rootOverride = null
        dir.deleteRecursively()
    }

    private fun load(pro: Boolean, photos: Int) {
        val entries = (1..photos).joinToString(",") { n ->
            """"2027-01-0$n":{"text":"line","photo":"p-0000000$n.jpg"}"""
        }
        File(dir, "entries.json")
            .writeText("""{"version":1,"entries":{$entries},"settings":{"pro":$pro}}""")
        LineRepository.load()
    }

    @Test
    fun theFourthDayWithAPhotoNeedsPro() {
        load(pro = false, photos = FREE_PHOTO_LIMIT)
        assertFalse(LineRepository.canAddPhoto(LocalDate(2027, 2, 1)))

        // Replacing the photo of a day that already has one is not a fourth photo.
        assertTrue(LineRepository.canAddPhoto(LocalDate(2027, 1, 1)))

        load(pro = true, photos = FREE_PHOTO_LIMIT)
        assertTrue(LineRepository.canAddPhoto(LocalDate(2027, 2, 1)))
    }

    @Test
    fun underTheLimitNobodyIsAsked() {
        load(pro = false, photos = FREE_PHOTO_LIMIT - 1)
        assertTrue(LineRepository.canAddPhoto(LocalDate(2027, 2, 1)))
    }

    @Test
    fun aNewPhotoNeverReusesAName() {
        val taken = setOf("p-0000000a.jpg")
        val name = freePhotoName(taken)
        assertFalse(name in taken)
        assertTrue(Regex("p-[0-9a-f]{8}\\.jpg").matches(name), name)
        assertEquals(8, name.removePrefix("p-").removeSuffix(".jpg").length)
    }
}
