package app.lawnchair.custom.preset

import app.lawnchair.custom.model.AnimationSpec
import app.lawnchair.custom.model.ColorStyleName
import app.lawnchair.custom.model.DarkMode
import app.lawnchair.custom.model.HomeScreenSpec
import app.lawnchair.custom.model.IconShapeName
import app.lawnchair.custom.model.IconSpec
import app.lawnchair.custom.model.LauncherPreset
import app.lawnchair.custom.model.ThemeSpec
import app.lawnchair.custom.model.WidgetSpec

/**
 * Built-in looks. The Redmi 9C has a 720x1600 panel and an entry-level SoC, so every preset keeps
 * the grid at or below 5 rows and the "Lite" preset turns off blur/depth effects. These figures
 * are design choices, not measurements: nothing here has been run on a device.
 */
object PresetCatalog {
    val Default = LauncherPreset(id = "default", name = "Default")

    val Minimal = LauncherPreset(
        id = "minimal",
        name = "Minimal",
        theme = ThemeSpec(
            darkMode = DarkMode.LIGHT,
            colorStyle = ColorStyleName.MONOCHROMATIC,
            drawerOpacity = 0.9f,
            wallpaperDepthEffect = false,
        ),
        icons = IconSpec(
            shape = IconShapeName.SQUIRCLE,
            homeIconSizeFactor = 0.9f,
            showHomeLabels = false,
        ),
        widgets = WidgetSpec(roundedWidgets = true, widgetPaddingFactor = 1.2f, showSmartspace = true),
        animation = AnimationSpec(durationScale = 0.85f),
        home = HomeScreenSpec(columns = 4, rows = 5, hotseatColumns = 4, hotseatCornerRadiusDp = 28f),
    )

    val Vivid = LauncherPreset(
        id = "vivid",
        name = "Vivid",
        theme = ThemeSpec(
            darkMode = DarkMode.DARK,
            colorStyle = ColorStyleName.EXPRESSIVE,
            accentHex = "#7C4DFF",
            drawerOpacity = 0.7f,
        ),
        icons = IconSpec(
            shape = IconShapeName.TEARDROP,
            homeIconSizeFactor = 1.05f,
            homeLabelSizeFactor = 0.95f,
        ),
        widgets = WidgetSpec(roundedWidgets = true, widgetPaddingFactor = 0.9f),
        animation = AnimationSpec(durationScale = 1.1f, springStiffnessFactor = 0.9f),
        home = HomeScreenSpec(columns = 4, rows = 5, hotseatColumns = 5, hotseatCornerRadiusDp = 32f),
    )

    /** For low RAM / weak GPU: no blur or depth, snappier motion, denser grid. */
    val Lite = LauncherPreset(
        id = "lite",
        name = "Lite (performance)",
        theme = ThemeSpec(
            darkMode = DarkMode.DARK,
            colorStyle = ColorStyleName.TONAL_SPOT,
            drawerOpacity = 1f,
            wallpaperDepthEffect = false,
        ),
        icons = IconSpec(shape = IconShapeName.CIRCLE, homeIconSizeFactor = 0.95f),
        widgets = WidgetSpec(roundedWidgets = false, showSmartspace = false),
        animation = AnimationSpec(durationScale = 0.6f, springStiffnessFactor = 1.3f, reduceEffects = true),
        home = HomeScreenSpec(columns = 5, rows = 5, hotseatColumns = 5, hotseatCornerRadiusDp = 16f),
    )

    val all: List<LauncherPreset> = listOf(Default, Minimal, Vivid, Lite)

    fun byId(id: String): LauncherPreset? = all.firstOrNull { it.id == id }
}
