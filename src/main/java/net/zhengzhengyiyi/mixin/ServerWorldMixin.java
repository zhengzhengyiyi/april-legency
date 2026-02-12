package net.zhengzhengyiyi.mixin;

import java.util.List;
import java.util.Set;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.zhengzhengyiyi.item.ModItems;
import net.zhengzhengyiyi.network.ClientPacket0;
import net.zhengzhengyiyi.rules.VoteRules;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {
	protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef,
			DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, boolean isClient,
			boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
		super(properties, registryRef, registryManager, dimensionEntry, isClient, debugWorld, seed, maxChainedNeighborUpdates);
	}

	@Shadow
	public List<ServerPlayerEntity> getPlayers() {
		return null;
	}
	
	@Override
	public RegistryKey<World> getRegistryKey() {return super.getRegistryKey();}
	
	@Unique
	private boolean field_43412;
	
	@Shadow
	@Final
	private MinecraftServer server;
	
	@Unique
	public void method_69089(RegistryEntry<DimensionType> registryEntry) {
//		if (this.getRegistryKey() == World.OVERWORLD) {
//	       this.server.getSaveProperties().method_70234(registryEntry);
//		}
		
		getPlayers().forEach(serverPlayerEntity -> {
		    ServerPlayNetworking.send(serverPlayerEntity, new ClientPacket0(registryEntry));
		});
	}
	
	@Inject(method="tick", at=@At("TAIL"))
	public void tick(CallbackInfo ci) {
		if (this.field_43412 != VoteRules.FRENCH_MODE.isActive()) {
	         this.field_43412 = VoteRules.FRENCH_MODE.isActive();
	         if (this.field_43412) {
	            getPlayers().forEach(serverPlayerEntity -> {
	               serverPlayerEntity.giveItemStack(new ItemStack(ModItems.LA_BAGUETTE));
	               if (!serverPlayerEntity.getInventory().containsAny(Set.of(ModItems.LE_TRICOLORE))) {
	                  serverPlayerEntity.giveItemStack(new ItemStack(ModItems.LE_TRICOLORE));
	               }

	               serverPlayerEntity.currentScreenHandler.sendContentUpdates();
	            });
	         } else {
	            getPlayers().forEach(serverPlayerEntity -> {
//	               serverPlayerEntity.getInventory().method_50711(ModItems.LE_TRICOLORE, Items.AIR);
	            	serverPlayerEntity.getInventory().removeOne(new ItemStack(ModItems.LE_TRICOLORE));
	               serverPlayerEntity.currentScreenHandler.sendContentUpdates();
	            });
	         }
	      }
	}
}
