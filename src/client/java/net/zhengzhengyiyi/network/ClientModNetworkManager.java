package net.zhengzhengyiyi.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientModNetworkManager {
    public static void registerReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(class_8480.PAYLOAD_ID, (payload, context) -> {
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
    }
}