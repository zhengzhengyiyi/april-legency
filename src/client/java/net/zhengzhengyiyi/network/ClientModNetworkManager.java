package net.zhengzhengyiyi.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.MinecraftClient;

public class ClientModNetworkManager {
    public static void registerReceivers() {
    	PayloadTypeRegistry.playS2C().register(VoteRuleSyncS2CPacket.ID, VoteRuleSyncS2CPacket.CODEC);
    	
    	ClientPlayNetworking.registerGlobalReceiver(VoteRuleSyncS2CPacket.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            
            client.execute(() -> {
                boolean shouldClear = payload.clearExisting();
                var action = payload.action();
                var values = payload.values();

//                 if (client.getNetworkHandler() != null) {
//                     ((VotePacketHandler)client.getNetworkHandler()).onVoteRuleSync(payload);
//                 }
                
                System.out.println("receved packet Action=" + action + ", number: " + values.size());
            });
        });
    }
}
