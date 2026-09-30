package io.github.supermonster003.autojs6.plugin.threeterraplayer.theme

import android.graphics.Color
import android.os.Build
import io.github.supermonster003.autojs6.plugin.threeterraplayer.settings.LauncherIcons
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
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
    private var userInteracted = false
    private val appearanceDialogs = java.util.WeakHashMap<android.app.Dialog, Unit>()
    internal fun trackAppearanceDialog(dialog: android.app.Dialog) { appearanceDialogs[dialog] = Unit }
    override fun onUserInteraction() {
        super.onUserInteraction()
        userInteracted = true
    }
    private val hostAppearanceListener: () -> Unit = {
        if (!userInteracted && lifecycle.currentState.isAtLeast(androidx.lifecycle.Lifecycle.State.RESUMED)) refreshEffectiveAppearance()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        AutoJs6AppearanceClient.addListener(hostAppearanceListener)
        // A cold Application can run before AutoJs6's provider is ready. Resolve once more at the
        // Activity boundary so host-following locale and night mode are in place before AppCompat
        // creates delegates or inflates any resources.
        val appearance = AppAppearanceController.apply(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        LauncherIcons.normalizeAsync(this)
        resolvedAudioTheme = AudioThemeResolver.resolve(this, appearance.hostResult)
        appliedPreferenceRevision = ThemePreferenceStore(this).revision()
        val appStore = AppPreferenceStore(this)
        appliedAppearanceRevision = appStore.appearanceRevision()
        appliedHostSignature = effectiveHostSignature(appearance.hostResult, ThemePreferenceStore(this).load(), appStore)
        applyWindowPalette(audioPalette)
    }

    override fun onResume() {
        super.onResume()
        userInteracted = false
        AutoJs6AppearanceClient.refresh(this)
        refreshEffectiveAppearance()
    }

    override fun onDestroy() {
        AutoJs6AppearanceClient.removeListener(hostAppearanceListener)
        super.onDestroy()
    }

    private fun refreshEffectiveAppearance() {
        if (recreationRequested || isFinishing || isDestroyed || appearanceDialogs.keys.any { it.isShowing }) return
        val store = ThemePreferenceStore(this)
        val preference = store.load()
        val appStore = AppPreferenceStore(this)
        val hostResult = if (followsHost(preference, appStore)) AutoJs6AppearanceClient.query(this) else null
        val hostSignature = effectiveHostSignature(hostResult, preference, appStore)
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

    @Suppress("DEPRECATION") // Color setters remain the compatibility path below Android 15.
    internal fun applyWindowPalette(palette: AudioThemePalette) {
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Color.TRANSPARENT
        } else {
            // API 24–25 cannot request dark navigation-bar icons. Keep the legacy three-button
            // region opaque black instead of risking white icons over a light page.
            OPAQUE_BLACK
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.navigationBarDividerColor = Color.TRANSPARENT
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isStatusBarContrastEnforced = false
            window.isNavigationBarContrastEnforced = false
        }
        val lightStatusBar = AudioThemePaletteGenerator.bestMonochromeForeground(palette.appBar) ==
            OPAQUE_BLACK
        val lightNavigationBar = AudioThemePaletteGenerator.bestMonochromeForeground(palette.background) ==
            OPAQUE_BLACK
        window.decorView.post {
            if (isFinishing || isDestroyed) return@post
            WindowCompat.getInsetsController(window, window.decorView).apply {
                isAppearanceLightStatusBars = lightStatusBar
                isAppearanceLightNavigationBars =
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && lightNavigationBar
            }
        }
    }

    /** Keeps controls clear of cutouts and gesture areas while their backgrounds draw underneath. */
    internal fun applyEdgeToEdge(
        root: View,
        toolbar: View,
        bottomContent: View,
        vararg sideInsetViews: View,
    ) {
        val toolbarPadding = toolbar.initialPadding()
        val toolbarHeight = toolbar.layoutParams.height
        val toolbarMinimumHeight = toolbar.minimumHeight
        val contentPadding = bottomContent.initialPadding()
        val sidePaddings = sideInsetViews.associateWith { view -> view.initialPadding() }
        ViewCompat.setOnApplyWindowInsetsListener(root) { _, windowInsets ->
            val insets = windowInsets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout(),
            )
            toolbar.setPadding(
                toolbarPadding.left + insets.left,
                toolbarPadding.top + insets.top,
                toolbarPadding.right + insets.right,
                toolbarPadding.bottom,
            )
            if (toolbarHeight >= 0) {
                toolbar.layoutParams = toolbar.layoutParams.apply {
                    height = toolbarHeight + insets.top
                }
            } else {
                toolbar.minimumHeight = toolbarMinimumHeight + insets.top
            }
            bottomContent.setPadding(
                contentPadding.left + insets.left,
                contentPadding.top,
                contentPadding.right + insets.right,
                contentPadding.bottom + insets.bottom,
            )
            sidePaddings.forEach { (view, padding) ->
                view.setPadding(
                    padding.left + insets.left,
                    padding.top,
                    padding.right + insets.right,
                    padding.bottom,
                )
            }
            windowInsets
        }
        ViewCompat.requestApplyInsets(root)
    }

    internal fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun effectiveHostSignature(
        result: AutoJs6AppearanceResult?,
        preference: ThemeSourcePreference,
        store: AppPreferenceStore,
    ): Int? {
        if (!followsHost(preference, store)) return null
        val host = result?.snapshot
        return listOf(
            result?.availability,
            if (preference.mode == ThemeSourceMode.AUTOJS6) host?.themeColorPrimary ?: AudioThemePaletteGenerator.AUTOJS6_FALLBACK_SOURCE else null,
            if (preference.mode == ThemeSourceMode.AUTOJS6) host?.themeColorAccent ?: AudioThemePaletteGenerator.AUTOJS6_FALLBACK_SOURCE else null,
            if (store.language().mode == AppLanguageMode.AUTOJS6) AppAppearanceController.resolveLanguageTag(store.language(), host) else null,
            if (store.nightMode() == AppNightMode.AUTOJS6) AppAppearanceController.resolveNightMode(store.nightMode(), host) else null,
        ).hashCode()
    }

    private fun followsHost(
        themePreference: ThemeSourcePreference,
        store: AppPreferenceStore,
    ): Boolean = themePreference.mode == ThemeSourceMode.AUTOJS6 ||
        store.language().mode == AppLanguageMode.AUTOJS6 ||
        store.nightMode() == AppNightMode.AUTOJS6

    private fun View.initialPadding() = ViewPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)

    private data class ViewPadding(
        val left: Int,
        val top: Int,
        val right: Int,
        val bottom: Int,
    )

    private companion object {
        const val OPAQUE_BLACK = -0x1000000
    }
}
