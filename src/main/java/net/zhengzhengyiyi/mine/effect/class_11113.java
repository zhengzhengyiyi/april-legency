package net.zhengzhengyiyi.mine.effect;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;
import net.minecraft.world.rule.GameRules;
import net.zhengzhengyiyi.AprilsLegacy;
import net.zhengzhengyiyi.component.MineEffectComponent;
import net.zhengzhengyiyi.component.ModDataComponentTypes;
import net.zhengzhengyiyi.generator.generation.class_11075;
import net.zhengzhengyiyi.mine.MineEffect;
import net.zhengzhengyiyi.mine.SpawnLocator;
import net.zhengzhengyiyi.mine.class_11056;

public class class_11113 {
   public static final float field_59184 = 0.1F;
   public static final MineEffectGroup WORLD_TYPE = method_70055("world_type");
   public static final MineEffect field_59246 = MineEffect.builder("surface_world")
      .method_69939(arg -> arg.setBaseSettings(ChunkGeneratorSettings.OVERWORLD))
      .method_69937("surface_world")
      .group(WORLD_TYPE)
      .method_69962()
      .build();
   public static final MineEffect field_59247 = MineEffect.builder("floating_islands_world")
      .method_69939(arg -> arg.setBaseSettings(ChunkGeneratorSettings.FLOATING_ISLANDS))
      .method_69937("floating_islands_world")
      .group(WORLD_TYPE)
      .condition(MineUnlockCondition.method_69659((serverWorld, serverPlayerEntity) -> true))
      .build();
   public static final MineEffect field_59248 = MineEffect.builder("amplified")
      .method_69939(arg -> arg.setBaseSettings(ChunkGeneratorSettings.AMPLIFIED))
      .group(WORLD_TYPE)
      .method_69937("amplified")
      .method_69944(field_59247)
      .condition(MineUnlockCondition.itemUse((serverWorld, blockState, blockPos) -> blockPos.getY() >= 100))
      .build();
   public static final MineEffect field_59249 = MineEffect.builder("cave_world")
      .method_69939(arg -> arg
         .setBaseSettings(ChunkGeneratorSettings.CAVES)
         .setSpawnLocator(SpawnLocator.CAVE)
         // DimensionType::method_69790 (cave-specific dim type) is April Fools-only and is not
         // available in the mod's Minecraft version — dimension type modification skipped.
      )
      .method_69937("cave_world")
      .group(WORLD_TYPE)
      .method_69944(field_59247)
      .condition(MineUnlockCondition.method_69656((serverWorld, blockState, blockPos) -> blockPos.getY() < 0))
      .build();
   // field_59250 (shattered_blocks_world): DensityFunctions.createSurfaceNoiseRouter is protected in
   // this Minecraft version and cannot be called from outside the package without a mixin accessor.
   // TODO: add an @Accessor mixin for DensityFunctions.createSurfaceNoiseRouter to enable this effect.
   public static final MineEffect field_59251 = MineEffect.builder("grid_world")
      .method_69939(arg -> arg.setGeneratorFactory((wrapperLookup, biomeSource, registryEntry) -> {
         // Use salt from ChunkGeneratorSettings for terrain variation
         // This now properly uses the custom salt set in DimensionSettingsBuilder
         Random random = ((ChunkGeneratorSettings) registryEntry.value()).getRandomProvider().create(
            ((net.zhengzhengyiyi.accessor.ISaltSettings)(Object)registryEntry.value()).getCustomSalt()
         );
         int i = 2 << random.nextInt(3);
         int j = random.nextBetween(1, Math.min(i - 1, 3));
         int k = random.nextBetween(0, 64);
         return new class_11075(biomeSource, registryEntry, i, j, k, true);
      }))
      .method_69937("grid_world")
      .group(WORLD_TYPE)
      .method_69944(field_59248, field_59249)
      .condition(MineUnlockCondition.method_69640(true))
      .build();
   public static final MineEffect field_59252 = MineEffect.builder("dark_cave_world")
      .method_69939(arg -> arg.setBaseSettings(ChunkGeneratorSettings.CAVES).setSpawnLocator(SpawnLocator.CAVE))
      .method_69937("dark_cave_world")
      .group(WORLD_TYPE)
      .method_69944(field_59251)
      .condition(MineUnlockCondition.method_69640(true))
      .build();
   public static final MineEffectGroup EXITS = method_70055("exits");
//   public static final MineEffect field_59254 = MineEffect.builder("surface_exits")
//      .method_69937("surface_exits")
//      .method_69939(arg -> arg.method_70208(argx -> argx.method_69680(builder -> builder.method_69689(BiomeEffects.class_11060.SURFACE))))
//      .group(EXITS)
//      .method_69952(field_59249, field_59252)
//      .method_69962()
//      .build();
//   public static final MineEffect field_59255 = MineEffect.builder("cave_exits")
//      .method_69937("cave_exits")
//      .method_69939(arg -> arg.method_70208(argx -> argx.method_69680(builder -> builder.method_69689(BiomeEffects.class_11060.CAVE))))
//      .group(EXITS)
//      .condition(MineUnlockCondition.method_69650((serverWorld, blockState) -> blockState.isOf(Blocks.DEEPSLATE) && serverWorld.getRandom().nextFloat() < 0.01F))
//      .condition(MineUnlockCondition.method_69636(field_59249))
//      .build();
//   public static final MineEffect field_59256 = MineEffect.builder("rare_surface_exits")
//      .method_69937("rare_surface_exits")
//      .method_69939(arg -> arg.method_70208(argx -> argx.method_69680(builder -> builder.method_69689(BiomeEffects.class_11060.RARE_SURFACE))))
//      .group(EXITS)
//      .method_69952(field_59249, field_59252)
//      .condition(MineUnlockCondition.method_69660((serverWorld, serverPlayerEntity, itemStack) -> itemStack.isOf(Items.EXIT_EYE)))
//      .build();
   public static final MineEffect field_59257 = MineEffect.builder("event_exit").method_69946().method_69953().build();
   public static final MineEffectGroup BIOME = method_70019("biome");
   public static final MineEffect field_59259 = MineEffect.builder("plains")
      .item(Items.GRASS_BLOCK)
      .method_69942(BiomeKeys.PLAINS, BiomeKeys.SUNFLOWER_PLAINS, BiomeKeys.MEADOW)
      .method_69962()
      .group(BIOME)
      .build();
   public static final MineEffect field_59260 = MineEffect.builder("savannas")
      .item(Items.ACACIA_SAPLING)
      .method_69942(BiomeKeys.SAVANNA, BiomeKeys.SAVANNA_PLATEAU, BiomeKeys.WINDSWEPT_SAVANNA)
      .method_69962()
      .group(BIOME)
      .build();
   public static final MineEffect field_59261 = MineEffect.builder("forests")
      .item(Items.OAK_SAPLING)
      .method_69942(
         BiomeKeys.FOREST, BiomeKeys.BIRCH_FOREST, BiomeKeys.FLOWER_FOREST, BiomeKeys.OLD_GROWTH_BIRCH_FOREST, BiomeKeys.GROVE, BiomeKeys.CHERRY_GROVE
      )
      .condition(MineUnlockCondition.method_69655((serverWorld, blockState) -> blockState.isIn(BlockTags.LEAVES) && serverWorld.getRandom().nextFloat() < 0.01F))
      .method_69960()
      .group(BIOME)
      .build();
   public static final MineEffect field_59262 = MineEffect.builder("taigas")
      .item(Items.SPRUCE_SAPLING)
      .method_69942(BiomeKeys.TAIGA, BiomeKeys.OLD_GROWTH_PINE_TAIGA, BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA)
      .condition(MineUnlockCondition.method_69655((serverWorld, blockState) -> blockState.isIn(BlockTags.LEAVES) && serverWorld.getRandom().nextFloat() < 0.01F))
      .method_69960()
      .group(BIOME)
      .build();
   public static final MineEffect field_59263 = MineEffect.builder("jungles")
      .item(Items.JUNGLE_SAPLING)
      .method_69942(BiomeKeys.JUNGLE, BiomeKeys.BAMBOO_JUNGLE, BiomeKeys.SPARSE_JUNGLE)
      .condition(MineUnlockCondition.method_69655((serverWorld, blockState) -> blockState.isIn(BlockTags.LEAVES) && serverWorld.getRandom().nextFloat() < 0.01F))
      .method_69960()
      .group(BIOME)
      .build();
   public static final MineEffect field_59264 = MineEffect.builder("snowy")
      .item(Items.SNOWBALL)
      .method_69942(
         BiomeKeys.SNOWY_PLAINS,
         BiomeKeys.SNOWY_TAIGA,
         BiomeKeys.SNOWY_SLOPES,
         BiomeKeys.SNOWY_BEACH,
         BiomeKeys.COLD_OCEAN,
         BiomeKeys.DEEP_COLD_OCEAN,
         BiomeKeys.FROZEN_OCEAN,
         BiomeKeys.DEEP_FROZEN_OCEAN,
         BiomeKeys.FROZEN_PEAKS,
         BiomeKeys.FROZEN_RIVER
      )
      .group(BIOME)
      .condition(
         MineUnlockCondition.method_69650(
            (serverWorld, blockState) -> (blockState.isIn(BlockTags.SNOW) || blockState.isIn(BlockTags.ICE)) && serverWorld.getRandom().nextFloat() < 0.05F
         )
      )
      .method_69960()
      .build();
   public static final MineEffect field_59265 = MineEffect.builder("desert")
      .item(Items.SAND)
      .method_69942(BiomeKeys.DESERT)
      .group(BIOME)
      .condition(MineUnlockCondition.method_69650((serverWorld, blockState) -> blockState.isIn(BlockTags.SAND) && serverWorld.getRandom().nextFloat() < 0.01F))
      .method_69960()
      .build();
   public static final MineEffect field_59266 = MineEffect.builder("badlands")
      .item(Items.GRAY_TERRACOTTA)
      .method_69942(BiomeKeys.BADLANDS, BiomeKeys.ERODED_BADLANDS, BiomeKeys.WOODED_BADLANDS, BiomeKeys.DRIPSTONE_CAVES)
      .condition(
         MineUnlockCondition.method_69650(
            (serverWorld, blockState) -> (blockState.isIn(BlockTags.TERRACOTTA) || blockState.isOf(Blocks.CLAY)) && serverWorld.getRandom().nextFloat() < 0.05F
         )
      )
      .method_69960()
      .group(BIOME)
      .build();
   public static final MineEffect field_59267 = MineEffect.builder("swamps")
      .item(Items.MANGROVE_ROOTS)
      .method_69942(BiomeKeys.SWAMP, BiomeKeys.MANGROVE_SWAMP, BiomeKeys.LUSH_CAVES)
      .group(BIOME)
      .condition(MineUnlockCondition.method_69650((serverWorld, blockState) -> blockState.isOf(Blocks.FIREFLY_BUSH)))
      .method_69960()
      .build();
   public static final MineEffect field_59268 = MineEffect.builder("dark_forests")
      .item(Items.CREAKING_HEART)
      .method_69942(BiomeKeys.DARK_FOREST, BiomeKeys.PALE_GARDEN)
      .group(BIOME)
      .method_69944(field_59261, field_59263, field_59262)
      .condition(MineUnlockCondition.method_69655((serverWorld, blockState) -> blockState.isIn(BlockTags.LEAVES) && serverWorld.getRandom().nextFloat() < 0.01F))
      .method_69960()
      .build();
   public static final MineEffect field_59269 = MineEffect.builder("ice_spikes")
      .item(Items.PACKED_ICE)
      .method_69942(BiomeKeys.ICE_SPIKES)
      .group(BIOME)
      .condition(MineUnlockCondition.method_69642(true, field_59264))
      .method_69960()
      .build();
   public static final MineEffect field_59158 = MineEffect.builder("mushroom_fields")
      .item(Items.RED_MUSHROOM)
      .method_69942(BiomeKeys.MUSHROOM_FIELDS)
      .group(BIOME)
      .condition(
         MineUnlockCondition.method_69650(
            (serverWorld, blockState) -> (blockState.isOf(Blocks.BROWN_MUSHROOM) || blockState.isOf(Blocks.RED_MUSHROOM))
               && serverWorld.getRandom().nextFloat() < 0.1F
         )
      )
      .method_69960()
      .build();
   public static final MineEffect field_59159 = MineEffect.builder("nether_barrens")
      .item(Items.NETHERRACK)
      .method_69939(arg -> arg.modifySettings(argx -> argx.method_69804(argx.method_69803() - 64)))
      .method_69942(BiomeKeys.NETHER_WASTES, BiomeKeys.SOUL_SAND_VALLEY, BiomeKeys.BASALT_DELTAS)
      .group(BIOME)
      .condition(MineUnlockCondition.method_69662((serverWorld, serverPlayerEntity, itemStack) -> itemStack.isOf(Items.OBSIDIAN)))
      .build();
   public static final MineEffect field_59160 = MineEffect.builder("nether_forests")
      .item(Items.WARPED_FUNGUS)
      .method_69942(BiomeKeys.CRIMSON_FOREST, BiomeKeys.WARPED_FOREST)
      .method_69939(arg -> arg.modifySettings(argx -> argx.method_69804(argx.method_69803() - 64)))
      .group(BIOME)
      .condition(MineUnlockCondition.method_69596(0.2F, EntityType.ENDERMAN))
      .build();
   public static final MineEffect field_59161 = MineEffect.builder("deep_dark")
      .item(Items.SCULK)
      .group(BIOME)
      .method_69942(BiomeKeys.DEEP_DARK)
      .condition(
         MineUnlockCondition.method_69656((serverWorld, blockState, blockPos) -> serverWorld.getRandom().nextFloat() < 0.1F && method_70003(serverWorld, blockPos))
      )
      .build();
   public static final MineEffect field_59162 = MineEffect.builder("end")
      .item(Items.END_STONE)
      .group(BIOME)
      .method_69939(arg -> arg.modifySettings(argx -> argx.method_69804(argx.method_69803() - 64)))
      .method_69942(BiomeKeys.THE_END, BiomeKeys.END_HIGHLANDS, BiomeKeys.END_BARRENS, BiomeKeys.END_MIDLANDS, BiomeKeys.SMALL_END_ISLANDS)
      .condition(MineUnlockCondition.method_69596(0.5F, EntityType.BLAZE))
      .build();
   public static final MineEffect field_59163 = MineEffect.builder("dry_land")
      .method_69939(arg -> arg.modifySettings(argx -> argx.method_69804(-64)))
      .condition(
         MineUnlockCondition.method_69662(
            (serverWorld, serverPlayerEntity, itemStack) -> serverWorld.getBiome(serverPlayerEntity.getBlockPos()).isIn(BiomeTags.IS_BADLANDS)
               && itemStack.isOf(Items.LAVA_BUCKET)
         )
      )
      .item(Items.BUCKET)
      .build();
   public static final MineEffect field_59164 = MineEffect.builder("water_world")
      .method_69939(arg -> arg.modifySettings(argx -> argx.method_69804(128)))
      .method_69952(field_59163)
      .condition(MineUnlockCondition.method_69642(true, field_59163))
      .item(Items.WATER_BUCKET)
      .build();
   public static final MineEffectGroup PASSIVE_MOBS = method_70019("passive_mobs");
   public static final Style field_59166 = Style.EMPTY.withColor(Formatting.DARK_GREEN);
   public static final MineEffect field_59167 = MineEffect.builder("sheep")
      .style(field_59166)
      .method_69962()
      .item(Items.SHEEP_SPAWN_EGG)
      .group(PASSIVE_MOBS)
      .method_69939(arg -> arg.addSpawn(SpawnGroup.CREATURE, 12, new SpawnSettings.SpawnEntry(EntityType.SHEEP, 4, 4)))
      .build();
   public static final MineEffect field_59168 = MineEffect.builder("cows")
      .style(field_59166)
      .method_69962()
      .item(Items.COW_SPAWN_EGG)
      .group(PASSIVE_MOBS)
      .method_69939(arg -> arg.addSpawn(SpawnGroup.CREATURE, 8, new SpawnSettings.SpawnEntry(EntityType.COW, 4, 4)))
      .build();
   public static final MineEffect field_59169 = MineEffect.builder("pigs")
      .style(field_59166)
      .method_69962()
      .item(Items.PIG_SPAWN_EGG)
      .group(PASSIVE_MOBS)
      .method_69939(arg -> arg.addSpawn(SpawnGroup.CREATURE, 10, new SpawnSettings.SpawnEntry(EntityType.PIG, 4, 4)))
      .build();
   public static final MineEffect field_59170 = MineEffect.builder("chickens")
      .style(field_59166)
      .method_69962()
      .item(Items.CHICKEN_SPAWN_EGG)
      .group(PASSIVE_MOBS)
      .method_69939(arg -> arg.addSpawn(SpawnGroup.CREATURE, 10, new SpawnSettings.SpawnEntry(EntityType.CHICKEN, 4, 4)))
      .build();
   public static final MineEffect field_59171 = MineEffect.builder("frogs")
      .style(field_59166)
      .method_69962()
      .item(Items.FROG_SPAWN_EGG)
      .group(PASSIVE_MOBS)
      .method_69939(arg -> arg.addSpawn(SpawnGroup.CREATURE, 10, new SpawnSettings.SpawnEntry(EntityType.FROG, 4, 4)))
      .build();
   public static final MineEffect field_59172 = MineEffect.builder("foxes")
      .style(field_59166)
      .condition(MineUnlockCondition.method_69662((serverWorld, serverPlayerEntity, itemStack) -> itemStack.isOf(Items.SWEET_BERRIES)))
      .item(Items.FOX_SPAWN_EGG)
      .group(PASSIVE_MOBS)
      .method_69939(arg -> arg.addSpawn(SpawnGroup.CREATURE, 10, new SpawnSettings.SpawnEntry(EntityType.FOX, 1, 3)))
      .build();
   public static final MineEffect field_59173 = MineEffect.builder("goats")
      .style(field_59166)
      .condition(MineUnlockCondition.method_69656((serverWorld, blockState, blockPos) -> blockPos.getY() > 128 && serverWorld.getRandom().nextFloat() < 0.1))
      .item(Items.GOAT_SPAWN_EGG)
      .group(PASSIVE_MOBS)
      .method_69939(arg -> arg.addSpawn(SpawnGroup.CREATURE, 10, new SpawnSettings.SpawnEntry(EntityType.GOAT, 1, 3)))
      .build();
   public static final MineEffect field_59174 = MineEffect.builder("ocelots")
      .style(field_59166)
      .condition(MineUnlockCondition.method_69662((serverWorld, serverPlayerEntity, itemStack) -> itemStack.isOf(Items.STRING)))
      .method_69928()
      .item(Items.OCELOT_SPAWN_EGG)
      .group(PASSIVE_MOBS)
      .method_69939(arg -> arg.addSpawn(SpawnGroup.CREATURE, 20, new SpawnSettings.SpawnEntry(EntityType.OCELOT, 15, 15)))
      .build();
   public static final MineEffect field_59175 = MineEffect.builder("axolotls")
      .style(field_59166)
      .condition(MineUnlockCondition.method_69662((serverWorld, serverPlayerEntity, itemStack) -> itemStack.isOf(Items.AXOLOTL_BUCKET)))
      .method_69928()
      .item(Items.AXOLOTL_SPAWN_EGG)
      .group(PASSIVE_MOBS)
      .method_69942(BiomeKeys.LUSH_CAVES)
      .method_69939(arg -> arg.addSpawn(SpawnGroup.AXOLOTLS, 20, new SpawnSettings.SpawnEntry(EntityType.AXOLOTL, 15, 15)))
      .method_69952(field_59163)
      .build();
   public static final MineEffect field_59176 = MineEffect.builder("armadillos")
      .style(field_59166)
      .condition(MineUnlockCondition.method_69660((serverWorld, serverPlayerEntity, itemStack) -> itemStack.isOf(Items.BRUSH)))
      .item(Items.ARMADILLO_SPAWN_EGG)
      .group(PASSIVE_MOBS)
      .method_69939(arg -> arg.addSpawn(SpawnGroup.CREATURE, 20, new SpawnSettings.SpawnEntry(EntityType.ARMADILLO, 1, 3)))
      .build();
   public static final MineEffect field_59177 = MineEffect.builder("mooshrooms")
      .style(field_59166)
      .condition(
         MineUnlockCondition.method_69650(
            (serverWorld, blockState) -> serverWorld.getRandom().nextFloat() < 0.1
               && (blockState.isOf(Blocks.RED_MUSHROOM) || blockState.isOf(Blocks.BROWN_MUSHROOM))
         )
      )
      .item(Items.MOOSHROOM_SPAWN_EGG)
      .group(PASSIVE_MOBS)
      .method_69939(arg -> arg.addSpawn(SpawnGroup.CREATURE, 20, new SpawnSettings.SpawnEntry(EntityType.MOOSHROOM, 1, 3)))
      .build();
   public static final MineEffect field_59178 = MineEffect.builder("pandas")
      .style(field_59166)
      .condition(MineUnlockCondition.method_69661((serverWorld, serverPlayerEntity, itemStack) -> itemStack.isOf(Items.BAMBOO)))
      .item(Items.PANDA_SPAWN_EGG)
      .group(PASSIVE_MOBS)
      .method_69939(arg -> arg.addSpawn(SpawnGroup.CREATURE, 100, new SpawnSettings.SpawnEntry(EntityType.PANDA, 3, 5)))
      .build();
   public static final MineEffect field_59179 = MineEffect.builder("parrots")
      .style(field_59166)
      .condition(MineUnlockCondition.method_69661((serverWorld, serverPlayerEntity, itemStack) -> itemStack.isOf(Items.COOKIE)))
      .item(Items.PARROT_SPAWN_EGG)
      .group(PASSIVE_MOBS)
      .method_69939(arg -> arg.addSpawn(SpawnGroup.CREATURE, 100, new SpawnSettings.SpawnEntry(EntityType.PARROT, 3, 5)))
      .build();
   public static final MineEffect field_59180 = MineEffect.builder("rabbits")
      .style(field_59166)
      .condition(MineUnlockCondition.method_69621((serverWorld, serverPlayerEntity, damageSource, float_) -> damageSource.isOf(DamageTypes.FALL) && float_ > 10.0F))
      .item(Items.RABBIT_SPAWN_EGG)
      .group(PASSIVE_MOBS)
      .method_69939(arg -> arg.addSpawn(SpawnGroup.CREATURE, 100, new SpawnSettings.SpawnEntry(EntityType.PARROT, 3, 5)))
      .build();
   public static final MineEffect field_59181 = MineEffect.builder("sniffers")
      .style(field_59166)
      .condition(MineUnlockCondition.method_69655((serverWorld, blockState) -> blockState.isIn(BlockTags.FLOWERS) && serverWorld.getRandom().nextFloat() < 0.05F))
      .item(Items.SNIFFER_SPAWN_EGG)
      .group(PASSIVE_MOBS)
      .method_69939(arg -> arg.addSpawn(SpawnGroup.CREATURE, 10, new SpawnSettings.SpawnEntry(EntityType.SNIFFER, 1, 2)))
      .build();
   public static final MineEffect field_59182 = MineEffect.builder("striders")
      .style(field_59166)
      .condition(MineUnlockCondition.method_69662((serverWorld, serverPlayerEntity, itemStack) -> itemStack.isOf(Items.SADDLE)))
      .item(Items.STRIDER_SPAWN_EGG)
      .group(PASSIVE_MOBS)
      .method_69939(arg -> arg.addSpawn(SpawnGroup.CREATURE, 25, new SpawnSettings.SpawnEntry(EntityType.STRIDER, 1, 2)))
      .build();
   public static final MineEffectGroup HOSTILE_MOBS = method_70019("hostile_mobs");
   public static final Style field_59211 = Style.EMPTY.withColor(Formatting.RED);
   public static final MineEffect field_59212 = MineEffect.builder("zombies")
      .group(HOSTILE_MOBS)
      .item(Items.ZOMBIE_SPAWN_EGG)
      .style(field_59211)
      .condition(
         MineUnlockCondition.method_69651(
            (serverWorld, serverPlayerEntity, entity) -> serverWorld.getRandom().nextFloat() < 0.05F && entity.getType().getSpawnGroup() == SpawnGroup.MONSTER
         )
      )
      .method_69960()
      .method_69928()
      .method_69939(arg -> arg.addSpawn(SpawnGroup.MONSTER, 95, new SpawnSettings.SpawnEntry(EntityType.ZOMBIE, 4, 4)))
      .build();
   public static final MineEffect field_59213 = MineEffect.builder("skeletons")
      .group(HOSTILE_MOBS)
      .item(Items.SKELETON_SPAWN_EGG)
      .style(field_59211)
      .condition(
         MineUnlockCondition.method_69651(
            (serverWorld, serverPlayerEntity, entity) -> serverWorld.getRandom().nextFloat() < 0.05F && entity.getType().getSpawnGroup() == SpawnGroup.MONSTER
         )
      )
      .method_69960()
      .method_69928()
      .method_69939(arg -> arg.addSpawn(SpawnGroup.MONSTER, 100, new SpawnSettings.SpawnEntry(EntityType.SKELETON, 4, 4)))
      .build();
   public static final MineEffect field_59214 = MineEffect.builder("spiders")
      .group(HOSTILE_MOBS)
      .item(Items.SPIDER_SPAWN_EGG)
      .style(field_59211)
      .condition(
         MineUnlockCondition.method_69651(
            (serverWorld, serverPlayerEntity, entity) -> serverWorld.getRandom().nextFloat() < 0.05F && entity.getType().getSpawnGroup() == SpawnGroup.MONSTER
         )
      )
      .method_69960()
      .method_69928()
      .method_69939(arg -> arg.addSpawn(SpawnGroup.MONSTER, 100, new SpawnSettings.SpawnEntry(EntityType.SPIDER, 4, 4)))
      .build();
   public static final MineEffect field_59215 = MineEffect.builder("cave_spiders")
      .group(HOSTILE_MOBS)
      .item(Items.CAVE_SPIDER_SPAWN_EGG)
      .style(field_59211)
      .condition(
         MineUnlockCondition.method_69651((serverWorld, serverPlayerEntity, entity) -> serverWorld.getRandom().nextFloat() < 0.2F && entity instanceof SpiderEntity)
      )
      .method_69960()
      .method_69939(arg -> arg.addSpawn(SpawnGroup.MONSTER, 50, new SpawnSettings.SpawnEntry(EntityType.CAVE_SPIDER, 4, 4)))
      .build();
   public static final MineEffect field_59216 = MineEffect.builder("creepers")
      .group(HOSTILE_MOBS)
      .item(Items.CREEPER_SPAWN_EGG)
      .style(field_59211)
      .condition(
         MineUnlockCondition.method_69651(
            (serverWorld, serverPlayerEntity, entity) -> serverWorld.getRandom().nextFloat() < 0.05F && entity.getType().getSpawnGroup() == SpawnGroup.MONSTER
         )
      )
      .method_69960()
      .method_69928()
      .method_69939(arg -> arg.addSpawn(SpawnGroup.MONSTER, 100, new SpawnSettings.SpawnEntry(EntityType.CREEPER, 4, 4)))
      .build();
   public static final MineEffect field_59217 = MineEffect.builder("slimes")
      .group(HOSTILE_MOBS)
      .item(Items.SLIME_SPAWN_EGG)
      .style(field_59211)
      .condition(
         MineUnlockCondition.method_69651(
            (serverWorld, serverPlayerEntity, entity) -> serverWorld.getRandom().nextFloat() < 0.05F && entity.getType().getSpawnGroup() == SpawnGroup.MONSTER
         )
      )
      .method_69960()
      .method_69928()
      .method_69939(arg -> arg.addSpawn(SpawnGroup.MONSTER, 100, new SpawnSettings.SpawnEntry(EntityType.SLIME, 4, 4)))
      .build();
   public static final MineEffect field_59218 = MineEffect.builder("endermen")
      .group(HOSTILE_MOBS)
      .item(Items.ENDERMAN_SPAWN_EGG)
      .style(field_59211)
      .method_69944(field_59212, field_59213, field_59216, field_59214)
      .condition(
         MineUnlockCondition.method_69651(
            (serverWorld, serverPlayerEntity, entity) -> serverWorld.getRandom().nextFloat() < 0.05F && entity.getType().getSpawnGroup() == SpawnGroup.MONSTER
         )
      )
      .method_69960()
      .method_69939(arg -> arg.addSpawn(SpawnGroup.MONSTER, 10, new SpawnSettings.SpawnEntry(EntityType.ENDERMAN, 1, 4)))
      .build();
   public static final MineEffect field_59219 = MineEffect.builder("witches")
      .group(HOSTILE_MOBS)
      .item(Items.WITCH_SPAWN_EGG)
      .style(field_59211)
      .method_69944(field_59212, field_59213, field_59216, field_59214)
      .condition(
         MineUnlockCondition.method_69651(
            (serverWorld, serverPlayerEntity, entity) -> serverWorld.getRandom().nextFloat() < 0.05F && entity.getType().getSpawnGroup() == SpawnGroup.MONSTER
         )
      )
      .method_69960()
      .method_69939(arg -> arg.addSpawn(SpawnGroup.MONSTER, 5, new SpawnSettings.SpawnEntry(EntityType.WITCH, 1, 1)))
      .build();
   public static final MineEffect field_59220 = MineEffect.builder("magma_cubes")
      .group(HOSTILE_MOBS)
      .item(Items.MAGMA_CUBE_SPAWN_EGG)
      .style(field_59211)
      .condition(
         MineUnlockCondition.method_69662((serverWorld, serverPlayerEntity, itemStack) -> itemStack.isOf(Items.MAGMA_CREAM) || itemStack.isOf(Items.MAGMA_BLOCK))
      )
      .method_69939(arg -> arg.addSpawn(SpawnGroup.MONSTER, 100, new SpawnSettings.SpawnEntry(EntityType.MAGMA_CUBE, 2, 5)))
      .build();
   public static final MineEffect field_59221 = MineEffect.builder("blazes")
      .group(HOSTILE_MOBS)
      .item(Items.BLAZE_SPAWN_EGG)
      .style(field_59211)
      .condition(MineUnlockCondition.method_69650((serverWorld, blockState) -> blockState.isOf(Blocks.NETHER_QUARTZ_ORE) || blockState.isOf(Blocks.NETHER_GOLD_ORE)))
      .method_69939(arg -> arg.addSpawn(SpawnGroup.MONSTER, 10, new SpawnSettings.SpawnEntry(EntityType.BLAZE, 2, 3)))
      .build();
   public static final MineEffect field_59222 = MineEffect.builder("breezes")
      .group(HOSTILE_MOBS)
      .item(Items.BREEZE_SPAWN_EGG)
      .style(field_59211)
      .condition(MineUnlockCondition.method_69662((serverWorld, serverPlayerEntity, itemStack) -> itemStack.isOf(Items.TRIAL_KEY)))
      .method_69939(arg -> arg.addSpawn(SpawnGroup.MONSTER, 10, new SpawnSettings.SpawnEntry(EntityType.BREEZE, 2, 3)))
      .build();
   public static final MineEffect field_59223 = MineEffect.builder("pillagers")
      .group(HOSTILE_MOBS)
      .item(Items.PILLAGER_SPAWN_EGG)
      .style(field_59211)
      .condition()
      .method_69939(arg -> arg.addSpawn(SpawnGroup.MONSTER, 10, new SpawnSettings.SpawnEntry(EntityType.PILLAGER, 1, 5)))
      .build();
   public static final MineEffect field_59224 = MineEffect.builder("vindicators")
      .group(HOSTILE_MOBS)
      .item(Items.VINDICATOR_SPAWN_EGG)
      .style(field_59211)
      .condition()
      .method_69939(arg -> arg.addSpawn(SpawnGroup.MONSTER, 10, new SpawnSettings.SpawnEntry(EntityType.VINDICATOR, 1, 1)))
      .build();
   public static final MineEffect field_59225 = MineEffect.builder("evokers")
      .group(HOSTILE_MOBS)
      .item(Items.EVOKER_SPAWN_EGG)
      .style(field_59211)
      .condition()
      .method_69939(arg -> arg.addSpawn(SpawnGroup.MONSTER, 10, new SpawnSettings.SpawnEntry(EntityType.EVOKER, 1, 1)))
      .build();
   public static final MineEffect field_59226 = MineEffect.builder("ravagers")
      .group(HOSTILE_MOBS)
      .item(Items.RAVAGER_SPAWN_EGG)
      .style(field_59211)
      .condition()
      .method_69939(arg -> arg.addSpawn(SpawnGroup.MONSTER, 1, new SpawnSettings.SpawnEntry(EntityType.RAVAGER, 1, 1)))
      .build();
   public static final MineEffect field_59227 = MineEffect.builder("illusioners")
      .group(HOSTILE_MOBS)
      .style(field_59211)
      .method_69937("illusioners")
      .condition()
      .method_69939(arg -> arg.addSpawn(SpawnGroup.MONSTER, 1, new SpawnSettings.SpawnEntry(EntityType.ILLUSIONER, 1, 1)))
      .build();
   public static final MineEffect field_59228 = MineEffect.builder("guardians")
      .group(HOSTILE_MOBS)
      .style(field_59211)
      .item(Items.GUARDIAN_SPAWN_EGG)
      .condition(MineUnlockCondition.method_69651((serverWorld, serverPlayerEntity, entity) -> entity.getType().isIn(EntityTypeTags.AQUATIC)))
      .method_69939(arg -> arg.addSpawn(SpawnGroup.MONSTER, 10, new SpawnSettings.SpawnEntry(EntityType.GUARDIAN, 1, 1)))
      .build();
   public static final MineEffect field_59229 = MineEffect.builder("endermites")
      .group(HOSTILE_MOBS)
      .style(field_59211)
      .item(Items.ENDERMITE_SPAWN_EGG)
      .condition(MineUnlockCondition.method_69650((serverWorld, blockState) -> blockState.isOf(Blocks.END_STONE) && serverWorld.getRandom().nextFloat() < 0.1))
      .method_69939(arg -> arg.addSpawn(SpawnGroup.MONSTER, 100, new SpawnSettings.SpawnEntry(EntityType.ENDERMITE, 2, 4)))
      .build();
   public static final MineEffect field_59230 = MineEffect.builder("shulkers")
      .group(HOSTILE_MOBS)
      .style(field_59211)
      .item(Items.SHULKER_SPAWN_EGG)
      .condition(MineUnlockCondition.method_69596(0.1F, EntityType.ENDERMITE))
      .method_69939(arg -> arg.addSpawn(SpawnGroup.MONSTER, 50, new SpawnSettings.SpawnEntry(EntityType.SHULKER, 1, 1)))
      .build();
   public static final MineEffect field_59231 = MineEffect.builder("ghasts")
      .group(HOSTILE_MOBS)
      .style(field_59211)
      .item(Items.GHAST_SPAWN_EGG)
      .condition(MineUnlockCondition.method_69650((serverWorld, blockState) -> blockState.isOf(Blocks.BONE_BLOCK)))
      .method_69939(arg -> arg.addSpawn(SpawnGroup.MONSTER, 50, new SpawnSettings.SpawnEntry(EntityType.GHAST, 1, 1)))
      .build();
   public static final MineEffect field_59232 = MineEffect.builder("zombified_piglins")
      .group(HOSTILE_MOBS)
      .style(field_59211)
      .item(Items.ZOMBIFIED_PIGLIN_SPAWN_EGG)
      .condition(MineUnlockCondition.method_69651((serverWorld, serverPlayerEntity, entity) -> entity instanceof PigEntity pigEntity && pigEntity.isOnFire()))
      .method_69939(arg -> arg.addSpawn(SpawnGroup.MONSTER, 100, new SpawnSettings.SpawnEntry(EntityType.ZOMBIFIED_PIGLIN, 4, 4)))
      .build();
   public static final MineEffect field_59233 = MineEffect.builder("piglins")
      .group(HOSTILE_MOBS)
      .style(field_59211)
      .item(Items.PIGLIN_SPAWN_EGG)
      .condition(
         MineUnlockCondition.method_69646(
            (serverWorld, serverPlayerEntity, animalEntity, itemStack) -> animalEntity instanceof PigEntity && itemStack.isOf(Items.GOLDEN_CARROT)
         )
      )
      .method_69939(
         arg -> arg.addSpawn(SpawnGroup.MONSTER, 100, new SpawnSettings.SpawnEntry(EntityType.PIGLIN, 4, 4))
            .addSpawn(SpawnGroup.MONSTER, 2, new SpawnSettings.SpawnEntry(EntityType.PIGLIN_BRUTE, 1, 1))
      )
      .build();
   public static final MineEffect field_59234 = MineEffect.builder("wither_skeletons")
      .group(HOSTILE_MOBS)
      .style(field_59211)
      .item(Items.ZOMBIFIED_PIGLIN_SPAWN_EGG)
      .condition(MineUnlockCondition.method_69596(0.1F, EntityType.SKELETON))
      .method_69939(arg -> arg.addSpawn(SpawnGroup.MONSTER, 25, new SpawnSettings.SpawnEntry(EntityType.WITHER_SKELETON, 1, 3)))
      .build();
   public static final MineEffect field_59235 = MineEffect.builder("bees")
      .group(HOSTILE_MOBS)
      .style(Style.EMPTY.withColor(Formatting.GREEN))
      .item(Items.BEE_SPAWN_EGG)
      .condition(MineUnlockCondition.method_69651((serverWorld, serverPlayerEntity, entity) -> entity instanceof BeeEntity))
      .method_69939(arg -> arg.addSpawn(SpawnGroup.CREATURE, 250, new SpawnSettings.SpawnEntry(EntityType.BEE, 5, 5)))
      .build();
   public static final MineEffect field_59236 = MineEffect.builder("hoglins")
      .group(HOSTILE_MOBS)
      .style(field_59211)
      .item(Items.HOGLIN_SPAWN_EGG)
      .condition(MineUnlockCondition.method_69596(0.01F, EntityType.PIG))
      .method_69939(
         arg -> arg.addSpawn(SpawnGroup.MONSTER, 25, new SpawnSettings.SpawnEntry(EntityType.HOGLIN, 1, 3))
      )
      .build();
   public static final MineEffect field_59185 = MineEffect.builder("zoglins")
      .group(HOSTILE_MOBS)
      .style(field_59211)
      .item(Items.ZOGLIN_SPAWN_EGG)
      .condition(
         MineUnlockCondition.method_69651(
            (serverWorld, serverPlayerEntity, entity) -> entity instanceof ZombieEntity zombieEntity
               && zombieEntity.getSteppingBlockState().isIn(BlockTags.NYLIUM)
         )
      )
      .method_69939(arg -> arg.addSpawn(SpawnGroup.MONSTER, 2, new SpawnSettings.SpawnEntry(EntityType.ZOGLIN, 1, 3)))
      .build();
   public static final MineEffect field_59186 = MineEffect.builder("icy")
      .style(Style.EMPTY.withColor(Formatting.BLUE))
      .item(Items.BLUE_ICE)
      .condition(MineUnlockCondition.method_69661((serverWorld, serverPlayerEntity, itemStack) -> itemStack.isIn(ItemTags.BOATS)))
      .method_69929(2.0F)
      .build();
   public static final MineEffect field_59187 = MineEffect.builder("bouncy")
      .style(Style.EMPTY.withColor(Formatting.GREEN))
      .item(Items.SLIME_BLOCK)
      .condition(MineUnlockCondition.method_69651((serverWorld, serverPlayerEntity, entity) -> entity instanceof SlimeEntity))
      .method_69929(2.0F)
      .build();
   public static final MineEffectGroup BASE_STONE = method_70055("base_stone");
   public static final MineEffect field_59189 = MineEffect.builder("base_stone")
      .group(BASE_STONE)
      .item(Items.STONE)
      .method_69962()
      .build();
   public static final MineEffect field_59190 = MineEffect.builder("base_blackstone")
      .group(BASE_STONE)
      .item(Items.BLACKSTONE)
      .method_69939(arg -> arg.modifySettings(argx -> argx.method_69806(Blocks.BLACKSTONE.getDefaultState())))
      .condition(MineUnlockCondition.method_69655((serverWorld, blockState) -> blockState.isOf(Blocks.BLACKSTONE) && serverWorld.getRandom().nextFloat() < 0.1F))
      .method_69960()
      .method_69928()
      .build();
   public static final MineEffect field_59191 = MineEffect.builder("base_diorite")
      .group(BASE_STONE)
      .item(Items.DIORITE)
      .method_69939(arg -> arg.modifySettings(argx -> argx.method_69806(Blocks.DIORITE.getDefaultState())))
      .condition(MineUnlockCondition.method_69655((serverWorld, blockState) -> blockState.isOf(Blocks.DIORITE) && serverWorld.getRandom().nextFloat() < 0.1F))
      .method_69960()
      .method_69928()
      .build();
   public static final MineEffect field_59192 = MineEffect.builder("base_andesite")
      .group(BASE_STONE)
      .item(Items.ANDESITE)
      .method_69939(arg -> arg.modifySettings(argx -> argx.method_69806(Blocks.ANDESITE.getDefaultState())))
      .condition(MineUnlockCondition.method_69655((serverWorld, blockState) -> blockState.isOf(Blocks.ANDESITE) && serverWorld.getRandom().nextFloat() < 0.1F))
      .method_69960()
      .method_69928()
      .build();
   public static final MineEffect field_59193 = MineEffect.builder("base_granite")
      .group(BASE_STONE)
      .item(Items.GRANITE)
      .method_69939(arg -> arg.modifySettings(argx -> argx.method_69806(Blocks.GRANITE.getDefaultState())))
      .condition(MineUnlockCondition.method_69655((serverWorld, blockState) -> blockState.isOf(Blocks.GRANITE) && serverWorld.getRandom().nextFloat() < 0.1F))
      .method_69960()
      .method_69928()
      .build();
   public static final MineEffect field_59194 = MineEffect.builder("base_tuff")
      .group(BASE_STONE)
      .item(Items.TUFF)
      .method_69939(arg -> arg.modifySettings(argx -> argx.method_69806(Blocks.TUFF.getDefaultState())))
      .condition(MineUnlockCondition.method_69655((serverWorld, blockState) -> blockState.isOf(Blocks.TUFF) && serverWorld.getRandom().nextFloat() < 0.1F))
      .method_69960()
      .method_69928()
      .build();
   public static final MineEffect field_59195 = MineEffect.builder("base_deepslate")
      .group(BASE_STONE)
      .item(Items.DEEPSLATE)
      .method_69939(arg -> arg.modifySettings(argx -> argx.method_69806(Blocks.DEEPSLATE.getDefaultState())))
      .condition(MineUnlockCondition.method_69655((serverWorld, blockState) -> blockState.isOf(Blocks.DEEPSLATE) && serverWorld.getRandom().nextFloat() < 0.1F))
      .method_69960()
      .method_69928()
      .build();
   public static final MineEffect field_59196 = MineEffect.builder("base_end_stone")
      .group(BASE_STONE)
      .item(Items.END_STONE)
      .method_69939(arg -> arg.modifySettings(argx -> argx.method_69806(Blocks.END_STONE.getDefaultState())))
      .condition(MineUnlockCondition.method_69655((serverWorld, blockState) -> blockState.isOf(Blocks.END_STONE) && serverWorld.getRandom().nextFloat() < 0.1F))
      .build();
   public static final Style field_59197 = Style.EMPTY.withColor(Formatting.LIGHT_PURPLE);
   public static final MineEffect field_59198 = MineEffect.builder("one_hp")
      .style(field_59197)
      .item(Items.POTION)
      .condition(MineUnlockCondition.method_69651((serverWorld, serverPlayerEntity, entity) -> serverPlayerEntity.getHealth() <= 1.0F))
      .method_69929(4.0F)
      .build();
   public static final MineEffect field_59199 = MineEffect.builder("wednesday_frogs")
      .style(field_59197)
      .item(Items.FROG_SPAWN_EGG)
      .condition(MineUnlockCondition.method_69651((serverWorld, serverPlayerEntity, entity) -> entity instanceof FrogEntity))
      .method_69929(1.2F)
      .build();
   public static final MineEffect field_59200 = MineEffect.builder("universal_anger")
      .style(field_59197)
      .item(Items.ROTTEN_FLESH)
      .condition(MineUnlockCondition.method_69642(true, field_59235))
      .method_69929(2.0F)
      .build();
   public static final MineEffect field_59201 = MineEffect.builder("soul_link")
      .style(field_59197)
      .item(Items.LEAD)
      .condition(MineUnlockCondition.method_69642(true, field_59198))
      .method_69929(2.0F)
      .method_69958()
      .build();
   // field_59202 (eternal_night): GameRules.DO_DAYLIGHT_CYCLE doesn't exist in this Minecraft version.
   // The game rule for controlling the daylight cycle is not available in the mod's target version.
   // TODO: Find an alternative approach or use a mixin to control the daylight cycle if needed.
//   public static final MineEffect field_59202 = MineEffect.builder("eternal_night")
//      .item(Items.BLUE_DYE)
//      .style(field_59197)
//      .method_69951(serverWorld -> {
//         serverWorld.getServer().getOverworld().setTimeOfDay(18000L);
//         serverWorld.getServer().getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(false, serverWorld.getServer());
//      })
//      .method_69957(serverWorld -> serverWorld.getServer().getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(true, serverWorld.getServer()))
//      .method_69952(field_59249, field_59252)
//      .method_69929(5.0F)
//      .method_69947(20)
//      .build();
// field_59203 (eternal_rain): depends on field_59202 (eternal_night) which is commented out.
//   public static final MineEffect field_59203 = MineEffect.builder("eternal_rain")
//      .item(Items.BLACK_DYE)
//      .style(field_59197)
//      .method_69951(serverWorld -> {
//         serverWorld.setRainGradient(1.0F);
//         serverWorld.getGameRules().setValue(GameRules.ADVANCE_WEATHER, false, serverWorld.getServer());
//      })
//      .method_69957(serverWorld -> {
//         serverWorld.setRainGradient(0.0F);
//         serverWorld.getGameRules().setValue(GameRules.ADVANCE_WEATHER, true, serverWorld.getServer());
//      })
//      .condition(MineUnlockCondition.method_69642(true, field_59202))
//      .method_69952(field_59249, field_59252)
//      .method_69929(2.0F)
//      .build();
// field_59204 (eternal_lightning): depends on field_59203 (eternal_rain) which is commented out.
//   public static final MineEffect field_59204 = MineEffect.builder("eternal_lightning")
//      .item(Items.YELLOW_DYE)
//      .style(field_59197)
//      .method_69951(serverWorld -> {
//         serverWorld.setThunderGradient(1.0F);
//         serverWorld.setRainGradient(1.0F);
//         serverWorld.getGameRules().setValue(GameRules.ADVANCE_WEATHER, false, serverWorld.getServer());
//      })
//      .method_69957(serverWorld -> {
//         serverWorld.setThunderGradient(0.0F);
//         serverWorld.setRainGradient(0.0F);
//         serverWorld.getGameRules().setValue(GameRules.ADVANCE_WEATHER, true, serverWorld.getServer());
//      })
//      .condition(MineUnlockCondition.method_69642(true, field_59203))
//      .method_69952(field_59249, field_59252)
//      .method_69929(5.0F)
//      .build();
// field_59205 (insomniacs): depends on field_59202 (eternal_night) which is commented out.
//   public static final MineEffect field_59205 = MineEffect.builder("insomniacs")
//      .item(Items.PHANTOM_SPAWN_EGG)
//      .style(field_59211)
//      .method_69959(
//         serverPlayerEntity -> serverPlayerEntity.getStatHandler().setStat(serverPlayerEntity, Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST), 72000)
//      )
//      .condition(MineUnlockCondition.method_69642(true, field_59202))
//      .method_69952(field_59249, field_59252)
//      .method_69929(2.0F)
//      .build();
   public static final MineEffect field_59206 = MineEffect.builder("no_drops")
      .style(field_59197)
      .item(Items.DIRT)
      .method_69947(20)
      .method_69929(5.0F)
      .build();
// field_59207 (ultrawarm): method_69693() doesn't exist on Float (temperature value), and
   // skyColor() doesn't exist on BiomeEffects in this Minecraft version.
//   public static final MineEffect field_59207 = MineEffect.builder("ultrawarm")
//      .style(field_59197)
//      .item(Items.LAVA_BUCKET)
//      .method_69939(
//         arg -> arg.addDimensionModifier(dt -> new DimensionType(
//            dt.hasFixedTime(), dt.hasSkyLight(), dt.hasCeiling(),
//            dt.coordinateScale(), dt.minY(), dt.height(),
//            dt.logicalHeight(), dt.infiniburn(),
//            dt.ambientLight(), dt.monsterSettings(),
//            dt.skybox(), dt.cardinalLightType(), dt.attributes(), dt.timelines()
//         ))
//            .addGlobalBiomeModifier(
//               argx -> argx.method_69677(builder -> builder.temperature(builder + 1.0F))
//                  .method_69680(builder -> builder.withSkyColor(ColorHelper.getArgb(234, 178, 255)))
//            )
//            .modifySettings(argx -> argx.method_69814(Blocks.LAVA.getDefaultState()))
//      )
//      .condition(MineUnlockCondition.method_69621((serverWorld, serverPlayerEntity, damageSource, float_) -> damageSource.isOf(DamageTypes.LAVA)))
//      .method_69960()
//      .method_69947(20)
//      .build();
// field_59208 (explosive_traps): getWorlds() returns Iterable<ServerWorld> which has no size() method.
//   public static final MineEffect field_59208 = MineEffect.builder("explosive_traps")
//      .style(field_59197)
//      .item(Items.TNT)
//      .method_69929(1.5F)
//      .method_69952(field_59164, field_59249, field_59252)
//      .condition(MineUnlockCondition.method_69659((serverWorld, serverPlayerEntity) -> serverWorld.getServer().getWorlds().size() > 10))
//      .build();
   public static final MineEffect field_59209 = MineEffect.builder("fish_out_of_water")
      .item(Items.TROPICAL_FISH)
      .style(field_59197)
      .condition(MineUnlockCondition.method_69661((serverWorld, serverPlayerEntity, itemStack) -> itemStack.isIn(ItemTags.FISHING_ENCHANTABLE)))
      .method_69952(field_59163)
      .method_69929(5.0F)
      .method_69946()
      .build();
   public static final MineEffect field_59210 = MineEffect.builder("kuiper_world")
      .method_69951(serverWorld -> {
         serverWorld.getServer().getOverworld().setTimeOfDay(18000L);
         serverWorld.getGameRules().setValue(GameRules.ADVANCE_TIME, false, serverWorld.getServer());
      })
      .method_69957(serverWorld -> serverWorld.getGameRules().setValue(GameRules.ADVANCE_TIME, true, serverWorld.getServer()))
      .method_69939(
         arg -> arg.setGeneratorFactory(
            (wrapperLookup, biomeSource, registryEntry) -> {
               RegistryEntryLookup<Biome> registryEntryLookup = wrapperLookup.getOrThrow(RegistryKeys.BIOME);
               FlatChunkGeneratorConfig flatChunkGeneratorConfig = new FlatChunkGeneratorConfig(
                  Optional.of(RegistryEntryList.of()), registryEntryLookup.getOrThrow(BiomeKeys.THE_VOID), List.of()
               );
               flatChunkGeneratorConfig.getLayers().add(new FlatChunkGeneratorLayer(1, Blocks.AIR));
               flatChunkGeneratorConfig.updateLayerBlocks();
               return new FlatChunkGenerator(flatChunkGeneratorConfig);
            }
         )
      )
      .method_69953()
      .method_69937("kuiper_world")
      .build();
   public static final MineEffect field_59238 = MineEffect.builder("ender_dragon_boss_fight")
      .item(Items.ENDER_DRAGON_SPAWN_EGG)
      .method_69951(serverWorld -> ((net.zhengzhengyiyi.accessor.MinecraftServerAccessor) serverWorld.getServer()).method_69081(new EndDragonBattleEvent()))
      // TODO: add END_PLATFORM feature via addGlobalBiomeModifier once GenerationSettings supports
      // mutable feature addition (requires a mixin accessor for GenerationSettings).
      .method_69953()
      .method_69946()
      .method_69952(field_59249, field_59252)
      .build();
   public static final MineEffect field_59239 = MineEffect.builder("angry_ghast_boss_fight")
      .item(Items.GHAST_TEAR)
      .method_69951(
         serverWorld -> ((net.zhengzhengyiyi.accessor.MinecraftServerAccessor) serverWorld.getServer()).method_69081(
            WaveEvent.builder(serverWorld, "angry_ghast")
               .addWave(
                  wave -> wave.addGroup(
                        group -> group.type(EntityType.GHAST)
                           .spawnStrategy(
                              strategy -> strategy.type(WaveEvent.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(25, 25, 25))
                           )
                     )
                     .hideBar()
                     .countdown(Text.translatable("world.event.angry_ghast"))
                     .delay(1200)
               )
               .build()
         )
      )
      .method_69959(p -> p.giveItemStack(new ItemStack(net.zhengzhengyiyi.item.ModItems.FIREBALL_WAND)))
      .method_69953()
      .method_69946()
      .build();
   public static final MineEffect field_59240 = MineEffect.builder("raid")
      .item(Items.OMINOUS_BOTTLE)
      .method_69951(
         serverWorld -> ((net.zhengzhengyiyi.accessor.MinecraftServerAccessor) serverWorld.getServer()).method_69081(
            WaveEvent.builder(serverWorld, "raid")
               .addWave(
                  wave -> wave.addGroup(
                        group -> group.type(EntityType.PILLAGER)
                           .count(5)
                           .spawnStrategy(strategy -> strategy.type(WaveEvent.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(0, 3, 0)))
                     )
                     .delay(200)
               )
               .addWave(
                  wave -> wave.addGroup(
                        group -> group.type(EntityType.PILLAGER)
                           .count(5)
                           .spawnStrategy(strategy -> strategy.type(WaveEvent.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(0, 3, 0)))
                     )
                     .addGroup(
                        group -> group.type(EntityType.VINDICATOR)
                           .count(3)
                           .spawnStrategy(strategy -> strategy.type(WaveEvent.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(0, 3, 0)))
                     )
                     .delay(400)
               )
               .addWave(
                  wave -> wave.addGroup(
                        group -> group.type(EntityType.PILLAGER)
                           .count(5)
                           .spawnStrategy(strategy -> strategy.type(WaveEvent.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(0, 3, 0)))
                     )
                     .addGroup(
                        group -> group.type(EntityType.VINDICATOR)
                           .count(3)
                           .spawnStrategy(strategy -> strategy.type(WaveEvent.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(0, 3, 0)))
                     )
                     .addGroup(
                        group -> group.type(EntityType.RAVAGER)
                           .count(1)
                           .spawnStrategy(strategy -> strategy.type(WaveEvent.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(0, 3, 0)))
                     )
                     .delay(600)
               )
               .build()
         )
      )
      .method_69959(serverPlayerEntity -> serverPlayerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.BAD_OMEN, -1)))
      .method_69953()
      .method_69946()
      .build();
   public static final MineEffect field_59241 = MineEffect.builder("wither_boss_fight")
      .item(Items.WITHER_SPAWN_EGG)
      .method_69951(
         serverWorld -> ((net.zhengzhengyiyi.accessor.MinecraftServerAccessor) serverWorld.getServer()).method_69081(
            WaveEvent.builder(serverWorld, "wither")
               .addWave(
                  wave -> wave.addGroup(
                        group -> group.type(EntityType.WITHER)
                           .spawnStrategy(strategy -> strategy.type(WaveEvent.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(0, 3, 0)))
                     )
                     .hideBar()
                     .countdown(Text.translatable("world.event.wither"))
                     .delay(600)
               )
               .build()
         )
      )
      .method_69959(p -> p.giveItemStack(new ItemStack(net.zhengzhengyiyi.item.ModItems.WIND_CHARGE_WAND)))
      .method_69953()
      .method_69946()
      .build();
   public static final MineEffect field_59242 = MineEffect.builder("the_enderman_boss_fight")
      .item(Items.ENDER_PEARL)
      .method_69951(
         serverWorld -> ((net.zhengzhengyiyi.accessor.MinecraftServerAccessor) serverWorld.getServer()).method_69081(
            WaveEvent.builder(serverWorld, "enderman")
               .addWave(
                  wave -> wave.addGroup(
                        group -> group.type(EntityType.ENDERMAN)
                           .spawnStrategy(strategy -> strategy.type(WaveEvent.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(0, 3, 0)))
                     )
                     .countdown(Text.translatable("world.event.enderman"))
                     .delay(600)
               )
               .build()
         )
      )
      .method_69959(p -> p.giveItemStack(new ItemStack(net.zhengzhengyiyi.item.ModItems.ENDER_PEARL_LAUNCHER)))
      .method_69953()
      .method_69946()
      .build();
   public static final MineEffect field_59243 = MineEffect.builder("spooky_scary_skeletons_boss_fight")
      .item(Items.SKELETON_SKULL)
      .method_69951(
         serverWorld -> ((net.zhengzhengyiyi.accessor.MinecraftServerAccessor) serverWorld.getServer()).method_69081(
            WaveEvent.builder(serverWorld, "spooky_scary_skeletons")
               .addWave(
                  wave -> wave.addGroup(
                        group -> group.type(EntityType.SKELETON)
                           .count(10)
                           .spawnStrategy(strategy -> strategy.type(WaveEvent.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(0, 3, 0)))
                     )
                     .delay(600)
               )
               .addWave(
                  wave -> wave.addGroup(
                        group -> group.type(EntityType.WITHER_SKELETON)
                           .count(10)
                           .spawnStrategy(strategy -> strategy.type(WaveEvent.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(0, 3, 0)))
                     )
                     .delay(600)
               )
               .addWave(
                  wave -> wave.addGroup(
                        group -> group.type(EntityType.WITHER_SKELETON)
                           .count(20)
                           .spawnStrategy(strategy -> strategy.type(WaveEvent.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(0, 3, 0)))
                     )
                     .addGroup(
                        group -> group.type(EntityType.SKELETON)
                           .count(20)
                           .spawnStrategy(strategy -> strategy.type(WaveEvent.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(0, 3, 0)))
                     )
                     .delay(600)
               )
               .build()
         )
      )
      .method_69953()
      .method_69946()
      .build();
   public static final MineEffect field_59244 = MineEffect.builder("warden_boss_fight")
      .item(Items.WARDEN_SPAWN_EGG)
      .method_69951(
         serverWorld -> ((net.zhengzhengyiyi.accessor.MinecraftServerAccessor) serverWorld.getServer()).method_69081(
            WaveEvent.builder(serverWorld, "warden")
               .addWave(
                  wave -> wave.addGroup(
                        group -> group.type(EntityType.WARDEN)
                           .spawnStrategy(strategy -> strategy.type(WaveEvent.SpawnType.WARDEN_ARENA))
                     )
                     .hideBar()
                     .countdown(Text.translatable("world.event.warden"))
               )
               .build()
         )
      )
      .condition(
         MineUnlockCondition.method_69650(
            (serverWorld, blockState) -> blockState.isOf(Blocks.AMETHYST_BLOCK)
               || blockState.isOf(Blocks.AMETHYST_CLUSTER)
               || blockState.isOf(Blocks.BUDDING_AMETHYST)
               || blockState.isOf(Blocks.SMALL_AMETHYST_BUD)
               || blockState.isOf(Blocks.MEDIUM_AMETHYST_BUD)
               || blockState.isOf(Blocks.LARGE_AMETHYST_BUD)
         )
      )
      .method_69946()
      .build();
   public static final MineEffect field_59245 = MineEffect.builder("small_but_deadly_boss_fight")
      .item(Items.SILVERFISH_SPAWN_EGG)
      .method_69942(BiomeKeys.WINDSWEPT_GRAVELLY_HILLS)
      .method_69951(
         serverWorld -> ((net.zhengzhengyiyi.accessor.MinecraftServerAccessor) serverWorld.getServer()).method_69081(
            WaveEvent.builder(serverWorld, "small_but_deadly")
            .addWave(
                    wave -> wave.addGroup(
                          group -> group.type(EntityType.SILVERFISH)
                             .count(25)
                             .spawnStrategy(strategy -> strategy.type(WaveEvent.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(0, 3, 0)))
                       )
                       .delay(300)
                 )
                 .addWave(
                    wave -> wave.addGroup(
                          group -> group.type(EntityType.SILVERFISH)
                             .count(25)
                             .spawnStrategy(strategy -> strategy.type(WaveEvent.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(0, 3, 0)))
                       )
                       .addGroup(
                          group -> group.type(EntityType.ZOMBIE)
                             .baby(true)
                             .count(3)
                             .spawnStrategy(strategy -> strategy.type(WaveEvent.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(0, 3, 0)))
                       )
                       .delay(600)
                 )
                 .addWave(
                    wave -> wave.addGroup(
                          group -> group.type(EntityType.SILVERFISH)
                             .count(30)
                             .spawnStrategy(strategy -> strategy.type(WaveEvent.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(0, 3, 0)))
                       )
                       .addGroup(
                          group -> group.type(EntityType.ZOMBIE)
                             .baby(true)
                             .count(4)
                             .spawnStrategy(strategy -> strategy.type(WaveEvent.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(0, 3, 0)))
                       )
                       .addGroup(
                          group -> group.type(EntityType.VEX)
                             .count(3)
                             .spawnStrategy(strategy -> strategy.type(WaveEvent.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(0, 3, 0)))
                       )
                       .delay(1200)
                 )
                 .build()
         )
      )
      .method_69953()
      .method_69946()
      .build();

   public static boolean method_70003(ServerWorld serverWorld, BlockPos blockPos) {
      for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-1, -1, -1), blockPos.add(1, 1, 1))) {
         if (serverWorld.getLightLevel(blockPos2) > 0) {
            return false;
         }
      }

      return true;
   }

   public static MineEffect method_70022(Registry<MineEffect> registry) {
      return field_59198;
   }

   public static MineEffectGroup method_70057(Registry<MineEffectGroup> registry) {
      return WORLD_TYPE;
   }

   public static MineEffectGroup method_70019(String string) {
      return Registry.register(AprilsLegacy.MINE_EFFECTS, string, new MineEffectGroup(false));
   }

   public static MineEffectGroup method_70055(String string) {
      return Registry.register(AprilsLegacy.MINE_EFFECTS, string, new MineEffectGroup(true));
   }

   public static ItemStack method_70013(MineEffect arg, boolean bl) {
      return method_70023(Text.translatable("item.minecraft.mine_ingredient.desc", arg.name()), bl, List.of(arg));
   }

   public static ItemStack method_70023(Text text, boolean bl, List<MineEffect> list) {
      ItemStack itemStack = net.zhengzhengyiyi.item.ModItems.MINE_INGREDIENT.getDefaultStack();
      itemStack.set(ModDataComponentTypes.WORLD_MODIFIERS, new class_11056(list, list.size() <= 1));
      itemStack.set(DataComponentTypes.ITEM_NAME, text);
      if (bl) {
         itemStack.set(ModDataComponentTypes.WORLD_EFFECT_UNLOCK, Unit.INSTANCE);
      }
      // Set the item icon from the effect's itemModel so the slot shows e.g. a sheep spawn egg.
      if (list.size() == 1 && list.get(0).itemModel() != null) {
         itemStack.set(DataComponentTypes.ITEM_MODEL, list.get(0).itemModel());
      }
      return itemStack;
   }

   public static <T extends MineEffectComponent> Stream<T> method_70020(List<MineEffect> list, Class<T> class_) {
      return list.stream().flatMap(arg -> arg.method_69922(class_));
   }

   public static void method_69994() {
   }
}
