package domain.usecase.impl.source.usecase.mqtt

import domain.core.source.model.CompanionTelemetryModel
import domain.core.source.monad.Failure
import domain.repository.api.source.repository.MqttRepository
import domain.usecase.api.source.common.Optional
import domain.usecase.api.source.usecase.mqtt.MqttSendCompanionTelemetryUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [MqttSendCompanionTelemetryUseCase] using [MqttRepository].
 *
 * @see MqttSendCompanionTelemetryUseCase
 * @since 2.1.0
 */
@Single(binds = [MqttSendCompanionTelemetryUseCase::class])
internal class MqttSendCompanionTelemetryUseCaseImpl(
    private val mqttRepository: MqttRepository,
) : MqttSendCompanionTelemetryUseCase {
    override suspend operator fun invoke(telemetry: CompanionTelemetryModel): Optional = resultLauncher(
        errorMapper = Failure.Technical::Network,
    ) {
        mqttRepository.sendCompanionTelemetry(telemetry)
    }
}
