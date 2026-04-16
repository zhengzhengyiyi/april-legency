package net.zhengzhengyiyi.component;

import java.util.function.UnaryOperator;

import com.mojang.serialization.Codec;

import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Unit;
import net.zhengzhengyiyi.mine.SpecialMine;
import net.zhengzhengyiyi.mine.class_11056;
import net.zhengzhengyiyi.render.DimensionEffects;

public class ModDataComponentTypes {
	public static final ComponentType<DimensionEffects.class_11082> SKY = register(
		      "sky", builder -> builder.codec(DimensionEffects.class_11082.field_59008).packetCodec(DimensionEffects.class_11082.field_59009)
		   );
	
	public static final ComponentType<Unit> WORLD_EFFECT_UNLOCK = register(
		"world_effect_unlock", builder -> builder.codec(Unit.CODEC).packetCodec(Unit.PACKET_CODEC)
	);
	public static final ComponentType<class_11056> WORLD_MODIFIERS = register(
		"world_modifiers", builder -> builder.codec(class_11056.field_58857).packetCodec(class_11056.field_58858).cache()
	);
	public static final ComponentType<Unit> WORLD_EFFECT_UHINT = register(
		"world_effect_uhint", builder -> builder.codec(Unit.CODEC).packetCodec(Unit.PACKET_CODEC)
	);
	public static final ComponentType<Unit> MINE_ACTIVE = register("mine_active", builder -> builder.codec(Unit.CODEC).packetCodec(Unit.PACKET_CODEC));
	public static final ComponentType<SpecialMine> SPECIAL_MINE = register(
		"special_mine", builder -> builder.codec(SpecialMine.CODEC).packetCodec(SpecialMine.PACKET_CODEC)
	);
	public static final ComponentType<Boolean> MINE_COMPLETED = register(
		"mine_completed", builder -> builder.codec(Codec.BOOL).packetCodec(PacketCodecs.BOOLEAN)
	);
	public static final ComponentType<net.minecraft.registry.RegistryKey<net.minecraft.world.dimension.DimensionOptions>> DIMENSION_ID = register(
		"dimension_id",
		builder -> builder.codec(net.minecraft.registry.RegistryKey.createCodec(net.minecraft.registry.RegistryKeys.DIMENSION))
			.packetCodec(net.minecraft.registry.RegistryKey.createPacketCodec(net.minecraft.registry.RegistryKeys.DIMENSION)).cache()
	);
	public static final ComponentType<net.zhengzhengyiyi.block.TrophyType> TYPE_TROPHY = register(
		"trophy/type", builder -> builder.codec(net.zhengzhengyiyi.block.TrophyType.CODEC).packetCodec(net.zhengzhengyiyi.block.TrophyType.PACKET_CODEC)
	);
	public static final ComponentType<net.zhengzhengyiyi.component.MobTrophyComponent> TYPE_MOB_TROPHY = register(
		"mob_trophy/type", builder -> builder.codec(net.zhengzhengyiyi.component.MobTrophyComponent.CODEC).packetCodec(net.zhengzhengyiyi.component.MobTrophyComponent.PACKET_CODEC)
	);

	/** Mirrors craftmine DataComponentTypes.INSTANT_ROOM — identifies which hub room a Shimmering Key unlocks. */
	public static final ComponentType<net.zhengzhengyiyi.component.RoomComponent> INSTANT_ROOM = register(
		"instant_room", builder -> builder.codec(net.zhengzhengyiyi.component.RoomComponent.CODEC).packetCodec(net.zhengzhengyiyi.component.RoomComponent.PACKET_CODEC).cache()
	);
	
	private static <T> ComponentType<T> register(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
		return Registry.register(Registries.DATA_COMPONENT_TYPE, id, (builderOperator.apply(ComponentType.builder())).build());
	}
	
	public static void init() {
		
	}
}
