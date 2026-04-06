package net.zhengzhengyiyi.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;

public class TravellingBlockEntity extends BlockEntity {
   private static final int TRAVEL_DURATION = 200;
   private long age = 0L;
   private RegistryKey<DimensionOptions> dimensionKey = DimensionOptions.OVERWORLD;
   private boolean revisit;

   public TravellingBlockEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.TRAVELLING_BLOCK_ENTITY, pos, state);
   }

   public long getAge() {
      return this.age;
   }

   @Override
   protected void writeData(WriteView view) {
      super.writeData(view);
      view.putLong("age", this.age);
      view.putBoolean("revisit", this.revisit);
      view.put("dimension", RegistryKey.createCodec(RegistryKeys.DIMENSION), this.dimensionKey);
   }

   @Override
   protected void readData(ReadView view) {
      super.readData(view);
      this.age = view.getLong("age", 0L);
      this.revisit = view.getBoolean("revisit", false);
      this.dimensionKey = view.<RegistryKey<DimensionOptions>>read("dimension", RegistryKey.createCodec(RegistryKeys.DIMENSION))
         .orElse(DimensionOptions.OVERWORLD);
   }

   public void setDimensionKey(RegistryKey<DimensionOptions> key) {
      this.dimensionKey = key;
      this.markDirty();
   }

   public void setRevisit(boolean revisit) {
      this.revisit = revisit;
   }

   public static void clientTick(World world, BlockPos pos, BlockState state, TravellingBlockEntity entity) {
      entity.age++;
   }

   public static void serverTick(World world, BlockPos pos, BlockState state, TravellingBlockEntity entity) {
      boolean wasActive = entity.isActive();
      entity.age++;
      if (wasActive != entity.isActive()) {
         markDirty(world, pos, state);
      }
   }

   public boolean isActive() {
      return this.age < TRAVEL_DURATION;
   }

   public float getProgress(float tickDelta) {
      float g = ((float)this.age + tickDelta) % TRAVEL_DURATION;
      return MathHelper.clamp(g / TRAVEL_DURATION, 0.0F, 1.0F);
   }

   public float getTotalProgress(float tickDelta) {
      return MathHelper.clamp(((float)this.age + tickDelta) / TRAVEL_DURATION, 0.0F, 1.0F);
   }

   public BlockEntityUpdateS2CPacket toUpdatePacket() {
      return BlockEntityUpdateS2CPacket.create(this);
   }

   @Override
   public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
      return this.createComponentlessNbt(registries);
   }

   public boolean isFaceVisible(Direction direction) {
      return Block.shouldDrawSide(this.getCachedState(), this.world.getBlockState(this.getPos().offset(direction)), direction);
   }

   public int getVisibleFaceCount() {
      int count = 0;
      for (Direction dir : Direction.values()) {
         if (this.isFaceVisible(dir)) count++;
      }
      return count;
   }

   public RegistryKey<World> getDimensionKey() {
      return RegistryKeys.toWorldKey(this.dimensionKey);
   }

   public boolean isRevisit() {
      return this.revisit;
   }
}
