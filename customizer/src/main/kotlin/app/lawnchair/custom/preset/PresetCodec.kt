package app.lawnchair.custom.preset

import app.lawnchair.custom.model.LauncherPreset
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

/** Import/export of presets as JSON so they can be shared and backed up. */
object PresetCodec {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun encode(preset: LauncherPreset): String = json.encodeToString(LauncherPreset.serializer(), preset)

    sealed interface Decoded {
        data class Ok(val preset: LauncherPreset) : Decoded
        data class Invalid(val violations: List<Violation>) : Decoded
        data class Malformed(val reason: String) : Decoded
    }

    /** Parses and validates. Never throws; callers get a typed reason instead. */
    fun decode(text: String): Decoded {
        val preset = try {
            json.decodeFromString(LauncherPreset.serializer(), text)
        } catch (e: SerializationException) {
            return Decoded.Malformed(e.message ?: "invalid JSON")
        } catch (e: IllegalArgumentException) {
            return Decoded.Malformed(e.message ?: "invalid JSON")
        }
        val violations = PresetValidator.validate(preset)
        return if (violations.isEmpty()) Decoded.Ok(preset) else Decoded.Invalid(violations)
    }
}
