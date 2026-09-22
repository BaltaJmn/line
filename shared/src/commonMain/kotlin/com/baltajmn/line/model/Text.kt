package com.baltajmn.line.model

const val LINE_LIMIT = 280
const val COUNTER_FROM = 250

/** Characters as a person counts them: a surrogate pair (most emoji) is one. */
fun String.codePointCount(): Int {
    var n = 0
    var i = 0
    while (i < length) {
        i += if (this[i].isHighSurrogate() && i + 1 < length && this[i + 1].isLowSurrogate()) 2 else 1
        n++
    }
    return n
}

/** The first [n] code points, never splitting a surrogate pair. */
fun String.clampCodePoints(n: Int): String {
    var i = 0
    var taken = 0
    while (i < length && taken < n) {
        i += if (this[i].isHighSurrogate() && i + 1 < length && this[i + 1].isLowSurrogate()) 2 else 1
        taken++
    }
    return substring(0, i)
}

/**
 * The edit the field accepts. [old] is the text before the keystroke, [new] what the keyboard or
 * the paste proposes. Only the inserted segment is trimmed, so pasting into a full line never cuts
 * its end, and a text already over the limit (imported or dictated) can shrink but not grow.
 */
fun limitEdit(old: String, new: String, limit: Int = LINE_LIMIT): String {
    val allowed = maxOf(limit, old.codePointCount())
    if (new.codePointCount() <= allowed) return new
    var p = new.commonPrefixWith(old).length
    var q = new.commonSuffixWith(old).length
    // Prefix and suffix may not overlap in either string: with old = "ab" and new = "abb" both
    // would claim the same "b" and the insertion would vanish.
    q = minOf(q, new.length - p, old.length - p)
    if (p > 0 && new[p - 1].isHighSurrogate()) p--
    if (q > 0 && new[new.length - q].isLowSurrogate()) q--
    val inserted = new.substring(p, new.length - q)
    val base = new.substring(0, p) + new.substring(new.length - q)
    val room = (allowed - base.codePointCount()).coerceAtLeast(0)
    return new.substring(0, p) + inserted.clampCodePoints(room) + new.substring(new.length - q)
}

/** Lowercase without diacritics, so "cafe" finds "Café" and "strasse" finds "Straße". */
fun fold(s: String): String {
    val out = StringBuilder(s.length)
    for (c in s.lowercase()) {
        when (c) {
            'á', 'à', 'â', 'ã', 'ä', 'å' -> out.append('a')
            'é', 'è', 'ê', 'ë' -> out.append('e')
            'í', 'ì', 'î', 'ï' -> out.append('i')
            'ó', 'ò', 'ô', 'õ', 'ö' -> out.append('o')
            'ú', 'ù', 'û', 'ü' -> out.append('u')
            'ý', 'ÿ' -> out.append('y')
            'ç' -> out.append('c')
            'ñ' -> out.append('n')
            'ß' -> out.append("ss")
            'œ' -> out.append("oe")
            'æ' -> out.append("ae")
            else -> out.append(c)
        }
    }
    return out.toString()
}
