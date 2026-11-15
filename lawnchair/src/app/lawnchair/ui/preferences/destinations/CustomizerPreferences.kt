package app.lawnchair.ui.preferences.destinations

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import app.lawnchair.custom.CustomMotion
import app.lawnchair.custom.DeviceReader
import app.lawnchair.custom.PresetApplier
import app.lawnchair.custom.device.DeviceCompat
import app.lawnchair.custom.preset.PresetCatalog
import app.lawnchair.ui.preferences.components.controls.ClickablePreference
import app.lawnchair.ui.preferences.components.layout.PreferenceGroup
import app.lawnchair.ui.preferences.components.layout.PreferenceLayout
import com.android.launcher3.R

@Composable
fun CustomizerPreferences(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var lastResult by remember { mutableStateOf<String?>(null) }
    val findings = remember { DeviceCompat.assess(DeviceReader.read()) }

    PreferenceLayout(
        label = stringResource(id = R.string.customizer_label),
        modifier = modifier,
    ) {
        PreferenceGroup(
            heading = stringResource(id = R.string.customizer_presets),
            description = lastResult,
        ) {
            PresetCatalog.all.forEach { preset ->
                ClickablePreference(
                    label = preset.name,
                    subtitle = "${preset.home.columns}x${preset.home.rows} · ${preset.icons.shape.key} · ${preset.theme.colorStyle.key}",
                    confirmationText = stringResource(id = R.string.customizer_apply_confirm, preset.name),
                    onClick = {
                        lastResult = when (val r = PresetApplier.apply(context, preset)) {
                            PresetApplier.Result.Applied -> "${preset.name}: applied"
                            is PresetApplier.Result.Rejected -> r.reasons.joinToString()
                        }
                    },
                )
            }
        }
        PreferenceGroup(
            heading = stringResource(id = R.string.customizer_device_check),
            description = stringResource(id = R.string.customizer_device_check_note),
        ) {
            findings.forEach {
                ClickablePreference(
                    label = "${it.topic}: ${it.status.name.lowercase()}",
                    subtitle = it.detail,
                    onClick = {},
                )
            }
        }
        // Keep the reference so the motion policy is visibly wired to this screen.
        CustomMotion.current
    }
}
