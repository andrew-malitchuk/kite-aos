# domain-usecase-impl

This module provides the concrete implementations of the Use Case interfaces defined in `domain-usecase-api`. It contains the actual business logic of the application, orchestrating calls to repositories and applying domain-specific rules.

## Features

- **Logic Execution**: Implements business rules by coordinating repository calls.
- **Unified Error Handling**: Utilizes `resultLauncher` to consistently wrap operations in `Result<T>` and map exceptions to domain `Failure` types.
- **Automated DI**: Uses Koin annotations for automatic dependency discovery and injection.

## Implementation Details

### Result Launcher
The `resultLauncher` is a utility used in implementations to catch platform-specific or technical exceptions and convert them into structured domain failures.

```kotlin
override suspend fun invoke(): Result<ThemeModel> = resultLauncher(
    errorMapper = Failure.Technical::Preference
) {
    repository.getTheme() ?: throw Failure.Logic.NotFound
}
```
