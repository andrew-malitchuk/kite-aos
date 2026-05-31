package domain.core.source.model

import domain.core.source.model.base.Model

public sealed class ScreenStateModel : Model {
    public data object Active : ScreenStateModel()
    public data object Screensaver : ScreenStateModel()
}
