package net.zhengzhengyiyi.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnLocationTypes;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;
import net.zhengzhengyiyi.world.FakePlayerEntity;

import net.zhengzhengyiyi.entity.pet.*;

public class ModEntities {
	public static final EntityType<MoonCowEntity> MOON_COW = register(
		"moon_cow", EntityType.Builder.create(MoonCowEntity::new, SpawnGroup.CREATURE).dimensions(0.9F, 1.4F).maxTrackingRange(10)
	);
	
	public static final EntityType<FakePlayerEntity> FAKE_PLAYER = register("fake_player",
		EntityType.Builder.create(FakePlayerEntity::new, net.minecraft.entity.SpawnGroup.MISC)
	);

	// Angry Ghast
	public static final EntityType<AngryGhastEntity> ANGRY_GHAST = register(
		"angry_ghast", EntityType.Builder.create(AngryGhastEntity::new, SpawnGroup.MONSTER).dimensions(8.0F, 8.0F).maxTrackingRange(100)
	);

	// Pet entities
	public static final EntityType<PetArmadilloEntity> PET_ARMADILLO = register(
		"pet_armadillo", EntityType.Builder.create(PetArmadilloEntity::new, SpawnGroup.CREATURE).dimensions(0.7F, 0.65F)
	);
	public static final EntityType<PetAxolotlEntity> PET_AXOLOTL = register(
		"pet_axolotl", EntityType.Builder.create(PetAxolotlEntity::new, SpawnGroup.AXOLOTLS).dimensions(0.75F, 0.42F)
	);
	public static final EntityType<PetBeeEntity> PET_BEE = register(
		"pet_bee", EntityType.Builder.create(PetBeeEntity::new, SpawnGroup.CREATURE).dimensions(0.7F, 0.6F)
	);
	public static final EntityType<PetCatEntity> PET_CAT = register(
		"pet_cat", EntityType.Builder.create(PetCatEntity::new, SpawnGroup.CREATURE).dimensions(0.6F, 0.7F)
	);
	public static final EntityType<PetChickenEntity> PET_CHICKEN = register(
		"pet_chicken", EntityType.Builder.create(PetChickenEntity::new, SpawnGroup.CREATURE).dimensions(0.4F, 0.7F)
	);
	public static final EntityType<PetCowEntity> PET_COW = register(
		"pet_cow", EntityType.Builder.create(PetCowEntity::new, SpawnGroup.CREATURE).dimensions(0.9F, 1.4F)
	);
	public static final EntityType<PetCreeperEntity> PET_CREEPER = register(
		"pet_creeper", EntityType.Builder.create(PetCreeperEntity::new, SpawnGroup.CREATURE).dimensions(0.6F, 1.7F)
	);
	public static final EntityType<PetFoxEntity> PET_FOX = register(
		"pet_fox", EntityType.Builder.create(PetFoxEntity::new, SpawnGroup.CREATURE).dimensions(0.6F, 0.7F)
	);
	public static final EntityType<PetFrogEntity> PET_FROG = register(
		"pet_frog", EntityType.Builder.create(PetFrogEntity::new, SpawnGroup.CREATURE).dimensions(0.5F, 0.5F)
	);
	public static final EntityType<PetMooshroomEntity> PET_MOOSHROOM = register(
		"pet_mooshroom", EntityType.Builder.create(PetMooshroomEntity::new, SpawnGroup.CREATURE).dimensions(0.9F, 1.4F)
	);
	public static final EntityType<PetPolarBearEntity> PET_POLAR_BEAR = register(
		"pet_polar_bear", EntityType.Builder.create(PetPolarBearEntity::new, SpawnGroup.CREATURE).dimensions(1.4F, 1.4F)
	);
	public static final EntityType<PetSlimeEntity> PET_SLIME = register(
		"pet_slime", EntityType.Builder.create(PetSlimeEntity::new, SpawnGroup.CREATURE).dimensions(2.04F, 2.04F)
	);
	public static final EntityType<PetTurtleEntity> PET_TURTLE = register(
		"pet_turtle", EntityType.Builder.create(PetTurtleEntity::new, SpawnGroup.CREATURE).dimensions(1.2F, 0.4F)
	);
	public static final EntityType<PetWolfEntity> PET_WOLF = register(
		"pet_wolf", EntityType.Builder.create(PetWolfEntity::new, SpawnGroup.CREATURE).dimensions(0.6F, 0.85F)
	);
	
	public static void init() {
		FabricDefaultAttributeRegistry.register(MOON_COW, CowEntity.createCowAttributes());
		SpawnRestriction.register(MOON_COW, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MoonCowEntity::canMobSpawn);

		FabricDefaultAttributeRegistry.register(ANGRY_GHAST, AngryGhastEntity.createAttributes().build());
		FabricDefaultAttributeRegistry.register(PET_ARMADILLO, PetArmadilloEntity.createAttributes().build());
		FabricDefaultAttributeRegistry.register(PET_AXOLOTL, PetAxolotlEntity.createAttributes().build());
		FabricDefaultAttributeRegistry.register(PET_BEE, PetBeeEntity.createAttributes().build());
		FabricDefaultAttributeRegistry.register(PET_CAT, PetCatEntity.createAttributes().build());
		FabricDefaultAttributeRegistry.register(PET_CHICKEN, PetChickenEntity.createAttributes().build());
		FabricDefaultAttributeRegistry.register(PET_COW, PetCowEntity.createAttributes().build());
		FabricDefaultAttributeRegistry.register(PET_CREEPER, PetCreeperEntity.createAttributes().build());
		FabricDefaultAttributeRegistry.register(PET_FOX, PetFoxEntity.createAttributes().build());
		FabricDefaultAttributeRegistry.register(PET_FROG, PetFrogEntity.createAttributes().build());
		FabricDefaultAttributeRegistry.register(PET_MOOSHROOM, PetMooshroomEntity.createAttributes().build());
		FabricDefaultAttributeRegistry.register(PET_POLAR_BEAR, PetPolarBearEntity.createAttributes().build());
		FabricDefaultAttributeRegistry.register(PET_SLIME, PetSlimeEntity.createAttributes().build());
		FabricDefaultAttributeRegistry.register(PET_TURTLE, PetTurtleEntity.createAttributes().build());
		FabricDefaultAttributeRegistry.register(PET_WOLF, PetWolfEntity.createAttributes().build());
	}
	
	private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> type) {
	   Identifier identifier = Identifier.of("zhengzhengyiyi", id);
	   RegistryKey<EntityType<?>> key = RegistryKey.of(RegistryKeys.ENTITY_TYPE, identifier);
	   return Registry.register(Registries.ENTITY_TYPE, key, type.build(key));
	}
}
