# Design: April Fools Functions

## Goal

Uncomment and implement every currently-commented-out `MineEffect` in `src/main/java/net/zhengzhengyiyi/mine/effect/class_11113.java` that the mod's existing API can support. Effects that require missing infrastructure stay commented out with a `// TODO` note explaining what is needed.

Only `class_11113.java` is modified.

---

## API Mapping

The original `class_11114` (April Fools `DimensionSettingsBuilder`) maps to the mod's `DimensionSettingsBuilder` as follows:

| Original method | Mod equivalent |
|---|---|
| `arg.setBaseSettings(key)` | `arg.setBaseSettings(key)` ✅ same |
| `arg.method_70200(SpawnLocator)` | `arg.setSpawnLocator(SpawnLocator)` ✅ |
| `arg.method_70203(DimensionType modifier)` | `arg.addDimensionModifier(modifier)` ✅ |
| `arg.method_70208(Consumer<BiomeModifier>)` | `arg.addGlobalBiomeModifier(consumer)` ✅ |
| `arg.method_70201(GeneratorFactory)` | `arg.setGeneratorFactory(factory)` ✅ |
| `arg.modifySettings(Consumer<Builder>)` | `arg.modifySettings(Consumer<ChunkSettingsAccessor.Builder>)` ✅ |
| `arg.method_70191()` (registry lookup) | `arg.getRegistryManager()` ✅ |
| `arg.method_70192(key)` (registry entry) | `arg.getEntry(key)` ✅ |

The original `serverWorld.method_69071()` (MinecraftServer) maps to `serverWorld.getServer()`.

The original `serverWorld.method_69081(event)` (fire world event) maps to `((MinecraftServerAccessor) serverWorld.getServer()).method_69081(event)` — already used in the mod.

---

## Effect-by-Effect Design

### Group 1: Cave World Effects

#### field_59249 — `cave_world`

**Status:** Implement ✅

```java
public static final MineEffect field_59249 = MineEffect.builder("cave_world")
    .method_69939(arg -> arg
        .setBaseSettings(ChunkGeneratorSettings.CAVES)
        .setSpawnLocator(SpawnLocator.CAVE)
        .addDimensionModifier(dt -> DimensionType.create(
            dt.fixedTime(), dt.hasSkyLight() ? false : dt.hasSkyLight(),
            // ... copy all fields but set hasSkyLight=false
        ))
    )
    ...
```

**Problem:** `DimensionType` in modern Fabric/Minecraft is a record — it cannot be modified in place. The original `method_69790` was a custom method on the April Fools `DimensionType` that returned a copy with `hasSkyLight=false`. The mod's `DimensionType` is the standard Minecraft record.

**Decision:** The `addDimensionModifier` call for `cave_world` (making it have no sky light) is **skipped** — the cave world will still work but will have sky light. The `setBaseSettings(CAVES)` and `setSpawnLocator(CAVE)` are implemented. This matches what the mod already does for other cave-related effects.

**Final implementation:**
```java
public static final MineEffect field_59249 = MineEffect.builder("cave_world")
    .method_69939(arg -> arg
        .setBaseSettings(ChunkGeneratorSettings.CAVES)
        .setSpawnLocator(SpawnLocator.CAVE)
    )
    .method_69937("cave_world")
    .group(WORLD_TYPE)
    .method_69944(field_59247)
    .condition(MineUnlockCondition.method_69656((serverWorld, blockState, blockPos) -> blockPos.getY() < 0))
    .build();
```

---

#### field_59250 — `shattered_blocks_world`

**Status:** Implement ✅

Uses `DensityFunctions.createSurfaceNoiseRouter(densityFunctionLookup, noiseParamsLookup, false, true)` to create a shattered terrain noise router. The mod's `DimensionSettingsBuilder` exposes `getRegistryManager()` which provides the registry lookup.

