# presentation-core-ui

The UI foundation of the "Kite" project. This module acts as the central Design System and UI Kit.

## Features

*   **Atomic UI Components**: Reusable atoms (Buttons, Toggles) and molecules (Settings Items, Headers).
*   **Custom Shape Engine**: Advanced Squircle and Gentle Squircle implementations with adjustable smoothing.
*   **Advanced Animations**: Sequential animation host, circular reveal transitions, and interactive steppers.
*   **Glassmorphism**: Side and bottom drawers with real-time background blurring.
*   **Smart Feedback**: A stacked snackbar system that manages multiple notifications gracefully.
*   **Branding**: Centralized repository of custom icons and animated splash screen logic.
*   **Android TV**: A D-pad focus ring modifier and launcher banner assets for the TV form factor.

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

### Android TV

On Android TV, D-pad users need a visible cue for which element the remote points at (the touch ripple only fires on press). Apply `Modifier.tvFocusRing` early in the modifier chain — before `clip`/`background` — so the brand-colored ring hugs the component's real silhouette.

```kotlin
val interactionSource = remember { MutableInteractionSource() }

Box(
    modifier = Modifier
        .tvFocusRing(interactionSource, SquircleShape(Theme.size.sizeXL))
        .clip(SquircleShape(Theme.size.sizeXL))
        .clickable(interactionSource = interactionSource, indication = null) { /* action */ }
)
```

The ring paints in `Theme.color.brand` only when the form factor is `FormFactor.TV`; on mobile it stays transparent, leaving touch UI unchanged. Pass the same `interactionSource` used by the component's `clickable`/`toggleable` so focus state is shared, not duplicated.

The module also ships the TV launcher banner (`tv_banner.png`) across `res/drawable-{mdpi,hdpi,xhdpi,xxhdpi,xxxhdpi}/` for the Android TV home-screen launcher row.

D-pad focus support is applied **kit-wide** — a remote user can reach and operate every interactive primitive. Each treatment is gated to the TV form factor (or the `Theme.is10Foot` 10-foot token multiplier) and is a no-op on touch:

*   **All buttons** (`PrimaryButton`, `SecondaryButton`, `TextButton`, `IconButton`) respond to a `FOCUSED` interaction state with the same treatment as `PRESSED`, so the button the remote points at looks pressed. Touch never raises `FOCUSED`, so mobile is unchanged.
*   **The focus ring is attached across the kit** — `SquircleCard` (and every list-item row built on it), `Toggle`, the `ValueStepper` `-`/`+` buttons, and `SimpleApplicationListItem`.
*   **`SquircleCard.onClick` is nullable** (default `null`): pass `null` when the card wraps its own interactive content (e.g. a text field) so the inner control — not the card — receives focus and input.

```kotlin
// Non-clickable card wrapping its own interactive content.
SquircleCard(onClick = null) {
    BasicTextField(/* the inner field gets focus, not the card */)
}
```

*   **`ValueStepper`** responds to D-pad SELECT / center / Enter; a held key triggers the same accelerating auto-repeat as a touch long-press.
*   **List items are D-pad aware**: `ToggleListItem` flips from anywhere on the row (a single focus target), `TextInputListItem` tracks its own focus so dismissing the keyboard doesn't clear D-pad focus off other rows, and `SimpleApplicationListItem` scales by `TEN_FOOT_SCALE` on TV and shows a focus ring.
*   **Side and bottom drawers** scale their panel by `TEN_FOOT_SCALE` on TV, and on TV the remote's BACK key dismisses the drawer.

## Structure

*   `core/`: Internal configurations and helper extensions.
*   `source/kit/atom/`: Primitives like buttons, icons, and shapes.
*   `source/kit/molecule/`: Functional items like `LanguageListItem` or `ValueStepper`.
*   `source/kit/organism/`: Complex systems like the `StackedSnackbarHost`.
