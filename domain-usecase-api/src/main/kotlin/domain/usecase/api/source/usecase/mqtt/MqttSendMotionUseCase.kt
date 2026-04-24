package domain.usecase.api.source.usecase.mqtt

import domain.usecase.api.core.common.Optional

/**
 * Use case for reporting a motion detection event to the MQTT broker.
 *
 * @since 0.0.1
 */
public interface MqttSendMotionUseCase {
    /**
     * Publishes the motion detection state to the MQTT broker.
     *
     * @param isDetected `true` if motion was detected, `false` otherwise.
     * @return An [Optional] result.
     */
    public suspend operator fun invoke(isDetected: Boolean): Optional
}
