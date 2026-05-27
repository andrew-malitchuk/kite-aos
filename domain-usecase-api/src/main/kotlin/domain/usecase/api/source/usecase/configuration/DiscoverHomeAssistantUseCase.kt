package domain.usecase.api.source.usecase.configuration

import domain.core.source.model.HomeAssistantInstanceModel

/**
 * Use case for discovering Home Assistant instances on the local network.
 *
 * Performs mDNS hostname probes followed by a parallel subnet scan.
 * The total operation is capped at 12 seconds.
 *
 * @since 0.0.5
 */
public interface DiscoverHomeAssistantUseCase {

    /**
     * Scans the local network for Home Assistant instances.
     *
     * @return [Result] wrapping a list of [HomeAssistantInstanceModel]. The list is empty
     *   if no instances are found. Returns a failure on unexpected errors.
     */
    public suspend operator fun invoke(): Result<List<HomeAssistantInstanceModel>>
}
