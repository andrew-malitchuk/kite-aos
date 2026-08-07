# presentation-feature-onboarding

This module provides the "First Run" experience for the Kite application.

## Features

*   **Setup Wizard**: A fluid, animated 4-step guide:
    1.  **Welcome**: Branding and introduction.
    2.  **Permissions**: Interactive toggles for system-level access.
    3.  **Configuration**: Direct entry of Dashboard and Whitelist URLs with validation.
    4.  **Final**: Completion confirmation.
*   **Permissions Management**: Robust handling of complex Android permissions including Device Administration and System Settings modification.
*   **Visual Transitions**: Uses `AnimatedCookieShape` and `AnimationSequenceHost` for a modern, high-quality feel.
*   **URL Validation**: Prevents completing setup until a valid dashboard URL is provided.

## Workflow

The onboarding flow is designed to be blocking. The application's host activity checks the `OnboardingStatus` via the domain layer and redirects the user to this module if the setup is not yet complete. 

Once the user provides the necessary URLs and grants permissions, the module triggers a `GoToMainEffect`, and the status is marked as complete in the persistent storage.

## Components

### WizardPager
A generic setup component that manages a list of `WizardPageData`. It handles:
*   Horizontal navigation.
*   Progress indication.
*   Conditional "Next" button enabling (e.g., requires permissions to be granted).
*   D-pad focus handoff on Android TV (see below).

### AnimatedCookieShape
A decorative morphing star shape that rotates and changes its vertex count continuously, used as a signature background element.

## Android TV

_(@since 1.2.0)_ The onboarding flow adapts to leanback / TV hardware, which often has no microphone and may run a stripped ROM missing some system-settings screens. The form factor is detected via `LocalFormFactor`.

*   **Microphone step skipped on TV**: The `RECORD_AUDIO` permission (needed by the WebRTC camera stack) is removed from the "all permissions granted" check on TV, so a mic-less TV box can still finish setup. The camera permission remains required, and the audio toggle row is still displayed.
*   **Conditional permission rows**: The overlay, device-admin, and write-settings rows appear only if the device actually exposes the corresponding system-settings screen. Unavailable rows are hidden and dropped from the completion requirement, and launching a missing screen fails gracefully with a snackbar instead of crashing.
*   **D-pad focus**: On TV, `WizardPager` moves D-pad focus into the current slide's first input (permission toggle or URL field) whenever the page changes, using a `FocusRequester` and `focusGroup()` with a short delay so it does not race the slide animation. This is a no-op on touch devices.
