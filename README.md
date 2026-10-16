# Dino Squad — Android arcade launcher

Dino Squad is an Android 7+ launcher built around the supplied `dino(1).zip` ROM set.

## What works

- **Play Solo**: an offline playable training run with touch and gamepad/keyboard jump controls.
- **Play Supplied ROM**: packages only the supplied ROM archive and launches the embedded open-source MAME4droid 0.139u1 emulator inside Dino Squad. The app does not allow selecting or bundling other ROMs.
- **Multiplayer rooms**: Supabase-backed create/join/start lobby flow with public or password-protected rooms, player limits, refresh, and copyable room codes.
- **Support / Emulator Link**: opens the requested smart link: https://asiafilm.org/4/90cc2ce73f5daf47690eaa29106accba

## Important emulator note

Dino Squad embeds the open-source MAME4droid 0.139u1 Android emulator code and ARM native cores. The exact supplied ZIP is copied into app-private storage and launched by the internal emulator activity. GPL/MAME notices and the upstream source reference are included in the repository.

The cloud room service currently synchronizes the room/lobby state, not the original arcade machine's CPU/input state. Full online arcade synchronization requires a separately deployed authoritative emulator service.

## Build

```bash
gradle --no-daemon assembleDebug
```

GitHub Actions also builds `app-debug.apk` on pushes to `main` and on manual workflow runs.
