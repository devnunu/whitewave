package co.kr.whitewave.presentation.util

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
            }
        },
        exitTransition = {
            when (screenAnim) {
                ScreenAnim.HORIZONTAL_SLIDE -> null
                ScreenAnim.VERTICAL_SLIDE -> null
                ScreenAnim.FADE_IN_OUT -> null
            }
        },
        popEnterTransition = {
            when (screenAnim) {
                ScreenAnim.HORIZONTAL_SLIDE -> null
                ScreenAnim.VERTICAL_SLIDE -> null
                ScreenAnim.FADE_IN_OUT -> null
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
            }
        },
        typeMap = typeMap,
        content = content
    )
}