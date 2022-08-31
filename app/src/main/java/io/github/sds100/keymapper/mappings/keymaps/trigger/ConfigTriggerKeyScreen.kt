package io.github.sds100.keymapper.mappings.keymaps.trigger

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import io.github.sds100.keymapper.R
import io.github.sds100.keymapper.mappings.ClickType
import io.github.sds100.keymapper.util.ui.CheckBoxWithText

@Destination(style = DestinationStyle.BottomSheet::class)
@Composable
fun ConfigTriggerKeyScreen(
    viewModel: ConfigKeyMapViewModel2,
    navigator: DestinationsNavigator,
) {
    val uriHandler = LocalUriHandler.current
    val helpUrl = stringResource(R.string.url_trigger_key_options_guide)
    val state by viewModel.triggerKeyState.collectAsState()

    BackHandler(onBack = navigator::navigateUp)

    ConfigTriggerKeyScreen(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        state = state,
        onDoNotRemapCheckedChange = viewModel::onDoNotRemapKeyCheckedChange,
        onSelectClickType = viewModel::onSelectKeyClickType,
        onDoneButtonClick = navigator::navigateUp,
        onHelpButtonClick = { uriHandler.openUri(helpUrl) }
    )
}

@Composable
private fun ConfigTriggerKeyScreen(
    modifier: Modifier = Modifier,
    state: ConfigTriggerKeyState,
    onDoNotRemapCheckedChange: (Boolean) -> Unit = {},
    onSelectClickType: (ClickType) -> Unit = {},
    onDoneButtonClick: () -> Unit = {},
    onHelpButtonClick: () -> Unit = {},
) {
    Column(modifier) {
        Icon(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            imageVector = Icons.Rounded.DragHandle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )

        CheckBoxWithText(
            modifier = Modifier.fillMaxWidth(),
            isChecked = state.isDoNotRemapChecked,
            text = { Text(stringResource(R.string.config_trigger_key_do_not_remap_checkbox)) },
            onCheckedChange = onDoNotRemapCheckedChange
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            text = stringResource(R.string.config_trigger_key_do_not_remap_caption),
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (state.showClickTypeButtons) {
            Divider(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))

            ClickTypeButtons(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp),
                clickType = state.clickType,
                availableButtons = listOf(
                    ClickType.SHORT_PRESS,
                    ClickType.LONG_PRESS,
                    ClickType.DOUBLE_PRESS
                ),
                onSelect = onSelectClickType
            )
        }

        Row {
            OutlinedButton(modifier = Modifier.weight(1f), onClick = onHelpButtonClick) {
                Icon(
                    modifier = Modifier.size(18.dp),
                    imageVector = Icons.Outlined.HelpOutline,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(text = stringResource(R.string.config_trigger_key_help_button))
            }

            Spacer(Modifier.width(8.dp))

            Button(modifier = Modifier.weight(1f), onClick = onDoneButtonClick) {
                Text(stringResource(R.string.config_trigger_key_done_button))
            }
        }
    }
}

@Composable
@Preview(device = Devices.PIXEL_4)
private fun Preview() {
    MaterialTheme {
        Surface {
            ConfigTriggerKeyScreen(
                Modifier.fillMaxWidth(),
                state = ConfigTriggerKeyState(isDoNotRemapChecked = true)
            )
        }
    }
}