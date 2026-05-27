# Module: presentation-core-ui

## Overview
This module is the foundational UI library and design system for the entire application. It centralizes all reusable UI components (Atoms, Molecules, Organisms), custom shapes, branding assets (icons), and advanced animation utilities to ensure visual consistency across all feature modules.

## Responsibilities
*   **Design System Implementation**: Provides a consistent set of primitive components like buttons, toggles, and inputs that strictly adhere to the project's visual identity.
*   **Custom Shapes**: Implements complex geometric shapes, most notably the **Squircle** (standard and gentle variants) with adjustable smoothing, which is a signature element of the app's branding.
*   **Branding & Identity**: Stores and provides all custom `ImageVector` icons and the application's splash screen logic (`SplashApi`), which integrates Compose animations into the native Android startup flow.
*   **Advanced Animation Utilities**:
    *   `AnimatedSequenceHost`: Choreographs sequential "entry" animations for UI elements.
    *   `CircularReveal`: Provides smooth, geometry-based transitions for theme switching.
    *   `HorizontalAnimatedDivider`: Visual separators with alpha-based entry.
*   **Layout & Feedback Primitives**:
    *   `SafeContainer`: A standardized `Scaffold` wrapper that handles system bars and IME (keyboard) insets automatically.
    *   `StackedSnackbarHost`: A notification system supporting multiple stacked messages with swipe-to-dismiss functionality.
    *   `BlurredCustomDrawer`: Highly customizable side and bottom drawers with glassmorphism effects (using the Haze library).

## Architecture
The module is organized using a simplified **Atomic Design** philosophy:
*   **Core**: Low-level configurations (Animations, Theme extensions).
*   **Atoms**: Single-purpose primitives (Buttons, Icons, Dividers, Shapes).
*   **Molecules**: Combinations of atoms forming functional units (List Items, Headers, Steppers).
*   **Organisms**: Complex, stateful UI systems (Animated Sequences, Snackbar Host).

### Component Design Pattern
Most components follow a "Provider" pattern where their appearance (colors, sizes, typography) is defined by interfaces (e.g., `ButtonColor`, `ButtonSize`). This allows for high reusability and easy swapping of "T-shirt sizes" or styles without duplicating logic.

## Key Components
*   **Button System**: A robust kit comprising `PrimaryButton`, `SecondaryButton`, `TextButton`, and `IconButton`, all supporting state-aware animations and Lottie-based loading indicators.
*   **SquircleShape**: A custom `CornerBasedShape` implementation that provides smoother transitions than standard rounded corners.
*   **AutoSizeText**: A specialized text component that dynamically scales font size to fit its container's constraints.
*   **Haze Overlays**: Drawers and dialogs that utilize the Haze library for real-time background blurring.

## Dependencies
*   **`presentation-core-styling`**: For access to the global `Theme` object (colors, spacing, typography).
*   **Lottie Compose**: For vector-based loading animations.
*   **Haze**: For GPU-accelerated blur effects.
*   **AndroidX SplashScreen**: For the core bootstrapping logic.
