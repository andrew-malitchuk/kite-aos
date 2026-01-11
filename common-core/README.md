# common-core

Fundamental utilities and base abstractions for the "kite-aos" project.

## Features

- **Coroutine Utilities**: Simplified execution of asynchronous tasks with built-in support for:
    - Loading indicators.
    - Error handling blocks.
    - Debouncing.
    - Context switching.
- **Mapping Abstractions**: Generic interfaces (`Mapper`) to enforce consistency in data
  transformation across layers.

## Usage

### Coroutines

```kotlin
executeCoroutine(
    scope = myScope,
    loading = { isLoading -> /* update state */ },
    result = { data -> /* handle data */ },
    errorBlock = { error -> /* handle error */ }
) {
    // Suspendable operation
}
```

### Mapping

Implement `Mapper<I, O>` for simple transformations.
