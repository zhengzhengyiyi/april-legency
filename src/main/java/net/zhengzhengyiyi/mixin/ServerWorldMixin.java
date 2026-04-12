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
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
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
	 * Mirrors craftmine ServerWorld.method_69093.
	 * Places the spawn platform and optional structures on first entry,
	 * fires onMineEnter, teleports players into this mine world.
	 *
	 * Structure placement mirrors craftmine exactly:
	 *   field_59596 (start_platform)  — always on first entry, at terrain surface
	 *   field_59597 (warden_arena)    — when warden_boss_fight effect active, at (spawnX+40, surfaceY, spawnZ)
	 */
	@Override
	public void method_69093(boolean revisit, Optional<UUID> playerUuid) {
		ServerWorld self = (ServerWorld)(Object)this;

		// Read the SpawnLocator from persisted state — mirrors craftmine reading it from DimensionOptions.
		MineWorldEffectsState effectsState = self.getPersistentStateManager()
			.getOrCreate(MineWorldEffectsState.TYPE);
		SpawnLocator spawnLocator = effectsState.getSpawnLocator();

		// Get the initial spawn Vec3d from the locator, then find the mutable block pos.
		Vec3d spawnVec = spawnLocator.getSpawnPos(self);
		BlockPos.Mutable mutable = BlockPos.ofFloored(spawnVec).mutableCopy().move(net.minecraft.util.math.Direction.DOWN);
		if (mutable.getY() < self.getBottomY()) {
			mutable.setY(self.getBottomY());
		}

		// bl3: whether to do the surface heightmap walk (only for SURFACE locator)
		boolean bl3 = spawnLocator == SpawnLocator.SURFACE;

		// Walk up past any solid blocks (mirrors craftmine's loop)
		net.minecraft.block.BlockState blockState;

		// For SURFACE: walk the heightmap to find the first non-full-cube block
		while (bl3 && self.getTopY(net.minecraft.world.Heightmap.Type.WORLD_SURFACE, mutable) <= self.getBottomY()) {
			mutable.move(net.minecraft.util.math.Direction.NORTH);
			self.getBlockState(mutable);
		}

		if (bl3) {
			mutable.setY(self.getTopY(net.minecraft.world.Heightmap.Type.WORLD_SURFACE, mutable));
		}

		for (blockState = self.getBlockState(mutable);
			 blockState.isFullCube(self, mutable);
			 blockState = self.getBlockState(mutable)) {
			mutable.move(net.minecraft.util.math.Direction.UP);
		}

		MineProgressState progress = getMineProgress();

		// bl2: first entry — not a revisit, platform not yet placed, this is a mine world
		boolean bl2 = !revisit && !progress.hasPlacedStartStructures() && progress.isMine();

		if (bl2) {
			progress.setPlacedStartStructures(true);

			// field_59597: warden_arena — placed at (spawnX+40, surfaceY, spawnZ) when warden effect active
			if (isMineWorldEffect(net.zhengzhengyiyi.mine.effect.class_11113.field_59244)) {
				int arenaY = self.getTopY(net.minecraft.world.Heightmap.Type.WORLD_SURFACE, mutable.getX() + 40, mutable.getZ());
				BlockPos wardenPos = new BlockPos(mutable.getX() + 40, arenaY, mutable.getZ());
				ModConfiguredFeatures.MINE_START_FEATURE.generateIfValid(
					new net.zhengzhengyiyi.feature.StructureFeatureConfig(Identifier.ofVanilla("mines/warden_arena")),
					self, self.getChunkManager().getChunkGenerator(), self.getRandom(), wardenPos
				);
				bl3 = false;
			}

			// field_59596: start_platform — placed at mutable.down() (one below the first air block)
			if (bl2) {
				ModConfiguredFeatures.MINE_START_FEATURE.generateIfValid(
					new net.zhengzhengyiyi.feature.StructureFeatureConfig(Identifier.ofVanilla("mines/start_platform")),
					self, self.getChunkManager().getChunkGenerator(), self.getRandom(), mutable.down()
				);
			}
		}

		// Player spawns at the first air block above the platform
		double collisionHeight = 0.0;
		if (!blockState.getCollisionShape(self, mutable).isEmpty()) {
			collisionHeight = blockState.getCollisionShape(self, mutable).getMax(net.minecraft.util.math.Direction.Axis.Y);
			if (!Double.isFinite(collisionHeight)) collisionHeight = 0.0;
		}
		Vec3d teleportPos = new Vec3d(mutable.getX() + 0.5, mutable.getY() + collisionHeight, mutable.getZ() + 0.5);

		// Teleport matching players into this mine world
		for (ServerPlayerEntity player : self.getServer().getPlayerManager().getPlayerList()) {
			if ((playerUuid.isEmpty() || playerUuid.get().equals(player.getUuid())) && !player.isSpectator()) {
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
					// First entry — reset food/health, fire onMineEnter
					teleported.getHungerManager().setFoodLevel(20);
					teleported.setHealth(teleported.getMaxHealth());
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
		// French mode item handling
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
	            	serverPlayerEntity.getInventory().removeOne(new ItemStack(ModItems.LE_TRICOLORE));
	               serverPlayerEntity.currentScreenHandler.sendContentUpdates();
	            });
	         }
	      }

		// Mine world tick logic — mirrors craftmine ServerWorld.method_69095
		if (!getPlayers().isEmpty() && isMineWorld()) {
			tickMineMechanics();
		}
	}

	/**
	 * Mirrors craftmine ServerWorld.method_69095.
	 * Handles onMineTick callbacks, leave countdown with broadcasts/sounds, and onMineLeave.
	 * Also mirrors method_69094: when event_exit effect is active and all wave events are won,
	 * generates the mine exit structure via StructurePoolBasedGenerator.
	 */
	@Unique
	private void tickMineMechanics() {
		ServerWorld self = (ServerWorld)(Object)this;
		MineProgressState progress = getMineProgress();

		if (!isMineCompleted()) {
			// Mine still ongoing — fire onMineTick for all active effects
			for (MineEffect effect : getEffectSet()) {
				effect.onMineTick().accept(self);
			}

			// Mirrors method_69094: if event_exit effect is active, tick wave events and
			// generate exit structure when all in-progress events are won.
			if (isMineWorldEffect(net.zhengzhengyiyi.mine.effect.class_11113.field_59257)) {
				net.zhengzhengyiyi.accessor.MinecraftServerAccessor serverAccessor =
					(net.zhengzhengyiyi.accessor.MinecraftServerAccessor)(Object) self.getServer();
				java.util.List<net.zhengzhengyiyi.mine.class_11099> inProgress = serverAccessor.method_69107();
				if (!inProgress.isEmpty()) {
					inProgress.forEach(event -> event.tick(self));
					// Check if any failed → complete mine as failed
					if (inProgress.stream().anyMatch(e -> e.getStatus() == net.zhengzhengyiyi.mine.class_11099.Status.FAILED)) {
						// mine failed — handled by the event system
					} else if (inProgress.stream().allMatch(e -> e.getStatus() == net.zhengzhengyiyi.mine.class_11099.Status.WON)) {
						// All events won — generate exit structure at the first event's spawn pos
						BlockPos exitPos = inProgress.get(0).getPos();
						try {
							net.minecraft.structure.pool.StructurePoolBasedGenerator.generate(
								self,
								self.getRegistryManager().getEntryOrThrow(
									net.minecraft.registry.RegistryKey.of(
										net.minecraft.registry.RegistryKeys.TEMPLATE_POOL,
										net.minecraft.util.Identifier.ofVanilla("mine_exits/starts")
									)
								),
								net.minecraft.util.Identifier.ofVanilla("start"),
								7,
								exitPos,
								false
							);
						} catch (Exception e) {
							AprilsLegacy.LOGGER.warn("Failed to generate mine exit structure: {}", e.getMessage());
						}
					}
					inProgress.removeIf(e -> e.getStatus() == net.zhengzhengyiyi.mine.class_11099.Status.WON);
				}
			}
		} else {
			// Mine completed — run leave countdown
			java.util.Optional<BlockPos> travellingBlockPos = progress.getTravellingBlockPos();
			if (travellingBlockPos.isPresent()) {
				int countdown = progress.getLeaveCountdown();
				if (progress.tickLeaveCountdown()) {
					// Countdown hit zero — fire onMineLeave and teleport everyone out
					onMineLeave(self);
					teleportAllToOverworld(self);
				} else if (countdown % 20 == 0) {
					// Broadcast countdown every second with portal sound
					int secondsLeft = countdown / 20;
					float f = 1.0F - countdown / 200.0F;
					float pitch = 0.75F + 6.0F * f * f;
					if (isMineWon()) {
						self.getServer().getPlayerManager()
							.broadcast(Text.translatable("mine.leave", secondsLeft)
								.setStyle(Style.EMPTY.withBold(true)), true);
					} else {
						self.getServer().getPlayerManager()
							.broadcast(Text.translatable("mine.leave", secondsLeft), true);
					}
					self.playSound(null, travellingBlockPos.get(),
						SoundEvents.BLOCK_END_PORTAL_SPAWN, SoundCategory.AMBIENT,
						(float)(0.15 + 0.04 * pitch), (float)(0.3 + 0.08 * pitch));
				}
			} else if (progress.getLeaveCountdown() != MineProgressState.DEFAULT_COUNTDOWN) {
				progress.resetLeaveCountdown();
			}
		}
	}

	/** Mirrors craftmine method_69100 — fires onMineLeave for all effects, resets overworld time */
	@Unique
	private void onMineLeave(ServerWorld world) {
		for (MineEffect effect : getEffectSet()) {
			effect.onMineLeave().accept(world);
		}
		world.getServer().getOverworld().setTimeOfDay(1000L);
	}

	/** Teleports all players in this mine world back to the overworld spawn */
	@Unique
	private void teleportAllToOverworld(ServerWorld self) {
		ServerWorld overworld = self.getServer().getOverworld();
		Vec3d spawnPos = overworld.getSpawnPoint().getPos().toCenterPos();
		TeleportTarget target = new TeleportTarget(overworld, spawnPos, Vec3d.ZERO,
			0.0F, 0.0F, TeleportTarget.NO_OP);

		for (ServerPlayerEntity player : new java.util.ArrayList<>(self.getPlayers())) {
			if (!player.isSpectator()) {
				Text result = isMineWon()
					? Text.translatable("mine.won").setStyle(Style.EMPTY.withBold(true).withColor(0xFF55FF))
					: Text.translatable("mine.lost").setStyle(Style.EMPTY.withBold(true).withColor(0xFF0000));
				ServerPlayerEntity teleported = player.teleportTo(target);
				if (teleported != null) {
					teleported.sendMessage(result, true);
					teleported.getHungerManager().setFoodLevel(20);
					teleported.setHealth(teleported.getMaxHealth());
					teleported.networkHandler.syncWithPlayerPosition();
				}
			}
		}
	}
}
