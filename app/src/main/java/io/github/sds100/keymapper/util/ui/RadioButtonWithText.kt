package io.github.sds100.keymapper.util.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview

/**
 * Created by sds100 on 30/07/2022.
 */

@Composable
fun RadioButtonWithText(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    text: @Composable () -> Unit,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .clip(ShapeDefaults.Medium)
            .clickable(enabled = enabled, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = isSelected, onClick = onClick, enabled = enabled)
        text()
    }
}

@Preview(widthDp = 400)
@Composable
private fun Preview() {
    MaterialTheme {
        RadioButtonWithText(isSelected = true, text = { Text("Radio button") })
    }
}