package co.kr.whitewave.utils

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.Navigator

// 콜백을 관리하는 Registry
object NavCallbackRegistry {
    private val callbackMap = mutableMapOf<String, Any>()

    fun <T> registerCallback(key: String, callback: NavResultCallback<T>) {
        callbackMap[key] = callback
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getCallback(key: String): NavResultCallback<T>? {
        val callBack = callbackMap[key] as? NavResultCallback<T>
        unregisterCallback(key)
        return callBack
    }

    private fun unregisterCallback(key: String) {
        callbackMap.remove(key)
    }
}

/**
 * The navigation result callback between two call screens.
 */
typealias NavResultCallback<T> = (T) -> Unit

// Generate a unique key based on the route or screen ID
private fun generateNavResultCallbackKey(route: String?): String {
    return "NavResultCallbackKey_$route"
}

/**
 * Set the navigation result callback on calling screen.
 *
 * @param callback The navigation result callback.
 */
fun <T> NavController.setNavResultCallback(callback: NavResultCallback<T>) {
    val currentRouteId = currentBackStackEntry?.destination?.route
    val key = generateNavResultCallbackKey(currentRouteId)
    NavCallbackRegistry.registerCallback(key, callback)
}

/**
 *  Get the navigation result callback on called screen.
 *
 * @return The navigation result callback if the previous backstack entry exists
 */
fun <T> NavController.getNavResultCallback(): NavResultCallback<T>? {
    val previousRouteId = previousBackStackEntry?.destination?.route
    return NavCallbackRegistry.getCallback(generateNavResultCallbackKey(previousRouteId))
}

/**
 *  Attempts to pop the controller's back stack and returns the result.
 *
 * @param result the navigation result
 */
fun <T> NavController.popBackStackWithResult(result: T) {
    val callback = getNavResultCallback<T>()
    if (popBackStack()) {
        callback?.invoke(result)
    }
}

/**
 * Navigate to a route in the current NavGraph. If an invalid route is given, an
 * [IllegalArgumentException] will be thrown.
 *
 * @param route route for the destination
 * @param navResultCallback the navigation result callback
 * @param navOptions special options for this navigation operation
 * @param navigatorExtras extras to pass to the [Navigator]
 *
 * @throws IllegalArgumentException if the given route is invalid
 */
fun <T> NavController.navigateForResult(
    route: Any,
    navResultCallback: NavResultCallback<T>,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    setNavResultCallback(navResultCallback)
    navigate(route, navOptions, navigatorExtras)
}

/**
 * Navigate to a route in the current NavGraph. If an invalid route is given, an
 * [IllegalArgumentException] will be thrown.
 *
 * @param route route for the destination
 * @param navResultCallback the navigation result callback
 * @param builder DSL for constructing a new [NavOptions]
 *
 * @throws IllegalArgumentException if the given route is invalid
 */
fun <T> NavController.navigateForResult(
    route: Any,
    navResultCallback: NavResultCallback<T>,
    builder: NavOptionsBuilder.() -> Unit
) {
    setNavResultCallback(navResultCallback)
    navigate(route, builder)
}
