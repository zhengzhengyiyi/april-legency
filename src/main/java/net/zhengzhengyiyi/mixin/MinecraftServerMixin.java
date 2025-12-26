package net.zhengzhengyiyi.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.zhengzhengyiyi.vote.VoteDefinition;
import net.zhengzhengyiyi.vote.VoteManager;
import net.zhengzhengyiyi.vote.VoterAction;
import net.zhengzhengyiyi.vote.VoteResults;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

/**
 * Mixin into MinecraftServer to handle missing vote-related methods.
 */
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    /**
     * Named: method_51115
     * Official: bgp.method_51115
     * Returns the VoteManager instance.
     */
    @Shadow
    public abstract VoteManager getVoteManager();

    /**
     * Named: method_51112
     * Official: bgp.method_51112
     * Starts a new vote with a unique ID and definition.
     */
    @Shadow
    public abstract void startVote(UUID id, VoteDefinition definition);

    /**
     * Named: method_51107
     * Official: bgp.method_51107
     * Casts a vote from a specific player.
     */
    @Shadow
    public abstract void castVote(ServerPlayerEntity player, VoterAction action);

    /**
     * Named: method_51110
     * Official: bgp.method_51110
     * Checks if an entity can vote at a specific weight.
     */
    @Shadow
    public abstract boolean canVote(VoterAction action, Entity entity, int weight);

    /**
     * Example of injecting into the vote starting logic.
     */
    @Inject(method = "startVote", at = @At("HEAD"))
    private void onStartVote(UUID id, VoteDefinition definition, CallbackInfo ci) {
        // Custom logic when a vote begins
    }

    /**
     * Interface-style accessor for the internal vote manager if shadowing fails.
     */
    @Accessor("voteManager")
    public abstract VoteManager getVoteManagerAccessor();
}
