package app.lawnchair.custom.model

import kotlinx.serialization.Serializable

/**
 * Platform-independent description of a launcher look. Everything here maps onto a Lawnchair
 * preference that already exists; nothing in this module touches the Android framework or root.
 */
@Serializable
enum class DarkMode { SYSTEM, LIGHT, DARK }

/** Names match `app.lawnchair.theme.color.ColorStyle.toString()`. */
@Serializable
enum class ColorStyleName(val key: String) {
    TONAL_SPOT("tonal_spot"),
    VIBRANT("vibrant"),
    EXPRESSIVE("expressive"),
    SPRITZ("spritz"),
    RAINBOW("rainbow"),
    FRUIT_SALAD("fruit_salad"),
    CONTENT("content"),
    MONOCHROMATIC("monochromatic"),
}

/** Names match the string tokens accepted by `IconShape.fromString`. */
@Serializable
enum class IconShapeName(val key: String) {
    CIRCLE("circle"),
    SQUARE("square"),
    ROUNDED_SQUARE("roundedSquare"),
    SQUIRCLE("squircle"),
    TEARDROP("teardrop"),
    HEXAGON("hexagon"),
}

@Serializable
data class ThemeSpec(
    val darkMode: DarkMode = DarkMode.SYSTEM,
    val colorStyle: ColorStyleName = ColorStyleName.TONAL_SPOT,
    /** `#RRGGBB` custom accent, or null to follow the system/wallpaper accent. */
    val accentHex: String? = null,
    /** Opacity of the app drawer background, 0..1. */
    val drawerOpacity: Float = 0.5f,
    val wallpaperDepthEffect: Boolean = true,
)

@Serializable
data class IconSpec(
    val shape: IconShapeName = IconShapeName.CIRCLE,
    val homeIconSizeFactor: Float = 1f,
    val drawerIconSizeFactor: Float = 1f,
    val showHomeLabels: Boolean = true,
    val homeLabelSizeFactor: Float = 1f,
)

@Serializable
data class WidgetSpec(
    val roundedWidgets: Boolean = true,
    val widgetPaddingFactor: Float = 1f,
    val showSmartspace: Boolean = true,
)

/**
 * Motion budget. The factors are consumed by [app.lawnchair.custom.animation.MotionPolicy].
 */
@Serializable
data class AnimationSpec(
    /** Multiplies every transition duration. 1 = stock, <1 faster. */
    val durationScale: Float = 1f,
    /** Spring stiffness used for physics-based motion, relative to stock (1 = stock). */
    val springStiffnessFactor: Float = 1f,
    /** Drops optional decoration (blur, depth, staggered icon reveals) for weaker GPUs. */
    val reduceEffects: Boolean = false,
)

@Serializable
data class HomeScreenSpec(
    val columns: Int = 4,
    val rows: Int = 5,
    val hotseatColumns: Int = 4,
    val hotseatCornerRadiusDp: Float = 24f,
)

@Serializable
data class LauncherPreset(
    val id: String,
    val name: String,
    val theme: ThemeSpec = ThemeSpec(),
    val icons: IconSpec = IconSpec(),
    val widgets: WidgetSpec = WidgetSpec(),
    val animation: AnimationSpec = AnimationSpec(),
    val home: HomeScreenSpec = HomeScreenSpec(),
)
