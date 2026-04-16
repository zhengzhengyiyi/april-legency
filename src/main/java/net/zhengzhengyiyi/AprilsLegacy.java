package net.zhengzhengyiyi;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
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
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;
import net.zhengzhengyiyi.advancement.MineCriteria;
import net.zhengzhengyiyi.advancement.VoteCriteria;
import net.zhengzhengyiyi.biome.ModBiomeKeys;
import net.zhengzhengyiyi.block.ModBlocks;
import net.zhengzhengyiyi.command.DebugDimensionCommand;
import net.zhengzhengyiyi.command.DebugdimCommand;
import net.zhengzhengyiyi.command.LevelCommand;
import net.zhengzhengyiyi.command.RoomCommand;
import net.zhengzhengyiyi.command.TransformCommand;
import net.zhengzhengyiyi.command.UnlockWorldEffectCommand;
import net.zhengzhengyiyi.command.VoteCommands;
import net.zhengzhengyiyi.command.WarpCommand;
import net.zhengzhengyiyi.component.ModDataComponentTypes;
import net.zhengzhengyiyi.datagen.ModWorldGenerator;
import net.zhengzhengyiyi.entity.ModEntities;
import net.zhengzhengyiyi.entity.task.ModMemoryModuleTypes;
import net.zhengzhengyiyi.feature.CraterFeature;
import net.zhengzhengyiyi.feature.CraterFeatureConfig;
import net.zhengzhengyiyi.feature.LunarBaseFeature;
import net.zhengzhengyiyi.generator.generation.RainbowBlockStateProvider;
import net.zhengzhengyiyi.item.ModItems;
import net.zhengzhengyiyi.mine.MineEffect;
import net.zhengzhengyiyi.mine.SpecialMine;
import net.zhengzhengyiyi.mine.class_11099;
import net.zhengzhengyiyi.mine.effect.MineEffectGroup;
import net.zhengzhengyiyi.mine.effect.class_11113;
import net.zhengzhengyiyi.network.class_8481;
import net.zhengzhengyiyi.rules.VoteRules;
import net.zhengzhengyiyi.screen.ModScreenHandlerType;
import net.zhengzhengyiyi.stat.VoteStats;
import net.zhengzhengyiyi.util.TickScheduler;
import net.zhengzhengyiyi.vote.VoteRegistries;
import xyz.nucleoid.fantasy.Fantasy;
import net.zhengzhengyiyi.network.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.serialization.MapCodec;

public class AprilsLegacy implements ModInitializer {
	public static final String MOD_ID = "aprils-legacy";
	
	public static Fantasy fantasy;
	public static MinecraftServer server;
	
	public static final RegistryKey<Registry<MineEffect>> WORLD_EFFECT = RegistryKey.ofRegistry(Identifier.ofVanilla("world_effect"));
	public static final RegistryKey<Registry<MineEffectGroup>> WORLD_EFFECT_SET = RegistryKey.ofRegistry(Identifier.ofVanilla("world_effect_set"));
	
	public static final RegistryKey<Registry<MapCodec<? extends class_11099>>> MINE_EVENT_TYPE = RegistryKey.ofRegistry(Identifier.ofVanilla("mine_event_type"));
	public static final Registry<MapCodec<? extends class_11099>> field_59578 = FabricRegistryBuilder.createSimple(MINE_EVENT_TYPE).buildAndRegister();
	
	public static final RegistryKey<Registry<MineEffect>> WORLD_EFFECT_KEY = RegistryKey.ofRegistry(WORLD_EFFECT.getValue());
	public static final RegistryKey<Registry<MineEffectGroup>> WORLD_EFFECT_SET_KEY = RegistryKey.ofRegistry(WORLD_EFFECT_SET.getValue());

	public static final Registry<MineEffect> MINE_EFFECT = FabricRegistryBuilder.createSimple(WORLD_EFFECT_KEY).buildAndRegister();
	public static final Registry<MineEffectGroup> MINE_EFFECTS = FabricRegistryBuilder.createSimple(WORLD_EFFECT_SET_KEY).buildAndRegister();
	
	public static final RegistryKey<Registry<SpecialMine>> SPECIAL_MINE_KEY = RegistryKey.ofRegistry(Identifier.ofVanilla("special_mine"));
	public static final Registry<SpecialMine> SPECIAL_MINE = FabricRegistryBuilder.createSimple(SPECIAL_MINE_KEY).buildAndRegister();
	
