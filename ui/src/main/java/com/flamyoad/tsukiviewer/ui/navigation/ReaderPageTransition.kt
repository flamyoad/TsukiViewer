package com.flamyoad.tsukiviewer.ui.navigation

import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation3.ui.LocalNavAnimatedContentScope

internal const val ReaderReturnDurationMillis = 280
internal val ReaderReturnEasing = CubicBezierEasing(0.2f, 0f, 0f, 1f)

internal data class ReaderReturnAnchor(val galleryEntry: String, val page: Int)

internal val LocalReaderReturnAnchor = staticCompositionLocalOf<ReaderReturnAnchor?> { null }

@OptIn(ExperimentalSharedTransitionApi::class)
internal val LocalReaderSharedScope = staticCompositionLocalOf<SharedTransitionScope?> { null }

@OptIn(ExperimentalSharedTransitionApi::class)
private val ReaderReturnBounds = BoundsTransform { _, _ ->
    tween(ReaderReturnDurationMillis, easing = ReaderReturnEasing)
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun Modifier.readerPageTransition(page: Int, thumbnail: Boolean): Modifier {
    val anchor = LocalReaderReturnAnchor.current ?: return this
    if (page != anchor.page) return this
    val sharedScope = LocalReaderSharedScope.current ?: return this
    val navScope = LocalNavAnimatedContentScope.current
    // Match only while returning so opening the reader uses its own window animation.
    val entering = navScope.transition.targetState == EnterExitState.Visible
    if (thumbnail != entering) return this
    return with(sharedScope) {
        sharedElement(
            sharedContentState = rememberSharedContentState(anchor),
            animatedVisibilityScope = navScope,
            boundsTransform = ReaderReturnBounds
        )
    }
}