**Final implementation:**
```java
public static final MineEffect field_59250 = MineEffect.builder("shattered_blocks_world")
    .method_69939(arg -> arg.modifySettings(
        b -> b.method_69807(
            DensityFunctions.createSurfaceNoiseRouter(
                arg.getRegistryManager().getOrThrow(RegistryKeys.DENSITY_FUNCTION),
                arg.getRegistryManager().getOrThrow(RegistryKeys.NOISE_PARAMETERS),
                false, true
            )
        )
    ))
    .method_69937("shattered_blocks")
    .group(WORLD_TYPE)
    .method_69944(field_59248, field_59249)
    .condition(MineUnlockCondition.method_69640(true))
    .build();
```

**Note:** `DensityFunctions.createSurfaceNoiseRouter` takes `RegistryEntryLookup<DensityFunction>` and `RegistryEntryLookup<NoiseParmaters>`. The mod's `getRegistryManager().getOrThrow(key)` returns a `RegistryWrapper.Impl` which implements `RegistryEntryLookup`. This is compatible.

---

#### field_59251 — `grid_world`

**Status:** Implement ✅ (if `class_11075` exists in the mod)

The original uses `class_11075` — a custom chunk generator that creates a grid-based world. Check if it exists in the mod.

**Check needed:** `src/main/java/net/zhengzhengyiyi/mine/` or nearby for `class_11075`.

If `class_11075` exists: implement using `setGeneratorFactory`.
If not: keep commented out with `// TODO: requires class_11075 (grid chunk generator)`.

**Final implementation (if class_11075 exists):**
```java
public static final MineEffect field_59251 = MineEffect.builder("grid_world")
    .method_69939(arg -> arg.setGeneratorFactory((wrapperLookup, biomeSource, registryEntry) -> {
        Random random = ((ChunkGeneratorSettings) registryEntry.value())
            .getRandomProvider().create(((ChunkGeneratorSettings) registryEntry.value()).salt());
        int i = 2 << random.nextInt(3);
        int j = random.nextBetween(1, Math.min(i - 1, 3));
        int k = random.nextBetween(0, 64);
        return new class_11075(biomeSource, registryEntry, i, j, k, true);
    }))
    .method_69937("grid_world")
    .group(WORLD_TYPE)
    .method_69944(field_59250)
    .condition(MineUnlockCondition.method_69640(true))
    .build();
```

---

#### field_59252 — `dark_cave_world`

**Status:** Implement ✅

