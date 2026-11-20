package io.github.supermonster003.autojs6.plugin.threeterraplayer

import android.app.Activity
import android.content.Intent
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AlertDialog
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.ComposeTimeoutException
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isFocused
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import io.github.supermonster003.autojs6.plugin.threeterraplayer.settings.ReleaseHistoryActivity
import io.github.supermonster003.autojs6.plugin.threeterraplayer.settings.ReleaseHistoryAssetPolicy
import io.github.supermonster003.autojs6.plugin.threeterraplayer.settings.SettingsActivity
import io.github.supermonster003.autojs6.plugin.threeterraplayer.settings.loadReleaseHistory
import io.github.supermonster003.autojs6.plugin.threeterraplayer.update.AppRelease
import io.github.supermonster003.autojs6.plugin.threeterraplayer.update.AppUpdateCoordinator
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * The release history page (AUTOJS6_PLUGIN_RELEASE_HISTORY_AGENTS.md): its title is "Release history" with the plugin
 * name as the subtitle, the newest release starts expanded, and both the settings row and the update dialog open it.
 */
@RunWith(AndroidJUnit4::class)
class ReleaseHistoryPageInstrumentationTest {

    @get:Rule
    val compose = createEmptyComposeRule()

    private val instrumentation get() = InstrumentationRegistry.getInstrumentation()
    private val context get() = instrumentation.targetContext

