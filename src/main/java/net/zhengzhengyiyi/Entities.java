package net.zhengzhengyiyi;

import java.util.Optional;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.zhengzhengyiyi.world.FakePlayerEntity;

public class Entities {
	public static void init() {
		
	}
	
	public static final EntityType<FakePlayerEntity> FAKE_PLAYER = register("fake_player",
		EntityType.Builder.create(FakePlayerEntity::new, net.minecraft.entity.SpawnGroup.MISC)
	);
	
	private static <T extends Entity> EntityType<T> register(RegistryKey<EntityType<?>> key, EntityType.Builder<T> type) {
		return Registry.register(Registries.ENTITY_TYPE, key, type.build(key));
	}
	
	private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> type) {
		return register(keyOf(id), type);
	}
	
	public static Identifier getId(EntityType<?> type) {
		return Registries.ENTITY_TYPE.getId(type);
	}

	public static Optional<EntityType<?>> get(String id) {
		return Registries.ENTITY_TYPE.getOptionalValue(Identifier.tryParse(id));
	}
	
	private static RegistryKey<EntityType<?>> keyOf(String id) {
		return RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.ofVanilla(id));
	}
}
