package net.zhengzhengyiyi;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.zhengzhengyiyi.command.VoteCommands;
import net.zhengzhengyiyi.rules.VoteRules;
import net.zhengzhengyiyi.vote.VoteManager;
import net.zhengzhengyiyi.vote.VoteRegistries;
import net.zhengzhengyiyi.vote.VoteServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AprilsLegacy implements ModInitializer {
	public static final String MOD_ID = "aprils-legacy";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info(MOD_ID + "init, please enjoy april fools");
		
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
	}
}