    @After fun finishActivities() {
        instrumentation.setInTouchMode(true)
        instrumentation.runOnMainSync {
            ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED).forEach(Activity::finish)
        }
        instrumentation.waitForIdleSync()
    }

    /** Unmerged: the count chips sit inside the clickable header, which merges its children. */
    private fun exists(tag: String): Boolean =
        runCatching { compose.onAllNodes(hasTestTag(tag), useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty() }.getOrDefault(false)

    private fun resumed(): Activity? {
        var activity: Activity? = null
        instrumentation.runOnMainSync {
            activity = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED).firstOrNull()
        }
        return activity
    }

    private fun assertThePageOpened() {
        compose.waitUntil(TIMEOUT) { resumed() is ReleaseHistoryActivity && exists("terra.releaseHistory.list") }
        assertTitleAndSubtitle()
    }

    private fun assertTitleAndSubtitle() {
        val inTopBar = hasAnyAncestor(hasTestTag("terra.topBar"))
        compose.onNode(hasText(context.getString(R.string.setting_release_history)) and inTopBar).assertIsDisplayed()
        compose.onNode(hasText(context.getString(R.string.app_name)) and inTopBar).assertIsDisplayed()
    }

    @Test fun theTitleIsReleaseHistoryAndTheSubtitleThePluginName() {
        ActivityScenario.launch(ReleaseHistoryActivity::class.java).use {
            compose.waitUntil(TIMEOUT) { exists("terra.releaseHistory.list") }
            assertTitleAndSubtitle()
            assertFalse(exists("terra.releaseHistory.empty"))
        }
    }

    private fun versions(scenario: ActivityScenario<ReleaseHistoryActivity>): List<String> {
        var versions = emptyList<String>()
        scenario.onActivity { activity ->
            val candidates = ReleaseHistoryAssetPolicy.candidatesFor(activity.resources.configuration.locales[0])
            versions = loadReleaseHistory(candidates) { path ->
                activity.assets.open(path).use { it.readBytes().toString(Charsets.UTF_8) }
            }.map { it.version }
        }
        return versions
    }

    /**
     * The newest release starts expanded, the others show their category counts; a header expands or collapses its
     * release, the top bar expands or collapses all of them, and the choice survives recreation.
     */
    @Test fun releasesExpandAndCollapse() {
        ActivityScenario.launch(ReleaseHistoryActivity::class.java).use { scenario ->
            compose.waitUntil(TIMEOUT) { exists("terra.releaseHistory.list") }
            val versions = versions(scenario)
            assertTrue("releases: ${versions.size}", versions.size > 2)
            val (newest, second) = versions
            assertTrue(exists("terra.releaseHistory.body.$newest"))
            assertFalse(exists("terra.releaseHistory.counts.$newest"))
            assertFalse(exists("terra.releaseHistory.body.$second"))
            assertTrue(exists("terra.releaseHistory.counts.$second"))

            compose.onNodeWithTag("terra.releaseHistory.entry.$second").performClick()
            compose.waitUntil(TIMEOUT) { exists("terra.releaseHistory.body.$second") }
            compose.onNodeWithTag("terra.releaseHistory.entry.$newest").performClick()
            compose.waitUntil(TIMEOUT) { !exists("terra.releaseHistory.body.$newest") && exists("terra.releaseHistory.counts.$newest") }

            scenario.recreate()
            compose.waitUntil(TIMEOUT) { exists("terra.releaseHistory.list") }
            assertTrue(exists("terra.releaseHistory.body.$second"))
            assertFalse(exists("terra.releaseHistory.body.$newest"))

            compose.onNodeWithTag("terra.releaseHistory.toggleAll")
                .assertContentDescriptionEquals(context.getString(R.string.release_history_expand_all))
                .performClick()
            compose.waitUntil(TIMEOUT) { exists("terra.releaseHistory.body.$newest") }
            compose.onNodeWithTag("terra.releaseHistory.toggleAll")
                .assertContentDescriptionEquals(context.getString(R.string.release_history_collapse_all))
                .performClick()
            compose.waitUntil(TIMEOUT) { !exists("terra.releaseHistory.body.$newest") && !exists("terra.releaseHistory.body.$second") }
        }
    }

    @Test fun theSettingsRowOpensThePage() {
        val intent = Intent(context, SettingsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val activity = instrumentation.startActivitySync(intent) as SettingsActivity
        instrumentation.runOnMainSync { activity.findViewById<android.view.View>(R.id.release_history_setting).performClick() }
        assertThePageOpened()
    }

    @Test fun theUpdateDialogOpensThePage() {
        val intent = Intent(context, SettingsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val activity = instrumentation.startActivitySync(intent) as SettingsActivity
        val release = AppRelease("v99.0.0", "v99.0.0", "* `Feature` Note", "https://example.invalid/", "2026-11-19T00:00:00Z")
        var dialog: AlertDialog? = null
        instrumentation.runOnMainSync { dialog = AppUpdateCoordinator.showAvailableRelease(activity, release) }
        instrumentation.waitForIdleSync()
        instrumentation.runOnMainSync { requireNotNull(dialog).getButton(AlertDialog.BUTTON_NEUTRAL).performClick() }
        assertThePageOpened()
    }

    private fun key(code: Int) {
        instrumentation.sendKeyDownUpSync(code)
        compose.waitForIdle()
    }

    /**
     * Leaves touch mode as a keyboard user does. Newer versions take [android.app.Instrumentation.setInTouchMode] at
     * once; older ones (API 24) learn it only from a key, so an up arrow goes first.
     */
    private fun leaveTouchMode() {
        instrumentation.setInTouchMode(false)
        key(KeyEvent.KEYCODE_DPAD_UP)
    }

    /**
     * The test tag of the focused control (or of its nearest tagged ancestor), null when nothing has focus. The back
     * button has no tag of its own, so inside the top bar it is told apart by its content description.
     */
    private fun focusedControl(): String? {
        val node = compose.onAllNodes(isFocused(), useUnmergedTree = true).fetchSemanticsNodes().firstOrNull() ?: return null
        val tag = generateSequence(node) { it.parent }.mapNotNull { it.config.getOrNull(SemanticsProperties.TestTag) }.firstOrNull()
        if (tag == "terra.topBar") {
            val description = compose.onNode(isFocused()).fetchSemanticsNode().config.getOrNull(SemanticsProperties.ContentDescription)
            if (description == listOf(context.getString(R.string.action_back))) return BACK
        }
        return tag ?: UNTAGGED
    }

    /** Runs [command] as the shell user (it connects UiAutomation) and returns its output. */
    private fun shell(command: String): String =
        ParcelFileDescriptor.AutoCloseInputStream(instrumentation.uiAutomation.executeShellCommand(command)).bufferedReader().use { it.readText() }

    /**
     * The focused window and app as `dumpsys window` reports them, for diagnostics only. API 33 and 35 leave
     * mCurrentFocus out of `dumpsys window windows`, so the whole dump is read.
     */
    private fun focusedWindow(): String = runCatching {
        shell("dumpsys window").lineSequence().map(String::trim)
            .filter { it.startsWith("mCurrentFocus=") || it.startsWith("mFocusedApp=") }.distinct().joinToString()
            .ifEmpty { "mCurrentFocus not in dumpsys" }
    }.getOrElse { "dumpsys failed: $it" }

    private fun windowFocused(scenario: ActivityScenario<*>): Boolean = try {
        var focused = false
        compose.waitUntil(TIMEOUT) { scenario.onActivity { focused = it.hasWindowFocus() }; focused }
        true
    } catch (timeout: ComposeTimeoutException) {
        false
    }

    /**
     * Keys go to the focused window: while a system dialog has focus (an ANR or crash dialog of another app on a slow
     * emulator, say), every Tab would be lost. When focus does not come, the test closes system dialogs (error dialogs
     * take that as "wait" or "close") and brings the page back to the front, once, before it gives up.
     */
    private fun awaitWindowFocus(scenario: ActivityScenario<*>, visited: List<String> = emptyList()) {
        if (windowFocused(scenario)) return
        val before = focusedWindow()
        Log.w(LOG_TAG, "no window focus after $TIMEOUT ms ($before), closing system dialogs")
        shell("am broadcast -a android.intent.action.CLOSE_SYSTEM_DIALOGS")
        scenario.onActivity { it.startActivity(Intent(it, it.javaClass).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)) }
        if (!windowFocused(scenario)) {
            fail("the page has no window focus after $TIMEOUT ms, nor after closing system dialogs; visited: $visited; " +
                "before: $before; after: ${focusedWindow()}")
        }
        Log.i(LOG_TAG, "window focus is back: ${focusedWindow()}")
    }

    /** Presses Tab until focus returns to a control seen before; every focused control must be on screen. */
    private fun tabCycle(scenario: ActivityScenario<*>): List<String> {
        val visited = mutableListOf<String>()
        repeat(MAX_TABS) {
            awaitWindowFocus(scenario, visited)
            key(KeyEvent.KEYCODE_TAB)
            val control = focusedControl() ?: return@repeat
            if (control in visited) return visited
            compose.onNode(isFocused(), useUnmergedTree = true).assertIsDisplayed()
            visited += control
        }
        return visited
    }

    /**
     * Keyboard users read the whole page (AUTOJS6_PLUGIN_RELEASE_HISTORY_AGENTS.md section 10): Tab passes the back
     * button and then every version header in order, down to the oldest release.
     */
    @Test fun theReleaseHistoryCanBeReadToTheEndFromTheKeyboard() {
        ActivityScenario.launch(ReleaseHistoryActivity::class.java).use { scenario ->
            compose.waitUntil(TIMEOUT) { exists("terra.releaseHistory.list") }
            val versions = versions(scenario)
            assertTrue("releases: ${versions.size}", versions.size > 2)
            awaitWindowFocus(scenario)
            leaveTouchMode()
            val visited = tabCycle(scenario)
            fun expect(ok: Boolean, what: String) { if (!ok) fail("$what; visited: $visited; ${focusedWindow()}") }
            expect(BACK in visited, "Tab never reached the back button")
            val expected = versions.map { "terra.releaseHistory.entry.$it" }
            val entries = visited.filter { it.startsWith("terra.releaseHistory.entry.") }
            expect(entries == expected, "the releases in Tab order: expected $expected, got $entries")
        }
    }

    private companion object {
        const val LOG_TAG = "ReleaseHistoryTest"
        const val TIMEOUT = 15_000L
        const val MAX_TABS = 120
        const val UNTAGGED = "(untagged)"
        const val BACK = "(back)"
    }
}
