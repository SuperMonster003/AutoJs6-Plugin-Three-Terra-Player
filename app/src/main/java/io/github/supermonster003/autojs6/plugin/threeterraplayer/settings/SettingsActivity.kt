package io.github.supermonster003.autojs6.plugin.threeterraplayer.settings

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import io.github.supermonster003.autojs6.plugin.threeterraplayer.PlaybackPositionStore
import io.github.supermonster003.autojs6.plugin.threeterraplayer.R
import io.github.supermonster003.autojs6.plugin.threeterraplayer.databinding.ActivitySettingsBinding
import io.github.supermonster003.autojs6.plugin.threeterraplayer.theme.AudioThemePaletteGenerator
import io.github.supermonster003.autojs6.plugin.threeterraplayer.theme.AudioThemePicker
import io.github.supermonster003.autojs6.plugin.threeterraplayer.theme.AudioThemedActivity
import io.github.supermonster003.autojs6.plugin.threeterraplayer.theme.AutoJs6AppearanceClient
import io.github.supermonster003.autojs6.plugin.threeterraplayer.theme.AutoJs6AppearanceResult
import io.github.supermonster003.autojs6.plugin.threeterraplayer.theme.AutoJs6HostAvailability
import io.github.supermonster003.autojs6.plugin.threeterraplayer.theme.ThemePreferenceStore
import io.github.supermonster003.autojs6.plugin.threeterraplayer.theme.ThemePresetCatalog
import io.github.supermonster003.autojs6.plugin.threeterraplayer.theme.ThemeSourceMode
import io.github.supermonster003.autojs6.plugin.threeterraplayer.update.AppUpdateCoordinator
import io.github.supermonster003.autojs6.plugin.threeterraplayer.update.AppUpdateStore
import java.util.Locale
import org.autojs.plugin.common.api.AutoJs6HostSettingsContract as HostContract

