package net.zhengzhengyiyi;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemGroups;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;
import net.zhengzhengyiyi.advancement.VoteCriteria;
import net.zhengzhengyiyi.biome.ModBiomeKeys;
import net.zhengzhengyiyi.block.ModBlocks;
import net.zhengzhengyiyi.command.DebugDimensionCommand;
import net.zhengzhengyiyi.command.DebugdimCommand;
import net.zhengzhengyiyi.command.TransformCommand;
import net.zhengzhengyiyi.command.VoteCommands;
import net.zhengzhengyiyi.command.WarpCommand;
import net.zhengzhengyiyi.component.ModDataComponentTypes;
import net.zhengzhengyiyi.datagen.ModWorldGenerator;
import net.zhengzhengyiyi.entity.ModEntities;
import net.zhengzhengyiyi.feature.CraterFeature;
import net.zhengzhengyiyi.feature.CraterFeatureConfig;
import net.zhengzhengyiyi.feature.LunarBaseFeature;
import net.zhengzhengyiyi.generator.generation.RainbowBlockStateProvider;
import net.zhengzhengyiyi.item.ModItems;
import net.zhengzhengyiyi.network.class_8481;
import net.zhengzhengyiyi.rules.VoteRules;
import net.zhengzhengyiyi.screen.DimensionControlScreenHandler;
import net.zhengzhengyiyi.screen.ModScreenHandlerType;
import net.zhengzhengyiyi.stat.VoteStats;
import net.zhengzhengyiyi.util.TickScheduler;
import net.zhengzhengyiyi.vote.VoteRegistries;
import xyz.nucleoid.fantasy.Fantasy;
import net.zhengzhengyiyi.network.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AprilsLegacy implements ModInitializer {
	public static final String MOD_ID = "aprils-legacy";
	
	public static Fantasy fantasy;
	
	public static final ScreenHandlerType<DimensionControlScreenHandler> DIMENSION_CONTROL = ScreenHandlerType.register("dimension_control", DimensionControlScreenHandler::new);
	
	private static class SoundEventRegister {
		static RegistryEntry<SoundEvent> register(Identifier id, Identifier soundId, float distanceToTravel) {
			return Registry.registerReference(Registries.SOUND_EVENT, id, SoundEvent.of(soundId, distanceToTravel));
		}

		static SoundEvent register(String id) {
			return register(Identifier.ofVanilla(id));
		}

		static SoundEvent register(Identifier id) {
			return register(id, id);
		}

		static RegistryEntry.Reference<SoundEvent> registerReference(String id) {
			return registerReference(Identifier.ofVanilla(id));
		}

		static RegistryEntry.Reference<SoundEvent> registerReference(Identifier id) {
			return registerReference(id, id);
		}

		static SoundEvent register(Identifier id, Identifier soundId) {
			return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(soundId));
		}

		static RegistryEntry.Reference<SoundEvent> registerReference(Identifier id, Identifier soundId) {
			return Registry.registerReference(Registries.SOUND_EVENT, id, SoundEvent.of(soundId));
		}
	}
	
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	
	public static final RegistryKey<ConfiguredFeature<?, ?>> MEGA_CRATER = registerFeature("mega_crater");
	public static final RegistryKey<ConfiguredFeature<?, ?>> LARGE_CRATER = registerFeature("large_crater");
	public static final RegistryKey<ConfiguredFeature<?, ?>> SMALL_CRATER = registerFeature("small_crater");
	
	public static final Feature<CraterFeatureConfig> CRATER_FEATURE = Registry.register(
    	    Registries.FEATURE,
    	    Identifier.of("zhengzhengyiyi", "crater"),
    	    new CraterFeature(CraterFeatureConfig.CODEC)
    	);
	public static final Feature<DefaultFeatureConfig> LUNAR_BASE = ModWorldGenerator.register("lunar_base", new LunarBaseFeature(DefaultFeatureConfig.CODEC));
	
	public static final SoundEvent field_58484 = SoundEventRegister.register(Identifier.of("nothingtoseehere", "ui.player_unlock_success"));
	public static final SoundEvent field_58485 = SoundEventRegister.register(Identifier.of("nothingtoseehere", "ui.player_unlock_fail"));
	
	private static void registryNetworkPacket() {
		PayloadTypeRegistry.playS2C().register(voteResponsepacket.PAYLOAD_ID, PacketCodec.of((v, b) -> v.write(b), voteResponsepacket::new));
        PayloadTypeRegistry.playS2C().register(class_8481.PAYLOAD_ID, PacketCodec.of((v, b) -> v.write(b), class_8481::new));
        PayloadTypeRegistry.playS2C().register(class_8482.PAYLOAD_ID, PacketCodec.of((v, b) -> v.write(b), class_8482::new));
        PayloadTypeRegistry.playS2C().register(class_8483.PAYLOAD_ID, PacketCodec.of((v, b) -> v.write(b), class_8483::new));
        PayloadTypeRegistry.playS2C().register(VoteRuleSyncS2CPacket.PAYLOAD_ID, PacketCodec.of((v, b) -> v.write(b), VoteRuleSyncS2CPacket::new));
        PayloadTypeRegistry.playS2C().register(VoteUpdateS2CPacket.PAYLOAD_ID, PacketCodec.of((v, b) -> v.write(b), VoteUpdateS2CPacket::new));
        PayloadTypeRegistry.playS2C().register(ClientPacket0.PAYLOAD_ID, ClientPacket0.CODEC);

        PayloadTypeRegistry.playC2S().register(VoteCastpacket.PAYLOAD_ID, PacketCodec.of((v, b) -> v.write(b), VoteCastpacket::new));
        PayloadTypeRegistry.playC2S().register(class_8484.PAYLOAD_ID, PacketCodec.of((v, b) -> v.write(b), class_8484::new));
        
        ServerPlayNetworking.registerGlobalReceiver(VoteCastpacket.PAYLOAD_ID, (payload, context) -> {
        	payload.apply(context.player().networkHandler);
        });
        ServerPlayNetworking.registerGlobalReceiver(class_8484.PAYLOAD_ID, (payload, context) -> {
        	payload.apply(context.player().networkHandler);
        });
	}

	@Override
	public void onInitialize() {
		registryNetworkPacket();
		
		TickScheduler.init();
		
		ModBlocks.init();
		VoteRules.init();
		VoteRegistries.init();
		VoteCriteria.init();
		ModEntities.init();
		ModItems.init();
		ModDimensionTypes.init();
		ModBiomeKeys.init();
		VoteStats.init();
		ModScreenHandlerType.init();
		ModDataComponentTypes.init();
		
		Registry.register(Registries.BLOCK_STATE_PROVIDER_TYPE, Identifier.ofVanilla("rainbow_provider"), new BlockStateProviderType<>(RainbowBlockStateProvider.CODEC));
		
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE)
			.register((itemGroup) -> {
				itemGroup.add(ModItems.DOUBLE_TAG);
				itemGroup.add(ModItems.INT_TAG);
				itemGroup.add(ModItems.LIST_TAG);
				itemGroup.add(ModItems.BYTE_TAG);
				itemGroup.add(ModItems.LONG_TAG);
				itemGroup.add(ModItems.FLOAT_TAG);
				itemGroup.add(ModItems.PICKAXE_BLOCK_ITEM);
				itemGroup.add(ModItems.PLACE_BLOCK_ITEM);
			});
		
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL)
			.register((itemGroup) -> {
				itemGroup.add(ModItems.PICKAXE_BLOCK_ITEM);
				itemGroup.add(ModItems.PLACE_BLOCK_ITEM);
			});
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE)
			.register((itemGroup) -> {
				itemGroup.add(ModItems.PICKAXE_BLOCK_ITEM);
				itemGroup.add(ModItems.PLACE_BLOCK_ITEM);
			});
		
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
		    VoteCommands.register(dispatcher, registryAccess);
		    DebugDimensionCommand.register(dispatcher);
		    WarpCommand.register(dispatcher);
		    TransformCommand.register(dispatcher, registryAccess);
		    DebugdimCommand.register(dispatcher);
		});
		
		LOGGER.info(MOD_ID + " init, please enjoy april fools");
		
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			fantasy = Fantasy.get(server);
		});
	}
	
	public static RegistryKey<ConfiguredFeature<?, ?>> registerFeature(String id) {
		return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Identifier.of("zhengzhengyiyi", id));
	}
}
