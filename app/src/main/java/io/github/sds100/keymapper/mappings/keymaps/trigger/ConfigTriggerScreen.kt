package io.github.sds100.keymapper.mappings.keymaps.trigger

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import io.github.sds100.keymapper.theme.Colors
import io.github.sds100.keymapper.util.ui.dragdrop.dragAndDrop
import io.github.sds100.keymapper.util.ui.dragdrop.dragAndDropItemAnimation
import io.github.sds100.keymapper.util.ui.dragdrop.rememberDragDropListState

@Composable
fun ConfigTriggerScreen(
    viewModel: ConfigTriggerViewModel,
) {
    val state by viewModel.state.collectAsState()
    ConfigTriggerScreen(
        modifier = Modifier.fillMaxSize(),
        state = state,
        onRecordTriggerClick = viewModel::onRecordTriggerClick)
}

@Composable
private fun ConfigTriggerScreen(
    modifier: Modifier = Modifier,
    state: ConfigTriggerState,
    onRecordTriggerClick: () -> Unit = {},
) {
    Column(modifier) {
        when (state) {
            ConfigTriggerState.Empty ->
                EmptyTriggerUi(modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .weight(1f))

            is ConfigTriggerState.Trigger ->
                TriggerUi(
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1f),
                    state = state)
        }

        RecordTriggerButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            onClick = onRecordTriggerClick
        )
    }
}

@Composable
private fun TriggerUi(modifier: Modifier = Modifier, state: ConfigTriggerState.Trigger) {
    Column(modifier) {
        KeyList(modifier = Modifier.weight(1f), keys = state.keys)
    }
}

@Composable
private fun EmptyTriggerUi(modifier: Modifier = Modifier) {
    Box(modifier) {
        Text(modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.Center)
            .padding(48.dp),
            text = stringResource(R.string.config_trigger_empty_text),
            style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun RecordTriggerButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    val harmonizedRedColorInt =
        MaterialColors.harmonizeWithPrimary(LocalContext.current, Colors.recordTriggerButton.toArgb())

    val harmonizedRedColor = Color(harmonizedRedColorInt)

    val buttonColors = ButtonDefaults.buttonColors(
        containerColor = harmonizedRedColor,
        contentColor = Color.White
    )

    Button(modifier = modifier, onClick = onClick, colors = buttonColors) {
        Text(stringResource(R.string.config_trigger_button_record))
    }
}

@Composable
private fun KeyList(modifier: Modifier = Modifier, keys: List<TriggerKeyListItem2>) {
    val dragDropState = rememberDragDropListState(onMove = { from, to -> })
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = modifier.dragAndDrop(dragDropState, scope),
        state = dragDropState.lazyListState) {

        itemsIndexed(items = keys, key = { _, item -> item.uid }) { index, item ->
            TriggerKeyListItem(
                modifier = Modifier
                    .dragAndDropItemAnimation(index, dragDropState),
                description = item.description,
                extraInfo = item.extraInfo,
                linkType = TriggerKeyLinkType.ARROW)
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
                state = ConfigTriggerState.Trigger(
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
                    )
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
                state = ConfigTriggerState.Empty
            )
        }
    }
}