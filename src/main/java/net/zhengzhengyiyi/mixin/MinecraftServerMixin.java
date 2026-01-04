package net.zhengzhengyiyi.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.level.storage.LevelStorage;
import net.zhengzhengyiyi.network.VoteRuleSyncS2CPacket;
import net.zhengzhengyiyi.network.VoterData;
import net.zhengzhengyiyi.network.class_8481;
import net.zhengzhengyiyi.network.class_8482;
import net.zhengzhengyiyi.network.class_8483;
import net.zhengzhengyiyi.rules.VoteRules;
import net.zhengzhengyiyi.vote.TieBreaker;
import net.zhengzhengyiyi.vote.VoteChoice;
import net.zhengzhengyiyi.vote.VoteDefinition;
import net.zhengzhengyiyi.vote.VoteManager;
import net.zhengzhengyiyi.vote.VoteOptionId;
import net.zhengzhengyiyi.vote.VoteResults;
import net.zhengzhengyiyi.vote.VoterAction;
import net.zhengzhengyiyi.world.Vote;
import net.zhengzhengyiyi.world.VoteSession;
import net.zhengzhengyiyi.vote.VoteServer;
import net.zhengzhengyiyi.vote.VoteState;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Lists;

import net.zhengzhengyiyi.vote.VoteValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BooleanSupplier;

/**
 * Mixin into MinecraftServer to handle missing vote-related methods.
 */
