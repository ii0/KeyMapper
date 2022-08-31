package io.github.sds100.keymapper.mappings.keymaps.trigger

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.material.color.MaterialColors
import io.github.sds100.keymapper.R
import io.github.sds100.keymapper.mappings.ClickType
import io.github.sds100.keymapper.theme.Colors
import io.github.sds100.keymapper.util.ui.RadioButtonWithText
import io.github.sds100.keymapper.util.ui.dragdrop.dragAndDrop
import io.github.sds100.keymapper.util.ui.dragdrop.dragAndDropItemAnimation
import io.github.sds100.keymapper.util.ui.dragdrop.rememberDragDropListState

@Composable
fun ConfigTriggerScreen(
    modifier: Modifier = Modifier,
    configState: ConfigTriggerState,
    onRecordTriggerClick: () -> Unit = {},
    onRemoveTriggerKeyClick: (String) -> Unit = {},
    onFixTriggerErrorClick: (KeyMapTriggerError) -> Unit = {},
    onMoveTriggerKey: (from: Int, to: Int) -> Unit = { _, _ -> },
    onSelectClickType: (ClickType) -> Unit = {},
    onSelectParallelTriggerMode: () -> Unit = {},
    onSelectSequenceTriggerMode: () -> Unit = {},
    onChooseTriggerKeyDeviceClick: (String) -> Unit = {},
    onTriggerKeyOptionsClick: (String) -> Unit = {},
) {
    Column(modifier) {
        if (configState.keys.isEmpty()) {
            EmptyTriggerUi(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .weight(1f)
            )
        } else {
            TriggerUi(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f),
                state = configState,
                onRemoveTriggerKeyClick = onRemoveTriggerKeyClick,
                onFixErrorClick = onFixTriggerErrorClick,
                onMoveKey = onMoveTriggerKey,
                onSelectClickType = onSelectClickType,
                onSelectParallelMode = onSelectParallelTriggerMode,
                onSelectSequenceMode = onSelectSequenceTriggerMode,
                onChooseTriggerKeyDeviceClick = onChooseTriggerKeyDeviceClick,
                onTriggerKeyOptionsClick = onTriggerKeyOptionsClick
            )
        }

        RecordTriggerButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
            onClick = onRecordTriggerClick,
            state = configState.recordTriggerState
        )
    }
}

@Composable
private fun TriggerUi(
    modifier: Modifier = Modifier,
    state: ConfigTriggerState,
    onRemoveTriggerKeyClick: (String) -> Unit,
    onFixErrorClick: (KeyMapTriggerError) -> Unit,
    onMoveKey: (from: Int, to: Int) -> Unit,
    onSelectClickType: (ClickType) -> Unit,
    onSelectParallelMode: () -> Unit,
    onSelectSequenceMode: () -> Unit,
    onChooseTriggerKeyDeviceClick: (String) -> Unit,
    onTriggerKeyOptionsClick: (String) -> Unit,
) {
    Column(modifier) {
        if (state.errors.isNotEmpty()) {
            ErrorList(
                modifier = Modifier.wrapContentHeight(),
                errors = state.errors,
                onFixClick = onFixErrorClick
            )
            Spacer(Modifier.height(8.dp))
        }

        KeyList(
            modifier = Modifier.weight(1f),
            keys = state.keys,
            onRemoveClick = onRemoveTriggerKeyClick,
            onMove = onMoveKey,
            onChooseDeviceClick = onChooseTriggerKeyDeviceClick,
            onOptionsClick = onTriggerKeyOptionsClick
        )
        if (state.availableClickTypes.isNotEmpty() && state.clickType != null) {
            ClickTypeButtons(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp),
                clickType = state.clickType,
                availableButtons = state.availableClickTypes,
                onSelect = onSelectClickType
            )
        }
        TriggerModeButtons(
            modifier = Modifier.fillMaxWidth(),
            mode = state.mode,
            enabled = state.isModeButtonsEnabled,
            onSelectParallelMode = onSelectParallelMode,
            onSelectSequenceMode = onSelectSequenceMode
        )
    }
}

