# Project memory — v5, 2026-10-10

- Purpose: Independent Kotlin/JVM state holder, revision/cancellation guards and joined shutdown.
- Repository: https://github.com/lolpul/vpn-android-showcase, PUBLIC, main; review branch portfolio-review-2026-10-10. Use git log/PR for delivery SHA.
- Entry: README Review in 30 seconds → key implementation files/tests; existing architecture and deeper decisions retained.
- Run/test commands: README and [dated verification](verification.md), checked during this review.
- Verified: JDK21/Gradle8.13 compilation and 11 deterministic tests passed; no Android runtime/device tests.
- Limits: No Compose/AndroidX/VpnService/actual VPN; single-thread dispatcher confinement required.
- Changes: review navigation, honest maturity/validation wording, reproducible demonstration notes and this compact memory. Application algorithms/tests/workflows unchanged; 3D package description corrected to MVP.
- Existing public portfolio integration was accepted 2026-10-02; no site deployment or production/network change in this review.
- IP: no private original files/history, configuration, identifiers or working data transferred. New code/visibility/license/history changes require separate owner decision.
- Backup: central ignored career-materials/.backups/github-review-20261010-015431 manifest; published-doc rollback via ordinary revert; unrelated files preserved.
- Next: use these source/test paths for interviews; address documented integration/security/rights gaps through separate scoped work.
- Patch: [portfolio review](patches/2026-10-10-portfolio-review.md).
