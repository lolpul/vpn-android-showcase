# Independent Kotlin state evidence

## Intent and changes

Add a small JVM module demonstrating generic Android architecture concerns: immutable observable state, repository isolation, startup authority checks, cached-hint reconciliation, expiry, cancellation, stale-result guards, and lifecycle ownership. Implementation/test review informed the concerns, but no original file, model, naming, interface, endpoint, protocol, configuration, identifier, integration, or Git history was transferred.

Three main Kotlin files total 123 lines including comments/spacing: sealed states (12), repository/preparation/cache boundaries (18), and state holder (93). The pure JVM holder is suitable for ViewModel ownership but is not an AndroidX ViewModel subclass. Ready is distinct from Connected; Cancelled does not assert external disconnection. Actions/callbacks require one UI dispatcher. StateFlow is latest-state storage, not an event log.

Build configuration pins JDK 21, Gradle 8.13, Kotlin 2.2.21 and coroutines 1.10.2. The official generated wrapper has an executable Unix script, distribution SHA-256, and independently verified wrapper JAR checksum. No private build configuration was imported. Root documentation, interview notes, and metadata explain the sample and its limits.

## Verification

`gradlew.bat --no-daemon test`: all 11 Windows JVM tests passed. They cover initial state, valid/expired/absent preparation, failure, explicit successful operation, duplicate-action rejection, cancellation cleanup, parent lifecycle cancellation, restart reconciliation, observer ordering, superseded lookup, and joining workers at shutdown. No sleeps, Android runtime, or services. Linux Actions acceptance follows publication.

Automatic inventory and manual review cover the full publication set. URL matches are public portfolio/GitHub, SVG namespace, official Gradle tooling and license references. Wrapper comments about shell tokens are benign; a version-number regex match was classified as a dependency version. No confidential value, private source path/URL, address, email, runtime identifier, or original import was found. Scans are not guarantees; private audit evidence and backups are ignored.

The stale private README was corrected separately from source-tree evidence. Private app/device/end-to-end tests were not rerun and are not implied by these JVM sample results.

## Rollback and remaining limits

Original public main: `49ca3890f7f075692c43cdeacc8472bf1013977b`. Timestamped ignored backups and manifest cover changed existing files and the vault note. Rollback through normal revert; no force push. Thread confinement and cooperative adapter cancellation are contracts; late-result guards do not undo external side effects. No physical, network, production-service or original application change was made by this public patch.

## Publication acceptance

Implementation `10c9b116a91cd499b22ee7a81da228cd2acdf9c1` pushed to public main. [Actions 37052359044](https://github.com/lolpul/vpn-android-showcase/actions/runs/37052359044) passed the Linux Gradle/JDK 21 build and all 11 JVM tests. Official wrapper checksums and whitespace checks pass.
