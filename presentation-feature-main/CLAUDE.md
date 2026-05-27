# Module: presentation-feature-main

## Overview
This feature module contains the main screen of the application, serving as the primary kiosk interface. It is responsible for hosting the central dashboard (web-based) and providing seamless navigation to other allowed applications and system settings.

## Responsibilities
*   **Web Kiosk Hosting**: Manages a full-screen `WebView` optimized for long-running dashboard displays (JS enabled, DOM storage, hardware acceleration).
*   **Navigation & Application Hub**: Provides a hidden "Control Drawer" that allows users to switch between allowed system applications, reload the kiosk, or jump to settings.
*   **Motion-Activated UI**: Automatically manages the visibility of the "Open Drawer" FAB (Floating Action Button) based on motion detection events from the `MotionService`.
*   **Domain Security**: Implements a URL whitelist to ensure the kiosk remains locked to authorized domains and prevents unauthorized browsing.
*   **Immersive Experience**: Enforces a full-screen interface by hiding system bars and managing activity-level immersive settings.

## Architecture
The module follows the **MVI (Model-View-Intent)** pattern using the **Orbit** framework:
*   **`MainState`**: Holds the current kiosk configuration (URL, whitelist, dock position) and transient UI state (FAB visibility, loading status).
*   **`MainIntent`**: Dispatched by the UI to trigger loads, reloads, or navigation to settings/apps.
*   **`MainSideEffect`**: Handles one-off events such as opening external applications or displaying errors via the snackbar system.
*   **`MainViewModel`**: The core coordinator. It orchestrates initial data loading and subscribes to the motion detection flow to toggle the navigation UI.

## Key Components
*   **`KioskWebView`**: A specialized `WebView` wrapper that handles state restoration, custom loading indicators (shimmer), and navigation filtering.
*   **`ControlDrawer`**: A dual-layout component that adapts to the configured dock position (Bottom or Left), providing navigation controls and app shortcuts.
*   **`SideBar`**: A glassmorphic (blurred) overlay implementation using the **Haze** library, ensuring the drawer feels like a modern layer over the dashboard.
*   **Draggable FAB**: A motion-aware action button that users can reposition to avoid obscuring dashboard elements.

## Dependencies
*   **`presentation-core-ui`**: For design system atoms, molecules (like `SimpleApplicationListItem`), and shimmer effects.
*   **`presentation-core-platform`**: For direct integration with the `MotionService`.
*   **`domain-usecase-api`**: For accessing configuration settings and hardware sensor observations.
*   **Haze**: For modern GPU-accelerated blur effects.
