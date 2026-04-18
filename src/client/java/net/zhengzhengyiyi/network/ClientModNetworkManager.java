package net.zhengzhengyiyi.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientModNetworkManager {
    public static void registerReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(voteResponsepacket.PAYLOAD_ID, (payload, context) -> {
            context.client().execute(() -> payload.apply(context.client().getNetworkHandler()));
        });

        ClientPlayNetworking.registerGlobalReceiver(class_8481.PAYLOAD_ID, (payload, context) -> {
            context.client().execute(() -> payload.apply(context.client().getNetworkHandler()));
        });

        ClientPlayNetworking.registerGlobalReceiver(class_8482.PAYLOAD_ID, (payload, context) -> {
            context.client().execute(() -> payload.apply(context.client().getNetworkHandler()));
        });

        ClientPlayNetworking.registerGlobalReceiver(class_8483.PAYLOAD_ID, (payload, context) -> {
            context.client().execute(() -> payload.apply(context.client().getNetworkHandler()));
        });

        ClientPlayNetworking.registerGlobalReceiver(VoteRuleSyncS2CPacket.PAYLOAD_ID, (payload, context) -> {
            context.client().execute(() -> payload.apply(context.client().getNetworkHandler()));
        });

        ClientPlayNetworking.registerGlobalReceiver(VoteUpdateS2CPacket.PAYLOAD_ID, (payload, context) -> {
            context.client().execute(() -> payload.apply(context.client().getNetworkHandler()));
        });
        
        ClientPlayNetworking.registerGlobalReceiver(ClientPacket0.PAYLOAD_ID, (payload, context) -> {
            context.client().execute(() -> payload.apply((ModClientPlayPacketListener)(Object)context.client().getNetworkHandler()));
        });
        
        ClientPlayNetworking.registerGlobalReceiver(ClientPacket6.ID, (payload, context) -> {
            context.client().execute(() -> payload.apply((ModClientPlayPacketListener)context.client().getNetworkHandler()));
        });

        // ClientPacket4: S2C unlock sync — update client unlock state after a purchase
        ClientPlayNetworking.registerGlobalReceiver(net.zhengzhengyiyi.network.ClientPacket4.PAYLOAD_ID, (payload, context) -> {
            context.client().execute(() -> {
                net.zhengzhengyiyi.gui.ClientUnlockState.apply(payload.unlocks(), payload.currency());
            });
        });
    }
}
