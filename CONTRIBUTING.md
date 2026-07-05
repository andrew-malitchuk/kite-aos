# Contributing to Kite AOS

Thank you for your interest in contributing. This project started as a personal smart home tool and has grown through community feedback. Before investing time in a large change, open an issue first so we can align on scope and approach.

## Quick Start

1. Fork the repository and clone your fork.
2. Open in Android Studio Ladybug (or newer) with JDK 21.
3. Run `./gradlew build` to verify your environment is set up correctly.

## Branching

| Branch pattern | Purpose |
|---|---|
| `main` | Stable, released code |
| `release/*` | Release preparation — bug fixes only |
| `feat/*` | New features |
| `fix/*` | Bug fixes |
| `docs/*` | Documentation-only changes |

Target your PR at `main` unless you are fixing a regression in an active release branch.

## Code Style

Enforcement is automated — the build fails on violations:

```bash
./gradlew ktlintFormat   # auto-fix formatting
./gradlew detekt         # static analysis (maxIssues: 0)
./gradlew check          # runs both
```

Rules to know:
- 120-character line limit, 4-space indent, trailing commas allowed.
- All public declarations in pure Kotlin modules (`domain-*`, `data-core`, `common-core`) require explicit visibility modifiers and return types (`ExplicitApiMode.Strict`).
- `@Composable` functions are exempt from MagicNumber, LongMethod, and CyclomaticComplexity thresholds.

## Module Structure

Every new feature follows the same layered structure:

```
data-{feature}-api/       # Repository and DataSource interfaces (pure Kotlin)
data-{feature}-impl/      # Room / DataStore / network implementations (Android)
domain-usecase-api/       # UseCase interfaces — add new interfaces here
domain-usecase-impl/      # UseCase implementations — add new classes here
presentation-feature-{name}/
  source/{name}/
    {Name}Screen.kt       # Navigation entry — collectAsState + collectSideEffect
    {Name}Content.kt      # Stateless @Composable receiving state + callbacks
    {Name}ViewModel.kt    # ContainerHost<State, SideEffect>
    {Name}Contract.kt     # State + SideEffect
    {Name}Intent.kt       # Sealed user-action class
  di/
    PresentationFeature{Name}Module.kt  # @Module @ComponentScan
```

Apply the matching convention plugin in the module's `build.gradle.kts`:

| Plugin | Use when |
|---|---|
| `dev.yahk.convention.feature` | Android library with Compose |
| `dev.yahk.convention.library` | Pure Kotlin/JVM |
| `dev.yahk.convention.di.android` | Koin + KSP, Android module |
| `dev.yahk.convention.di` | Koin + KSP, pure Kotlin module |

## Adding a New Feature — Checklist

- [ ] Discuss the feature in a GitHub issue before starting.
- [ ] Create API interfaces in the appropriate `-api` module.
- [ ] Implement in the corresponding `-impl` module.
- [ ] Wire DI: add `@Single(binds = [Interface::class])` and register the module in `AppModule.kt`.
- [ ] Add navigation destination to `Destination` sealed class if the feature has a screen.
- [ ] Write a module-level `README.md` (see existing modules for the template).
- [ ] Run `./gradlew check` and fix all issues before opening a PR.

## Commit Messages

Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
type(scope): short description

# Examples
feat(screensaver): add clock overlay composable
fix(mqtt): reconnect on broker timeout
docs(readme): update feature table for v1.1.0
refactor(motion): extract luma threshold to constant
```

Types: `feat`, `fix`, `docs`, `refactor`, `test`, `chore`, `perf`.

## Pull Request Process

1. Keep PRs focused — one feature or fix per PR.
2. Fill in the PR description: **what** changed and **why**.
3. Link the related GitHub issue (`Closes #123`).
4. Ensure `./gradlew check` passes locally before marking ready for review.
5. Documentation updates (module README or root docs) must be included in the same PR as the code change.

## Build Flavors

The project has two flavors:

| Flavor | Description |
|---|---|
| `foss` | No Google Mobile Services. Compatible with F-Droid. Uses GeckoView (Firefox engine). |
| `gms` | Includes Firebase Crashlytics. Uses both WebView and GeckoView. |

When adding a new dependency, check whether it pulls in GMS transitively — if so, scope it to the `gms` flavor only to keep the `foss` build clean.

## Reporting Bugs

Open a GitHub issue with:
- Device model and Android version.
- Kite AOS version (from Settings → About).
- Steps to reproduce.
- Expected vs. actual behavior.
- Logcat output if available.

For quick questions, join the [Telegram channel @kite_aos](https://t.me/kite_aos).

## License

By contributing you agree that your changes will be licensed under the [Apache 2.0 License](LICENSE).
