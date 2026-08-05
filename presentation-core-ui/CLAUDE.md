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

### Android TV
The module includes primitives that adapt the touch-first design system for D-pad-driven Android TV without altering the mobile experience:
*   **`Modifier.tvFocusRing(interactionSource, shape)`** (`@since 1.2.0`): Draws a brand-colored focus ring around a component when it is D-pad focused on Android TV. D-pad users need a visible cue for which element the remote points at (touch UI's ripple only fires on press). The modifier reads focus from the component's own `interactionSource` (shared with its `clickable`/`toggleable`, not duplicated) and paints a `shape`-matched border in `Theme.color.brand` while focused — but **only** when `LocalFormFactor.current == FormFactor.TV`. On mobile the ring stays transparent, so touch UI is visually unchanged. Apply it early in the modifier chain (before `clip`/`background`) so the ring hugs the component's real silhouette (squircle, pill, etc.). Located in `source/kit/core/focus/TvFocus.kt`.
*   **TV launcher banner**: `tv_banner.png` is provided across `res/drawable-{mdpi,hdpi,xhdpi,xxhdpi,xxxhdpi}/` — the banner Android TV shows on its home-screen launcher row.

D-pad focus support is now wired **kit-wide** so a remote user can reach and operate every interactive primitive; each treatment is gated to TV (or the `Theme.is10Foot` 10-foot token multiplier) and is a no-op on touch:
*   **Buttons** (`PrimaryButton`, `SecondaryButton`, `TextButton`, `IconButton`): each `ButtonColor` now resolves a `ButtonInteractionState.FOCUSED` bit with the same treatment as `PRESSED` (brand border / brand-variant fill), so the D-pad-focused button reads exactly like a pressed one. Because touch never raises `FOCUSED`, the mobile appearance is unchanged.
*   **`Modifier.tvFocusRing` applied across the kit**: beyond ad-hoc use, the ring is now attached to `SquircleCard` (hence every `BaseListItem`-derived row), `Toggle` (pill outline, for standalone toggles), the `ValueStepper` `-`/`+` buttons, and `SimpleApplicationListItem`.
*   **`SquircleCard.onClick` is now nullable** (default `null`): when the card wraps its own interactive content (e.g. a `TextInputListItem` text field), pass `null` so the card stays transparent to input and the inner control — not the card — receives focus and taps. Passing a handler keeps the whole card clickable/focusable as before.
*   **`ValueStepper`** responds to D-pad SELECT / center / Enter: a `KeyDown` presses the button and `KeyUp` releases it, reusing the exact touch long-press auto-repeat loop, so a held key accelerates through large ranges just like a touch long-press.
*   **List items are D-pad aware**: `ToggleListItem` sets the whole row as a single interactive target (`onClick` flips the toggle from anywhere on the row, not just the switch); `TextInputListItem` tracks its own focus via `onFocusChanged` so dismissing the IME only clears *its* focus and never yanks D-pad focus off other rows; `SimpleApplicationListItem` scales its container by `TEN_FOOT_SCALE` on 10-foot layouts and shows a focus ring so the shortcut matches the (scaled) control buttons and reads as reachable.
*   **Drawers**: `BlurredCustomSideDrawerOverlay` and `BlurredCustomBottomDrawerOverlay` scale their panel (width / height) by `TEN_FOOT_SCALE` on 10-foot layouts so the enlarged icon buttons don't overflow, and on TV a `BackHandler` maps the remote's BACK key to `onDismiss` (there is no scrim tap or swipe with a remote); both are gated to TV so the kiosk's global BACK block on mobile is untouched.

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
