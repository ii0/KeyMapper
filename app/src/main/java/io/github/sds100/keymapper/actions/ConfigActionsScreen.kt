package io.github.sds100.keymapper.actions

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.sds100.keymapper.R
import io.github.sds100.keymapper.mappings.keymaps.trigger.ActionChip
import io.github.sds100.keymapper.mappings.keymaps.trigger.ConfigActionsListItem
import io.github.sds100.keymapper.mappings.keymaps.trigger.ConfigActionsState
import io.github.sds100.keymapper.util.ui.KMIcon

@Composable
fun ConfigActionsScreen(
    modifier: Modifier = Modifier,
    state: ConfigActionsState,
    onAddActionClick: () -> Unit = {},
) {
    if (state.listItems.isEmpty()) {
        EmptyUi(
            Modifier
                .fillMaxSize()
                .padding(8.dp), onAddActionClick = onAddActionClick
        )
    } else {

    }
}

@Composable
private fun EmptyUi(
    modifier: Modifier = Modifier,
    onAddActionClick: () -> Unit,
) {
    Column(modifier) {
        Box(Modifier.weight(1f)) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(48.dp),
                text = stringResource(R.string.config_actions_empty_text),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp), onClick = onAddActionClick
        ) {
            Text(stringResource(R.string.config_actions_add_action_button))
        }
    }
}

@Preview(device = Devices.PIXEL_3)
@Composable
private fun Preview() {
    val state = ConfigActionsState(
        listItems = listOf(
            ConfigActionsListItem(
                actions = listOf(
                    ActionChip(KMIcon.ImageVector(Icons.Outlined.Android), title = "Open app")
                )
            )
        )
    )

    MaterialTheme {
        Surface {
            ConfigActionsScreen(state = state)
        }
    }
}

@Preview(device = Devices.PIXEL_3)
@Composable
private fun EmptyPreview() {
    val state = ConfigActionsState(listItems = emptyList())

    MaterialTheme {
        Surface {
            ConfigActionsScreen(state = state)
        }
    }
}