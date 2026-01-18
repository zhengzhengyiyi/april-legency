package net.zhengzhengyiyi;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemGroups;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.zhengzhengyiyi.advancement.VoteCriteria;
import net.zhengzhengyiyi.block.ModBlocks;
import net.zhengzhengyiyi.command.VoteCommands;
import net.zhengzhengyiyi.datagen.ModWorldGenerator;
import net.zhengzhengyiyi.entity.ModEntities;
import net.zhengzhengyiyi.feature.CraterFeature;
import net.zhengzhengyiyi.feature.CraterFeatureConfig;
import net.zhengzhengyiyi.feature.LunarBaseFeature;
import net.zhengzhengyiyi.item.ModItems;
import net.zhengzhengyiyi.network.class_8481;
import net.zhengzhengyiyi.rules.VoteRules;
import net.zhengzhengyiyi.stat.VoteStats;
import net.zhengzhengyiyi.util.TickScheduler;
import net.zhengzhengyiyi.vote.VoteManager;
import net.zhengzhengyiyi.vote.VoteRegistries;
import net.zhengzhengyiyi.vote.VoteServer;
import net.zhengzhengyiyi.network.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AprilsLegacy implements ModInitializer {
	public static final String MOD_ID = "aprils-legacy";
	
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
	
	private static void registryNetworkPacket() {
		PayloadTypeRegistry.playS2C().register(class_8480.PAYLOAD_ID, PacketCodec.of((v, b) -> v.write(b), class_8480::new));
        PayloadTypeRegistry.playS2C().register(class_8481.PAYLOAD_ID, PacketCodec.of((v, b) -> v.write(b), class_8481::new));
        PayloadTypeRegistry.playS2C().register(class_8482.PAYLOAD_ID, PacketCodec.of((v, b) -> v.write(b), class_8482::new));
        PayloadTypeRegistry.playS2C().register(class_8483.PAYLOAD_ID, PacketCodec.of((v, b) -> v.write(b), class_8483::new));
        PayloadTypeRegistry.playS2C().register(VoteRuleSyncS2CPacket.PAYLOAD_ID, PacketCodec.of((v, b) -> v.write(b), VoteRuleSyncS2CPacket::new));
        PayloadTypeRegistry.playS2C().register(VoteUpdateS2CPacket.PAYLOAD_ID, PacketCodec.of((v, b) -> v.write(b), VoteUpdateS2CPacket::new));

        PayloadTypeRegistry.playC2S().register(class_8258.PAYLOAD_ID, PacketCodec.of((v, b) -> v.write(b), class_8258::new));
        PayloadTypeRegistry.playC2S().register(class_8484.PAYLOAD_ID, PacketCodec.of((v, b) -> v.write(b), class_8484::new));
        
        ServerPlayNetworking.registerGlobalReceiver(class_8258.PAYLOAD_ID, (payload, context) -> {
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
		
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
		    VoteCommands.register(dispatcher, registryAccess);
		});
		
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
		    dispatcher.register(CommandManager.literal("test")
		        .executes(context -> {
		            var source = context.getSource();
		            var server = source.getServer();
		            
		            source.sendFeedback(() -> Text.literal("§e[Debug] testing mod"), false);
		            
		            VoteManager manager = ((VoteServer) server).getVoteManager();
		            
		            if (manager != null) {
		                AprilsLegacy.LOGGER.info("test: registries has {} rules", 
		                    net.zhengzhengyiyi.vote.VoteRegistries.VOTE_RULE_TYPE.size());
		                
		                source.sendFeedback(() -> Text.literal("§a test finished"), false);
		            } else {
		                source.sendError(Text.literal("error: vote manager is null"));
		            }
		            
		            return 1;
		        }));
		});
		
		LOGGER.info(MOD_ID + " init, please enjoy april fools");
	}
	
	public static RegistryKey<ConfiguredFeature<?, ?>> registerFeature(String id) {
		return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Identifier.of("zhengzhengyiyi", id));
	}
}
