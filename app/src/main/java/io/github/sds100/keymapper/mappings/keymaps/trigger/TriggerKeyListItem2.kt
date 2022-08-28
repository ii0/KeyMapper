package io.github.sds100.keymapper.mappings.keymaps.trigger

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.sds100.keymapper.R

data class TriggerKeyListItem2(
    val uid: String,
    val description: String,
    val extraInfo: String,
    val linkType: TriggerKeyLinkType,
)

@Composable
fun TriggerKeyListItem(
    modifier: Modifier = Modifier,
    description: String,
    extraInfo: String?,
    linkType: TriggerKeyLinkType,
    onDevicesClick: () -> Unit = {},
    onOptionsClick: () -> Unit = {},
    onRemoveClick: () -> Unit = {},
) {
    Column(modifier = modifier) {
        ElevatedCard {
            Row(
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.DragHandle, contentDescription = null)

                Spacer(Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (extraInfo != null) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = extraInfo,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Row {
                    IconButton(
                        modifier = Modifier.size(36.dp),
                        onClick = onDevicesClick
                    ) {
                        Icon(
                            Icons.Outlined.Devices,
                            contentDescription = stringResource(R.string.config_trigger_key_devices_content_description)
                        )
                    }

                    IconButton(
                        modifier = Modifier.size(36.dp),
                        onClick = onOptionsClick
                    ) {
                        Icon(
                            Icons.Outlined.MoreVert,
                            contentDescription = stringResource(R.string.config_trigger_key_options_content_description)
                        )
                    }

                    IconButton(
                        modifier = Modifier.size(36.dp),
                        onClick = onRemoveClick
                    ) {
                        Icon(
                            Icons.Outlined.Close,
                            contentDescription = stringResource(R.string.config_trigger_remove_key_content_description)
                        )
                    }
                }
            }
        }

        val icon = when (linkType) {
            TriggerKeyLinkType.HIDDEN -> null
            TriggerKeyLinkType.PLUS -> Icons.Outlined.Add
            TriggerKeyLinkType.ARROW -> Icons.Outlined.ArrowDownward
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (icon != null) {
            Icon(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                imageVector = icon,
                contentDescription = null
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Preview(widthDp = 300)
@Composable
private fun Preview() {
    MaterialTheme {
        Surface {
            Column {
                TriggerKeyListItem(
                    description = "A very very very very long description",
                    extraInfo = "Keyboard",
                    linkType = TriggerKeyLinkType.ARROW
                )

                TriggerKeyListItem(
                    description = "A short one",
                    extraInfo = "Headphones",
                    linkType = TriggerKeyLinkType.HIDDEN
                )
            }
        }
    }
}