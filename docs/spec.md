# Independent Kotlin state example

## Problem and evidence

The private root README still describes a scaffold, while the active Gradle tree contains Compose screens, a ViewModel, repository and persistence boundaries, startup reconciliation, and unit tests. Read-only review confirms immutable observable state, coroutine ownership, cancellation propagation, authoritative startup lookup, stale-cache replacement, and a restored record being ready rather than already connected.

## Scope and acceptance stages

1. Correct only the private README's stale scope statement from tree/code evidence; describe tests as present, not newly executed. Preserve private implementation and configuration.
2. Independently write a pure Kotlin/JVM state holder, sealed UI states, repository contract, and in-memory hint boundary. No Android runtime, VPN engine, endpoint, protocol, credentials, advertising, or signing information. The state holder demonstrates logic that an Android ViewModel can own; it is not a Jetpack ViewModel subclass.
3. Test initial state, successful/failed lookup and operation, cancellation, expiry, restart/cache reconciliation, observable transition ordering, overlapping requests, and lifecycle shutdown. Use coroutine test scheduling and fakes, with no sleeps or real services.
4. Add pinned Gradle/Kotlin/coroutines configuration, generated official wrapper, README/interview notes, and CI. Verify Gradle tests, automatic/manual confidentiality review, diff checks, public push and exact-revision Actions.

Actions are confined to one UI dispatcher. Cancellation requests do not prove an external connection stopped; the sample exposes cancellation explicitly. Worker shutdown joins owned jobs. Cached hints are never presented as current connection authority. Public code is independently written; private source/history are not imported. Backups stay ignored; rollback is a normal revert.