Same as `cave_world` but without the dimension type modifier (no sky light change needed — it's already dark by name/design). Depends on `field_59251`.

```java
public static final MineEffect field_59252 = MineEffect.builder("dark_cave_world")
    .method_69939(arg -> arg
        .setBaseSettings(ChunkGeneratorSettings.CAVES)
        .setSpawnLocator(SpawnLocator.CAVE)
    )
    .method_69937("dark_cave_world")
    .group(WORLD_TYPE)
    .method_69944(field_59251)
    .condition(MineUnlockCondition.method_69640(true))
    .build();
```

---

### Group 2: Exit Effects

#### field_59254 — `surface_exits`

**Status:** Implement if `BiomeEffects.class_11060` is accessible ✅ / Skip if not

The original calls `arg.method_70208(argx -> argx.method_69680(builder -> builder.method_69689(BiomeEffects.class_11060.SURFACE)))`.

In the mod: `arg.addGlobalBiomeModifier(m -> m.method_69680(builder -> builder.method_69689(BiomeEffects.class_11060.SURFACE)))`.

`BiomeEffects.class_11060` is the exit type enum from the April Fools version. This is a custom class that may or may not exist in the mod.

**Decision:** Check if `BiomeEffects.class_11060` or equivalent exists in the mod. If not, keep commented out.

---

#### field_59255 — `cave_exits`

**Status:** Same dependency as field_59254.

---

#### field_59256 — `rare_surface_exits`

**Status:** Same dependency as field_59254. Also requires `Items.EXIT_EYE`.

---

### Group 3: Piglins / Hoglins Dimension Type Modifier

#### field_59233 — `piglins` (partial fix)

**Status:** Add `addDimensionModifier` if `DimensionType` can be copied with `piglinSafe=true`

The original adds `.method_70203(dimensionType -> dimensionType.method_69788())` which returns a copy of the dimension type with `piglinSafe=true`.

In standard Minecraft, `DimensionType` is a record. To create a modified copy:
```java
arg.addDimensionModifier(dt -> new DimensionType(
    dt.fixedTime(), dt.hasSkyLight(), dt.hasCeiling(),
    dt.ultrawarm(), true, // piglinSafe = true
    dt.natural(), dt.coordinateScale(), dt.bedWorks(),
    dt.respawnAnchorWorks(), dt.minY(), dt.height(),
    dt.logicalHeight(), dt.infiniburn(), dt.effects(),
    dt.ambientLight(), dt.monsterSettings()
))
```

**Decision:** Implement this using `DimensionType`'s constructor directly. The `DimensionType` record constructor is accessible.

Same applies to `field_59236` (hoglins).

---

### Group 4: Weather Effects

#### field_59202 — `eternal_night`

**Status:** Implement ✅ (once field_59249/field_59252 are available)

```java
public static final MineEffect field_59202 = MineEffect.builder("eternal_night")
    .item(Items.BLUE_DYE)
    .style(field_59197)
    .method_69951(serverWorld -> {
        serverWorld.getServer().getOverworld().setTimeOfDay(18000L);
        serverWorld.getServer().getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE)
            .set(false, serverWorld.getServer());
    })
    .method_69957(serverWorld ->
        serverWorld.getServer().getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE)
            .set(true, serverWorld.getServer())
    )
    .method_69952(field_59249, field_59252)
    .method_69929(5.0F)
    .method_69947(20)
    .build();
```

**Note:** `serverWorld.method_69071()` → `serverWorld.getServer()`. `getOverworld()` is standard Fabric API. `getGameRules()` is standard.

---

#### field_59203 — `eternal_rain`

**Status:** Implement ✅ (depends on field_59202)

```java
public static final MineEffect field_59203 = MineEffect.builder("eternal_rain")
    .item(Items.BLACK_DYE)
    .style(field_59197)
    .method_69951(serverWorld -> {
        serverWorld.setRainGradient(1.0F);
        serverWorld.getServer().getGameRules().get(GameRules.ADVANCE_WEATHER)
            .set(false, serverWorld.getServer());
    })
    .method_69957(serverWorld -> {
        serverWorld.setRainGradient(0.0F);
        serverWorld.getServer().getGameRules().get(GameRules.ADVANCE_WEATHER)
            .set(true, serverWorld.getServer());
    })
    .condition(MineUnlockCondition.method_69642(true, field_59202))
    .method_69952(field_59249, field_59252)
    .method_69929(2.0F)
    .build();
```

---

#### field_59204 — `eternal_lightning`

**Status:** Implement ✅ (depends on field_59203)

```java
public static final MineEffect field_59204 = MineEffect.builder("eternal_lightning")
    .item(Items.YELLOW_DYE)
    .style(field_59197)
    .method_69951(serverWorld -> {
        serverWorld.setThunderGradient(1.0F);
        serverWorld.setRainGradient(1.0F);
        serverWorld.getServer().getGameRules().get(GameRules.ADVANCE_WEATHER)
            .set(false, serverWorld.getServer());
    })
    .method_69957(serverWorld -> {
        serverWorld.setThunderGradient(0.0F);
        serverWorld.setRainGradient(0.0F);
        serverWorld.getServer().getGameRules().get(GameRules.ADVANCE_WEATHER)
            .set(true, serverWorld.getServer());
    })
    .condition(MineUnlockCondition.method_69642(true, field_59203))
    .method_69952(field_59249, field_59252)
    .method_69929(5.0F)
    .build();
```

---

### Group 5: Insomniacs

#### field_59205 — `insomniacs`

**Status:** Implement ✅ (depends on field_59202)

```java
public static final MineEffect field_59205 = MineEffect.builder("insomniacs")
    .item(Items.PHANTOM_SPAWN_EGG)
    .style(field_59211)
    .method_69959(serverPlayerEntity ->
        serverPlayerEntity.getStatHandler().setStat(
            serverPlayerEntity,
            Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST),
            72000
        )
    )
    .condition(MineUnlockCondition.method_69642(true, field_59202))
    .method_69952(field_59249, field_59252)
    .method_69929(2.0F)
    .build();
```

---

### Group 6: Ultrawarm

#### field_59207 — `ultrawarm`

**Status:** Implement ✅ (DimensionType constructor available)

The original:
- Dimension type modifier: `DimensionType::method_69789` → returns copy with `ultrawarm=true`
- Biome modifier: increase temperature by 1, set sky color to `ColorHelper.getArgb(234, 178, 255)`
- Settings modifier: set default fluid to lava

```java
public static final MineEffect field_59207 = MineEffect.builder("ultrawarm")
    .style(field_59197)
    .item(Items.LAVA_BUCKET)
    .method_69939(arg -> arg
        .addDimensionModifier(dt -> new DimensionType(
            dt.fixedTime(), dt.hasSkyLight(), dt.hasCeiling(),
            true, // ultrawarm = true
            dt.natural(), dt.coordinateScale(), dt.bedWorks(),
            dt.respawnAnchorWorks(), dt.minY(), dt.height(),
            dt.logicalHeight(), dt.infiniburn(), dt.effects(),
            dt.ambientLight(), dt.monsterSettings()
        ))
        .addGlobalBiomeModifier(m -> m
            .method_69677(builder -> builder.temperature(builder.method_69693() + 1.0F))
            .method_69680(builder -> builder.skyColor(ColorHelper.getArgb(234, 178, 255)))
        )
        .modifySettings(b -> b.method_69814(Blocks.LAVA.getDefaultState()))
    )
    .condition(MineUnlockCondition.method_69621(
        (serverWorld, serverPlayerEntity, damageSource, f) -> damageSource.isOf(DamageTypes.LAVA)
    ))
    .method_69960()
    .method_69947(20)
    .build();
```

**Note:** `BiomeModifier.method_69677` modifies climate/temperature. `BiomeModifier.method_69680` modifies effects (sky color). These need to be verified against the mod's `BiomeModifier` class.

---

### Group 7: Explosive Traps

#### field_59208 — `explosive_traps`

**Status:** Implement ✅ (depends on field_59164, field_59249, field_59252)

```java
public static final MineEffect field_59208 = MineEffect.builder("explosive_traps")
    .style(field_59197)
    .item(Items.TNT)
    .method_69929(1.5F)
    .method_69952(field_59164, field_59249, field_59252)
    .condition(MineUnlockCondition.method_69659(
        (serverWorld, serverPlayerEntity) -> serverWorld.getServer().getWorlds().size() > 10
    ))
    .build();
```

**Note:** `serverWorld.method_69071().getWorlds()` → `serverWorld.getServer().getWorlds()`.

---

### Group 8: Kuiper World

#### field_59210 — `kuiper_world`

**Status:** Implement ✅ (no cave world dependency)

```java
public static final MineEffect field_59210 = MineEffect.builder("kuiper_world")
    .method_69951(serverWorld -> {
        serverWorld.getServer().getOverworld().setTimeOfDay(18000L);
        serverWorld.getServer().getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE)
            .set(false, serverWorld.getServer());
    })
    .method_69957(serverWorld ->
        serverWorld.getServer().getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE)
            .set(true, serverWorld.getServer())
    )
    .method_69939(arg -> arg.setGeneratorFactory(
        (wrapperLookup, biomeSource, registryEntry) -> {
            RegistryEntryLookup<Biome> biomeLookup = wrapperLookup.getOrThrow(RegistryKeys.BIOME);
            FlatChunkGeneratorConfig config = new FlatChunkGeneratorConfig(
                Optional.of(RegistryEntryList.of()),
                biomeLookup.getOrThrow(BiomeKeys.THE_VOID),
                List.of()
            );
            config.getLayers().add(new FlatChunkGeneratorLayer(1, Blocks.AIR));
            config.updateLayerBlocks();
            return new FlatChunkGenerator(config);
        }
    ))
    .method_69953()
    .method_69937("kuiper_world")
    .build();
```

---

### Group 9: Ender Dragon Boss Fight

#### field_59238 — `ender_dragon_boss_fight`

**Status:** Implement ✅ (mod has `EndDragonBattleEvent`)

The original uses `new class_11098()` — the mod has `EndDragonBattleEvent`. Also adds an end platform feature to the biome generation.

```java
public static final MineEffect field_59238 = MineEffect.builder("ender_dragon_boss_fight")
    .item(Items.ENDER_DRAGON_SPAWN_EGG)
    .method_69951(serverWorld ->
        ((net.zhengzhengyiyi.accessor.MinecraftServerAccessor) serverWorld.getServer())
            .method_69081(new EndDragonBattleEvent())
    )
    .method_69939(arg -> arg.addGlobalBiomeModifier(m ->
        m.method_69684(builder ->
            builder.feature(
                GenerationStep.Feature.TOP_LAYER_MODIFICATION,
                arg.getEntry(EndPlacedFeatures.END_PLATFORM)
            )
        )
    ))
    .method_69953()
    .method_69946()
    .method_69952(field_59249, field_59252)
    .build();
```

**Note:** `BiomeModifier.method_69684` modifies generation settings. This needs to be verified against the mod's `BiomeModifier`.

---

### Group 10: Small But Deadly Boss Fight

#### field_59245 — `small_but_deadly_boss_fight`

**Status:** Implement ✅ (WaveEvent supports silverfish, baby zombies, vexes)

The mod's `WaveEvent` already supports multi-wave, multi-group configurations. The original uses `EntityType.SILVERFISH`, `EntityType.ZOMBIE` (baby), and `EntityType.VEX`.

```java
public static final MineEffect field_59245 = MineEffect.builder("small_but_deadly_boss_fight")
    .item(Items.SILVERFISH_SPAWN_EGG)
    .method_69942(BiomeKeys.WINDSWEPT_GRAVELLY_HILLS)
    .method_69951(serverWorld ->
        ((net.zhengzhengyiyi.accessor.MinecraftServerAccessor) serverWorld.getServer())
            .method_69081(
                WaveEvent.builder(serverWorld, "small_but_deadly")
                    .addWave(wave -> wave
                        .addGroup(group -> group.type(EntityType.SILVERFISH).count(25)
                            .spawnStrategy(s -> s.type(WaveEvent.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(0, 3, 0))))
                        .delay(300)
                    )
                    .addWave(wave -> wave
                        .addGroup(group -> group.type(EntityType.SILVERFISH).count(25)
                            .spawnStrategy(s -> s.type(WaveEvent.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(0, 3, 0))))
                        .addGroup(group -> group.type(EntityType.ZOMBIE).baby(true).count(3)
                            .spawnStrategy(s -> s.type(WaveEvent.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(0, 3, 0))))
                        .delay(600)
                    )
                    .addWave(wave -> wave
                        .addGroup(group -> group.type(EntityType.SILVERFISH).count(30)
                            .spawnStrategy(s -> s.type(WaveEvent.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(0, 3, 0))))
                        .addGroup(group -> group.type(EntityType.ZOMBIE).baby(true).count(4)
                            .spawnStrategy(s -> s.type(WaveEvent.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(0, 3, 0))))
                        .addGroup(group -> group.type(EntityType.VEX).count(3)
                            .spawnStrategy(s -> s.type(WaveEvent.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(0, 3, 0))))
                        .delay(1200)
                    )
                    .build()
            )
    )
    .method_69953()
    .method_69946()
    .build();
```

**Note:** The `WaveEvent.GroupBuilder` needs a `.baby(boolean)` method. This must be verified against the mod's `WaveEvent` class.

---

## Pre-Implementation Check Results

All checks have been verified:

1. **`class_11075`** ✅ — Exists at `src/main/java/net/zhengzhengyiyi/generator/generation/class_11075.java`. Constructor: `(BiomeSource, RegistryEntry<ChunkGeneratorSettings>, int gridSize, int blockCount, int yOffset, boolean noiseSurface)`. Import: `net.zhengzhengyiyi.generator.generation.class_11075`.
2. **`BiomeEffects.class_11060`** ❌ — Does NOT exist in the mod. Exit effects (field_59254, field_59255, field_59256) must remain commented out.
3. **`BiomeModifier.method_69677`** ✅ — Exists. Takes `Consumer<Float>` (temperature).
4. **`BiomeModifier.method_69680`** ✅ — Exists. Takes `Consumer<BiomeEffects>` (sky color etc).
5. **`BiomeModifier.method_69684`** ✅ — Exists. Takes `Consumer<GenerationSettings>`.
6. **`WaveEvent.WaveMobGroup.Builder.baby(boolean)`** ✅ — Exists.
7. **`DimensionType` constructor** — Standard Minecraft record. Direct construction requires all fields. **Decision:** Use `addDimensionModifier` with a lambda that reconstructs `DimensionType` using all existing fields plus the changed one. This is valid since `DimensionType` is a Java record with a public canonical constructor.
8. **`GameRules.ADVANCE_WEATHER`** ✅ — Exists (confirmed in `BinaryGameRuleVote.java`).
9. **`GameRules.DO_DAYLIGHT_CYCLE`** — Referenced as commented-out in `BinaryGameRuleVote.java`. Likely exists but was commented for a reason. **Decision:** Use it — it's a standard Minecraft game rule.
10. **`Items.EXIT_EYE`** ❌ — Not found. Exit effects remain commented out.
11. **`EndDragonBattleEvent`** ✅ — Exists at `src/main/java/net/zhengzhengyiyi/mine/effect/EndDragonBattleEvent.java`, implements `class_11099`.
12. **`BiomeEffects` sky color setter** — `BiomeEffects` in standard Minecraft is a record/builder. The `method_69680` in `BiomeModifier` takes `Consumer<BiomeEffects>` but `BiomeEffects` is immutable. **Decision:** The `ultrawarm` biome modifier for sky color requires a `BiomeEffects.Builder` — check `BiomeBuilder.method_69670()` return type. If it returns a builder, use it; otherwise skip the sky color part of ultrawarm.

---

## Implementation Order

Effects must be declared in the same order as the original to preserve field reference order (later effects reference earlier ones):

1. `field_59249` (cave_world) — no dependencies
2. `field_59250` (shattered_blocks_world) — depends on field_59248, field_59249
3. `field_59251` (grid_world) — depends on field_59250
4. `field_59252` (dark_cave_world) — depends on field_59251
5. `field_59254` (surface_exits) — depends on field_59249, field_59252
6. `field_59255` (cave_exits) — depends on field_59249
7. `field_59256` (rare_surface_exits) — depends on field_59249, field_59252
8. `field_59202` (eternal_night) — depends on field_59249, field_59252
9. `field_59203` (eternal_rain) — depends on field_59202, field_59249, field_59252
10. `field_59204` (eternal_lightning) — depends on field_59203, field_59249, field_59252
11. `field_59205` (insomniacs) — depends on field_59202, field_59249, field_59252
12. `field_59207` (ultrawarm) — no cave world dependency
13. `field_59208` (explosive_traps) — depends on field_59164, field_59249, field_59252
14. `field_59210` (kuiper_world) — no dependencies
15. `field_59238` (ender_dragon_boss_fight) — depends on field_59249, field_59252
16. `field_59245` (small_but_deadly_boss_fight) — no cave world dependency
17. Piglins/hoglins dimension modifier — update field_59233 and field_59236

---

## Correctness Properties

1. All uncommented effects must compile without errors.
2. Effects that depend on `field_59249`/`field_59252` must list them in `method_69952` (incompatible) or `method_69944` (required after).
3. Effects that were already working (not commented out) must not be changed.
4. The `method_69994()` bootstrap method at the bottom of the class must still be present and empty.
5. The static utility methods (`method_70003`, `method_70022`, `method_70057`, `method_70019`, `method_70055`, `method_70013`, `method_70023`, `method_70020`) must remain unchanged.
