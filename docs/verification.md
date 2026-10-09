# Verification — 2026-10-10

Windows, JDK 21.0.10, official Gradle 8.13 wrapper: `gradlew.bat --no-daemon test` compiled the module and passed **11 tests**. The documented Linux equivalent is `./gradlew --no-daemon test`; [Actions](https://github.com/lolpul/vpn-android-showcase/actions/workflows/kotlin.yml) runs it.

The deterministic test harness is the runnable demonstration; the project has no application entry point or UI. No separate lint/formatter task is configured. Kotlin compilation and changed-document whitespace were checked. Android build, emulator, Compose, process death, backend integration and VPN connectivity were not exercised.
