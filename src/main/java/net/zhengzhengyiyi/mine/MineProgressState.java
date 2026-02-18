package net.zhengzhengyiyi.mine;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;
import net.zhengzhengyiyi.*;

public class MineProgressState extends PersistentState {
   private static final String DATA_KEY = "mine_data";
   public static final Codec<MineProgressState> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Codec.BOOL.fieldOf("is_mine").forGetter(state -> state.isMine),
            Codec.BOOL.fieldOf("has_placed_start_structures").forGetter(state -> state.hasPlacedStartStructures),
            StringIdentifiable.createCodec(MineProgressState.Status::values).fieldOf("mine_state").forGetter(state -> state.status),
            Codec.INT.fieldOf("leave_countdown").forGetter(state -> state.leaveCountdown),
            Codec.INT.fieldOf("experience_to_drop").forGetter(state -> state.experienceToDrop),
            Codec.INT.fieldOf("keys_to_roll").forGetter(state -> state.keysToRoll),
            BlockPos.CODEC.optionalFieldOf("travelling_block_activated").forGetter(state -> state.travellingBlockActivated),
            Codec.list(Uuids.INT_STREAM_CODEC).fieldOf("dead_players").forGetter(state -> state.deadPlayers)
         )
         .apply(instance, MineProgressState::new)
   );
   public static final PersistentStateType<MineProgressState> TYPE = new PersistentStateType<>(
      "mine_data", MineProgressState::new, CODEC, AprilsLegacy.SAVED_DATA_MINE_PROGRESS
   );
   public static final int DEFAULT_COUNTDOWN = 200;
   private boolean isMine;
   private boolean hasPlacedStartStructures;
   private MineProgressState.Status status = MineProgressState.Status.ONGOING;
   private int leaveCountdown = 200;
   private int experienceToDrop = 0;
   private int keysToRoll = 0;
   private Optional<BlockPos> travellingBlockActivated = Optional.empty();
   private List<UUID> deadPlayers = new ArrayList<>();

   public MineProgressState() {
      this.markDirty();
   }

   private MineProgressState(boolean isMine, boolean hasPlacedStartStructures, MineProgressState.Status status, int leaveCountdown, int experienceToDrop, int keysToRoll, Optional<BlockPos> travellingBlockActivated, List<UUID> deadPlayers) {
      this.isMine = isMine;
      this.hasPlacedStartStructures = hasPlacedStartStructures;
      this.status = status;
      this.leaveCountdown = leaveCountdown;
      this.keysToRoll = keysToRoll;
      this.travellingBlockActivated = travellingBlockActivated;
      this.experienceToDrop = experienceToDrop;
      this.deadPlayers.addAll(deadPlayers);
   }

   public boolean isMine() {
      return this.isMine;
   }

   public void setMine(boolean mine) {
      this.isMine = mine;
      this.markDirty();
   }

   public boolean hasPlacedStartStructures() {
      return this.hasPlacedStartStructures;
   }

   public void setPlacedStartStructures(boolean placed) {
      this.hasPlacedStartStructures = placed;
      this.markDirty();
   }

   public MineProgressState.Status getStatus() {
      return this.status;
   }

   public void setStatus(MineProgressState.Status status) {
      this.status = status;
      this.markDirty();
   }

   public int getLeaveCountdown() {
      return this.leaveCountdown;
   }

   public void resetLeaveCountdown() {
      this.markDirty();
      this.leaveCountdown = 200;
   }

   public boolean tickLeaveCountdown() {
      this.markDirty();
      return --this.leaveCountdown <= 0;
   }

   public boolean toggleTravellingBlock(BlockPos pos) {
      this.markDirty();
      if (this.travellingBlockActivated.isPresent()) {
         this.travellingBlockActivated = Optional.empty();
         this.resetLeaveCountdown();
         return false;
      } else {
         this.travellingBlockActivated = Optional.of(pos);
         return true;
      }
   }

   public void deactivateTravellingBlock() {
      this.markDirty();
      this.travellingBlockActivated = Optional.empty();
      this.resetLeaveCountdown();
   }

   public int getExperienceToDrop() {
      return this.experienceToDrop;
   }

   public void addExperienceToDrop(int amount) {
      this.experienceToDrop += amount;
      this.markDirty();
   }

   public int getKeysToRoll() {
      return this.keysToRoll;
   }

   public void addKeysToRoll(int amount) {
      this.keysToRoll += amount;
      this.markDirty();
   }

   public void clearKeysToRoll() {
      this.field_58507 = 0;
      this.markDirty();
   }

   public void clearExperienceToDrop() {
      this.field_58506 = 0;
      this.markDirty();
   }
   
   public int field_58506;
   public int field_58507;

   public Optional<BlockPos> getTravellingBlockPos() {
      return this.travellingBlockActivated;
   }

   public void onTick(ServerWorld world) {
   }

   public void addDeadPlayer(UUID uuid) {
      if (!this.deadPlayers.contains(uuid)) {
         this.deadPlayers.add(uuid);
         this.markDirty();
      }
   }

   public boolean isPlayerDead(UUID uuid) {
      return this.deadPlayers.contains(uuid);
   }

   public static MineProgressState fromNbt(NbtCompound nbt) {
      return CODEC.parse(NbtOps.INSTANCE, nbt).resultOrPartial().orElseGet(MineProgressState::new);
   }

   public static enum Status implements StringIdentifiable {
      ONGOING,
      WON,
      FAILED;

      @Override
      public String asString() {
         return this.name().toLowerCase();
      }
   }
}
