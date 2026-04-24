package domain.core.core.monad

/**
 * Base sealed class for representing all possible failures within the domain layer.
 * Extends [Throwable] to allow integration with coroutines and other standard error handling.
 *
 * @since 0.0.1
 */
public sealed class Failure : Throwable() {
    /**
     * Represents technical failures that occur in the infrastructure layer.
     *
     * @since 0.0.1
     */
    public sealed class Technical : Failure() {
        /**
         * Failure during database operations.
         *
         * @property cause The underlying exception from the database layer.
         * @since 0.0.1
         */
        public data class Database(override val cause: Throwable) : Technical()

        /**
         * Failure interacting with the Android platform.
         *
         * @property cause The underlying platform exception.
         * @since 0.0.1
         */
        public data class Platform(override val cause: Throwable) : Technical()

        /**
         * Failure during network communication.
         *
         * @property cause The underlying network exception.
         * @since 0.0.1
         */
        public data class Network(override val cause: Throwable) : Technical()

        /**
         * Failure during preference storage operations.
         *
         * @property cause The underlying preference storage exception.
         * @since 0.0.1
         */
        public data class Preference(override val cause: Throwable) : Technical()
    }

    /**
     * Represents logical failures that occur in the business domain.
     *
     * @since 0.0.1
     */
    public sealed class Logic : Failure() {
        /**
         * Triggered when a requested resource is not found.
         *
         * @since 0.0.1
         */
        public data object NotFound : Logic()

        /**
         * Triggered when a business rule is violated.
         *
         * @property message Human-readable description of the violated rule.
         * @since 0.0.1
         */
        public data class Business(public override val message: String) : Logic()
    }
}
