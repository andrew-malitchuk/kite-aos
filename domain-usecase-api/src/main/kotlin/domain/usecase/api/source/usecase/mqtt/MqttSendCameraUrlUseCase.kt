package domain.usecase.api.source.usecase.mqtt

import domain.usecase.api.source.common.Optional

public interface MqttSendCameraUrlUseCase {
    public suspend operator fun invoke(url: String): Optional
}
