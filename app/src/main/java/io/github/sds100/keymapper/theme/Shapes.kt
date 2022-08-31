package io.github.sds100.keymapper.theme

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape

object Shapes {
    @Composable
    fun bottomSheet(): Shape {
        return MaterialTheme.shapes.extraLarge.copy(
            bottomStart = CornerSize(0),
            bottomEnd = CornerSize(0)
        )
    }
}