/** Standalone-app settings. Host-following options remain visible when unavailable. */
class SettingsActivity : AudioThemedActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var preferenceStore: AppPreferenceStore
    private var hostResult = AutoJs6AppearanceResult(AutoJs6HostAvailability.NOT_INSTALLED)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceStore = AppPreferenceStore(this)
        hostResult = AutoJs6AppearanceClient.query(this)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        styleViews()
        binding.toolbar.setNavigationOnClickListener { finishAfterTransition() }
        bindRows()
        renderValues()
    }

    override fun onResume() {
        super.onResume()
        if (!::binding.isInitialized) return
        hostResult = AutoJs6AppearanceClient.query(this)
        renderValues()
    }

    private fun bindRows() {
        binding.languageSetting.setOnClickListener { showLanguageDialog() }
        binding.nightModeSetting.setOnClickListener { showNightModeDialog() }
        binding.themeColorSetting.setOnClickListener {
            AudioThemePicker(this) { recreate() }.show()
        }
        binding.rememberPositionSetting.setOnClickListener {
            val enabled = !binding.rememberPositionSwitch.isChecked
            preferenceStore.rememberPlaybackPosition = enabled
            binding.rememberPositionSwitch.isChecked = enabled
            if (!enabled) PlaybackPositionStore(this).clearAll()
        }
        binding.checkUpdateSetting.setOnClickListener {
            AppUpdateCoordinator.checkManually(this)
        }
        binding.autoUpdateSetting.setOnClickListener {
            val enabled = !binding.autoUpdateSwitch.isChecked
            preferenceStore.autoCheckUpdates = enabled
            binding.autoUpdateSwitch.isChecked = enabled
        }
        binding.ignoredUpdatesSetting.setOnClickListener {
            AppUpdateCoordinator.manageIgnoredUpdates(this) { renderValues() }
        }
        binding.releaseHistorySetting.setOnClickListener {
            startActivity(Intent(this, ReleaseHistoryActivity::class.java))
        }
        binding.aboutSetting.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
    }

    private fun showLanguageDialog() {
        val labels = buildList {
            add(getString(R.string.follow_autojs6))
            add(getString(R.string.follow_system))
            AppPreferenceStore.SUPPORTED_LANGUAGE_TAGS.forEach { tag -> add(displayLanguageName(tag)) }
        }
        val preference = preferenceStore.language()
        val checked = when (preference.mode) {
            AppLanguageMode.AUTOJS6 -> 0
            AppLanguageMode.SYSTEM -> 1
            AppLanguageMode.SPECIFIC -> AppPreferenceStore.SUPPORTED_LANGUAGE_TAGS
                .indexOf(preference.languageTag)
                .takeIf { it >= 0 }
                ?.plus(2)
                ?: 1
        }
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.setting_language)
            .setSingleChoiceItems(
                DisabledChoiceAdapter(labels, disableFirst = !hostResult.available),
                checked,
            ) { selectedDialog, which ->
                val selected = when (which) {
                    0 -> AppLanguagePreference(AppLanguageMode.AUTOJS6)
                    1 -> AppLanguagePreference(AppLanguageMode.SYSTEM)
                    else -> AppPreferenceStore.SUPPORTED_LANGUAGE_TAGS.getOrNull(which - 2)
                        ?.let { tag -> AppLanguagePreference(AppLanguageMode.SPECIFIC, tag) }
                        ?: return@setSingleChoiceItems
                }
                if (preferenceStore.saveLanguage(selected)) {
                    AppAppearanceController.apply(this, hostResult)
                }
                selectedDialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
        showTinted(dialog)
    }

    private fun showNightModeDialog() {
        val labels = listOf(
            getString(R.string.follow_autojs6),
            getString(R.string.follow_system),
            getString(R.string.night_mode_light),
            getString(R.string.night_mode_dark),
        )
        val values = listOf(
            AppNightMode.AUTOJS6,
            AppNightMode.SYSTEM,
            AppNightMode.LIGHT,
            AppNightMode.DARK,
        )
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.setting_night_mode)
            .setSingleChoiceItems(
                DisabledChoiceAdapter(labels, disableFirst = !hostResult.available),
                values.indexOf(preferenceStore.nightMode()),
            ) { selectedDialog, which ->
                values.getOrNull(which)?.let { value ->
                    if (preferenceStore.saveNightMode(value)) {
                        AppAppearanceController.apply(this, hostResult)
                    }
                }
                selectedDialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
        showTinted(dialog)
    }

    private fun renderValues() {
        binding.languageSummary.text = languageSummary()
        binding.nightModeSummary.text = nightModeSummary()
        binding.themeColorSummary.text = themeSummary()
        binding.rememberPositionSwitch.isChecked = preferenceStore.rememberPlaybackPosition
        binding.autoUpdateSwitch.isChecked = preferenceStore.autoCheckUpdates
        val ignoredCount = AppUpdateStore(this).ignoredVersions().size
        binding.ignoredUpdatesSummary.text = resources.getQuantityString(
            R.plurals.ignored_update_count,
            ignoredCount,
            ignoredCount,
        )
    }

    private fun languageSummary(): String = when (val preference = preferenceStore.language()) {
        is AppLanguagePreference -> when (preference.mode) {
            AppLanguageMode.AUTOJS6 -> hostResult.snapshot?.resolvedLanguageTag?.let { tag ->
                getString(R.string.follow_autojs6_value, displayLanguageName(tag))
            } ?: unavailableFollowSummary(getString(R.string.follow_system))
            AppLanguageMode.SYSTEM -> getString(R.string.follow_system)
            AppLanguageMode.SPECIFIC -> displayLanguageName(preference.languageTag.orEmpty())
        }
    }

    private fun nightModeSummary(): String = when (preferenceStore.nightMode()) {
        AppNightMode.AUTOJS6 -> hostResult.snapshot?.let { snapshot ->
            val value = when (snapshot.darkModePolicy) {
                HostContract.DARK_MODE_FOLLOW_SYSTEM -> getString(R.string.follow_system)
                HostContract.DARK_MODE_DARK -> getString(R.string.night_mode_dark)
                HostContract.DARK_MODE_LIGHT -> getString(R.string.night_mode_light)
                else -> getString(
                    if (snapshot.darkModeActive) R.string.night_mode_dark else R.string.night_mode_light,
                )
            }
            getString(R.string.follow_autojs6_value, value)
        } ?: unavailableFollowSummary(getString(R.string.follow_system))
        AppNightMode.SYSTEM -> getString(R.string.follow_system)
        AppNightMode.LIGHT -> getString(R.string.night_mode_light)
        AppNightMode.DARK -> getString(R.string.night_mode_dark)
    }

    private fun themeSummary(): String {
        val preference = ThemePreferenceStore(this).load()
        return when (preference.mode) {
            ThemeSourceMode.AUTOJS6 -> {
                val color = hostResult.snapshot?.themeColorPrimary
                    ?: AudioThemePaletteGenerator.AUTOJS6_FALLBACK_SOURCE
                getString(
                    if (hostResult.available) {
                        R.string.theme_source_autojs6_available
                    } else {
                        R.string.theme_source_autojs6_unavailable
                    },
                    AudioThemePaletteGenerator.colorHex(color),
                )
            }
            ThemeSourceMode.PRESET -> {
                val index = ThemePresetCatalog.colors.indexOfFirst { preset -> preset.key == preference.presetKey }
                resources.getStringArray(R.array.theme_preset_names).getOrNull(index)
                    ?: AudioThemePaletteGenerator.colorHex(audioPalette.source)
            }
            ThemeSourceMode.CUSTOM -> getString(
                R.string.theme_custom_value,
                AudioThemePaletteGenerator.colorHex(preference.customColor),
            )
        }
    }

    private fun unavailableFollowSummary(fallback: String): String = getString(
        R.string.follow_autojs6_unavailable_value,
        hostUnavailableReason(),
        fallback,
    )

    private fun hostUnavailableReason(): String = getString(
        when (hostResult.availability) {
            AutoJs6HostAvailability.NOT_INSTALLED -> R.string.autojs6_not_installed
            AutoJs6HostAvailability.DISABLED -> R.string.autojs6_disabled
            AutoJs6HostAvailability.CONTRACT_UNAVAILABLE -> R.string.autojs6_settings_unavailable
            AutoJs6HostAvailability.AVAILABLE -> R.string.autojs6_settings_available
        },
    )

    private fun displayLanguageName(tag: String): String {
        val normalized = AppPreferenceStore.normalizeSupportedLanguageTag(tag) ?: tag
        val locale = Locale.forLanguageTag(normalized)
        return locale.getDisplayName(locale).replaceFirstChar { character ->
            if (character.isLowerCase()) character.titlecase(locale) else character.toString()
        }
    }

    private fun styleViews() {
        val palette = audioPalette
        binding.settingsRoot.setBackgroundColor(palette.background)
        binding.settingsContent.setBackgroundColor(palette.background)
        binding.toolbar.setBackgroundColor(palette.appBar)
        binding.toolbar.setTitleTextColor(palette.onAppBar)
        binding.toolbar.navigationIcon = binding.toolbar.navigationIcon?.tinted(palette.onAppBar)
        listOf(
            binding.appearanceSection,
            binding.playbackSection,
            binding.updatesSection,
            binding.aboutSection,
        ).forEach { view -> view.setTextColor(palette.primary) }
        listOf(
            binding.languageTitle,
            binding.nightModeTitle,
            binding.themeColorTitle,
            binding.rememberPositionTitle,
            binding.checkUpdateTitle,
            binding.autoUpdateTitle,
            binding.ignoredUpdatesTitle,
            binding.releaseHistoryTitle,
            binding.aboutTitle,
        ).forEach { view -> view.setTextColor(palette.onSurface) }
        listOf(
            binding.languageSummary,
            binding.nightModeSummary,
            binding.themeColorSummary,
            binding.rememberPositionSummary,
            binding.checkUpdateSummary,
            binding.autoUpdateSummary,
            binding.ignoredUpdatesSummary,
            binding.releaseHistorySummary,
            binding.aboutSummary,
        ).forEach { view -> view.setTextColor(palette.onSurfaceVariant) }
        styleSwitch(binding.rememberPositionSwitch)
        styleSwitch(binding.autoUpdateSwitch)
    }

    private fun styleSwitch(view: SwitchMaterial) {
        view.thumbTintList = ColorStateList(
            arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
            intArrayOf(audioPalette.primary, audioPalette.outline),
        )
        view.trackTintList = ColorStateList(
            arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
            intArrayOf(audioPalette.primaryContainer, audioPalette.surfaceContainerHighest),
        )
    }

    private fun showTinted(dialog: AlertDialog) {
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(audioPalette.primary)
        }
        dialog.show()
    }

    private fun Drawable.tinted(color: Int): Drawable = DrawableCompat.wrap(mutate()).also {
        DrawableCompat.setTint(it, color)
    }

    private inner class DisabledChoiceAdapter(
        values: List<String>,
        private val disableFirst: Boolean,
    ) : ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice, values) {
        override fun isEnabled(position: Int): Boolean = !(disableFirst && position == 0)

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View =
            super.getView(position, convertView, parent).also { row ->
                row.alpha = if (isEnabled(position)) 1f else DISABLED_ALPHA
                (row as? TextView)?.setTextColor(
                    if (isEnabled(position)) audioPalette.onSurface else audioPalette.onSurfaceVariant,
                )
            }
    }

    private companion object {
        const val DISABLED_ALPHA = 0.5f
    }
}
