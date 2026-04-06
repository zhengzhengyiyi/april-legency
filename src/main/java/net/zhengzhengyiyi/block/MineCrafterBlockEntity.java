package net.zhengzhengyiyi.block;

import java.util.List;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.zhengzhengyiyi.accessor.MineServerWorldAccessor;
import net.zhengzhengyiyi.component.ModDataComponentTypes;

import org.jetbrains.annotations.Nullable;

public class MineCrafterBlockEntity extends LockableContainerBlockEntity implements RecipeInputProvider, ExtendedScreenHandlerFactory<List<Integer>> {
   protected DefaultedList<ItemStack> inventory = DefaultedList.ofSize(99, ItemStack.EMPTY);
   @Nullable
   private BlockPos travellingBlockPos;
   private int dropRewardsTimer = 0;

   public MineCrafterBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(ModBlocks.MINE_CRAFTER_BLOCKENTITY, blockPos, blockState);
   }

   public static int getMiningCooldown(int level) {
      if (level >= 15) {
         return 2000 + (level - 10) * 400;
      } else {
         return level >= 5 ? 400 + (level - 5) * 160 : 100 + Math.max(0, level) * 60;
      }
   }

   public static int getMaxInventorySize(int level) {
      return Math.min(50, 3 + level);
   }

   public static int getRewardCount(int level) {
      return 1 + MathHelper.floor(getMaxInventorySize(level) / 3.0F);
   }

   @Override
   protected void readData(ReadView view) {
      super.readData(view);
      this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
      Inventories.readData(view, this.inventory);

      this.travellingBlockPos = view.read("mine_travelling_block_pos", BlockPos.CODEC).filter(World::isValid).orElse(null);
      this.dropRewardsTimer = view.getInt("drop_rewards_in_ticks", 0);
   }

   @Override
   protected void writeData(WriteView view) {
      super.writeData(view);
      view.putNullable("mine_travelling_block_pos", BlockPos.CODEC, this.travellingBlockPos);
      view.putInt("drop_rewards_in_ticks", this.dropRewardsTimer);
      Inventories.writeData(view, this.inventory);
   }

   @Nullable
   @Override
   public BlockEntityUpdateS2CPacket toUpdatePacket() {
      return BlockEntityUpdateS2CPacket.create(this);
   }

   @Override
   public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
      return this.createComponentlessNbt(registries);
   }

   public static void tick(World world, BlockPos blockPos, BlockState blockState, MineCrafterBlockEntity blockEntity) {
      if (world instanceof ServerWorld serverWorld) {
         boolean needsUpdate = false;
         
         if (blockEntity.dropRewardsTimer > 0) {
            blockEntity.dropRewardsTimer--;
            if (blockEntity.dropRewardsTimer == 0) {
               blockEntity.spawnRewards(serverWorld, blockPos);
               blockEntity.markDirty();
            }
         }

         if (blockEntity.travellingBlockPos != null) {
            ItemStack coreStack = blockEntity.inventory.get(0);
            if (!coreStack.isEmpty()) {
               boolean isActive = coreStack.get(ModDataComponentTypes.MINE_ACTIVE) != null;
               Boolean isCompleted = coreStack.get(ModDataComponentTypes.MINE_COMPLETED);
               
               if (isActive && isCompleted == null && world.getBlockEntity(blockEntity.travellingBlockPos) instanceof TravellingBlockEntity travellingBlock) {
                  ServerWorld targetWorld = serverWorld.getServer().getWorld(travellingBlock.getDimensionKey());
                  if (targetWorld != null && ((MineServerWorldAccessor) targetWorld).isMineWorld() && ((MineServerWorldAccessor) targetWorld).isMineCompleted()) {
                     coreStack.set(ModDataComponentTypes.MINE_COMPLETED, ((MineServerWorldAccessor) targetWorld).isMineWon());
                     coreStack.remove(ModDataComponentTypes.MINE_ACTIVE);
                     world.breakBlock(blockEntity.travellingBlockPos, false, null);
                     blockEntity.travellingBlockPos = null;
                     blockEntity.dropRewardsTimer = 20;
                     needsUpdate = true;
                  }
               }
            } else {
               blockEntity.travellingBlockPos = null;
               needsUpdate = true;
            }
         }

         if (needsUpdate) {
            markDirty(world, blockPos, blockState);
         }
      }
   }

   @Override
   public int size() {
      return this.inventory.size();
   }

   @Override
   protected DefaultedList<ItemStack> getHeldStacks() {
      return this.inventory;
   }

   @Override
   protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
      this.inventory = inventory;
   }

   @Override
   public void setStack(int slot, ItemStack stack) {
      this.inventory.set(slot, stack);
      stack.capCount(this.getMaxCount(stack));
      this.markDirty();
   }

   @Override
   public boolean isValid(int slot, ItemStack stack) {
      return slot != 0;
   }

   @Override
   public void provideRecipeInputs(RecipeFinder finder) {
      for (ItemStack itemStack : this.inventory) {
         finder.addInput(itemStack);
      }
   }

   @Override
   protected Text getContainerName() {
      return Text.translatable("container.mine_crafter");
   }

   @Override
   public List<Integer> getScreenOpeningData(ServerPlayerEntity player) {
      return getMiningStats();
   }

   @Override
   protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
      return new net.zhengzhengyiyi.mine.MineEffectGenerator(
         syncId,
         playerInventory,
         ScreenHandlerContext.create(this.getWorld(), this.getPos()),
         this,
         dimensionKey -> this.startMining(this.getWorld(), this.getPos(), dimensionKey),
         this.getMiningStats()
      );
   }

   public List<Integer> getMiningStats() {
      return this.getWorld() instanceof ServerWorld serverWorld ? getServerMiningStats(serverWorld) : List.of(0, 0);
   }

   private void startMining(World world, BlockPos blockPos, RegistryKey<DimensionOptions> dimensionKey) {
      this.travellingBlockPos = blockPos.up();
      MiningPortalBlock.createPortal(world, blockPos.up(), dimensionKey, false);
      this.markDirty();
   }

   public static List<Integer> getServerMiningStats(ServerWorld serverWorld) {
      net.zhengzhengyiyi.accessor.LevelPropertiesAccessor props = (net.zhengzhengyiyi.accessor.LevelPropertiesAccessor)(Object)serverWorld.getServer().getSaveProperties().getMainWorldProperties();
      int level = props.getMineLevel();
      int exp = props.getMineExp();
      return List.of(level, exp);
   }

   @Override
   public void onBlockReplaced(BlockPos pos, BlockState oldState) {
      if (this.getWorld() instanceof ServerWorld serverWorld) {
         net.zhengzhengyiyi.accessor.LevelPropertiesAccessor props = (net.zhengzhengyiyi.accessor.LevelPropertiesAccessor)(Object)serverWorld.getServer().getSaveProperties().getMainWorldProperties();
         int count = getRewardCount(props.getMineLevel());
         for (int j = 0; j < count + 1; j++) {
            this.inventory.set(j, ItemStack.EMPTY);
         }
      }
      super.onBlockReplaced(pos, oldState);
   }

   public void spawnRewards(ServerWorld serverWorld, BlockPos blockPos) {
      ItemStack coreStack = this.getStack(0);
      Boolean isCompleted = coreStack.get(ModDataComponentTypes.MINE_COMPLETED);
      if (isCompleted != null) {
         boolean success = isCompleted;
         ItemStack resultStack = coreStack.copyWithCount(1);
         ItemEntity itemEntity = new ItemEntity(serverWorld, blockPos.getX() + 0.5, blockPos.getY() + 2.5, blockPos.getZ() + 0.5, resultStack);
         Vec3d velocity = new Vec3d(
            (serverWorld.random.nextDouble() * 0.2 - 0.1) * 2.0, 
            serverWorld.random.nextDouble() * 0.4, 
            (serverWorld.random.nextDouble() * 0.2 - 0.1) * 2.0
         );
         itemEntity.setVelocity(velocity);
         serverWorld.spawnEntity(itemEntity);
         
         serverWorld.playSound(null, blockPos, success ? SoundEvents.UI_LOOM_TAKE_RESULT : SoundEvents.ENTITY_VILLAGER_NO, SoundCategory.BLOCKS, 1.0F, 1.0F);
         serverWorld.updateNeighbors(blockPos, this.getCachedState().getBlock());
         this.inventory.forEach(stack -> stack.setCount(0));
      }
   }
}
