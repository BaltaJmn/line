package com.baltajmn.line.book

/**
 * The platform half of the book: fonts, a PDF canvas and the photos. Where things go is decided in
 * Book.kt; this only measures text and draws the pages it is handed, into a file in the app's
 * temporary space that [deliver] then hands to the system picker.
 */
expect object BookWriter {
    /**
     * Draws [pages] one at a time, blocking the calling thread from the first page to the last.
     * False when it failed or [cancelled] said stop between two pages; either way nothing is left.
     */
    fun write(
        literata: ByteArray,
        pages: (BookMeasure) -> Sequence<BookPage>,
        cancelled: () -> Boolean,
        onPage: (BookPage) -> Unit,
    ): Boolean

    /** Streams the finished book into [sink], a piece at a time. */
    fun deliver(sink: (ByteArray) -> Unit)

    /** Deletes the finished book once the picker is done with it, whatever the user chose. */
    fun discard()
}
