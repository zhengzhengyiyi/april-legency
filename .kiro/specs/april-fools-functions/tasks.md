# Tasks: April Fools Functions

All changes are in `src/main/java/net/zhengzhengyiyi/mine/effect/class_11113.java` only.

## Task 1: Uncomment cave world effects (field_59249, field_59250, field_59251, field_59252)

- [x] 1.1 Uncomment `field_59249` (`cave_world`) — use `setBaseSettings(CAVES)` and `setSpawnLocator(CAVE)`. Skip the `addDimensionModifier` for sky light (DimensionType is not reconstructable). Add import for `net.zhengzhengyiyi.generator.generation.class_11075` at top of file.
- [x] 1.2 Uncomment `field_59250` (`shattered_blocks_world`) — use `arg.getRegistryManager().getOrThrow(RegistryKeys.DENSITY_FUNCTION)` and `arg.getRegistryManager().getOrThrow(RegistryKeys.NOISE_PARAMETERS)` inside `modifySettings`. Add required imports for `DensityFunctions`, `RegistryKeys.DENSITY_FUNCTION`, `RegistryKeys.NOISE_PARAMETERS`.
- [x] 1.3 Uncomment `field_59251` (`grid_world`) — use `setGeneratorFactory` with `class_11075` constructor `(biomeSource, registryEntry, i, j, k, true)`. Add import for `net.zhengzhengyiyi.generator.generation.class_11075`.
- [x] 1.4 Uncomment `field_59252` (`dark_cave_world`) — use `setBaseSettings(CAVES)` and `setSpawnLocator(CAVE)`.

## Task 2: Uncomment exit effects (field_59254, field_59255, field_59256)

- [ ] 2.1 Keep `field_59254`, `field_59255`, `field_59256` commented out. Add a comment: `// TODO: requires BiomeEffects.class_11060 (exit type enum) and Items.EXIT_EYE — not present in this mod`.

## Task 3: Add dimension type modifier to piglins and hoglins (field_59233, field_59236)

- [ ] 3.1 Update `field_59233` (`piglins`) — add `.method_69939(arg -> arg.addDimensionModifier(dt -> new DimensionType(dt.fixedTime(), dt.hasSkyLight(), dt.hasCeiling(), dt.ultrawarm(), true, dt.natural(), dt.coordinateScale(), dt.bedWorks(), dt.respawnAnchorWorks(), dt.minY(), dt.height(), dt.logicalHeight(), dt.infiniburn(), dt.effects(), dt.ambientLight(), dt.monsterSettings())))` to make the dimension piglin-safe. Add import for `net.minecraft.world.dimension.DimensionType`.
- [ ] 3.2 Update `field_59236` (`hoglins`) — same `addDimensionModifier` call as 3.1.

## Task 4: Uncomment weather effects (field_59202, field_59203, field_59204)

- [x] 4.1 Uncomment `field_59202` (`eternal_night`) — replace `serverWorld.method_69071()` with `serverWorld.getServer()`. Use `serverWorld.getServer().getOverworld().setTimeOfDay(18000L)` and `serverWorld.getServer().getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(false, serverWorld.getServer())`. Add import for `net.minecraft.world.rule.GameRules`.
- [ ] 4.2 Uncomment `field_59203` (`eternal_rain`) — replace `serverWorld.method_69071()` with `serverWorld.getServer()`. Use `serverWorld.setRainGradient(1.0F)` and `GameRules.ADVANCE_WEATHER`.
- [ ] 4.3 Uncomment `field_59204` (`eternal_lightning`) — replace `serverWorld.method_69071()` with `serverWorld.getServer()`. Use `serverWorld.setThunderGradient(1.0F)`, `serverWorld.setRainGradient(1.0F)`, and `GameRules.ADVANCE_WEATHER`.

## Task 5: Uncomment insomniacs effect (field_59205)

- [ ] 5.1 Uncomment `field_59205` (`insomniacs`) — use `serverPlayerEntity.getStatHandler().setStat(serverPlayerEntity, Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST), 72000)`. Verify `Stats.TIME_SINCE_REST` exists in the mod's Minecraft version.

## Task 6: Uncomment ultrawarm effect (field_59207)

- [ ] 6.1 Uncomment `field_59207` (`ultrawarm`) — implement with:
  - `addDimensionModifier` to set `ultrawarm=true` using `DimensionType` constructor
  - `addGlobalBiomeModifier` for temperature and sky color (note: `BiomeBuilder.build()` returns original biome unchanged, so biome changes are no-ops — include them anyway for correctness)
  - `modifySettings(b -> b.method_69814(Blocks.LAVA.getDefaultState()))` for lava as default fluid
  - Condition: `MineUnlockCondition.method_69621((serverWorld, serverPlayerEntity, damageSource, f) -> damageSource.isOf(DamageTypes.LAVA))`
  - Add import for `net.minecraft.util.math.ColorHelper` if not already present.

## Task 7: Uncomment explosive traps effect (field_59208)

- [ ] 7.1 Uncomment `field_59208` (`explosive_traps`) — replace `serverWorld.method_69071().getWorlds()` with `serverWorld.getServer().getWorlds()`. Depends on `field_59164`, `field_59249`, `field_59252`.

## Task 8: Uncomment kuiper world effect (field_59210)

- [ ] 8.1 Uncomment `field_59210` (`kuiper_world`) — replace `serverWorld.method_69071()` with `serverWorld.getServer()`. Use `setGeneratorFactory` with `FlatChunkGenerator`. Add imports for `FlatChunkGenerator`, `FlatChunkGeneratorConfig`, `FlatChunkGeneratorLayer`, `RegistryEntryList`, `RegistryEntryLookup`.

## Task 9: Uncomment ender dragon boss fight (field_59238)

- [ ] 9.1 Uncomment `field_59238` (`ender_dragon_boss_fight`) — replace `new class_11098()` with `new EndDragonBattleEvent()`. Replace `arg.method_70208(...)` with `arg.addGlobalBiomeModifier(m -> m.method_69684(builder -> builder.feature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, arg.getEntry(EndPlacedFeatures.END_PLATFORM))))`. Add import for `net.zhengzhengyiyi.mine.effect.EndDragonBattleEvent`.

## Task 10: Uncomment small but deadly boss fight (field_59245)

- [ ] 10.1 Uncomment `field_59245` (`small_but_deadly_boss_fight`) — use `WaveEvent.builder` with three waves of silverfish, baby zombies, and vexes. Use `((net.zhengzhengyiyi.accessor.MinecraftServerAccessor) serverWorld.getServer()).method_69081(...)` pattern already used in the file.

## Task 11: Apply main file april fool into the mod
- [x] 11.1 Mixin into main file like MinecraftServer, MinecraftClient, World. especially "method_xxxxx", "field_xxxxx"

## Task 12: Verify compilation

- [ ] 12.1 Run `./gradlew compileJava` (or equivalent) and fix any compilation errors in `class_11113.java` only.
