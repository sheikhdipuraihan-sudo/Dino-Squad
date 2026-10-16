# Dino Squad — Android starter project

**Package:** `com.dinosquad.arcade`  
**Minimum Android:** 7.0 (API 24)  
**Target SDK:** 35  
**Current version:** 0.1.0

## Important status

This is a **menu and room-UI prototype**, not a finished playable game APK. It does not yet embed MAME, load the provided ROM, synchronize gameplay, or accept network room connections. The app intentionally explains these limitations in the UI. Do not advertise it as a functioning multiplayer game yet.

## Build an APK with GitHub Actions

1. Create a new GitHub repository and upload the contents of this project.
2. Open **Actions** and enable workflows if prompted.
3. Run **Build Dino Squad APK** (or push to `main`).
4. Download the `DinoSquad-debug-apk` artifact from the completed workflow.
5. Install `app-debug.apk` on an Android 7+ test device. Debug APKs are for testing, not Play Store release.

## Local build

Install Android Studio, JDK 17, Android SDK Platform 35 and Build Tools 35.0.0, then open this folder and run `gradle assembleDebug`.

## Next engineering steps

1. Integrate a compatible open-source arcade emulator and verify ROM-set compatibility.
2. Add a ROM picker using Android's Storage Access Framework.
3. Implement gamepad-to-emulator mapping.
4. Design a tested lockstep/input-sync protocol for the selected game; the original arcade game supports three players, so four network participants should not be assumed to mean four active in-game players.
5. Add host sockets, authentication, rate limits and explicit network permissions. Direct internet hosting cannot bypass CGNAT/firewalls automatically.
6. Verify GPL notices and source distribution requirements before redistributing a build containing MAME components.

No cloud service or database is configured in this starter.
