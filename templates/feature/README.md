# Feature template

Replace `__PACKAGE__`, `__FEATURE__`, and `Feature`. Place each Kotlin file in the directory matching its generated package. The UI files go in `app/view/<page>/ui/`, the presentation templates in `app/view/<page>/interaction/`, and reducer tests in the matching test package. Register the ViewModel in DI and the Route in the app navigation graph.

Place the page repository contract in `domain/port/` when needed, with its implementation in `data/repository/Default<Concept>Repository.kt` and a single Koin binding in `app/di/`. Domain models live in `domain/model/<concept>/`; pure policies live in `domain/rule/`; platform adapters live in `data/local/<capability>/`. Do not add `domain/usecase/` unless there is real cross-repository orchestration. Remove unused template actions and events.

Acceptance checklist:

- `XxxScreen.kt` assembles the page from state and callbacks. Place page-specific presentation components in `ui/part/` and name each file after its main Composable, using a role such as `Header`, `Row`, `Card`, `Dialog`, or `Content`. Shared page components go in `app/view/common/` and previews in `ui/preview/`.
- Route is the only Compose layer that obtains the ViewModel or performs platform effects.
- Reducer tests cover phase guards, success, failure, and one-time effects.
- ViewModel tests use fake constructor dependencies; no `GlobalContext` or Preview fallback.
- This repository has no ad layer; do not add ad hooks to feature templates.
