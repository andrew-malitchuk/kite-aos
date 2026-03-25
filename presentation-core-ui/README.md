# presentation-core-ui

The UI foundation of the "Kite" project. This module acts as the central Design System and UI Kit.

## Features

*   **Atomic UI Components**: Reusable atoms (Buttons, Toggles) and molecules (Settings Items, Headers).
*   **Custom Shape Engine**: Advanced Squircle and Gentle Squircle implementations with adjustable smoothing.
*   **Advanced Animations**: Sequential animation host, circular reveal transitions, and interactive steppers.
*   **Glassmorphism**: Side and bottom drawers with real-time background blurring.
*   **Smart Feedback**: A stacked snackbar system that manages multiple notifications gracefully.
*   **Branding**: Centralized repository of custom icons and animated splash screen logic.

## Usage

### Buttons
All buttons support `isLoading` state, which automatically replaces content with a Lottie animation.

```kotlin
PrimaryButton(
    text = "Save Changes",
    onClick = { /* action */ },
    sizes = PrimaryButtonDefault.buttonSizeSet().buttonSize48(),
    isLoading = state.isSaving
)
```

### Squircle Shapes
Use `SquircleShape` for components to match the app's signature style.

```kotlin
Box(
    modifier = Modifier
        .clip(SquircleShape(Theme.size.sizeXL))
        .background(Theme.color.surface)
)
```

### Sequential Animations
Choreograph how elements appear on screen by wrapping them in a host.

```kotlin
AnimationSequenceHost { scope ->
    AnimatedItem(index = 0) { Title() }
    AnimatedItem(index = 1) { Description() }
    AnimatedItem(index = 2) { ActionButton() }
}
```

### Auto-scaling Text
For UIs where text must fit into a fixed-size container (like kiosks).

```kotlin
AutoSizeText(
    text = "Dynamic Title",
    minFontSize = 12.sp,
    maxLines = 1,
    style = Theme.typography.title
)
```

## Structure

*   `core/`: Internal configurations and helper extensions.
*   `source/kit/atom/`: Primitives like buttons, icons, and shapes.
*   `source/kit/molecule/`: Functional items like `LanguageListItem` or `ValueStepper`.
*   `source/kit/organism/`: Complex systems like the `StackedSnackbarHost`.
