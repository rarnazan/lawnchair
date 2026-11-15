package app.lawnchair.custom.preset

import app.lawnchair.custom.model.LauncherPreset

/** One reason a preset cannot be applied safely. */
data class Violation(val field: String, val message: String)

/**
 * Range checks so that an imported or hand-edited preset can never push the launcher into an
 * unusable state (unreadable icons, off-screen grid, runaway animation durations).
 */
object PresetValidator {
    val HEX_COLOR = Regex("^#[0-9a-fA-F]{6}$")
    val COLUMNS = 3..6
    val ROWS = 4..7
    val HOTSEAT_COLUMNS = 3..6
    const val MIN_FACTOR = 0.5f
    const val MAX_FACTOR = 1.5f

    fun validate(preset: LauncherPreset): List<Violation> = buildList {
        if (preset.id.isBlank() || !preset.id.matches(Regex("[a-z0-9_\\-]{1,40}"))) {
            add(Violation("id", "must be 1-40 chars of a-z, 0-9, '_' or '-'"))
        }
        if (preset.name.isBlank() || preset.name.length > 60) {
            add(Violation("name", "must be 1-60 characters"))
        }
        preset.theme.accentHex?.let {
            if (!HEX_COLOR.matches(it)) add(Violation("theme.accentHex", "must look like #RRGGBB"))
        }
        factor("theme.drawerOpacity", preset.theme.drawerOpacity, 0f, 1f)
        factor("icons.homeIconSizeFactor", preset.icons.homeIconSizeFactor)
        factor("icons.drawerIconSizeFactor", preset.icons.drawerIconSizeFactor)
        factor("icons.homeLabelSizeFactor", preset.icons.homeLabelSizeFactor)
        factor("widgets.widgetPaddingFactor", preset.widgets.widgetPaddingFactor, 0f, 2f)
        factor("animation.durationScale", preset.animation.durationScale, 0.25f, 2f)
        factor("animation.springStiffnessFactor", preset.animation.springStiffnessFactor, 0.5f, 2f)
        if (preset.home.columns !in COLUMNS) add(Violation("home.columns", "must be in $COLUMNS"))
        if (preset.home.rows !in ROWS) add(Violation("home.rows", "must be in $ROWS"))
        if (preset.home.hotseatColumns !in HOTSEAT_COLUMNS) {
            add(Violation("home.hotseatColumns", "must be in $HOTSEAT_COLUMNS"))
        }
        factor("home.hotseatCornerRadiusDp", preset.home.hotseatCornerRadiusDp, 0f, 48f)
    }

    private fun MutableList<Violation>.factor(
        field: String,
        value: Float,
        min: Float = MIN_FACTOR,
        max: Float = MAX_FACTOR,
    ) {
        if (value.isNaN() || value < min || value > max) {
            add(Violation(field, "must be between $min and $max (was $value)"))
        }
    }

    fun isValid(preset: LauncherPreset) = validate(preset).isEmpty()
}
