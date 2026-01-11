package net.zhengzhengyiyi;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.codec.PacketCodec;
import net.zhengzhengyiyi.gui.PendingVoteScreen;
import net.zhengzhengyiyi.network.ClientModNetworkManager;
import net.zhengzhengyiyi.renderer.ModEntityRenderers;
import net.zhengzhengyiyi.network.*;

public class AprilsLegacyClient implements ClientModInitializer {
	private final KeyBinding pendingVoteKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
		"key.aprils_legacy.pending_vote",
		InputUtil.Type.KEYSYM,
		InputUtil.GLFW_KEY_V,
		KeyBinding.Category.GAMEPLAY
	));
	
	/**
	 * THis method will not be called, it is all registried inside server side entry point.
	 */
	public static void registerNetworkPacket() {
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
	public void onInitializeClient() {
		ClientModNetworkManager.registerReceivers();
		ModEntityRenderers.register();
		
//		registerNetworkPacket();
		
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (pendingVoteKey.wasPressed()) {
			    if (!(client.currentScreen instanceof PendingVoteScreen)) {
			        client.setScreen(new PendingVoteScreen());
			    }
			}
		});
	}
}