@Composable
private fun EmptyTriggerUi(modifier: Modifier = Modifier) {
    Box(modifier) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(48.dp),
            text = stringResource(R.string.config_trigger_empty_text),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun TriggerModeButtons(
    modifier: Modifier,
    mode: TriggerMode,
    enabled: Boolean,
    onSelectParallelMode: () -> Unit,
    onSelectSequenceMode: () -> Unit,
) {
    Column(modifier) {
        Text(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp),
            text = stringResource(R.string.config_trigger_press_dot_dot_dot),
            style = MaterialTheme.typography.bodyMedium
        )
        Row(Modifier.fillMaxWidth()) {
            RadioButtonWithText(
                modifier = Modifier.weight(1f),
                isSelected = mode is TriggerMode.Parallel,
                text = {
                    Text(
                        stringResource(R.string.config_trigger_parallel_mode_button),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                enabled = enabled,
                onClick = onSelectParallelMode
            )
            RadioButtonWithText(
                modifier = Modifier.weight(1f),
                isSelected = mode is TriggerMode.Sequence,
                text = {
                    Text(
                        stringResource(R.string.config_trigger_sequence_mode_button),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                enabled = enabled,
                onClick = onSelectSequenceMode
            )
        }
    }
}

private val clickTypeButtonText: Map<ClickType, Int> = mapOf(
    ClickType.SHORT_PRESS to R.string.clicktype_short_press,
    ClickType.LONG_PRESS to R.string.clicktype_long_press,
    ClickType.DOUBLE_PRESS to R.string.clicktype_double_press,
)

@Composable
fun ClickTypeButtons(
    modifier: Modifier,
    clickType: ClickType,
    availableButtons: List<ClickType>,
    onSelect: (ClickType) -> Unit,
) {
    Row(modifier) {
        availableButtons.forEach { button ->
            RadioButtonWithText(
                modifier = Modifier.weight(1f),
                isSelected = clickType == button,
                text = {
                    Text(
                        stringResource(clickTypeButtonText[button]!!),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                onClick = { onSelect(button) }
            )
        }
    }
}

@Composable
private fun RecordTriggerButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    state: RecordTriggerState,
) {
    val harmonizedRedColorInt =
        MaterialColors.harmonizeWithPrimary(
            LocalContext.current,
            Colors.recordTriggerButton.toArgb()
        )

    val harmonizedRedColor = Color(harmonizedRedColorInt)

    val buttonColors = ButtonDefaults.buttonColors(
        containerColor = harmonizedRedColor,
        contentColor = Color.White
    )

    val text = when (state) {
        is RecordTriggerState.CountingDown -> stringResource(
            R.string.config_trigger_record_countdown_button_text,
            state.timeLeft
        )
        RecordTriggerState.Stopped -> stringResource(R.string.config_trigger_record_button)
    }

    Button(modifier = modifier, onClick = onClick, colors = buttonColors) {
        Text(text)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ErrorList(
    modifier: Modifier = Modifier,
    errors: List<KeyMapTriggerError>,
    onFixClick: (KeyMapTriggerError) -> Unit,
) {
    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(errors, key = { item -> item }) { error ->
            val textRes = when (error) {
                KeyMapTriggerError.DND_ACCESS_DENIED -> R.string.trigger_error_dnd_access_denied
                KeyMapTriggerError.SCREEN_OFF_ROOT_DENIED -> R.string.trigger_error_screen_off_root_permission_denied
                KeyMapTriggerError.CANT_DETECT_IN_PHONE_CALL -> R.string.trigger_error_cant_detect_in_phone_call
            }

            ErrorListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItemPlacement(),
                text = stringResource(textRes),
                onFixClick = { onFixClick(error) }
            )
        }
    }
}

@Composable
private fun ErrorListItem(modifier: Modifier = Modifier, text: String, onFixClick: () -> Unit) {
    OutlinedCard(modifier = modifier) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )

            Spacer(Modifier.width(8.dp))

            Text(
                modifier = Modifier.weight(1f),
                text = text,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.width(8.dp))

            val buttonColors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            )

            Button(
                onClick = onFixClick,
                colors = buttonColors
            ) {
                Text(stringResource(R.string.button_fix))
            }
        }
    }
}

@Composable
private fun KeyList(
    modifier: Modifier = Modifier,
    keys: List<TriggerKeyListItem2>,
    onMove: (from: Int, to: Int) -> Unit,
    onRemoveClick: (String) -> Unit,
    onChooseDeviceClick: (String) -> Unit,
    onOptionsClick: (String) -> Unit,
) {
    val dragDropState = rememberDragDropListState(onMove = onMove)
    val scope = rememberCoroutineScope()
    val showDragHandle = keys.size > 1

    LazyColumn(
        modifier = modifier.dragAndDrop(dragDropState, scope),
        state = dragDropState.lazyListState
    ) {
        itemsIndexed(items = keys, key = { _, item -> item.uid }) { index, item ->
            TriggerKeyListItem(
                modifier = Modifier.dragAndDropItemAnimation(index, dragDropState),
                description = item.description,
                extraInfo = item.extraInfo,
                linkType = item.linkType,
                onRemoveClick = { onRemoveClick(item.uid) },
                showDragHandle = showDragHandle,
                onDevicesClick = { onChooseDeviceClick(item.uid) },
                onOptionsClick = { onOptionsClick(item.uid) }
            )
        }
    }
}

@Preview(device = Devices.PIXEL_4)
@Composable
private fun Preview() {
    MaterialTheme {
        Surface {
            ConfigTriggerScreen(
                modifier = Modifier.fillMaxSize(),
                configState = ConfigTriggerState(
                    keys = listOf(
                        TriggerKeyListItem2(
                            uid = "1",
                            description = "Long press • Vol down",
                            extraInfo = "Keyboard",
                            linkType = TriggerKeyLinkType.ARROW
                        ),
                        TriggerKeyListItem2(
                            uid = "2",
                            description = "Vol up",
                            extraInfo = "Headphones",
                            linkType = TriggerKeyLinkType.HIDDEN
                        ),
                    ),
                    recordTriggerState = RecordTriggerState.Stopped,
                    errors = listOf(
                        KeyMapTriggerError.SCREEN_OFF_ROOT_DENIED,
                        KeyMapTriggerError.DND_ACCESS_DENIED
                    ),
                    mode = TriggerMode.Parallel(ClickType.SHORT_PRESS)
                )
            )
        }
    }
}

@Preview(device = Devices.PIXEL_4)
@Composable
private fun PreviewEmpty() {
    MaterialTheme {
        Surface {
            ConfigTriggerScreen(
                modifier = Modifier.fillMaxSize(),
                configState = ConfigTriggerState(
                    recordTriggerState = RecordTriggerState.CountingDown(
                        timeLeft = 3
                    )
                )
            )
        }
    }
}