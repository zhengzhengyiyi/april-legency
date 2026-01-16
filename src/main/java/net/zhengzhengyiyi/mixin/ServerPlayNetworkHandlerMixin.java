package net.zhengzhengyiyi.mixin;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.zhengzhengyiyi.advancement.VoteCriteria;
import net.zhengzhengyiyi.network.VoteClientPlayPacketListener;
import net.zhengzhengyiyi.network.class_8258;
import net.zhengzhengyiyi.network.class_8480;
import net.zhengzhengyiyi.rules.VoteRules;
import net.zhengzhengyiyi.stat.VoteStats;
import net.zhengzhengyiyi.vote.VoteManager;
import net.zhengzhengyiyi.vote.VoteServer;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import it.unimi.dsi.fastutil.ints.IntList;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin implements VoteClientPlayPacketListener {
    @Shadow public ServerPlayerEntity player;

    @Override
    public void method_50043(class_8258 arg) {
    	MinecraftServer server = player.getEntityWorld().getServer();
    	server.execute(() -> {
	        VoteManager voteManager = ((VoteServer)server).getVoteManager();
	        VoteManager.OptionHandle handle = voteManager.getOptionHandle(arg.optionId());
	
	        if (handle == null) {
	            ServerPlayNetworking.getSender(this.player).sendPacket(class_8480.method_51142(
	                arg.transactionId(), 
	                Text.literal("Option " + arg.optionId() + " not found")
	            ));
	            return;
	        }
	
	        VoteManager.VoteAvailability availability = handle.checkRequirements(this.player);
	
	        if (availability == VoteManager.VoteAvailability.ALLOWED) {
	            handle.submit(this.player, 1);
	            
	            ServerPlayNetworking.getSender(this.player).sendPacket(class_8480.method_51141(arg.transactionId()));
	
	            ((VoteServer)server).sendVoteUpdatePacket(this.player, arg.optionId());

	            player.incrementStat(VoteStats.VOTES);
	            VoteCriteria.VOTE.trigger(this.player);
	
	            if (VoteRules.VOTING_FIREWORKS.isActive()) {
	                createFirework();
	            }
	
	            if (VoteRules.SNITCH.isActive()) {
	                MutableText snitchMsg = Text.translatable("rule.snitch.msg", 
	                    this.player.getDisplayName(), 
	                    handle.getVoteTitle(), 
	                    handle.getOptionName()
	                ).formatted(Formatting.GRAY, Formatting.ITALIC);
	                server.getPlayerManager().broadcast(snitchMsg, false);
	            }
	            return;
	        }
	
	        Text errorText = (availability == VoteManager.VoteAvailability.DENIED) ? Text.translatable("vote.no_resources") : Text.translatable("vote.already_voted");
	        ServerPlayNetworking.getSender(this.player).sendPacket(class_8480.method_51142(arg.transactionId(), errorText));
        });
    }
    
    @Unique
    private void createFirework() {
        net.minecraft.util.math.random.Random random = this.player.getRandom();
        
        int flightDuration = random.nextBetween(1, 3);
//        int explosionCount = random.nextBetween(1, 3);
        
        DyeColor randomDyeColor = net.minecraft.util.Util.getRandom(net.minecraft.util.DyeColor.values(), random);

        net.minecraft.item.ItemStack fireworkItemStack = createFirework(
        	randomDyeColor,
            flightDuration
        );

        net.minecraft.entity.projectile.FireworkRocketEntity firework = new net.minecraft.entity.projectile.FireworkRocketEntity(
            this.player.getEntityWorld(),
            this.player, 
            this.player.getX(), 
            this.player.getEyeY(), 
            this.player.getZ(), 
            fireworkItemStack
        );

        this.player.getEntityWorld().spawnEntity(firework);
    }
    
    @Unique
    private ItemStack createFirework(DyeColor color, int flight) {
		ItemStack itemStack = new ItemStack(Items.FIREWORK_ROCKET);
		itemStack.set(
			DataComponentTypes.FIREWORKS,
			new FireworksComponent(
				flight,
				List.of(new FireworkExplosionComponent(FireworkExplosionComponent.Type.BURST, IntList.of(color.getFireworkColor()), IntList.of(), false, false))
			)
		);
		return itemStack;
	}
}