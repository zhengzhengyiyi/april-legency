package net.zhengzhengyiyi.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemGroups;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;
import net.zhengzhengyiyi.accessor.VoteClientPlayNetworkHandler;
import net.zhengzhengyiyi.gui.PendingVoteScreen;
import net.zhengzhengyiyi.gui.VoteScreen;
import net.zhengzhengyiyi.network.VoteRuleSyncS2CPacket;
import net.zhengzhengyiyi.network.VoteUpdateS2CPacket;
import net.zhengzhengyiyi.network.*;
import net.zhengzhengyiyi.vote.ClientVoteManager;
import net.zhengzhengyiyi.vote.VoteMetadata;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin implements VoteClientPlayNetworkHandler {
    @Unique
    private ClientVoteManager voteManager = new ClientVoteManager();
    
    @Unique @Final private MinecraftClient client = MinecraftClient.getInstance();

    @Unique
    public ClientVoteManager getVoteManager() {
        return this.voteManager;
    }
    
    @Override
    public void onVoteRuleSync(VoteRuleSyncS2CPacket packet) {
        if (!this.client.isOnThread()) {
            this.client.execute(() -> this.onVoteRuleSync(packet));
            return;
        }
        
//        if (packet.clearExisting()) {
//            client.world.getRegistryManager().get(VoteRegistries.VOTE_RULE_TYPE).stream().forEach(rule -> rule.method_50203(true));
//        }
        packet.values().forEach(rule -> rule.apply(packet.action()));
        ItemGroups.updateDisplayContext(this.client.world.getEnabledFeatures(), this.client.options.getOperatorItemsTab().getValue(), this.client.world.getRegistryManager());
    }

    @Override
    public void onVoteStart(class_8483 packet) {
        if (!this.client.isOnThread()) {
            this.client.execute(() -> this.onVoteStart(packet));
            return;
        }

        VoteMetadata header = packet.voteData().metadata();
        this.client.inGameHud.getChatHud().addMessage(Text.translatable("vote.started", header.getDisplayName(), StringHelper.formatTicks((int)header.getDuration(), client.getRenderTickCounter().getDynamicDeltaTicks())));
        this.voteManager.addVote(packet.id(), packet.voteData());
    }

    @Override
    public void onVoteResult(class_8481 packet) {
        if (!this.client.isOnThread()) {
            this.client.execute(() -> this.onVoteResult(packet));
            return;
        }

        this.voteManager.removeVote(packet.id());
        
//        if (this.client.currentScreen instanceof class_8444 screen) {
//            screen.method_50959();
//        }
        
        if (client.currentScreen instanceof VoteScreen screen) {
        	screen.close();
        }
    }

    @Override
    public void onVoteStop(class_8482 packet) {
        if (!this.client.isOnThread()) {
            this.client.execute(() -> this.onVoteStop(packet));
            return;
        }
        
        Map<UUID, Integer> voteCountMap = packet.voters().voters().entrySet().stream()
        	    .collect(java.util.stream.Collectors.toMap(
        	        entry -> entry.getKey(),
        	        entry -> entry.getValue().voteCount()
        	    ));
        
        this.voteManager.updateVoterData(packet.id().voteId(), 0, voteCountMap);
        
        if (this.client.currentScreen instanceof VoteScreen screen) {
            screen.close();
        }
    }

    @Override
    public void onVoteResponse(class_8480 packet) {
        if (!this.client.isOnThread()) {
            this.client.execute(() -> this.onVoteResponse(packet));
            return;
        }

        packet.rejectReason().ifPresent(text -> this.client.inGameHud.getChatHud().addMessage(Text.translatable("vote.failed", text)));
//        this.voteManager.handleTransactionResponse(packet.transactionId(), packet.rejectReason());
        this.voteManager.handleResponse(packet.transactionId(), packet.rejectReason());
    }

    @Override
    public void onVoteUpdate(VoteUpdateS2CPacket packet) {
        if (!this.client.isOnThread()) {
            this.client.execute(() -> this.onVoteUpdate(packet));
            return;
        }

        if (packet.clear()) {
            this.voteManager = new ClientVoteManager();
        }
        
        Objects.requireNonNull(this.voteManager);
        packet.votes().forEach(this.voteManager::addVote);
        packet.voters().forEach((id, data) -> {
        	Map<UUID, Integer> voteCountMap = data.voters().entrySet().stream()
            	    .collect(java.util.stream.Collectors.toMap(
            	        entry -> entry.getKey(),
            	        entry -> entry.getValue().voteCount()
            	    ));
        	this.voteManager.updateVoterData(id.voteId(), 0, voteCountMap);
        });
        
        if (this.client.currentScreen instanceof VoteScreen screen) {
            screen.close();
        }
    }
    
    @Unique
    /**
     * class_8478 packet
     * */
    public void updateVotes(VoteUpdateS2CPacket packet) {
    	MinecraftClient client = MinecraftClient.getInstance();
    	
    	client.execute(() -> {
	        if (packet.clear()) {
	            this.voteManager = new ClientVoteManager();
	        }
	        
	        if (this.voteManager != null) {
	            packet.votes().forEach(this.voteManager::addVote);
//	            packet.voters().forEach(this.voteManager::updateVoterData);
	            packet.voters().forEach((id, data) -> {
	            	Map<UUID, Integer> voteCounts = data.voters().entrySet().stream()
		                    .collect(java.util.stream.Collectors.toMap(
		                        java.util.Map.Entry::getKey,
		                        e -> e.getValue().voteCount()
		                    ));
	            	voteManager.updateVoterData(id.voteId(), 0, voteCounts);
	            });
	        }
	        
	        if (client.currentScreen instanceof PendingVoteScreen || client.currentScreen instanceof VoteScreen) {
	            client.currentScreen.close();
	        }
        });
    }
}
