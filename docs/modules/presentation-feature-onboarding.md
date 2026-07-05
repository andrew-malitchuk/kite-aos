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

### AnimatedCookieShape
A decorative morphing star shape that rotates and changes its vertex count continuously, used as a signature background element.
