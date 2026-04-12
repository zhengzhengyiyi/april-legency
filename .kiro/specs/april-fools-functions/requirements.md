# Requirements: April Fools Functions

## Overview

Port all April Fools (20w14craftmine) `MineEffect` definitions from `mc-20w14craftmine-main/net/minecraft/class_11113.java` into the mod's `src/main/java/net/zhengzhengyiyi/mine/effect/class_11113.java`, without touching any other code in the mod.

---

## Scope

Only `class_11113.java` is modified. No other files are touched.

---

## Requirements

### R1 — Cave World effects (field_59249, field_59250, field_59251, field_59252)

Currently commented out. Must be uncommented and implemented using the mod's `DimensionSettingsBuilder` API.

- **field_59249** (`cave_world`): Sets base settings to `CAVES`, spawn locator to `SpawnLocator.CAVE`, and applies a dimension type modifier that makes the dimension have no sky (cave-like). Uses `setBaseSettings`, `setSpawnLocator`, and `addDimensionModifier`.
- **field_59250** (`shattered_blocks_world`): Modifies the noise router to use `DensityFunctions.createSurfaceNoiseRouter` with `isNoodle=true`. Requires `RegistryKeys.DENSITY_FUNCTION` and `RegistryKeys.NOISE_PARAMETERS` from the registry manager. Uses `modifySettings(b -> b.method_69807(...))`.
- **field_59251** (`grid_world`): Uses a custom `GeneratorFactory` (`setGeneratorFactory`) to create a `class_11075` chunk generator. Requires `class_11075` to exist in the mod.
- **field_59252** (`dark_cave_world`): Sets base settings to `CAVES`, spawn locator to `SpawnLocator.CAVE`. No dimension type modifier needed.

### R2 — Exit effects (field_59254, field_59255, field_59256)

Currently commented out because they depend on `field_59249` and `field_59252`.

- **field_59254** (`surface_exits`): Adds a global biome modifier setting exit type to `SURFACE`. Incompatible with `field_59249` and `field_59252`. Always unlocked.
- **field_59255** (`cave_exits`): Adds a global biome modifier setting exit type to `CAVE`. Requires deepslate mining condition and `field_59249` unlock.
- **field_59256** (`rare_surface_exits`): Adds a global biome modifier setting exit type to `RARE_SURFACE`. Requires `Items.EXIT_EYE` use condition. Incompatible with `field_59249` and `field_59252`.

These require `BiomeEffects.class_11060` (exit type enum) to be accessible in the mod. If it is not, these effects must remain commented out with a note.

### R3 — Piglins effect dimension type modifier (field_59233)

The original `piglins` effect calls `.method_70203(dimensionType -> dimensionType.method_69788())` to make the dimension piglin-safe. The mod's version omits this call. It must be added using `addDimensionModifier(dt -> /* piglin-safe copy of dt */)`.

This requires a way to create a modified `DimensionType`. If `DimensionType` is not mutable/copyable in the mod's API, this modifier is skipped and documented.

### R4 — Hoglins effect dimension type modifier (field_59236)

Same as R3 — the original calls `.method_70203(dimensionType -> dimensionType.method_69788())`. Must be added to `field_59236` if the API supports it.

### R5 — Eternal night / rain / lightning effects (field_59202, field_59203, field_59204)

Currently commented out because they depend on `field_59249`/`field_59252`. Once those are implemented (R1), these can be uncommented.

- **field_59202** (`eternal_night`): On mine enter, sets overworld time to 18000 and disables daylight cycle. On mine leave, re-enables daylight cycle. Requires `field_59249` or `field_59252` incompatibility.
- **field_59203** (`eternal_rain`): On mine enter, sets rain gradient to 1 and disables weather advance. On mine leave, resets. Requires `field_59202` unlock condition.
- **field_59204** (`eternal_lightning`): On mine enter, sets thunder and rain gradient to 1 and disables weather advance. On mine leave, resets. Requires `field_59203` unlock condition.

These use `serverWorld.method_69071()` (the `MinecraftServer`) to access the overworld and game rules. The mod equivalent is `serverWorld.getServer().getOverworld()` and `serverWorld.getServer().getGameRules()`.

### R6 — Insomniacs effect (field_59205)

Currently commented out because it depends on `field_59202`. Once `field_59202` is implemented, this can be uncommented.

- **field_59205** (`insomniacs`): On mine enter, sets each player's `TIME_SINCE_REST` stat to 72000 (causing phantoms to spawn). Requires `field_59202` unlock condition. Incompatible with `field_59249`/`field_59252`.

### R7 — Ultrawarm effect (field_59207)

Currently commented out. Requires:
- A dimension type modifier making the dimension ultrawarm (lava evaporates water).
- A global biome modifier increasing temperature and changing sky color.
- Setting default fluid to lava.
- Unlock condition: player takes lava damage.

If `DimensionType` modification is not supported, this effect remains commented out.

### R8 — Explosive traps effect (field_59208)

Currently commented out. Requires `field_59164`, `field_59249`, `field_59252` as incompatibilities. Unlock condition: server has more than 10 worlds. Can be uncommented once R1 is done.

### R9 — Kuiper world effect (field_59210)

Currently commented out. Uses a `FlatChunkGenerator` with a void biome and a single air layer. On mine enter, sets time to 18000 and disables daylight cycle. On mine leave, re-enables daylight cycle. No dependencies on cave world effects.

### R10 — Ender dragon boss fight effect (field_59238)

Currently commented out. Requires `class_11098` (ender dragon battle event) to exist in the mod. The mod already has `EndDragonBattleEvent.java`. Incompatible with `field_59249`/`field_59252`.

### R11 — Small but deadly boss fight (field_59245)

Currently commented out. Uses `WaveEvent` with silverfish, baby zombies, and vexes. No dependency on cave world effects. Can be uncommented once `WaveEvent` supports the required entity types.

---

## Non-Requirements

- No changes to any file other than `class_11113.java`.
- No new classes, mixins, accessors, or items are created.
- If a required API method does not exist in the mod, the effect stays commented out with a clear `// TODO: requires X` note.
