package app.lawnchair.custom

import android.os.Build
import app.lawnchair.custom.device.DeviceInfo

/**
 * Reads device facts available to an ordinary app. Public [Build] fields are used where they
 * exist; MIUI and bootloader properties are only reachable through the hidden SystemProperties
 * class, so each read is best-effort and stays null (= unverified) when the platform refuses.
 */
object DeviceReader {
    fun read(): DeviceInfo = DeviceInfo(
        androidRelease = Build.VERSION.RELEASE,
        sdkInt = Build.VERSION.SDK_INT,
        miuiVersionName = prop("ro.miui.ui.version.name"),
        miuiVersionCode = prop("ro.miui.ui.version.code"),
        deviceCodename = Build.DEVICE,
        model = Build.MODEL,
        flashLocked = prop("ro.boot.flash.locked"),
        verifiedBootState = prop("ro.boot.verifiedbootstate"),
    )

    private fun prop(key: String): String? = runCatching {
        val cls = Class.forName("android.os.SystemProperties")
        cls.getMethod("get", String::class.java).invoke(null, key) as? String
    }.getOrNull()?.takeIf { it.isNotBlank() }
}
