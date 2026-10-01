# VPN Android

**Native Android / Kotlin / Networking**
An engineering overview by [Elisey Kochura](https://github.com/lolpul).

## Project overview

A native Android networking application that connects a mobile interface with backend services and Android's VPN lifecycle. The work spans UI state, asynchronous requests, local state persistence and the boundaries between an application screen and a longer-lived platform service.

**Stage:** Android client under active development. The repository reviewed contains application and integration components; this overview does not claim a public app-store release or a fully released end-to-end service.

The engineering problem is to present understandable application state while remote operations and platform lifecycle events happen independently of what is currently on screen.

## My role

I develop the Kotlin application, its Compose interface, state management and backend integration. My work also covers persistence, startup/session reconciliation and the integration boundary around Android's VPN service.

## Engineering scope

- Kotlin and native Android development.
- Jetpack Compose UI and ViewModel-based state management.
- Coroutines and StateFlow for asynchronous work and observable state.
- Retrofit / OkHttp backend integration.
- DataStore for local state persistence.
- Android VpnService lifecycle integration.
- Unit tests around state transitions, repository behavior and startup coordination.

## Architecture

![Conceptual Android UI, state and domain logic, backend integration and platform-service responsibilities](docs/architecture.svg)

Compose screens observe state managed outside the UI. Repository boundaries connect application behavior to backend operations and persistence. Android platform-service responsibilities are kept separate from screen composition. The diagram omits API schemas, private endpoints and the networking runtime's implementation.

## Engineering decisions

| Problem | Decision | Reason / trade-off |
| --- | --- | --- |
| Network operations should not make screen composition responsible for business logic. | Keep observable state and actions in ViewModel/StateFlow boundaries. | Make state transitions explicit and testable; state ownership needs to stay consistent across screens. |
| Backend response and error details can otherwise leak through the interface. | Use repository abstractions and map remote results into application-level models. | Keep the UI focused on user-facing behavior; maintain a deliberate mapping layer. |
| Local state may no longer match the backend after an application restart. | Combine persisted metadata with startup/session reconciliation. | Restore context without treating cached state as current authority; startup includes additional asynchronous coordination. |

## Challenges

- Representing loading, success and error states without blocking the main thread.
- Coordinating local state with asynchronous backend results.
- Handling cancellation and Android lifecycle changes.
- Keeping VPN service ownership and cleanup outside UI composables.

## Validation approach

The private source includes unit tests around repositories, mappings, startup coordination and UI state. This overview documents implemented architectural boundaries, not device certification or production performance results.

## Screenshots

No reviewed screenshot set is included in this edition. The architecture illustration is conceptual; it is not a fabricated product screenshot.

## Source availability

The production source code is maintained in a private repository. This repository contains a public engineering overview only.

The Android project, application source, API contracts, runtime configuration, signing materials and private service details are not published here. This documentation does not grant a license to the closed-source application.

## Links

- Portfolio case study: pending website publication; planned route `/work/android-networking`.
- [Elisey Kochura on GitHub](https://github.com/lolpul).

*Documentation reviewed: 1 October 2026.*
