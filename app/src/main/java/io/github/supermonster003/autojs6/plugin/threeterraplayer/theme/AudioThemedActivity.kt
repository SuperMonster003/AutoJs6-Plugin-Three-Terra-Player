package io.github.supermonster003.autojs6.plugin.threeterraplayer.theme

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import io.github.supermonster003.autojs6.plugin.threeterraplayer.settings.AppAppearanceController
import io.github.supermonster003.autojs6.plugin.threeterraplayer.settings.AppLanguageMode
import io.github.supermonster003.autojs6.plugin.threeterraplayer.settings.AppNightMode
import io.github.supermonster003.autojs6.plugin.threeterraplayer.settings.AppPreferenceStore

/** AppCompat host that refreshes when either the local seed choice or followed AutoJs6 color changes. */
abstract class AudioThemedActivity : AppCompatActivity() {

    internal lateinit var resolvedAudioTheme: ResolvedAudioTheme
        private set

    internal val audioPalette: AudioThemePalette
        get() = resolvedAudioTheme.palette

    private var appliedPreferenceRevision = Long.MIN_VALUE
    private var appliedAppearanceRevision = Long.MIN_VALUE
    private var appliedHostSignature: Int? = null
    private var recreationRequested = false

    override fun onCreate(savedInstanceState: Bundle?) {
        // A cold Application can run before AutoJs6's provider is ready. Resolve once more at the
        // Activity boundary so host-following locale and night mode are in place before AppCompat
        // creates delegates or inflates any resources.
        AppAppearanceController.apply(this)
        super.onCreate(savedInstanceState)
        resolvedAudioTheme = AudioThemeResolver.resolve(this)
        appliedPreferenceRevision = ThemePreferenceStore(this).revision()
        val appStore = AppPreferenceStore(this)
        appliedAppearanceRevision = appStore.appearanceRevision()
        appliedHostSignature = resolvedAudioTheme.hostResult?.hashCode()
            ?: hostResultIfFollowed(appStore)?.hashCode()
        applyWindowPalette(audioPalette)
    }

    override fun onResume() {
        super.onResume()
        if (recreationRequested) return
        val store = ThemePreferenceStore(this)
        val preference = store.load()
        val appStore = AppPreferenceStore(this)
        val hostResult = if (followsHost(preference, appStore)) AutoJs6AppearanceClient.query(this) else null
        val hostSignature = hostResult?.hashCode()
        if (
            store.revision() != appliedPreferenceRevision ||
            appStore.appearanceRevision() != appliedAppearanceRevision ||
            hostSignature != appliedHostSignature
        ) {
            recreationRequested = true
            hostResult?.let { result -> AppAppearanceController.apply(this, result) }
            recreate()
        }
    }

    @Suppress("DEPRECATION") // Required below API 35; edge-to-edge migration is a separate layout change.
    internal fun applyWindowPalette(palette: AudioThemePalette) {
        window.statusBarColor = palette.appBar
        window.navigationBarColor = palette.background
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.navigationBarDividerColor = palette.outlineVariant
        }
        val lightStatusBar = AudioThemePaletteGenerator.bestMonochromeForeground(palette.appBar) ==
            OPAQUE_BLACK
        val lightNavigationBar = AudioThemePaletteGenerator.bestMonochromeForeground(palette.background) ==
            OPAQUE_BLACK
        val decorView = window.decorView
        decorView.post {
            if (isFinishing || isDestroyed) return@post
            applySystemBarIconAppearance(decorView, lightStatusBar, lightNavigationBar)
        }
    }

    internal fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun hostResultIfFollowed(store: AppPreferenceStore): AutoJs6AppearanceResult? =
        if (
            store.language().mode == AppLanguageMode.AUTOJS6 ||
            store.nightMode() == AppNightMode.AUTOJS6
        ) {
            AutoJs6AppearanceClient.query(this)
        } else {
            null
        }

    private fun followsHost(
        themePreference: ThemeSourcePreference,
        store: AppPreferenceStore,
    ): Boolean = themePreference.mode == ThemeSourceMode.AUTOJS6 ||
        store.language().mode == AppLanguageMode.AUTOJS6 ||
        store.nightMode() == AppNightMode.AUTOJS6

    private fun applySystemBarIconAppearance(
        decorView: View,
        lightStatusBar: Boolean,
        lightNavigationBar: Boolean,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            var appearance = 0
            var mask = WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            if (lightStatusBar) {
                appearance = appearance or WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            }
            mask = mask or WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            if (lightNavigationBar) {
                appearance = appearance or WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            }
            decorView.windowInsetsController?.setSystemBarsAppearance(appearance, mask)
            return
        }

        @Suppress("DEPRECATION")
        var visibility = decorView.systemUiVisibility
        @Suppress("DEPRECATION")
        visibility = if (lightStatusBar) {
            visibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            visibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @Suppress("DEPRECATION")
            visibility = if (lightNavigationBar) {
                visibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            } else {
                visibility and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
            }
        }
        @Suppress("DEPRECATION")
        run { decorView.systemUiVisibility = visibility }
    }

    private companion object {
        const val OPAQUE_BLACK = -0x1000000
    }
}
