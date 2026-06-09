# AGENTS.md — rules (always on)

This is an interview build: one day, one developer, demo at the end.
Goal: a polished, working product slice with a maintainable codebase.
Prefer simple, working software. Do not over-engineer.

## Stack
Kotlin, Jetpack Compose, Material 3, ViewModel, StateFlow, Navigation Compose, Room.
Do NOT add: Hilt, Retrofit, WorkManager, auth, extra Gradle modules.
Interface + `Default*` impl at the repository/network boundary is enough —
no use-case classes, no Clean Architecture layering.
No new dependencies without asking and explaining why.

## Architecture
- Package-by-feature. UI → ViewModel → repository interface → Room / fake network.
  Composables never touch DAOs or the network directly.
- Immutable `UiState` data classes exposed via StateFlow.
  `stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ...)`.
  Collect in UI with `collectAsStateWithLifecycle()`.
- State that must survive process death goes in `SavedStateHandle` or Room —
  never only in ViewModel memory.
- Inject `CoroutineDispatcher`s through constructors; never hardcode
  `Dispatchers.*` inside logic classes.
- Errors are sealed UI states (Loading / Content / Error / Empty) with retry
  where it makes sense — never uncaught exceptions reaching the UI.
- Keep composables small; no business logic in composables.
- Keep fake/seed data deterministic.

## State survival & resilience (non-negotiable)
- Every screen must survive rotation AND process death (relaunch from recents
  after the OS kills the process):
  - Transient UI input — text fields, selected radio option, scroll/tab
    position — lives in `rememberSaveable` or the ViewModel's
    `SavedStateHandle`. Never plain `remember {}` or ViewModel memory alone.
  - Committed data lives in Room.
- ViewModels read nav args and keep in-progress user input (draft text,
  selections) via `SavedStateHandle`.
- Failure paths: repository/network errors surface as an Error state with
  retry. A failed submit must preserve the user's input and show the error —
  never lose what they typed, never crash.
- Every slice's manual-verification checklist must include: (a) rotate
  mid-flow, (b) background + process kill + relaunch from recents, (c) the
  failure path — stating exactly what is expected to survive in each.

## UI source of truth
For any UI task: **Read the mock PNG for the target screen(s) first** — it is
not auto-injected. Precedence: mock PNG > SPEC.md behavior > task notes.
Never build layout from text bullets alone.
Reuse shared components in `ui/components/` and `ui/theme/`.

## Workflow
- One slice per task, scoped in SPEC.md. Implement **only** that slice.
  Do not refactor outside it.
- I start a fresh agent chat per slice; assume no memory of prior chats —
  read the relevant SPEC.md section before coding.
- Do not commit unless I explicitly ask.

## Definition of done (every slice)
1. `./gradlew assembleDebug` and `./gradlew testDebugUnitTest` pass.
2. The unit tests listed for the slice in SPEC.md are implemented.
   Tests target server rules, repository behavior, and ViewModel state
   transitions — no Compose UI tests.
3. Every screen state (loading / content / error / empty) has a `@Preview`.
4. Self-review the full diff against SPEC.md and the mock before reporting
   done. List every deviation explicitly. Mock mismatches are must-fix
   unless documented as intentional.
5. End with a short **manual verification checklist**: numbered steps for me
   to test in the running app.
