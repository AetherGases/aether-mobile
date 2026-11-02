package com.aether.application.core.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.app.LocaleManager
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

/** What the user picked in the settings; [SYSTEM] follows the device language. */
enum class AppLanguage(val tag: String?) {
    SYSTEM(null),
    PT_BR("pt-BR"),
    EN("en-US")
}

/**
 * Single source of truth for the language the app runs in. The in-app choice wins, then the device
 * language, and anything the app has no translation for falls back to pt-BR (the default resources).
 * Everything that formats a date or number, or tells the backend which language to answer in,
 * must go through here instead of hardcoding a locale.
 */
object AppLocale {
    private const val PREFS = "app_locale"
    private const val KEY_LANGUAGE = "language"

    private val PT_BR = Locale.forLanguageTag("pt-BR")
    private val EN = Locale.forLanguageTag("en-US")

    @Volatile
    var language: AppLanguage = AppLanguage.SYSTEM
        private set

    /**
     * Reads the saved choice. Safe to call repeatedly; call it before anything needs [current].
     * From Android 13 the system owns the per-app language (so the device settings screen and the
     * in-app picker stay in sync); before that it is kept in our own preferences.
     */
    fun init(context: Context) {
        val appContext = context.applicationContext
        language = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val locales = appContext.getSystemService(LocaleManager::class.java).applicationLocales
            when (locales.takeIf { !it.isEmpty }?.get(0)?.language) {
                null -> AppLanguage.SYSTEM
                EN.language -> AppLanguage.EN
                else -> AppLanguage.PT_BR
            }
        } else {
            val saved = appContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(KEY_LANGUAGE, null)
            AppLanguage.entries.firstOrNull { it.name == saved } ?: AppLanguage.SYSTEM
        }
    }

    /** The locale the app should use right now. */
    fun current(): Locale = language.tag?.let(Locale::forLanguageTag) ?: fromDevice()

    private fun fromDevice(): Locale {
        // Locale.getDefault() may already be our own override, so ask the system directly
        val device = Resources.getSystem().configuration.locales[0]
        return if (device.language == EN.language) EN else PT_BR
    }

    /** Applies the app language to an activity's base context (call from attachBaseContext). */
    fun wrap(base: Context): Context {
        init(base)
        val locale = current()
        Locale.setDefault(locale)
        val configuration = Configuration(base.resources.configuration).apply { setLocale(locale) }
        return base.createConfigurationContext(configuration)
    }

    fun setLanguage(context: Context, language: AppLanguage) {
        if (language == this.language) return
        this.language = language
        val appContext = context.applicationContext
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // The system recreates the activities itself
            appContext.getSystemService(LocaleManager::class.java).applicationLocales =
                language.tag?.let(LocaleList::forLanguageTags) ?: LocaleList.getEmptyLocaleList()
        } else {
            appContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_LANGUAGE, language.name)
                .apply()
            context.findActivity()?.recreate()
        }
    }

    private fun Context.findActivity(): Activity? {
        var context = this
        while (context is ContextWrapper) {
            if (context is Activity) return context
            context = context.baseContext
        }
        return null
    }

    // Formatters are built on demand because the language can change while the app is running.

    /** 31/10/2026 or 10/31/2026, following the locale's field order. */
    fun shortDateFormatter(): DateTimeFormatter = skeleton("ddMMyyyy")

    /** 31 out 2026 / Oct 31, 2026 */
    fun mediumDateFormatter(): DateTimeFormatter = skeleton("dMMMyyyy")

    /** Outubro 2026 / October 2026 */
    fun monthYearFormatter(): DateTimeFormatter = skeleton("MMMMyyyy")

    /** out / Oct */
    fun monthAbbreviationFormatter(): DateTimeFormatter = skeleton("MMM")

    /** 31/10 14:05 or 10/31 2:05 PM */
    fun dayMonthTimeFormatter(): DateTimeFormatter = skeleton("ddMMjjmm")

    fun integerFormat(): NumberFormat = NumberFormat.getIntegerInstance(current())

    fun decimalFormat(maxFractionDigits: Int): NumberFormat =
        NumberFormat.getNumberInstance(current()).apply {
            minimumFractionDigits = 0
            maximumFractionDigits = maxFractionDigits
        }

    private fun skeleton(skeleton: String): DateTimeFormatter {
        val locale = current()
        return DateTimeFormatter.ofPattern(
            android.text.format.DateFormat.getBestDateTimePattern(locale, skeleton),
            locale
        )
    }
}
