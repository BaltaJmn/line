package com.baltajmn.line.data

import com.baltajmn.line.i18n.S

/**
 * The published privacy policy. Play, the App Store and the About section all point at the same
 * URL, so it lives here and not in three places. Source of the page: store/privacy/index.html.
 */
const val PRIVACY_URL = "https://line.baltajmn.dev/"

/** The few things about the build itself that the About section needs. */
expect object AppInfo {
    /** Version name as the store shows it, "1.0". */
    val version: String

    fun open(url: String)
}

data class Sibling(val name: String, val tagline: String, val androidUrl: String?, val iosUrl: String?)

/** The store of the platform this build runs on, or null while that app has no page there. */
expect val Sibling.storeUrl: String?

/** True on iOS. The paywall names the lock screen widget, which only that platform has. */
expect val onIos: Boolean

/**
 * The sister apps. A store URL only once that app is in production on that store the day Purl is
 * published; until then it is null and its row is not shown (docs/tecnico.md 6.16, issue #30).
 */
val SIBLINGS: List<Sibling>
    get() = listOf(
        Sibling("Quilt", S.siblingQuilt, androidUrl = null, iosUrl = null),
        Sibling("MoodTraker", S.siblingMood, androidUrl = null, iosUrl = null),
    )
