package domain.usecase.api.source.usecase.configuration

/**
 * Use case for retrieving the auto-return kiosk setting.
 *
 * Returns `true` if the app should automatically return to the kiosk after leaving
 * to an external application, `false` otherwise.
 *
 * @since 0.0.4
 */
public interface GetAutoReturnUseCase {

    /**
     * Reads the auto-return flag from persistent storage.
     *
     * @return `Result.success(true)` if the kiosk should auto-return after leaving to an
     *   external app, `Result.success(false)` if disabled, or `Result.failure` with a
     *   `Failure` if the preference store is unavailable.
     */
    public suspend operator fun invoke(): Result<Boolean>
}