	public static final RegistryKey<Registry<net.zhengzhengyiyi.unlock.PlayerUnlock>> PLAYER_UNLOCK_KEY = RegistryKey.ofRegistry(Identifier.ofVanilla("player_unlock"));
	public static final Registry<net.zhengzhengyiyi.unlock.PlayerUnlock> PLAYER_UNLOCK = FabricRegistryBuilder.createSimple(PLAYER_UNLOCK_KEY).buildAndRegister();
	
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

		@SuppressWarnings("unused")
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
	
	public static final net.minecraft.datafixer.DataFixTypes SAVED_DATA_MINE_PROGRESS = net.minecraft.datafixer.DataFixTypes.SAVED_DATA_RANDOM_SEQUENCES;
	
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
        PayloadTypeRegistry.playS2C().register(ClientPacket6.ID, ClientPacket6.CODEC);
        PayloadTypeRegistry.playS2C().register(net.zhengzhengyiyi.network.ClientPacket4.PAYLOAD_ID, net.zhengzhengyiyi.network.ClientPacket4.CODEC);

        PayloadTypeRegistry.playC2S().register(VoteCastpacket.PAYLOAD_ID, PacketCodec.of((v, b) -> v.write(b), VoteCastpacket::new));
        PayloadTypeRegistry.playC2S().register(class_8484.PAYLOAD_ID, PacketCodec.of((v, b) -> v.write(b), class_8484::new));
        PayloadTypeRegistry.playC2S().register(net.zhengzhengyiyi.network.ServerPacket0.PAYLOAD_ID, net.zhengzhengyiyi.network.ServerPacket0.CODEC);
        