@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements VoteServer {
	@Shadow
	public void sendMessage(Text msg) {}

    @Unique
    private VoteManager voteManager;
    
    @Shadow
    private int getCurrentPlayerCount() {
    	return 1;
    }
    
    @Shadow
    private Random random;
    
    @Final
    @Shadow
    private LevelStorage.Session session;
    
    @Shadow
    @Final
    private PlayerManager playerManager;

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
    public boolean castVote(VoteOptionId optionId, Entity entity, int count) {
        this.voteManager.castVote(entity.getUuid(), (a, b)->{});
        
        return true;
    }

    /**
     * Named: method_51110
     * Official: bgp.method_51110
     * Checks if an entity can vote at a specific weight.
     */
//    @Override
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
    
    @SuppressWarnings("unchecked")
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
//            	            ServerPlayNetworking.send(player, stopPacket);
            	        	player.networkHandler.sendPacket(stopPacket);
            	            
            	        });
            	    },
            	    (id, definition) -> {
            	        List<VoteValue> values = (List<VoteValue>)(Object)definition.options().values().stream().sorted().toList();
            	        VoteRuleSyncS2CPacket syncPacket = new net.zhengzhengyiyi.network.VoteRuleSyncS2CPacket(false, VoterAction.APPROVE, values);
            	        
            	        server.getPlayerManager().getPlayerList().forEach(player -> {
//            	            ServerPlayNetworking.send(player, syncPacket);
            	        	player.networkHandler.sendPacket(syncPacket);
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

    @Override
    public Vote finishVote(UUID id, boolean applyResults) {
        return (Vote)(Object)this.method_51113(id, applyResults);
    }

    @Override
    public void reloadAndBroadcastVotes() {
        this.method_51121();
    }
    
    public void method_51112(UUID uUID, VoteDefinition arg) {
        this.voteManager.addVote(uUID, arg);
        this.playerManager.sendToAll((Packet<?>)new class_8483(uUID, arg));
    }
    
    public void method_51120() {
        VoteState lv = this.voteManager.save();
        ((VoteSession)(Object)session).saveVotes(lv);
    }
    
    private VoteState method_51116() {
    	VoteState lv = ((VoteSession)(Object)session).loadVotes();
        this.voteManager.load(lv);
        System.out.println("aaa");
//    	this.voteManager.save();
        return lv;
    }
    
    public VoteResults method_51113(UUID uUID, boolean bl) {
      VoteResults lv = this.voteManager.forceFinish(uUID);
      if (lv != null)
        method_51109(lv, bl); 
      return lv;
    }
     
    public void method_51121() {
        this.voteManager.initializeRules();
        VoteState lv = method_51116();
        this.playerManager.sendToAll((Packet<?>)new VoteRuleSyncS2CPacket(true, VoterAction.APPROVE, lv.activeValues()));
//        this.playerManager.getPlayerList().forEach(serverPlayerEntity -> serverPlayerEntity.networkHandler.sendPacket((Packet)this.playerManager.method_50050(serverPlayerEntity.getUuid())));
        // TODO
    }

    @Override
    public void sendVoteUpdatePacket(ServerPlayerEntity player, VoteOptionId optionId) {
        this.method_51107(player, (VoteOptionId)(Object)optionId);
    }
    
    private VoteResults.ResultConfig method_51117() {
//        return new VoteResults.ResultConfig(this.random, !VoteRules.SHOW_VOTERS.isActive(), getCurrentPlayerCount(), (VoteRules.QUORUM_PERCENT.getValue()).intValue() / 100.0F, (VoteRules.QUORUM_PERCENT.getValue()).intValue() / 100.0F, !VoteRules.SHOW_TALLY.isActive(), VoteRules.SHOW_VOTERS.isActive(), VoteRules.RANDOM_IF_FAIL.isActive(), !VoteRules.REVERSE_COUNTS.isActive(), !VoteRules.PASS_WITHOUT_VOTES.isActive(), ((Integer)VoteRules.MAX_RESULTS.getValue()).intValue(), VoteRules.TIE_STRATEGY.getValue());
    	return new VoteResults.ResultConfig(
    		    random,
    		    (boolean)!VoteRules.PASS_WITHOUT_VOTES.isActive(),
    		    (int)getCurrentPlayerCount(),
    		    (float)VoteRules.QUORUM_PERCENT.getValue().floatValue() / 100.0F,
    		    (float)VoteRules.QUORUM_PERCENT.getValue().floatValue() / 100.0F,
    		    (boolean)!VoteRules.SHOW_TALLY.isActive(),
    		    (boolean)VoteRules.SHOW_VOTERS.isActive(),
    		    (boolean)VoteRules.RANDOM_IF_FAIL.isActive(),
    		    (boolean)!VoteRules.REVERSE_COUNTS.isActive(),
    		    (boolean)!VoteRules.PASS_WITHOUT_VOTES.isActive(),
    		    (int)VoteRules.MAX_RESULTS.getValue().intValue(),
    		    (TieBreaker)VoteRules.TIE_STRATEGY.getValue()
    		);
    }
    
    private void method_51109(VoteResults arg, boolean bl) {
        List<VoteDefinition.Effect> list = new ArrayList<>();
        List<Text> list2 = new ArrayList<>();
        VoteResults.ResultConfig lv = method_51117();
        Objects.requireNonNull(list);
        Objects.requireNonNull(list2);
        arg.finish(list::add, list2::add, lv);
        for (Text text : list2)
          sendMessage(text);
        int i = list2.size();
        if (!VoteRules.SILENT_VOTE.isActive() && i > 0) {
          MutableText mutableText = ((Text)list2.get(0)).copy();
          if (list.isEmpty()) {
            mutableText.formatted(new Formatting[] { Formatting.ITALIC, Formatting.GRAY });
          } else {
            mutableText.formatted(new Formatting[] { Formatting.BOLD, Formatting.GOLD });
          } 
          if (i > 1) {
            mutableText.formatted(Formatting.UNDERLINE);
            Text text2 = ScreenTexts.joinLines(list2);
            mutableText.styled(style -> style.withHoverEvent(new HoverEvent.ShowText(text2)));
          } 
          this.playerManager.broadcast((Text)mutableText, false);
        } 
        this.playerManager.sendToAll((Packet<?>)new class_8481(arg.id()));
        if (bl)
          Lists.reverse(list).forEach($$1 -> $$1.apply((MinecraftServer)(Object)this)); 
     }
    
    public void method_51107(ServerPlayerEntity serverPlayerEntity, VoteOptionId arg) {
        UUID uUID = serverPlayerEntity.getUuid();
        VoterData lv = this.voteManager.method_50566(arg);
        VoteChoice lv2 = (VoteChoice)lv.voters().get(uUID);
        if (lv2 != null)
          serverPlayerEntity.networkHandler.sendPacket((Packet<?>)new class_8482(arg, VoterData.createSingle(uUID, lv2))); 
     }

    @Override
    public void saveVotes() {
        this.method_51120();
    }

    @Override
    public void settleVote(VoterAction result, boolean shouldApply) {
        this.method_51109((VoteResults)(Object)result, shouldApply);
    }
}