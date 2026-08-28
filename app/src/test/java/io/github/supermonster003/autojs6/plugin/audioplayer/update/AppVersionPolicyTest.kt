package io.github.supermonster003.autojs6.plugin.audioplayer.update

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AppVersionPolicyTest {

    @Test
    fun parsesReleaseTagsAndRejectsMalformedValues() {
        assertNotNull(AppVersionPolicy.parse("v1.2.3"))
        assertNotNull(AppVersionPolicy.parse("2.0.0-rc.1+42"))
        assertNull(AppVersionPolicy.parse(""))
        assertNull(AppVersionPolicy.parse("1..2"))
        assertNull(AppVersionPolicy.parse("release-1.2"))
    }

    @Test
    fun comparesCoreAndPrereleaseVersions() {
        assertTrue(AppVersionPolicy.isNewer("1.2.3", "1.2.2"))
        assertTrue(AppVersionPolicy.isNewer("2.0", "1.99.99"))
        assertTrue(AppVersionPolicy.isNewer("1.2.3", "1.2.3-rc.1"))
        assertTrue(AppVersionPolicy.isNewer("1.2.3-rc.2", "1.2.3-rc.1"))
        assertFalse(AppVersionPolicy.isNewer("1.2.2", "1.2.2"))
        assertFalse(AppVersionPolicy.isNewer("1.2.2-beta", "1.2.2"))
    }
}
