package net.zhengzhengyiyi.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

import java.util.List;

public class ModScreenHandlerType {
	public static final ScreenHandlerType<DimensionControlScreenHandler> DIMENSION_CONTROL = register("dimension_control", DimensionControlScreenHandler::new);

	private static final PacketCodec<PacketByteBuf, List<Integer>> STATS_CODEC =
		PacketCodecs.INTEGER.collect(PacketCodecs.toList()).cast();

	public static final ScreenHandlerType<net.zhengzhengyiyi.mine.MineEffectGenerator> MINE_CRAFTER =
		Registry.register(Registries.SCREEN_HANDLER, "mine_crafter",
			new ExtendedScreenHandlerType<>((syncId, inv, stats) ->
				new net.zhengzhengyiyi.mine.MineEffectGenerator(syncId, inv, stats),
				STATS_CODEC));
	
	static <T extends ScreenHandler> ScreenHandlerType<T> register(String id, ScreenHandlerType.Factory<T> factory) {
		return Registry.register(Registries.SCREEN_HANDLER, id, new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES));
	}

	static <T extends ScreenHandler> ScreenHandlerType<T> register(String id, ScreenHandlerType.Factory<T> factory, FeatureFlag... requiredFeatures) {
		return Registry.register(Registries.SCREEN_HANDLER, id, new ScreenHandlerType<>(factory, FeatureFlags.FEATURE_MANAGER.featureSetOf(requiredFeatures)));
	}
	
	public static void init() {
		
	}
}
