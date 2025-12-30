package net.zhengzhengyiyi;

import net.fabricmc.api.ClientModInitializer;
import net.zhengzhengyiyi.network.ClientModNetworkManager;

public class AprilsLegacyClient implements ClientModInitializer {
	
	@Override
	public void onInitializeClient() {
		ClientModNetworkManager.registerReceivers();
	}
}
