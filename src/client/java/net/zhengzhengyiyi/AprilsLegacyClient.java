package net.zhengzhengyiyi;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.zhengzhengyiyi.gui.PendingVoteScreen;
import net.zhengzhengyiyi.network.ClientModNetworkManager;

public class AprilsLegacyClient implements ClientModInitializer {
	private final KeyBinding pendingVoteKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
		"key.aprils_legacy.pending_vote",
		InputUtil.Type.KEYSYM,
		InputUtil.UNKNOWN_KEY.getCode(),
		KeyBinding.Category.GAMEPLAY
	));
	
	@Override
	public void onInitializeClient() {
		ClientModNetworkManager.registerReceivers();
		
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (pendingVoteKey.wasPressed()) {
			    if (!(client.currentScreen instanceof PendingVoteScreen)) {
			        client.setScreen(new PendingVoteScreen());
			    }
			}
		});
	}
}
