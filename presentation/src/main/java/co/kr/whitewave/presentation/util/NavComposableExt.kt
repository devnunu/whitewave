package co.kr.whitewave.presentation.util

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import kotlin.reflect.KType

enum class ScreenAnim {
    HORIZONTAL_SLIDE,
    VERTICAL_SLIDE,
    FADE_IN_OUT,
    EXPAND_FROM_BOTTOM,
}

inline fun <reified T : Any> NavGraphBuilder.composable(
    screenAnim: ScreenAnim = ScreenAnim.HORIZONTAL_SLIDE,
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
    noinline content: @Composable (AnimatedContentScope.(NavBackStackEntry) -> Unit)
) {
    composable<T>(
        enterTransition = {
            when (screenAnim) {
                ScreenAnim.HORIZONTAL_SLIDE -> {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = tween(durationMillis = 400)
                    )
                }

                ScreenAnim.VERTICAL_SLIDE -> {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(durationMillis = 400)
                    )
                }

                ScreenAnim.FADE_IN_OUT -> fadeIn()

                ScreenAnim.EXPAND_FROM_BOTTOM -> {
                    scaleIn(
                        initialScale = 0.9f,
                        transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 1f),
                        animationSpec = tween(durationMillis = 300)
                    ) + fadeIn(animationSpec = tween(durationMillis = 300))
                }
            }
        },
        exitTransition = {
            when (screenAnim) {
                ScreenAnim.HORIZONTAL_SLIDE -> null
                ScreenAnim.VERTICAL_SLIDE -> null
                ScreenAnim.FADE_IN_OUT -> fadeOut()
                ScreenAnim.EXPAND_FROM_BOTTOM -> fadeOut(animationSpec = tween(durationMillis = 150))
            }
        },
        popEnterTransition = {
            when (screenAnim) {
                ScreenAnim.HORIZONTAL_SLIDE -> null
                ScreenAnim.VERTICAL_SLIDE -> null
                ScreenAnim.FADE_IN_OUT -> fadeIn()
                ScreenAnim.EXPAND_FROM_BOTTOM -> fadeIn(animationSpec = tween(durationMillis = 150))
            }
        },
        popExitTransition = {
            when (screenAnim) {
                ScreenAnim.HORIZONTAL_SLIDE -> {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = tween(durationMillis = 400)
                    )
                }

                ScreenAnim.VERTICAL_SLIDE -> {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(durationMillis = 400)
                    )
                }

                ScreenAnim.FADE_IN_OUT -> {
                    fadeOut()
                }

                ScreenAnim.EXPAND_FROM_BOTTOM -> {
                    scaleOut(
                        targetScale = 0.9f,
                        transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 1f),
                        animationSpec = tween(durationMillis = 300)
                    ) + fadeOut(animationSpec = tween(durationMillis = 300))
                }
            }
        },
        typeMap = typeMap,
        content = content
    )
}