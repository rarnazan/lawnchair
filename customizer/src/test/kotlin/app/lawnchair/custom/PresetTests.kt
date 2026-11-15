package app.lawnchair.custom

import app.lawnchair.custom.model.AnimationSpec
import app.lawnchair.custom.model.HomeScreenSpec
import app.lawnchair.custom.model.IconSpec
import app.lawnchair.custom.model.ThemeSpec
import app.lawnchair.custom.preset.PresetCatalog
import app.lawnchair.custom.preset.PresetCodec
import app.lawnchair.custom.preset.PresetValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PresetTests {

    @Test
    fun builtInPresetsAreValidAndHaveUniqueIds() {
        PresetCatalog.all.forEach {
            assertEquals("${it.id}: ${PresetValidator.validate(it)}", emptyList<Any>(), PresetValidator.validate(it))
        }
        assertEquals(PresetCatalog.all.size, PresetCatalog.all.map { it.id }.toSet().size)
    }

    @Test
    fun byIdFindsAndMisses() {
        assertNotNull(PresetCatalog.byId("lite"))
        assertNull(PresetCatalog.byId("nope"))
    }

    @Test
    fun validatorRejectsOutOfRangeValues() {
        val bad = PresetCatalog.Default.copy(
            id = "Bad Id",
            theme = ThemeSpec(accentHex = "red", drawerOpacity = 1.5f),
            icons = IconSpec(homeIconSizeFactor = 3f),
            animation = AnimationSpec(durationScale = Float.NaN),
            home = HomeScreenSpec(columns = 9, rows = 2, hotseatColumns = 1),
        )
        val fields = PresetValidator.validate(bad).map { it.field }.toSet()
        assertTrue(
            fields.containsAll(
                setOf(
                    "id", "theme.accentHex", "theme.drawerOpacity", "icons.homeIconSizeFactor",
                    "animation.durationScale", "home.columns", "home.rows", "home.hotseatColumns",
                ),
            ),
        )
    }

    @Test
    fun codecRoundTripsEveryBuiltInPreset() {
        PresetCatalog.all.forEach {
            val decoded = PresetCodec.decode(PresetCodec.encode(it))
            assertEquals(PresetCodec.Decoded.Ok(it), decoded)
        }
    }

    @Test
    fun codecReportsMalformedAndInvalidWithoutThrowing() {
        assertTrue(PresetCodec.decode("not json") is PresetCodec.Decoded.Malformed)
        assertTrue(PresetCodec.decode("""{"id":"x","name":"x","home":{"columns":99}}""") is PresetCodec.Decoded.Invalid)
    }

    @Test
    fun codecIgnoresUnknownKeysAndFillsDefaults() {
        val decoded = PresetCodec.decode("""{"id":"mini","name":"Mini","future":1}""")
        assertTrue(decoded is PresetCodec.Decoded.Ok)
        assertEquals(PresetCatalog.Default.home, (decoded as PresetCodec.Decoded.Ok).preset.home)
    }

    @Test
    fun hexAccentAcceptedWhenWellFormed() {
        val p = PresetCatalog.Default.copy(theme = ThemeSpec(accentHex = "#00aaFF"))
        assertTrue(PresetValidator.isValid(p))
    }
}