        ServerPlayNetworking.registerGlobalReceiver(VoteCastpacket.PAYLOAD_ID, (payload, context) -> {
        	payload.apply(context.player().networkHandler);
        });
        ServerPlayNetworking.registerGlobalReceiver(class_8484.PAYLOAD_ID, (payload, context) -> {
        	payload.apply(context.player().networkHandler);
        });
        ServerPlayNetworking.registerGlobalReceiver(net.zhengzhengyiyi.network.ServerPacket0.PAYLOAD_ID,
        	net.zhengzhengyiyi.network.ServerPacket0::handle);
	}

	@Override
	public void onInitialize() {
		registryNetworkPacket();
		
		TickScheduler.init();
		
		ModBlocks.init();
		VoteRules.init();
		VoteRegistries.init();
		VoteCriteria.init();
		MineCriteria.init();
		ModEntities.init();
		ModItems.init();
		ModMemoryModuleTypes.init();
		ModDimensionTypes.init();
		ModBiomeKeys.init();
		VoteStats.init();
		ModDataComponentTypes.init();
		ModScreenHandlerType.init();
		net.zhengzhengyiyi.feature.ModConfiguredFeatures.init();
		class_11099.register(field_59578);
		class_11113.method_69994();
		// Initialize SpecialMine registry — triggers all static field registrations
		net.zhengzhengyiyi.mine.SpecialMineData.init();
		// Initialize PlayerUnlock registry — triggers all static field registrations
		net.zhengzhengyiyi.unlock.PlayerUnlockData.init();
		
		Registry.register(Registries.BLOCK_STATE_PROVIDER_TYPE, Identifier.ofVanilla("rainbow_provider"), new BlockStateProviderType<>(RainbowBlockStateProvider.CODEC));
		
		
		
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE)
			.register((itemGroup) -> {
				itemGroup.add(ModItems.DOUBLE_TAG);
				itemGroup.add(ModItems.INT_TAG);
				itemGroup.add(ModItems.LIST_TAG);
				itemGroup.add(ModItems.BYTE_TAG);
				itemGroup.add(ModItems.LONG_TAG);
				itemGroup.add(ModItems.FLOAT_TAG);
				itemGroup.add(ModItems.SHORT_TAG);
				itemGroup.add(ModItems.COMPOUND_TAG);
				itemGroup.add(ModItems.STRING_TAG);
				itemGroup.add(ModItems.TAG);
				itemGroup.add(ModItems.LEFT_SQUARE);
				itemGroup.add(ModItems.RIGHT_SQUARE);
				itemGroup.add(ModItems.LEFT_CURLY);
				itemGroup.add(ModItems.RIGHT_CURLY);
				itemGroup.add(ModItems.SYNTAX_ERROR);
				itemGroup.add(ModItems.PICKAXE_BLOCK_ITEM);
				itemGroup.add(ModItems.PLACE_BLOCK_ITEM);
			});

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL)
			.register((itemGroup) -> {
				itemGroup.add(ModItems.field_58841);       // mine_crafter block
				itemGroup.add(ModItems.SHIMMERING_KEY);
				itemGroup.add(ModItems.SHIMMERING_DOOR);
				itemGroup.add(ModItems.REVISIT_BLOCK);
				itemGroup.add(ModItems.MINE_ITEM);
				itemGroup.add(ModItems.MINE_INGREDIENT);
				itemGroup.add(ModItems.TROPHY);
				itemGroup.add(ModItems.MOB_TROPHY);
			});

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL)
			.register((itemGroup) -> {
				itemGroup.add(ModItems.SKY_BOX);
			});

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
			.register((itemGroup) -> {
				itemGroup.add(ModItems.LA_BAGUETTE);
				itemGroup.add(ModItems.ENDER_PEARL_LAUNCHER);
				itemGroup.add(ModItems.FIREBALL_WAND);
				itemGroup.add(ModItems.WIND_CHARGE_WAND);
				itemGroup.add(ModItems.WINGS);
				itemGroup.add(ModItems.EXIT_EYE);
				itemGroup.add(ModItems.ENDER_EYE_ITEM);
			});

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
			.register((itemGroup) -> {
				itemGroup.add(ModItems.CHEESE_ITEM);
			});
		
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
		    VoteCommands.register(dispatcher, registryAccess);
		    DebugDimensionCommand.register(dispatcher);
		    WarpCommand.register(dispatcher);
		    TransformCommand.register(dispatcher, registryAccess);
		    DebugdimCommand.register(dispatcher);
		    LevelCommand.register(dispatcher, registryAccess);
		    RoomCommand.register(dispatcher);
		    UnlockWorldEffectCommand.register(dispatcher, registryAccess);
		});
		
		LOGGER.info(MOD_ID + " init, please enjoy april fools");
		
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			fantasy = Fantasy.get(server);
			AprilsLegacy.server = server;

			// Set overworld spawn position (mirrors Craftmine GameInstance.initHub)
			// Craftmine uses BlockPos(13, 2, 8) / angle 90.0F
			((net.minecraft.world.MutableWorldProperties) server.getSaveProperties().getMainWorldProperties())
				.setSpawnPoint(net.minecraft.world.WorldProperties.SpawnPoint.create(
					net.minecraft.world.World.OVERWORLD,
					new net.minecraft.util.math.BlockPos(13, 2, 8),
					90.0F, 0.0F
				));

			// Re-open all previously created mine dimensions so they are accessible after
			// a world reload. getLevelCount() pre-increments, so peekLevelCount() gives the
			// number of mines already created (IDs are level1 … levelN).
			net.zhengzhengyiyi.accessor.LevelPropertiesAccessor props =
				(net.zhengzhengyiyi.accessor.LevelPropertiesAccessor)(Object)
					server.getSaveProperties().getMainWorldProperties();
			int count = props.peekLevelCount();
			for (int i = 1; i <= count; i++) {
				net.minecraft.util.Identifier id = net.minecraft.util.Identifier.ofVanilla("level" + i);
				try {
					// getOrOpenPersistentWorld will load the existing world from disk when it
					// already exists, ignoring the placeholder config we pass here.
					xyz.nucleoid.fantasy.RuntimeWorldConfig placeholder =
						new xyz.nucleoid.fantasy.RuntimeWorldConfig()
							.setGenerator(server.getOverworld().getChunkManager().getChunkGenerator())
							.setDimensionType(server.getRegistryManager()
								.getEntryOrThrow(net.zhengzhengyiyi.ModDimensionTypes.GENERATED))
							.setSeed((long) id.getPath().hashCode());
					xyz.nucleoid.fantasy.RuntimeWorldHandle handle =
						fantasy.getOrOpenPersistentWorld(id, placeholder);
					if (handle.asWorld() != null) {
						LOGGER.info("[AprilsLegacy] Restored mine dimension: {}", id);
					} else {
						LOGGER.warn("[AprilsLegacy] Failed to restore mine dimension: {}", id);
					}
				} catch (Exception e) {
					LOGGER.error("[AprilsLegacy] Error restoring mine dimension {}: {}", id, e.getMessage());
				}
			}
		});
	}
	
	public static RegistryKey<ConfiguredFeature<?, ?>> registerFeature(String id) {
		return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Identifier.of("zhengzhengyiyi", id));
	}
}
