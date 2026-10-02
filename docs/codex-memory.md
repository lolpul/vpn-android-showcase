# Kotlin showcase memory - v4

- Purpose: independent Kotlin/JVM state holder demonstrating Android architecture concerns without Android runtime.
- Repository: https://github.com/lolpul/vpn-android-showcase. Private application remains private and under active development.
- Entry: examples/ui-state/src/main/kotlin/example/state/{ConnectionModel,ConnectionState,ConnectionRepository}.kt; tests adjacent under src/test.
- Contracts: single UI-dispatcher confinement; authoritative lookup determines preparation; cached hints never imply connected; explicit operation acknowledgement determines Connected.
- Lifetime: replace/cancel old work; revision and active-job guards reject stale results; cancellation rethrows; shutdown cancels and joins all owned workers. No actual connection control.
- Build: JDK 21, Gradle 8.13 wrapper with official checksum, Kotlin 2.2.21, coroutines 1.10.2. Command: gradlew.bat --no-daemon test (Windows), ./gradlew --no-daemon test (Linux).
- Verified: all 11 JVM tests passed on Windows, including parent lifecycle cancellation. Public CI and publication acceptance follow scan/manual diff.
- Audit: observable state, repository boundaries, startup reconciliation, expiry and restored-ready distinction confirmed. Private stale README corrected separately; no private build or device validation claimed.
- No private source/history/configuration imported. Local source receipts and backups are ignored.
- Docs: [scope](spec.md), [interview notes](interview-notes.md), [patch](patches/2026-10-02-kotlin-state.md).
- Next: verify final tests, confidentiality review, push and exact-revision Actions, then website/profile code evidence.
