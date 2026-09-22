package com.baltajmn.line.model

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

const val BACKUP_VERSION = 1
const val REMINDER_DEFAULT_HOUR = 21
const val REMINDER_DEFAULT_MINUTE = 0

@Serializable
data class LineEntry(
    val text: String = "",
    /** File name inside photos/, extension included: "p-3f9a1c2e.jpg". */
    val photo: String? = null,
    /** Created after its own logical day. Counts as a line, never towards the streak. */
    val late: Boolean = false,
)

@Serializable
data class Settings(
    val reminderOn: Boolean = false,
    val reminderHour: Int = REMINDER_DEFAULT_HOUR,
    val reminderMinute: Int = REMINDER_DEFAULT_MINUTE,
    val reminderOffered: Boolean = false,
    val lockOn: Boolean = false,
    val cover: String = "sage",
    /** Local ISO date of the last export the system confirmed. */
    val lastBackup: String? = null,
    val backupNoticeDone: Boolean = false,
    /** RevenueCat's last answer, so Pro survives an offline start. */
    val pro: Boolean = false,
)

/** Local ISO date ("2027-01-17") of the logical day to its entry. */
typealias Journal = Map<String, LineEntry>

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class JournalFile(
    @EncodeDefault val version: Int = BACKUP_VERSION,
    @EncodeDefault val entries: Map<String, LineEntry> = emptyMap(),
    val settings: Settings = Settings(),
)

/** What goes into a backup: the diary without the settings of this phone. */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class ExportFile(
    @EncodeDefault val version: Int = BACKUP_VERSION,
    @EncodeDefault val entries: Map<String, LineEntry> = emptyMap(),
)

@OptIn(ExperimentalSerializationApi::class)
val JournalJson = Json {
    ignoreUnknownKeys = true
    encodeDefaults = false
    explicitNulls = false
}
