package io.github.sds100.keymapper.util.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Created by sds100 on 30/07/2022.
 */

@Composable
fun SwitchWithText(
    modifier: Modifier = Modifier,
    checked: Boolean,
    text: @Composable () -> Unit,
    onChange: (Boolean) -> Unit = {},
) {
    Row(
        modifier = modifier
            .clip(ShapeDefaults.Medium)
            .clickable { onChange(!checked) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(8.dp))
        text()
        Spacer(Modifier.width(8.dp))
        Switch(checked = checked, onCheckedChange = onChange)
        Spacer(Modifier.width(8.dp))
    }
}

@Preview(widthDp = 400)
@Composable
private fun Preview() {
    MaterialTheme {
        Surface {
            SwitchWithText(checked = true, text = { Text("Switch") })
        }
    }
}