package net.zhengzhengyiyi.mixin;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.zhengzhengyiyi.network.VoteRuleSyncS2CPacket;
import net.zhengzhengyiyi.vote.VoteDefinition;
import net.zhengzhengyiyi.vote.VoteManager;
import net.zhengzhengyiyi.vote.VoterAction;
import net.zhengzhengyiyi.vote.VoteServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.zhengzhengyiyi.vote.VoteValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.BooleanSupplier;

/**
 * Mixin into MinecraftServer to handle missing vote-related methods.
 */
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements VoteServer {

    @Unique
    private VoteManager voteManager;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        this.voteManager = new VoteManager((MinecraftServer) (Object) this);
    }

    /**
     * Named: method_51115
     * Official: bgp.method_51115
     * Returns the VoteManager instance.
     */
    @Override
    public VoteManager getVoteManager() {
        return this.voteManager;
    }

    /**
     * Named: method_51112
     * Official: bgp.method_51112
     * Starts a new vote with a unique ID and definition.
     */
    @Override
    public void startVote(UUID id, VoteDefinition definition) {
//        this.voteManager.startVote(id, definition);
    }

    /**
     * Named: method_51107
     * Official: bgp.method_51107
     * Casts a vote from a specific player.
     */
    @Override
    public void castVote(ServerPlayerEntity player, VoterAction action) {
        this.voteManager.castVote(player.getUuid(), (a, b)->{});
    }

    /**
     * Named: method_51110
     * Official: bgp.method_51110
     * Checks if an entity can vote at a specific weight.
     */
    @Override
    public boolean canVote(VoterAction action, Entity entity, int weight) {
//        return this.voteManager.canVote(action, entity, weight);
    	return true;
    }

    /**
     * Example of injecting into the vote starting logic.
     */
    @Inject(method = "startVote", at = @At("HEAD"))
    private void onStartVote(UUID id, VoteDefinition definition, CallbackInfo ci) {
    }
    
    @Inject(method = "tick", at = @At("HEAD"))
    private void onServerTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        MinecraftServer server = (MinecraftServer) (Object) this;
        ServerWorld overworld = server.getOverworld();
        
        if (overworld != null && this.voteManager != null) {
            long currentTime = overworld.getTime();

            VoteDefinition.Context context = new VoteDefinition.Context(
                overworld.getSpawnPoint().getPos(), 
                overworld.getDifficulty().getId(),
                Collections.emptyList(),
                60,
                3,
                overworld.getRandom(),
                true
            );
            
            this.voteManager.tick(
            	    currentTime, 
            	    server, 
            	    context,
            	    results -> {
            	        VoteRuleSyncS2CPacket stopPacket = new net.zhengzhengyiyi.network.VoteRuleSyncS2CPacket(true, VoterAction.APPROVE, Collections.emptyList());
            	        
            	        server.getPlayerManager().getPlayerList().forEach(player -> {
            	            ServerPlayNetworking.send(player, stopPacket);
            	        });
            	    }, 
            	    (id, definition) -> {
            	        List<VoteValue> values = (List<VoteValue>)(Object)definition.options().values().stream().sorted().toList();
            	        VoteRuleSyncS2CPacket syncPacket = new net.zhengzhengyiyi.network.VoteRuleSyncS2CPacket(false, VoterAction.APPROVE, values);
            	        
            	        server.getPlayerManager().getPlayerList().forEach(player -> {
            	            ServerPlayNetworking.send(player, syncPacket);
            	        });
            	    }
            	);

//            this.voteManager.tick(
//                currentTime, 
//                server, 
//                context,
//                results -> {
//                	VoteRuleSyncS2CPacket stopPacket = 
//                            new net.zhengzhengyiyi.network.VoteRuleSyncS2CPacket(true, VoterAction.APPROVE, Collections.emptyList());
//                }, 
//                (id, definition) -> {
//                	net.zhengzhengyiyi.network.VoteRuleSyncS2CPacket syncPacket = 
//                            new net.zhengzhengyiyi.network.VoteRuleSyncS2CPacket(false, VoterAction.APPROVE, definition.options().values().stream().sorted().toList());
//                        server.getPlayerManager().sendToAll(syncPacket);
//                }
//            );
        }
    }
}