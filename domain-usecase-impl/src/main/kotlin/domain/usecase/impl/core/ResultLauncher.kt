package domain.usecase.impl.core

import domain.core.core.monad.Failure
import kotlin.coroutines.cancellation.CancellationException

/**
 * Executes a suspendable [block] of code and wraps its outcome in a [Result].
 *
 * This utility ensures consistent error handling across use cases by:
 * 1. Catching [Failure] exceptions and wrapping them in [Result.failure].
 * 2. Catching other [Throwable]s and mapping them to domain [Failure]s using the provided [errorMapper].
 * 3. Correctly rethrowing [CancellationException] to maintain coroutine cooperative cancellation.
 *
 * @param T The type of the successful result value.
 * @param errorMapper A lambda that converts a generic exception into a technical [Failure].
 * @param block The suspendable logic to execute.
 * @return A [Result] containing either the success value or a [Failure].
 */
internal suspend fun <T> resultLauncher(errorMapper: (Throwable) -> Failure, block: suspend () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Failure) {
        Result.failure(e)
    } catch (e: Throwable) {
        Result.failure(errorMapper(e))
    }
}
