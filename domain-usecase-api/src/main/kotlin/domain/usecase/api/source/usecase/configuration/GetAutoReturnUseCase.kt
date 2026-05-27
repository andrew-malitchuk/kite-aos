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
    public suspend operator fun invoke(): Result<Boolean>
}
