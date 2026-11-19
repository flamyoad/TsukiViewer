package com.flamyoad.tsukiviewer.ui.components

import android.view.animation.AccelerateDecelerateInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.launch

private val SelectionEasing = Easing(AccelerateDecelerateInterpolator()::getInterpolation)

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun Modifier.selectionZoom(
    selectionActive: Boolean,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)?
): Modifier {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    return graphicsLayer {
        scaleX = scale.value
        scaleY = scale.value
    }.combinedClickable(
        onClick = onClick,
        onLongClick = onLongClick?.let { select ->
            {
                if (!selectionActive) {
                    scope.launch {
                        scale.animateTo(0.95f, tween(200, easing = SelectionEasing))
                        scale.animateTo(1f, tween(200, easing = SelectionEasing))
                    }
                }
                select()
            }
        }
    )
}
