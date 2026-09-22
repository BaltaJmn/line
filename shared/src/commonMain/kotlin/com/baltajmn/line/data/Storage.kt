package com.baltajmn.line.data

/**
 * The diary file, its previous version and the photos, in the app's private storage and never in
 * the App Group: the widgets only ever see widget.json.
 *
 * There is no cloud copy, so these files are the user's years. A write can never leave the main
 * file truncated, and a file that could not be read is moved aside, never overwritten.
 */
expect object Storage {
    /** entries.json, or null when it does not exist. */
    fun read(): String?

    /** entries.bak.json: the version before the last write. */
    fun readPrevious(): String?

    /** Atomic: the current file becomes the .bak and the new text replaces it in one rename. */
    fun write(text: String)

    /** Rewrites entries.json from a good .bak without rotating it, so the good copy survives. */
    fun restoreMain(text: String)

    /** Moves both files to corrupt/entries-<yyyyMMdd-HHmmss>.json and .bak.json. */
    fun quarantine()

    fun writePhoto(name: String, bytes: ByteArray)
    fun readPhoto(name: String): ByteArray?
    fun deletePhoto(name: String)
    fun listPhotos(): List<String>

    /** Path of import/, emptied and created. */
    fun importDir(): String
}
