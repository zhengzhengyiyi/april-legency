package net.zhengzhengyiyi.vote;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.UUID;

public interface VoteServer {
    VoteManager getVoteManager();
    void startVote(UUID id, VoteDefinition definition);
    void castVote(ServerPlayerEntity player, VoterAction action);
    boolean canVote(VoterAction action, Entity entity, int weight);
}