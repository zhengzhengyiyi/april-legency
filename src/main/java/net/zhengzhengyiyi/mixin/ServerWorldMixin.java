package net.zhengzhengyiyi.mixin;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.ServerWorldProperties;
import net.zhengzhengyiyi.AprilsLegacy;
import net.zhengzhengyiyi.accessor.LevelPropertiesAccessor;
import net.zhengzhengyiyi.accessor.MineServerWorldAccessor;
import net.zhengzhengyiyi.feature.ModConfiguredFeatures;
import net.zhengzhengyiyi.item.ModItems;
import net.zhengzhengyiyi.mine.MineEffect;
import net.zhengzhengyiyi.mine.MineProgressState;
import net.zhengzhengyiyi.mine.MineWorldEffectsState;
import net.zhengzhengyiyi.mine.SpawnLocator;
import net.zhengzhengyiyi.mine.effect.MineUnlockCondition;
import net.zhengzhengyiyi.mine.effect.UnlockMode;
import net.zhengzhengyiyi.network.ClientPacket0;
import net.zhengzhengyiyi.network.ClientPacket6;
import net.zhengzhengyiyi.rules.VoteRules;
import net.zhengzhengyiyi.screen.ScreenWorldAccess;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements ScreenWorldAccess, MineServerWorldAccessor {
	@Unique
	private MineProgressState minePregress;
	
	@Unique
	private Set<MineEffect> field_58290;
	
	protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef,
			DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, boolean isClient,
			boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
		super(properties, registryRef, registryManager, dimensionEntry, isClient, debugWorld, seed, maxChainedNeighborUpdates);
	}
	
	@Unique
	private Set<MineEffect> getEffectSet() {
		if (this.field_58290 == null) {
			// Try to load effects from persistent state (works for both normal and Fantasy worlds)
			MineWorldEffectsState effectsState = this.getPersistentStateManager()
				.getOrCreate(MineWorldEffectsState.TYPE);
			List<MineEffect> effects = effectsState.getEffects();
			this.field_58290 = effects.isEmpty() ? Set.of() : new ObjectArraySet<>(effects);
		}
		return this.field_58290;
	}
	
	@Unique
	private MineProgressState getMineProgress() {
		if (this.minePregress == null) {
			this.minePregress = this.getPersistentStateManager().getOrCreate(MineProgressState.TYPE);
			this.minePregress.setMine(!getEffectSet().isEmpty());
		}
		return this.minePregress;
	}
	
	@Shadow
	public PersistentStateManager getPersistentStateManager() {
	      return null;
	}
	
	@Shadow
	private ServerWorldProperties worldProperties;
	
	@Override
	public boolean isMineWorld() {
	   return getMineProgress().isMine();
	}
	
	@Override
	public boolean isMineCompleted() {
	   return getMineProgress().getStatus() != MineProgressState.Status.ONGOING;
	}
	
	@Override
	public boolean isMineWon() {
	   return getMineProgress().getStatus() == MineProgressState.Status.WON;
	}
	
	@Override
	public boolean hasMineEffect(MineEffect effect) {
	   // Use the overworld's save properties — not the current world's properties,
	   // which may be Fantasy's RuntimeWorldProperties and not castable.
	   LevelPropertiesAccessor overworldProps = (LevelPropertiesAccessor)(Object)
	      this.server.getSaveProperties().getMainWorldProperties();
	   return overworldProps.hasUnlockedMineEffect(effect);
	}

	@Override
	public boolean isMineWorldEffect(MineEffect effect) {
	   // Checks if this specific mine world was created with this effect (field_58290 equivalent)
	   return getEffectSet().contains(effect);
	}

	@Override
	public void dropOrUnlockMineEffect(net.minecraft.util.math.Vec3d pos, MineEffect effect, @org.jetbrains.annotations.Nullable ServerPlayerEntity player) {
	   // If mine is completed, unlock globally. Otherwise drop a mine ingredient item.
	   if (getMineProgress().getStatus() != MineProgressState.Status.ONGOING) {
	      unlockMineEffect(effect);
	   } else if (player == null
	      || !player.getInventory().getMainStacks().stream().anyMatch(
	            stack -> stack.isOf(net.zhengzhengyiyi.item.ModItems.MINE_INGREDIENT)
	               && stack.getOrDefault(net.zhengzhengyiyi.component.ModDataComponentTypes.WORLD_MODIFIERS,
	                     net.zhengzhengyiyi.mine.class_11056.field_58859).effects().contains(effect)
	         )) {
	      net.minecraft.entity.ItemEntity itemEntity = new net.minecraft.entity.ItemEntity(
	         (ServerWorld)(Object)this, pos.x, pos.y, pos.z,
	         net.zhengzhengyiyi.mine.effect.class_11113.method_70013(effect, true)
	      );
	      ((ServerWorld)(Object)this).spawnEntity(itemEntity);
	   }
	}

	@Override
	public Optional<net.zhengzhengyiyi.mine.SpecialMine> getCurrentSpecialMine() {
	   LevelPropertiesAccessor overworldProps = (LevelPropertiesAccessor)(Object)
	      this.server.getSaveProperties().getMainWorldProperties();
	   return overworldProps.getRandomSpecialMine(this.getRandom());
	}

	@Override
	public List<MineEffect> getUnlockedMineEffects() {
	   return AprilsLegacy.MINE_EFFECT.stream().filter(this::hasMineEffect).toList();
	}
	
	public void unlockMineEffect(MineEffect effect) {
	      LevelPropertiesAccessor overworldProps = (LevelPropertiesAccessor)(Object)
	         this.server.getSaveProperties().getMainWorldProperties();
	      if (!overworldProps.hasUnlockedMineEffect(effect) && effect.unlockMode() != UnlockMode.NEVER_UNLOCKED) {
	         this.server.getPlayerManager().broadcast(Text.translatable("world.effect.unlocked", effect.name()), true);
	         this.server.getPlayerManager().broadcast(Text.translatable("world.effect.unlocked", effect.name()), false);
	         overworldProps.setUnlockedMineEffect(effect);

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
	
	/**
	 * Mirrors craftmine ServerWorld.method_69093 exactly.
	 * Places the spawn platform on first entry, fires onMineEnter, teleports players into this mine world.
	 */
	@Override
	public void method_69093(boolean revisit, Optional<UUID> playerUuid) {
		ServerWorld self = (ServerWorld)(Object)this;

		// Get spawn position from SpawnLocator — for Fantasy worlds the dimension registry
		// entry doesn't exist, so we use SURFACE directly (matches the default in craftmine)
		Vec3d spawnVec = SpawnLocator.SURFACE.getSpawnPos(self);
		BlockPos.Mutable mutable = BlockPos.ofFloored(spawnVec).mutableCopy().move(Direction.DOWN);
		if (mutable.getY() < self.getBottomY()) {
			mutable.setY(self.getBottomY());
		}

		MineProgressState progress = getMineProgress();

		// bl2: first entry — not a revisit, platform not yet placed, this is a mine world
		boolean bl2 = !revisit && !progress.hasPlacedStartStructures() && progress.isMine();

		if (bl2) {
			progress.setPlacedStartStructures(true);
			// Mirrors craftmine: getRegistryManager().getEntryOrThrow(MiscConfiguredFeatures.field_59596).value().generate(...)
			// field_59596 = Feature.PLACE_TEMPLATE with class_11086(List.of("mines/start_platform"))
			// We use StructureFeature (class_11085) + StructureFeatureConfig (class_11086) directly.
			ModConfiguredFeatures.MINE_START_FEATURE.generateIfValid(
				new net.zhengzhengyiyi.feature.StructureFeatureConfig(Identifier.ofVanilla("mines/start_platform")),
				self,
				self.getChunkManager().getChunkGenerator(),
				self.getRandom(),
				mutable.down()
			);
		}

		// Walk up to find the first non-solid block (mirrors craftmine's loop)
		BlockState blockState;
		for (blockState = self.getBlockState(mutable);
			 blockState.isFullCube(self, mutable);
			 blockState = self.getBlockState(mutable)) {
			mutable.move(Direction.UP);
		}

		double d = 0.0;
		if (!blockState.getCollisionShape(self, mutable).isEmpty()) {
			d = blockState.getCollisionShape(self, mutable).getMax(Direction.Axis.Y);
			if (!Double.isFinite(d)) d = 0.0;
		}

		Vec3d teleportPos = new Vec3d(mutable.getX() + 0.5, mutable.getY() + d, mutable.getZ() + 0.5);

		// Teleport matching players into this mine world (mirrors craftmine exactly)
		for (ServerPlayerEntity player : self.getServer().getPlayerManager().getPlayerList()) {
			if ((playerUuid.isEmpty() || playerUuid.get().equals(player.getUuid())) && !player.isSpectator()) {
				player.changeGameMode(GameMode.SURVIVAL);
				TeleportTarget target = new TeleportTarget(
					self, teleportPos, Vec3d.ZERO, 0.0F, 0.0F,
					java.util.Set.of(),
					TeleportTarget.ADD_PORTAL_CHUNK_TICKET
				);
				ServerPlayerEntity teleported = player.teleportTo(target);
				if (teleported == null) return;

				teleported.networkHandler.syncWithPlayerPosition();

				if (revisit) {
					// Re-entry into completed mine — adventure mode, show result message
					teleported.changeGameMode(GameMode.ADVENTURE);
					if (isMineWon()) {
						teleported.sendMessageToClient(Text.translatable("world.mine.revisit.won"), true);
					} else {
						teleported.sendMessageToClient(Text.translatable("world.mine.revisit.lost"), true);
					}
				} else {
					// First entry — reset food/health/rest (method_69144 equivalent)
					teleported.getHungerManager().setFoodLevel(20);
					teleported.setHealth(teleported.getMaxHealth());
					// Fire onMineEnter effects (method_69099 equivalent)
					method_69099_onMineEnter(self);
				}
			}
		}
	}

	/** Mirrors craftmine method_69099 — fires onMineEnter for all effects */
	@Unique
	private void method_69099_onMineEnter(ServerWorld world) {
		world.getServer().getOverworld().setTimeOfDay(1000L);
		for (MineEffect effect : getEffectSet()) {
			effect.onMineEnter().accept(world);
		}
	}

	/** Places the 3x3 stone platform + MineCrafter + ShimmeringDoor at the spawn pos */
	@Unique
	private void method_69099_placeSpawnPlatform(ServerWorld world, BlockPos center) {
		
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
