package net.zhengzhengyiyi.vote;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.zhengzhengyiyi.world.Vote;
import java.util.UUID;

public interface VoteServer {
    /**
     * Gets the server's VoteManager.
     * Mapped to: field_44453
     */
    VoteManager getVoteManager();

    /**
     * Starts a new vote and broadcasts it to all players.
     * Mapped to: method_51112
     */
    void startVote(UUID id, VoteDefinition definition);

    /**
     * Ends a vote immediately.
     * Mapped to: method_51113
     * @param id The vote UUID
     * @param applyResults Whether to apply the rule changes
     * @return The finished vote result (class_8370)
     */
    Vote finishVote(UUID id, boolean applyResults);

    /**
     * Casts a vote for a specific entity (Player or otherwise).
     * Mapped to: method_51110
     * @return True if the vote was successfully cast
     */
    boolean castVote(VoteOptionId optionId, Entity entity, int count);

    /**
     * Reloads all vote rules and syncs approved rules to clients.
     * Used by the "io reload" command.
     * Mapped to: method_51121
     */
    void reloadAndBroadcastVotes();

    /**
     * Sends a packet to update a specific player's vote UI for a specific option.
     * Mapped to: method_51107
     */
    void sendVoteUpdatePacket(ServerPlayerEntity player, VoteOptionId optionId);
    
    /**
     * Saves all current votes to disk.
     * Mapped to: method_51120
     */
    void saveVotes();
    
    void settleVote(VoterAction result, boolean shouldApply);
}
