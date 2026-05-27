# Module: common-core

## Overview
This module contains fundamental, pure-Kotlin utilities and base abstractions used throughout the entire project. It is the base layer for all other modules, providing standard patterns for asynchronous operations and data transformation.

## Responsibilities
*   **Asynchronous Execution**: Provides standardized helpers for executing coroutines with support for loading states, error handling, and debouncing.
*   **Data Mapping**: Defines generic interfaces for one-way and two-way mapping between data models.

## Architecture
This is a pure Kotlin module that sits at the foundation of the project's architecture. It contains no Android dependencies, making it highly portable and easily testable.

### Key Components
*   **`common.core.core.execute.Execute`**: Utility functions like `executeCoroutine` and `executeResult` for consistent execution of business logic with lifecycle/state management hooks.
*   **`common.core.core.mapper.Mapper`**: A functional interface for mapping one type to another.

## Dependencies
*   **Kotlin Coroutines**: Used for building the execution utilities.
