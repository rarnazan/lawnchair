package app.lawnchair.custom

import android.content.Context
import app.lawnchair.custom.animation.MotionPolicy
import app.lawnchair.custom.model.DarkMode
import app.lawnchair.custom.model.LauncherPreset
import app.lawnchair.custom.preset.PresetValidator
import app.lawnchair.icons.shape.IconShape
import app.lawnchair.preferences.PreferenceManager
import app.lawnchair.preferences2.PreferenceManager2
import app.lawnchair.theme.color.ColorOption
import app.lawnchair.theme.color.ColorStyle
import app.lawnchair.ui.preferences.components.ThemeChoice
import com.patrykmichalik.opto.core.setBlocking

/**
 * Writes a [LauncherPreset] into Lawnchair's existing preferences. Only user-level launcher
 * settings are touched; no system setting, root access or partition is involved.
 */
object PresetApplier {

    sealed interface Result {
        data object Applied : Result
        data class Rejected(val reasons: List<String>) : Result
    }

    fun apply(context: Context, preset: LauncherPreset): Result {
        val violations = PresetValidator.validate(preset)
        if (violations.isNotEmpty()) {
            return Result.Rejected(violations.map { "${it.field}: ${it.message}" })
        }

        val prefs = PreferenceManager.getInstance(context)
        val prefs2 = PreferenceManager2.getInstance(context)

        // Grid / dock (legacy preference manager). Batched so the grid reloads once.
        prefs.batchEdit {
            prefs.workspaceColumns.set(preset.home.columns)
            prefs.workspaceRows.set(preset.home.rows)
            prefs.hotseatColumns.set(preset.home.hotseatColumns)
            prefs.drawerOpacity.set(preset.theme.drawerOpacity)
            prefs.launcherTheme.set(
                when (preset.theme.darkMode) {
                    DarkMode.SYSTEM -> ThemeChoice.SYSTEM
                    DarkMode.LIGHT -> ThemeChoice.LIGHT
                    DarkMode.DARK -> ThemeChoice.DARK
                },
            )
        }

        val motion = MotionPolicy(preset.animation)

        // Theme
        prefs2.colorStyle.setBlocking(ColorStyle.fromString(preset.theme.colorStyle.key))
        prefs2.accentColor.setBlocking(
            preset.theme.accentHex
                ?.let { ColorOption.fromString("custom|#ff${it.removePrefix("#")}") }
                ?: ColorOption.SystemAccent,
        )
        prefs2.wallpaperDepthEffect.setBlocking(preset.theme.wallpaperDepthEffect && motion.allowDepthEffect)

        // Icons
        IconShape.fromString(preset.icons.shape.key, context)?.let { prefs2.iconShape.setBlocking(it) }
        prefs2.homeIconSizeFactor.setBlocking(preset.icons.homeIconSizeFactor)
        prefs2.drawerIconSizeFactor.setBlocking(preset.icons.drawerIconSizeFactor)
        prefs2.showIconLabelsOnHomeScreen.setBlocking(preset.icons.showHomeLabels)
        prefs2.homeIconLabelSizeFactor.setBlocking(preset.icons.homeLabelSizeFactor)

        // Widgets
        prefs2.roundedWidgets.setBlocking(preset.widgets.roundedWidgets)
        prefs2.widgetPaddingFactor.setBlocking(preset.widgets.widgetPaddingFactor)
        prefs2.enableSmartspace.setBlocking(preset.widgets.showSmartspace)

        // Dock look
        prefs2.hotseatBackgroundCornerRadius.setBlocking(preset.home.hotseatCornerRadiusDp)

        // Animation: the launcher-wide blur switch follows the motion policy.
        if (!motion.allowBlur) prefs.enableWallpaperBlur.set(false)

        CustomMotion.current = motion
        return Result.Applied
    }
}

/** Process-wide motion settings that animation call sites can read via [MotionPolicy]. */
object CustomMotion {
    @Volatile
    var current: MotionPolicy = MotionPolicy(app.lawnchair.custom.model.AnimationSpec())
}
