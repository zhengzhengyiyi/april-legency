package net.zhengzhengyiyi.mine.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.boss.BossBarManager;
import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import org.jetbrains.annotations.Nullable;

public class WaveEvent implements net.zhengzhengyiyi.mine.class_11099 {
   private static final int MAX_SPAWN_ATTEMPTS = 500;
   private final Identifier id;
   private final List<Wave> waves;
   private final List<UUID> spawnedMobs;
   private int currentWaveIndex;
   private long waveCompletedTick;
   private BlockPos lastSpawnPos;
   private net.zhengzhengyiyi.mine.class_11099.Status status;

   public static final MapCodec<WaveEvent> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(event -> event.id),
            Wave.CODEC.listOf().fieldOf("waves").forGetter(event -> event.waves),
            Uuids.INT_STREAM_CODEC.listOf().fieldOf("spawned_mobs").forGetter(event -> event.spawnedMobs),
            Codec.INT.fieldOf("current_wave").forGetter(event -> event.currentWaveIndex),
            Codec.LONG.fieldOf("wave_completed_tick").forGetter(event -> event.waveCompletedTick),
            BlockPos.CODEC.fieldOf("position").forGetter(event -> event.lastSpawnPos),
            net.zhengzhengyiyi.mine.class_11099.Status.CODEC.fieldOf("status").forGetter(event -> event.status)
         )
         .apply(instance, WaveEvent::new)
   );

   public WaveEvent(Identifier id, List<Wave> waves, List<UUID> spawnedMobs, int currentWaveIndex, long waveCompletedTick, BlockPos lastSpawnPos, net.zhengzhengyiyi.mine.class_11099.Status status) {
      this.id = id;
      this.waves = waves;
      this.spawnedMobs = new ArrayList<>(spawnedMobs);
      this.currentWaveIndex = currentWaveIndex;
      this.waveCompletedTick = waveCompletedTick;
      this.lastSpawnPos = lastSpawnPos;
      this.status = status;
   }

   public WaveEvent(Identifier id, List<Wave> waves) {
      this(id, waves, List.of(), -1, 0L, BlockPos.ORIGIN, net.zhengzhengyiyi.mine.class_11099.Status.ACTIVE);
   }

   @Override
   public void tick(ServerWorld world) {
      if (this.getStatus() != net.zhengzhengyiyi.mine.class_11099.Status.ACTIVE) {
         BossBarManager bossBarManager = world.getServer().getBossBarManager();
         CommandBossBar bossBar = bossBarManager.get(this.id);
         if (bossBar != null) {
            bossBarManager.remove(bossBar);
         }
      } else {
         long currentTime = world.getTime();
         if (this.currentWaveIndex == -1) {
            this.currentWaveIndex = 0;
            this.waveCompletedTick = currentTime;
         }

         if (this.waveCompletedTick >= 0L) {
            if (this.currentWaveIndex < this.waves.size()) {
               Wave wave = this.waves.get(this.currentWaveIndex);
               BossBarManager bossBarManager = world.getServer().getBossBarManager();
               long spawnTime = this.waveCompletedTick + (long)wave.ticksDelay();
               if (spawnTime < currentTime) {
                  this.spawnWave(wave, world);
                  this.waveCompletedTick = -1L;
                  CommandBossBar bossBar = bossBarManager.get(this.id);
                  if (bossBar != null) {
                     bossBarManager.remove(bossBar);
                  }
               } else {
                  CommandBossBar bossBar = bossBarManager.get(this.id);
                  if (bossBar == null) {
                     bossBar = bossBarManager.add(this.id, wave.countdownText());
                  }
                  bossBar.setMaxValue(wave.ticksDelay());
                  bossBar.setName(wave.countdownText());
                  bossBar.setValue((int)(currentTime - this.waveCompletedTick));
                  bossBar.addPlayers(world.getPlayers());
               }
            }
         } else {
            List<Entity> aliveMobs = this.spawnedMobs.stream()
               .flatMap(uuid -> Optional.ofNullable(world.getEntity(uuid)).filter(Entity::isAlive).stream())
               .toList();
            
            if (this.currentWaveIndex < this.waves.size()) {
               Wave currentWave = this.waves.get(this.currentWaveIndex);
               if (currentWave.showBar()) {
                  BossBarManager bossBarManager = world.getServer().getBossBarManager();
                  CommandBossBar bossBar = bossBarManager.get(this.id);
                  if (bossBar == null) {
                     bossBar = bossBarManager.add(this.id, Text.translatable("world.event.remaining"));
                  }
                  bossBar.setName(Text.translatable("world.event.remaining"));
                  bossBar.setMaxValue(this.spawnedMobs.size());
                  bossBar.setValue(aliveMobs.size());
                  bossBar.addPlayers(world.getPlayers());
               }
            }

            if (aliveMobs.isEmpty()) {
               this.currentWaveIndex++;
               this.waveCompletedTick = currentTime;
            } else {
               this.lastSpawnPos = aliveMobs.getFirst().getBlockPos();
            }
         }
         this.status = this.currentWaveIndex < this.waves.size() ? net.zhengzhengyiyi.mine.class_11099.Status.ACTIVE : net.zhengzhengyiyi.mine.class_11099.Status.WON;
      }
   }

   public void finish(ServerWorld world, boolean success) {
      this.status = success ? net.zhengzhengyiyi.mine.class_11099.Status.WON : net.zhengzhengyiyi.mine.class_11099.Status.FAILED;
      this.currentWaveIndex = this.waves.size();
      this.waveCompletedTick = -1L;
      BossBarManager bossBarManager = world.getServer().getBossBarManager();
      CommandBossBar bossBar = bossBarManager.get(this.id);
      if (bossBar != null) {
         bossBarManager.remove(bossBar);
      }
   }

   @Override
   public void onRemoved(ServerWorld world, boolean force) {
      this.finish(world, !force);
   }

   @Override
   public BlockPos getPos() {
      return this.lastSpawnPos;
   }

   public void spawnWave(Wave wave, ServerWorld world) {
      this.spawnedMobs.clear();
      for (WaveMobGroup group : wave.groups()) {
         Optional<RegistryEntry<EntityType<?>>> typeEntry = group.types().getRandom(world.getRandom());
         if (typeEntry.isPresent()) {
            RegistryEntry<EntityType<?>> entityType = typeEntry.get();
            int count = group.count().get(world.getRandom());
            for (int i = 0; i < count; i++) {
               BlockPos spawnPos = group.spawnStrategy().getSpawnPos(world);
               if (spawnPos != null) {
                  Entity entity = entityType.value().spawn(world, spawnPos, SpawnReason.EVENT);
                  if (entity instanceof MobEntity mob) {
                     if (group.useBabyMobs()) {
                        mob.setBaby(true);
                        if (mob instanceof ZombieEntity) {
                           mob.tryEquip(world, Items.CHAINMAIL_HELMET.getDefaultStack());
                        }
                     }
                     mob.setPersistent();
                     mob.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, -1));
                     this.spawnedMobs.add(entity.getUuid());
                  }
               }
            }
         }
      }
   }

   @Override
   public net.zhengzhengyiyi.mine.class_11099.Status getStatus() {
      return this.status;
   }

   @Override
   public MapCodec<? extends net.zhengzhengyiyi.mine.class_11099> getCodec() {
      return CODEC;
   }

   public static WaveEvent.Builder builder(ServerWorld world, String name) {
      return new WaveEvent.Builder(world.getRegistryKey().getValue().withSuffixedPath("/" + name));
   }

   public static class Builder {
      private final Identifier id;
      private final List<Wave> waves = new ArrayList<>();

      public Builder(Identifier id) {
         this.id = id;
      }

      public WaveEvent.Builder addWave(Consumer<Wave.Builder> waveConsumer) {
         Wave.Builder builder = new Wave.Builder();
         waveConsumer.accept(builder);
         this.waves.add(builder.build());
         return this;
      }

      public WaveEvent build() {
         return new WaveEvent(this.id, this.waves);
      }
   }

   public record WaveMobGroup(RegistryEntryList<EntityType<?>> types, boolean useBabyMobs, IntProvider count, SpawnStrategy spawnStrategy) {
      public static final Codec<WaveMobGroup> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               RegistryCodecs.entryList(RegistryKeys.ENTITY_TYPE).fieldOf("types").forGetter(WaveMobGroup::types),
               Codec.BOOL.fieldOf("useBabyMobs").forGetter(WaveMobGroup::useBabyMobs),
               IntProvider.VALUE_CODEC.fieldOf("count").forGetter(WaveMobGroup::count),
               SpawnStrategy.CODEC.fieldOf("spawn_strategy").forGetter(WaveMobGroup::spawnStrategy)
            )
            .apply(instance, WaveMobGroup::new)
      );

      public static class Builder {
         private RegistryEntryList<EntityType<?>> types = RegistryEntryList.of();
         private boolean useBabyMobs = false;
         private IntProvider count = ConstantIntProvider.create(1);
         private SpawnStrategy spawnStrategy = new SpawnStrategy(SpawnType.NEAR_PLAYER, BlockPos.ORIGIN, 40, false);

         public WaveMobGroup.Builder types(RegistryEntryList<EntityType<?>> types) {
            this.types = types;
            return this;
         }

         @SuppressWarnings("deprecation")
		public WaveMobGroup.Builder type(EntityType<?> type) {
            return this.types(RegistryEntryList.of(type.getRegistryEntry()));
         }

         public WaveMobGroup.Builder baby(boolean baby) {
            this.useBabyMobs = baby;
            return this;
         }

         public WaveMobGroup.Builder count(IntProvider count) {
            this.count = count;
            return this;
         }

         public WaveMobGroup.Builder count(int count) {
            return this.count(ConstantIntProvider.create(count));
         }

         public WaveMobGroup.Builder spawnStrategy(Consumer<SpawnStrategy.Builder> consumer) {
            SpawnStrategy.Builder builder = new SpawnStrategy.Builder();
            consumer.accept(builder);
            this.spawnStrategy = builder.build();
            return this;
         }

         public WaveMobGroup build() {
            return new WaveMobGroup(this.types, this.useBabyMobs, this.count, this.spawnStrategy);
         }
      }
   }

   public record SpawnStrategy(SpawnType type, BlockPos offset, int range, boolean allowInAir) {
      public static final Codec<SpawnStrategy> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               SpawnType.CODEC.fieldOf("type").forGetter(SpawnStrategy::type),
               BlockPos.CODEC.fieldOf("offset").forGetter(SpawnStrategy::offset),
               Codecs.POSITIVE_INT.fieldOf("range").forGetter(SpawnStrategy::range),
               Codec.BOOL.fieldOf("allow_in_air").forGetter(SpawnStrategy::allowInAir)
            )
            .apply(instance, SpawnStrategy::new)
      );

      @Nullable
      public BlockPos getSpawnPos(ServerWorld world) {
         return switch (this.type) {
            case FIXED_POSITION -> this.offset;
            case ON_HEIGHTMAP -> this.offset.withY(world.getTopY(Heightmap.Type.WORLD_SURFACE, this.offset.getX(), this.offset.getZ()));
            case NEAR_POSITION -> this.findPosNear(world, this.offset);
            case NEAR_PLAYER -> this.findPosNearPlayer(world);
            case WARDEN_ARENA -> world.getTopPosition(Heightmap.Type.WORLD_SURFACE, BlockPos.ORIGIN).add(5, 2, -1);
         };
      }

      @Nullable
      private BlockPos findPosNear(ServerWorld world, BlockPos pos) {
         Random random = world.getRandom();
         for (int i = 0; i < MAX_SPAWN_ATTEMPTS; i++) {
            BlockPos targetPos = pos.add(random.nextBetween(-this.range, this.range), 0, random.nextBetween(-this.range, this.range));
            if (this.allowInAir) {
               if (isPosValid(world, targetPos)) return targetPos;
            } else {
               BlockPos.Mutable mutable = targetPos.mutableCopy();
               while (isAir(world, mutable) && Math.abs(mutable.getY() - pos.getY()) < this.range) {
                  mutable.move(Direction.DOWN);
               }
               mutable.move(Direction.UP);
               while (!isAir(world, mutable) && Math.abs(mutable.getY() - pos.getY()) < this.range) {
                  mutable.move(Direction.UP);
               }
               if (!isAir(world, mutable.down()) && isPosValid(world, mutable)) {
                  return mutable.toImmutable();
               }
            }
         }
         return null;
      }

      private static boolean isAir(ServerWorld world, BlockPos pos) {
         return world.getBlockState(pos).getCollisionShape(world, pos).isEmpty();
      }

      private static boolean isPosValid(ServerWorld world, BlockPos pos) {
         for (BlockPos p : BlockPos.iterate(pos.add(-1, 0, -1), pos.add(1, 1, 1))) {
            if (!isAir(world, p)) return false;
         }
         return true;
      }

      @Nullable
      private BlockPos findPosNearPlayer(ServerWorld world) {
         List<ServerPlayerEntity> players = world.getPlayers(p -> p.isAlive() && !p.isSpectator());
         return players.isEmpty() ? this.findPosNear(world, this.offset) : this.findPosNear(world, Util.getRandom(players, world.getRandom()).getBlockPos().add(this.offset));
      }

      public static class Builder {
         private SpawnType type = SpawnType.NEAR_PLAYER;
         private BlockPos offset = BlockPos.ORIGIN;
         private int range = 20;
         private boolean allowInAir = false;

         public Builder type(SpawnType type) {
            this.type = type;
            return this;
         }

         public Builder offset(BlockPos offset) {
            this.offset = offset;
            return this;
         }

         public Builder range(int range) {
            this.range = range;
            return this;
         }

         public Builder inAir() {
            this.allowInAir = true;
            return this;
         }

         public SpawnStrategy build() {
            return new SpawnStrategy(this.type, this.offset, this.range, this.allowInAir);
         }
      }
   }

   public enum SpawnType implements StringIdentifiable {
      FIXED_POSITION("fixed_position"),
      ON_HEIGHTMAP("on_heightmap"),
      NEAR_POSITION("near_position"),
      NEAR_PLAYER("near_player"),
      WARDEN_ARENA("warden_arena");

      public static final Codec<SpawnType> CODEC = StringIdentifiable.createCodec(SpawnType::values);
      private final String id;

      SpawnType(String id) { this.id = id; }

      @Override
      public String asString() { return this.id; }
   }

   public record Wave(List<WaveMobGroup> groups, boolean showBar, Text countdownText, int ticksDelay) {
      public static final Codec<Wave> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               WaveMobGroup.CODEC.listOf().fieldOf("groups").forGetter(Wave::groups),
               Codec.BOOL.fieldOf("show_bar").forGetter(Wave::showBar),
               TextCodecs.CODEC.fieldOf("countdown").forGetter(Wave::countdownText),
               Codecs.NON_NEGATIVE_INT.fieldOf("ticks_delay").forGetter(Wave::ticksDelay)
            )
            .apply(instance, Wave::new)
      );

      public static class Builder {
         private final List<WaveMobGroup> groups = new ArrayList<>();
         private boolean showBar = true;
         private Text countdownText = Text.translatable("world.event.next_wave");
         private int ticksDelay = 0;

         public Builder addGroup(Consumer<WaveMobGroup.Builder> consumer) {
            WaveMobGroup.Builder builder = new WaveMobGroup.Builder();
            consumer.accept(builder);
            this.groups.add(builder.build());
            return this;
         }

         public Builder hideBar() {
            this.showBar = false;
            return this;
         }

         public Builder countdown(Text text) {
            this.countdownText = text;
            return this;
         }

         public Builder delay(int ticks) {
            this.ticksDelay = ticks;
            return this;
         }

         public Wave build() {
            return new Wave(this.groups, this.showBar, this.countdownText, this.ticksDelay);
         }
      }
   }
}
