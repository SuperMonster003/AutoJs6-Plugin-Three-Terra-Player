package io.github.supermonster003.autojs6.plugin.threeterraplayer.settings

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import io.github.supermonster003.autojs6.plugin.threeterraplayer.theme.AutoJs6AppearanceClient
import io.github.supermonster003.autojs6.plugin.threeterraplayer.theme.AutoJs6AppearanceResult
import io.github.supermonster003.autojs6.plugin.threeterraplayer.theme.AutoJs6AppearanceSnapshot
import org.autojs.plugin.common.api.AutoJs6HostSettingsContract as HostContract

internal data class AppAppearanceResolution(
    val hostResult: AutoJs6AppearanceResult,
    val languageTag: String?,
    val nightMode: Int,
)

/** Applies app language and night mode before activities inflate their resources. */
internal object AppAppearanceController {

    fun apply(
        context: Context,
        suppliedHostResult: AutoJs6AppearanceResult? = null,
    ): AppAppearanceResolution {
        val store = AppPreferenceStore(context)
        val hostResult = suppliedHostResult ?: AutoJs6AppearanceClient.query(context)
        val languageTag = resolveLanguageTag(store.language(), hostResult.snapshot)
        val nightMode = resolveNightMode(store.nightMode(), hostResult.snapshot)

        applyLanguage(context, languageTag)
        if (AppCompatDelegate.getDefaultNightMode() != nightMode) {
            AppCompatDelegate.setDefaultNightMode(nightMode)
        }
        return AppAppearanceResolution(hostResult, languageTag, nightMode)
    }

    private fun applyLanguage(context: Context, languageTag: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val manager = context.getSystemService(LocaleManager::class.java)
            val desiredLocales = languageTag
                ?.let(LocaleList::forLanguageTags)
                ?: LocaleList.getEmptyLocaleList()
            if (manager.applicationLocales.toLanguageTags() != desiredLocales.toLanguageTags()) {
                manager.applicationLocales = desiredLocales
            }
            return
        }
        val desiredLocales = languageTag
            ?.let(LocaleListCompat::forLanguageTags)
            ?: LocaleListCompat.getEmptyLocaleList()
        if (AppCompatDelegate.getApplicationLocales().toLanguageTags() != desiredLocales.toLanguageTags()) {
            AppCompatDelegate.setApplicationLocales(desiredLocales)
        }
    }

    fun resolveLanguageTag(
        preference: AppLanguagePreference,
        host: AutoJs6AppearanceSnapshot?,
    ): String? = when (preference.mode) {
        AppLanguageMode.AUTOJS6 -> host?.resolvedLanguageTag
            ?.let(AppPreferenceStore::normalizeSupportedLanguageTag)
        AppLanguageMode.SYSTEM -> null
        AppLanguageMode.SPECIFIC -> AppPreferenceStore.normalizeSupportedLanguageTag(
            preference.languageTag,
        )
    }

    fun resolveNightMode(
        preference: AppNightMode,
        host: AutoJs6AppearanceSnapshot?,
    ): Int = when (preference) {
        AppNightMode.AUTOJS6 -> host?.let(::hostNightMode) ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        AppNightMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        AppNightMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
        AppNightMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
    }

    private fun hostNightMode(host: AutoJs6AppearanceSnapshot): Int = when (host.darkModePolicy) {
        HostContract.DARK_MODE_FOLLOW_SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        HostContract.DARK_MODE_DARK -> AppCompatDelegate.MODE_NIGHT_YES
        HostContract.DARK_MODE_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
        else -> if (host.darkModeActive) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
    }
}
