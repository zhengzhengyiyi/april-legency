package net.zhengzhengyiyi.mixin;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.ServerWorldProperties;
import net.zhengzhengyiyi.AprilsLegacy;
import net.zhengzhengyiyi.accessor.LevelPropertiesAccessor;
import net.zhengzhengyiyi.accessor.MineServerWorldAccessor;
import net.zhengzhengyiyi.item.ModItems;
import net.zhengzhengyiyi.mine.MineEffect;
import net.zhengzhengyiyi.mine.MineProgressState;
import net.zhengzhengyiyi.mine.effect.MineUnlockCondition;
import net.zhengzhengyiyi.mine.effect.UnlockMode;
import net.zhengzhengyiyi.network.ClientPacket0;
import net.zhengzhengyiyi.network.ClientPacket6;
import net.zhengzhengyiyi.rules.VoteRules;
import net.zhengzhengyiyi.screen.ScreenWorldAccess;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements ScreenWorldAccess, MineServerWorldAccessor {
	protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef,
			DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, boolean isClient,
			boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
		super(properties, registryRef, registryManager, dimensionEntry, isClient, debugWorld, seed, maxChainedNeighborUpdates);
		
		Optional<RegistryEntry.Reference<DimensionOptions>> optional = this.getRegistryManager().getOptionalEntry(RegistryKeys.toDimensionKey(getRegistryKey()));
		
//		this.field_58290 = optional.map(RegistryEntry::value).map(option -> option).<Set<MineEffect>>map(option -> new ObjectArraySet<>()).orElseGet(Set::of);
		this.field_58290 = optional.map(entry -> {
		    Set<MineEffect> set = new ObjectArraySet<>();
		    return set;
		}).orElseGet(Set::of);
		this.minePregress = this.getPersistentStateManager().getOrCreate(MineProgressState.TYPE);
	    this.minePregress.setMine(!this.field_58290.isEmpty());
	}
	
	@Shadow
	public PersistentStateManager getPersistentStateManager() {
	      return null;
	}
	
	@Unique
	protected final MineProgressState minePregress;
	
	@Unique
	private final Set<MineEffect> field_58290;
	
	@Shadow
	private ServerWorldProperties worldProperties;
	
	@Override
	public boolean isMineWorld() {
	   return this.minePregress.isMine();
	}
	
	@Override
	public boolean isMineCompleted() {
	   return this.minePregress.getStatus() != MineProgressState.Status.ONGOING;
	}
	
	@Override
	public boolean isMineWon() {
	   return this.minePregress.getStatus() == MineProgressState.Status.WON;
	}
	
	@Override
	public boolean hasMineEffect(MineEffect effect) {
	   return ((LevelPropertiesAccessor)(Object)worldProperties).hasUnlockedMineEffect(effect);
	}

	@Override
	public List<MineEffect> getUnlockedMineEffects() {
	   return AprilsLegacy.MINE_EFFECT.stream().filter(this::hasMineEffect).toList();
	}
	
	public void unlockMineEffect(MineEffect effect) {
	      if (!((LevelPropertiesAccessor)(Object)worldProperties).hasUnlockedMineEffect(effect) && effect.unlockMode() != UnlockMode.NEVER_UNLOCKED) {
	         this.server.getPlayerManager().broadcast(Text.translatable("world.effect.unlocked", effect.name()), true);
	         this.server.getPlayerManager().broadcast(Text.translatable("world.effect.unlocked", effect.name()), false);
	         ((LevelPropertiesAccessor)(Object)worldProperties).setUnlockedMineEffect(effect);

	         for (ServerPlayerEntity serverPlayerEntity : this.server.getPlayerManager().getPlayerList()) {
	            MineUnlockCondition.method_69629(this, serverPlayerEntity, effect);
	            ServerPlayNetworking.send(serverPlayerEntity, new ClientPacket6(this.getUnlockedMineEffects()));
	         }
	      }
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
	
	@Override
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
