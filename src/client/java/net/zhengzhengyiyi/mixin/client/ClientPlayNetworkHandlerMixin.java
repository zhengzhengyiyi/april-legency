package net.zhengzhengyiyi.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.NetworkThreadUtils;
import net.zhengzhengyiyi.accessor.VoteClientPlayNetworkHandler;
import net.zhengzhengyiyi.gui.PendingVoteScreen;
import net.zhengzhengyiyi.gui.VoteScreen;
import net.zhengzhengyiyi.vote.ClientVoteManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin implements VoteClientPlayNetworkHandler {
    @Unique
    private final ClientVoteManager voteManager = new ClientVoteManager();

    @Unique
    public ClientVoteManager getVoteManager() {
        return this.voteManager;
    }
    
    @Unique
    /**
     * class_8478 packet
     * */
    public void updateVotes(VoteUpdateS2CPacket packet) {
    	MinecraftClient client = MinecraftClient.getInstance();
    	
        NetworkThreadUtils.forceMainThread(packet, this, client);
        
        if (packet.clear()) {
            this.voteManager = new ClientVoteManager();
        }
        
        if (this.voteManager != null) {
            packet.votes().forEach(this.voteManager::addVote);
            packet.voters().forEach(this.voteManager::updateVoterData);
        }
        
        if (client.currentScreen instanceof PendingVoteScreen || client.currentScreen instanceof VoteScreen) {
            client.currentScreen.close();
        }
    }
}
