package io.github.supermonster003.autojs6.plugin.threeterraplayer

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.test.platform.app.InstrumentationRegistry
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import org.junit.Test

/** App-UID upgrade fixture. Uses only framework APIs, so the test APK also works with the old app. */
class LauncherIconPrepareUpgradeTest {
    @Test fun prepareAnExplicitlyRequestedOldLauncherState() {
        val args = InstrumentationRegistry.getArguments()
        val requested = args.getString("prepareLauncherState")
        assumeTrue("Requires an explicit AVD fixture argument", requested != null)
        assertTrue("Only run this fixture on an isolated AVD", Build.HARDWARE in listOf("ranchu", "goldfish"))
        require(requested in listOf("defaults", "explicit-dark", "mixed-dark"))
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val pm = context.packageManager
        val names = listOf("AdaptiveLightIconAlias", "AdaptiveDarkIconAlias", "AdaptiveAutoIconAlias", "TransparentIconAlias")
        val components = names.associateWith { ComponentName(context.packageName, context.packageName + ".launcher." + it) }
        components.values.forEach { pm.getActivityInfo(it, PackageManager.MATCH_DISABLED_COMPONENTS) }
        val expected = names.associateWith { name ->
            when {
                requested == "defaults" -> PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
                name == "AdaptiveDarkIconAlias" -> PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                requested == "explicit-dark" -> PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                else -> PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
            }
        }
        names.sortedBy { if (it == "AdaptiveDarkIconAlias") 0 else 1 }.forEach { name ->
            pm.setComponentEnabledSetting(components.getValue(name), expected.getValue(name), PackageManager.DONT_KILL_APP)
        }
        expected.forEach { (name, state) -> assertEquals(name, state, pm.getComponentEnabledSetting(components.getValue(name))) }
        val entries = pm.queryIntentActivities(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER).setPackage(context.packageName), 0)
        assertEquals(1, entries.size)
        val defaultName = args.getString("oldDefaultAlias") ?: "AdaptiveDarkIconAlias"
        val active = if (requested == "defaults") defaultName else "AdaptiveDarkIconAlias"
        assertEquals(components.getValue(active).className, entries.single().activityInfo.name)
        instrumentation.sendStatus(0, Bundle().apply {
            putString("launcherPrepared", JSONObject().apply {
                put("package", context.packageName)
                put("version", pm.getPackageInfo(context.packageName, 0).versionName)
                put("requested", requested)
                put("states", JSONObject(expected))
                put("active", active)
            }.toString())
        })
    }
}
