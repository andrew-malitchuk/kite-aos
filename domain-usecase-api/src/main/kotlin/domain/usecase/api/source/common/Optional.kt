package domain.usecase.api.source.common

/**
 * Represents the result of an operation that returns no specific value (Unit), but can fail with a [domain.core.source.monad.Failure].
 * This is a typealias for [Result<Unit>].
 *
 * @see domain.core.source.monad.Failure
 * @since 0.0.1
 */
public typealias Optional = Result<Unit>
