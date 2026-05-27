package common.core.core.execute

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/**
 * Executes a suspendable [request] within the given [scope] and [context].
 *
 * Provides optional hooks for [loading] state, [result] handling, and [errorBlock].
 * Supports a [debounce] delay before execution.
 *
 * @param T The type of the result.
 * @param context The [CoroutineContext] to run the request on. Defaults to [Dispatchers.Default].
 * @param scope The [CoroutineScope] in which to launch the coroutine.
 * @param debounce Optional delay in milliseconds before starting the request.
 * @param loading Callback triggered with `true` before the request starts and `false` after it completes or fails.
 * @param result Callback triggered with the successful result of the [request].
 * @param errorBlock Callback triggered when an exception occurs during execution.
 * @param request The suspendable operation to perform.
 * @return The [Job] representing the launched coroutine.
 *
 * @see executeResult
 * @since 0.0.1
 */
public fun <T> executeCoroutine(
    context: CoroutineContext = Dispatchers.Default,
    scope: CoroutineScope,
    debounce: Long? = null,
    loading: ((Boolean) -> Unit)? = null,
    result: ((T?) -> Unit)? = null,
    errorBlock: ((Throwable) -> Unit)? = null,
    request: suspend CoroutineScope.() -> T?,
): Job {
    return scope.launch {
        try {
            debounce?.let {
                delay(it)
            }
            loading?.invoke(true)
            val response = withContext(context) { request() }
            result?.invoke(response)
        } catch (e: Throwable) {
            errorBlock?.invoke(e)
        } finally {
            loading?.invoke(false)
        }
    }
}

/**
 * Executes a suspendable [request] that returns a [Result] within the given [scope] and [context].
 *
 * Similar to [executeCoroutine], but specifically designed for operations returning [Result].
 * Handles successful and failed results via [result] and [errorBlock] respectively.
 *
 * @param T The type of the successful result value.
 * @param context The [CoroutineContext] to run the request on. Defaults to [Dispatchers.Default].
 * @param scope The [CoroutineScope] in which to launch the coroutine.
 * @param debounce Optional delay in milliseconds before starting the request.
 * @param loading Callback triggered with `true` before the request starts and `false` after it completes or fails.
 * @param result Callback triggered with the value of a successful [Result].
 * @param errorBlock Callback triggered when the [Result] is a failure or an exception occurs.
 * @param request The suspendable operation returning a [Result].
 * @return The [Job] representing the launched coroutine.
 *
 * @see executeCoroutine
 * @since 0.0.1
 */
public fun <T> executeResult(
    context: CoroutineContext = Dispatchers.Default,
    scope: CoroutineScope,
    debounce: Long? = null,
    loading: ((Boolean) -> Unit)? = null,
    result: ((T?) -> Unit)? = null,
    errorBlock: ((Throwable) -> Unit)? = null,
    request: suspend CoroutineScope.() -> Result<T>?,
): Job {
    return scope.launch {
        try {
            debounce?.let {
                delay(it)
            }
            loading?.invoke(true)
            val monad = withContext(context) { request() }
            monad?.fold(
                onSuccess = { result?.invoke(it) },
                onFailure = { errorBlock?.invoke(it) },
            )
        } catch (e: Throwable) {
            errorBlock?.invoke(e)
        } finally {
            loading?.invoke(false)
        }
    }
}
