package io.github.sds100.keymapper.mappings.keymaps.trigger

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import io.github.sds100.keymapper.util.ui.dragdrop.DragDropList
import io.github.sds100.keymapper.util.ui.dragdrop.move

@Composable
fun ConfigTriggerScreen(
    viewModel: ConfigTriggerViewModel
) {
    ConfigTriggerScreen()
}

@Composable
private fun ConfigTriggerScreen() {
    val listState = rememberLazyListState()
    val list = remember { mutableStateListOf("bla1", "bla2", "bla3") }

    DragDropList(modifier = Modifier.fillMaxSize(), items = list, onMove = { from, to ->
        list.move(from, to)
    })
}

@Preview(device = Devices.PIXEL_4)
@Composable
private fun Preview() {
    ConfigTriggerScreen()
}