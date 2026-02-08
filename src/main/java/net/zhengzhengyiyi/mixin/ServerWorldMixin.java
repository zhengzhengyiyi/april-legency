package net.zhengzhengyiyi.mixin;

import java.util.List;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.zhengzhengyiyi.item.ModItems;
import net.zhengzhengyiyi.rules.VoteRules;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
	@Shadow
	public List<ServerPlayerEntity> getPlayers() {
		return null;
	}
	
	@Shadow
	public RegistryKey<World> getRegistryKey() {return null;}
	
	@Unique
	private boolean field_43412;
	
	@Unique
	// TODO - 
	public void method_69089(RegistryEntry<DimensionType> registryEntry) {
		if (this.getRegistryKey() == World.OVERWORLD) {
	       this.field_58288.getSaveProperties().method_70234(registryEntry);
		}

		getPlayers().forEach(serverPlayerEntity -> serverPlayerEntity.networkHandler.sendPacket(new ClientPacket0(registryEntry)));
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
