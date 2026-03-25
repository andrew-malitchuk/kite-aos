package common.core.core.mapper

/**
 * A functional interface for mapping an input of type [I] to an output of type [O].
 *
 * @param I The input type.
 * @param O The output type.
 */
public fun interface Mapper<I, O> {
    /**
     * Maps the [input] to the output type [O].
     *
     * @param input The object to be mapped.
     * @return The mapped object.
     */
    public fun map(input: I): O
}
