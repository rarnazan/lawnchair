package app.lawnchair.custom.device

/**
 * Raw values read from the phone, e.g. with [PROP_COMMANDS] over adb. A null means "not read",
 * which is different from "read and did not match".
 */
data class DeviceInfo(
    val androidRelease: String? = null,
    val sdkInt: Int? = null,
    val miuiVersionName: String? = null,
    val miuiVersionCode: String? = null,
    val deviceCodename: String? = null,
    val model: String? = null,
    /** `ro.boot.flash.locked`: "1" locked, "0" unlocked. */
    val flashLocked: String? = null,
    /** `ro.boot.verifiedbootstate`: green / yellow / orange / red. */
    val verifiedBootState: String? = null,
)

enum class Status { VERIFIED, UNVERIFIED, MISMATCH }

data class Finding(val topic: String, val status: Status, val detail: String)

object DeviceCompat {
    /** Codenames publicly listed for the Redmi 9C family. Treated as "expected", never proof. */
    val EXPECTED_CODENAMES = setOf("angelica", "angelican", "angelicain")

    /** Lawnchair's declared minSdk (see root build.gradle). */
    const val LAWNCHAIR_MIN_SDK = 26

    /** Quickstart/recents integration is documented for Android 15-16 only. */
    const val QUICKSTEP_DOCUMENTED_MIN_SDK = 35

    /** adb commands whose output fills [DeviceInfo]. Read-only. */
    val PROP_COMMANDS = listOf(
        "getprop ro.build.version.release",
        "getprop ro.build.version.sdk",
        "getprop ro.miui.ui.version.name",
        "getprop ro.miui.ui.version.code",
        "getprop ro.product.device",
        "getprop ro.product.model",
        "getprop ro.boot.flash.locked",
        "getprop ro.boot.verifiedbootstate",
    )

    fun parse(props: Map<String, String>): DeviceInfo {
        fun p(key: String) = props[key]?.trim()?.takeIf { it.isNotEmpty() }
        return DeviceInfo(
            androidRelease = p("ro.build.version.release"),
            sdkInt = p("ro.build.version.sdk")?.toIntOrNull(),
            miuiVersionName = p("ro.miui.ui.version.name"),
            miuiVersionCode = p("ro.miui.ui.version.code"),
            deviceCodename = p("ro.product.device"),
            model = p("ro.product.model"),
            flashLocked = p("ro.boot.flash.locked"),
            verifiedBootState = p("ro.boot.verifiedbootstate"),
        )
    }

    /** Parses `adb shell getprop` full dumps of the form `[key]: [value]`. */
    fun parseGetpropDump(dump: String): Map<String, String> =
        Regex("^\\[(.+?)]: \\[(.*)]$", RegexOption.MULTILINE).findAll(dump)
            .associate { it.groupValues[1] to it.groupValues[2] }

    fun assess(info: DeviceInfo): List<Finding> = buildList {
        add(
            when (val code = info.deviceCodename) {
                null -> Finding("device", Status.UNVERIFIED, "ro.product.device not read")
                in EXPECTED_CODENAMES -> Finding("device", Status.VERIFIED, "codename $code matches Redmi 9C list")
                else -> Finding("device", Status.MISMATCH, "codename '$code' is not a known Redmi 9C codename")
            },
        )
        add(
            when (val sdk = info.sdkInt) {
                null -> Finding("android", Status.UNVERIFIED, "SDK level not read")
                in 0 until LAWNCHAIR_MIN_SDK ->
                    Finding("android", Status.MISMATCH, "SDK $sdk is below Lawnchair minSdk $LAWNCHAIR_MIN_SDK")
                else -> Finding(
                    "android",
                    Status.VERIFIED,
                    "Android ${info.androidRelease ?: "?"} (SDK $sdk) satisfies minSdk $LAWNCHAIR_MIN_SDK; runtime behaviour still untested",
                )
            },
        )
        add(
            when {
                info.sdkInt == null -> Finding("recents", Status.UNVERIFIED, "SDK level not read")
                info.sdkInt < QUICKSTEP_DOCUMENTED_MIN_SDK ->
                    Finding("recents", Status.UNVERIFIED, "Quickstep/recents integration is documented for Android 15-16; expect it not to work on SDK ${info.sdkInt}")
                else -> Finding("recents", Status.UNVERIFIED, "documented range, not tested on this device")
            },
        )
        add(
            if (info.miuiVersionName == null && info.miuiVersionCode == null) {
                Finding("miui", Status.UNVERIFIED, "no MIUI properties read (not MIUI, or not queried)")
            } else {
                Finding("miui", Status.VERIFIED, "MIUI ${info.miuiVersionName ?: "?"} (code ${info.miuiVersionCode ?: "?"})")
            },
        )
        add(
            when (info.flashLocked) {
                null -> Finding("bootloader", Status.UNVERIFIED, "ro.boot.flash.locked not read")
                "1" -> Finding("bootloader", Status.VERIFIED, "locked (verifiedbootstate=${info.verifiedBootState ?: "?"})")
                "0" -> Finding("bootloader", Status.VERIFIED, "unlocked (verifiedbootstate=${info.verifiedBootState ?: "?"})")
                else -> Finding("bootloader", Status.UNVERIFIED, "unexpected value '${info.flashLocked}'")
            },
        )
    }

    /** The launcher itself needs none of root, unlocked bootloader or a ROM change. */
    val launcherNeedsRoot: Boolean = false
}
