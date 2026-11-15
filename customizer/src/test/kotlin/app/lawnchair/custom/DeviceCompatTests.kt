package app.lawnchair.custom

import app.lawnchair.custom.device.DeviceCompat
import app.lawnchair.custom.device.DeviceInfo
import app.lawnchair.custom.device.Status
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class DeviceCompatTests {

    private fun status(info: DeviceInfo, topic: String) =
        DeviceCompat.assess(info).first { it.topic == topic }.status

    @Test
    fun nothingReadMeansNothingVerified() {
        val findings = DeviceCompat.assess(DeviceInfo())
        assertEquals(setOf(Status.UNVERIFIED), findings.map { it.status }.toSet())
    }

    @Test
    fun knownCodenameVerifiedUnknownMismatch() {
        assertEquals(Status.VERIFIED, status(DeviceInfo(deviceCodename = "angelica"), "device"))
        assertEquals(Status.MISMATCH, status(DeviceInfo(deviceCodename = "lancelot"), "device"))
    }

    @Test
    fun oldAndroidIsMismatchAndRecentsNeverClaimedVerified() {
        assertEquals(Status.MISMATCH, status(DeviceInfo(sdkInt = 24), "android"))
        assertEquals(Status.VERIFIED, status(DeviceInfo(sdkInt = 29, androidRelease = "10"), "android"))
        assertEquals(Status.UNVERIFIED, status(DeviceInfo(sdkInt = 29), "recents"))
        assertEquals(Status.UNVERIFIED, status(DeviceInfo(sdkInt = 36), "recents"))
    }

    @Test
    fun bootloaderStateIsReportedNotAssumed() {
        assertEquals(Status.VERIFIED, status(DeviceInfo(flashLocked = "1"), "bootloader"))
        assertEquals(Status.VERIFIED, status(DeviceInfo(flashLocked = "0"), "bootloader"))
        assertEquals(Status.UNVERIFIED, status(DeviceInfo(flashLocked = "maybe"), "bootloader"))
        assertEquals(Status.UNVERIFIED, status(DeviceInfo(), "bootloader"))
    }

    @Test
    fun parsesGetpropDump() {
        val dump = """
            [ro.build.version.release]: [10]
            [ro.build.version.sdk]: [29]
            [ro.product.device]: [angelica]
            [ro.miui.ui.version.name]: [V12]
            [ro.boot.flash.locked]: [1]
        """.trimIndent()
        val info = DeviceCompat.parse(DeviceCompat.parseGetpropDump(dump))
        assertEquals("10", info.androidRelease)
        assertEquals(29, info.sdkInt)
        assertEquals("angelica", info.deviceCodename)
        assertEquals("V12", info.miuiVersionName)
        assertEquals("1", info.flashLocked)
    }

    @Test
    fun launcherNeverRequiresRoot() {
        assertFalse(DeviceCompat.launcherNeedsRoot)
    }
}
