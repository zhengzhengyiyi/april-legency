package net.zhengzhengyiyi;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.zhengzhengyiyi.command.VoteCommands;
import net.zhengzhengyiyi.network.class_8481;
import net.zhengzhengyiyi.rules.VoteRules;
import net.zhengzhengyiyi.vote.VoteManager;
import net.zhengzhengyiyi.vote.VoteRegistries;
import net.zhengzhengyiyi.vote.VoteServer;
import net.zhengzhengyiyi.network.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AprilsLegacy implements ModInitializer {
	public static final String MOD_ID = "aprils-legacy";
	
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	
	private static void registryNetworkPacket() {
		PayloadTypeRegistry.playS2C().register(class_8480.PAYLOAD_ID, PacketCodec.of((v, b) -> v.write(b), class_8480::new));
        PayloadTypeRegistry.playS2C().register(class_8481.PAYLOAD_ID, PacketCodec.of((v, b) -> v.write(b), class_8481::new));
        PayloadTypeRegistry.playS2C().register(class_8482.PAYLOAD_ID, PacketCodec.of((v, b) -> v.write(b), class_8482::new));
        PayloadTypeRegistry.playS2C().register(class_8483.PAYLOAD_ID, PacketCodec.of((v, b) -> v.write(b), class_8483::new));
        PayloadTypeRegistry.playS2C().register(VoteRuleSyncS2CPacket.PAYLOAD_ID, PacketCodec.of((v, b) -> v.write(b), VoteRuleSyncS2CPacket::new));
        PayloadTypeRegistry.playS2C().register(VoteUpdateS2CPacket.PAYLOAD_ID, PacketCodec.of((v, b) -> v.write(b), VoteUpdateS2CPacket::new));

        PayloadTypeRegistry.playC2S().register(class_8258.PAYLOAD_ID, PacketCodec.of((v, b) -> v.write(b), class_8258::new));
        PayloadTypeRegistry.playC2S().register(class_8484.PAYLOAD_ID, PacketCodec.of((v, b) -> v.write(b), class_8484::new));
	}

	@Override
	public void onInitialize() {
		registryNetworkPacket();
		
		VoteRules.init();
		VoteRegistries.init();
		
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
}
