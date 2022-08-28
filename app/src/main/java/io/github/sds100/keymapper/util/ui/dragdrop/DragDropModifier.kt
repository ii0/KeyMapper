package io.github.sds100.keymapper.util.ui.dragdrop

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Stable
fun Modifier.dragAndDrop(state: DragDropListState, scope: CoroutineScope): Modifier {
    return pointerInput(Unit) {
        detectDragGesturesAfterLongPress(
            onDrag = { change, offset ->
                change.consume()
                state.onDrag(offset = offset)

                if (state.overScrollJob?.isActive == true) {
                    return@detectDragGesturesAfterLongPress
                }

                state
                    .checkForOverScroll()
                    .takeIf { it != 0f }
                    ?.let {
                        state.overScrollJob = scope.launch {
                            state.lazyListState.scrollBy(it)
                        }
                    } ?: kotlin.run { state.overScrollJob?.cancel() }
            },
            onDragStart = { offset -> state.onDragStart(offset) },
            onDragEnd = { state.onDragInterrupted() },
            onDragCancel = { state.onDragInterrupted() }
        )
    }
}

@Stable
fun Modifier.dragAndDropItemAnimation(index: Int, state: DragDropListState): Modifier {
    return composed {
        val offsetOrNull = state.elementDisplacement.takeIf {
            index == state.currentIndexOfDraggedItem
        }
        Modifier.graphicsLayer {
            translationY = offsetOrNull ?: 0f
        }
    }
}