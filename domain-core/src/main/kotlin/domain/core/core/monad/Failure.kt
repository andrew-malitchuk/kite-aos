package domain.core.core.monad

/**
 * Base sealed class for representing all possible failures within the domain layer.
 * Extends [Throwable] to allow integration with coroutines and other standard error handling.
 */
public sealed class Failure : Throwable() {
    /**
     * Represents technical failures that occur in the infrastructure layer.
     */
    public sealed class Technical : Failure() {
        /** Failure during database operations. */
        public data class Database(override val cause: Throwable) : Technical()

        /** Failure interacting with the Android platform. */
        public data class Platform(override val cause: Throwable) : Technical()

        /** Failure during network communication. */
        public data class Network(override val cause: Throwable) : Technical()

        /** Failure during preference storage operations. */
        public data class Preference(override val cause: Throwable) : Technical()
    }

    /**
     * Represents logical failures that occur in the business domain.
     */
    public sealed class Logic : Failure() {
        /** Triggered when a requested resource is not found. */
        public data object NotFound : Logic()

        /** Triggered when a business rule is violated. */
        public data class Business(public override val message: String) : Logic()
    }
}
