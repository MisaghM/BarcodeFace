package com.misana.barcodeface.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

const val animationDuration = 400
const val alphas = 0.1f

val scaleInAnimation: EnterTransition = scaleIn(
    animationSpec = tween(durationMillis = animationDuration),
    initialScale = 1.2f
) + fadeIn(
    animationSpec = tween(durationMillis = animationDuration),
    initialAlpha = alphas
)

val scaleOutAnimation: ExitTransition = scaleOut(
    animationSpec = tween(durationMillis = animationDuration),
    targetScale = 1.2f
) + fadeOut(
    animationSpec = tween(durationMillis = animationDuration),
    targetAlpha = alphas
)

val welcomeExitAnimation: ExitTransition = slideOutHorizontally(
    targetOffsetX = { fullWidth -> -fullWidth },
    animationSpec = tween(durationMillis = animationDuration)
)

val slideInAnimation: EnterTransition = slideInHorizontally(
    initialOffsetX = { fullWidth -> fullWidth },
    animationSpec = tween(durationMillis = animationDuration)
)

val slideOutAnimation: ExitTransition = slideOutHorizontally(
    targetOffsetX = { fullWidth -> fullWidth },
    animationSpec = tween(durationMillis = animationDuration)
)
