package net.zhengzhengyiyi.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.zhengzhengyiyi.world.FakePlayerEntity;

public class ModEntities {
	public static final EntityType<MoonCowEntity> MOON_COW = register(
		"moon_cow", EntityType.Builder.create(MoonCowEntity::new, SpawnGroup.CREATURE).dimensions(0.9F, 1.4F).maxTrackingRange(10)
	);
	
	public static final EntityType<FakePlayerEntity> FAKE_PLAYER = register("fake_player",
		EntityType.Builder.create(FakePlayerEntity::new, net.minecraft.entity.SpawnGroup.MISC)
	);
	
	public static void init() {
		FabricDefaultAttributeRegistry.register(MOON_COW, CowEntity.createCowAttributes());
	}
	
	private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> type) {
	   Identifier identifier = Identifier.of("zhengzhengyiyi", id);
	   RegistryKey<EntityType<?>> key = RegistryKey.of(RegistryKeys.ENTITY_TYPE, identifier);
	   return Registry.register(Registries.ENTITY_TYPE, key, type.build(key));
	}
}
