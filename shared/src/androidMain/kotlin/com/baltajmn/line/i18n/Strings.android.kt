package com.baltajmn.line.i18n

import java.util.Locale

actual fun systemLanguage(): String = Locale.getDefault().language
