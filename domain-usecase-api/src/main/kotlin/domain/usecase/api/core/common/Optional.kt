package domain.usecase.api.core.common

/**
 * Represents the result of an operation that returns no specific value (Unit), but can fail with a [domain.core.core.monad.Failure].
 * This is a typealias for [Result<Unit>].
 *
 * @see domain.core.core.monad.Failure
 * @since 0.0.1
 */
public typealias Optional = Result<Unit>
