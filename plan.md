# Dino Squad implementation plan

## Scope
Upgrade the existing Android starter into a usable Dino Squad launcher centered on the supplied `dino(1).zip` ROM set only.

## Design
- **Neon arcade cabinet** (0.07): dark navy, electric blue, and cartridge-gold accents with compact full-screen panels.
- **Retro terminal** (0.06): monospaced diagnostic-first UI with scanlines and green phosphor.
- **Saturday-morning action** (0.08): saturated red/yellow comic panels and oversized character buttons.

Chosen: **Neon arcade cabinet**. Core principles are clear mode separation, tactile controls, immediate feedback, and safe local ownership of the supplied ROM. The solo fallback is a tiny playable training run; ROM play delegates the exact bundled set to an installed Android emulator. Supabase stores room membership and status only; authoritative arcade emulation remains local to the emulator.

## Structure
- `app/src/main/java/.../MainActivity.java`: menu, solo fallback, supplied-ROM launcher, settings, and room UI.
- `app/src/main/java/.../SupabaseClient.java`: small Android REST client for Supabase RPC/table calls.
- `app/src/main/assets/dino(1).zip`: user-supplied ROM archive, the only ROM exposed by the app.
- Supabase `dino_rooms` plus RPCs: create/join/start room lifecycle.

## Constraints
The app does not redistribute additional ROMs or pretend that a cloud room synchronizes MAME CPU state. The online flow is a working room/lobby foundation; actual arcade input synchronization requires a separately deployed emulator-compatible authoritative service.
