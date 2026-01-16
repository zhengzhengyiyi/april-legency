package net.zhengzhengyiyi.block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;
import net.zhengzhengyiyi.block.context.PlaceBlockContext;
import net.zhengzhengyiyi.rules.VoteRules;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;

public class PlaceBlock extends AbstractPlaceBlock {
	private static final int[][] AVAILABLE_SLOTS_CACHE = new int[54][];
	
   protected PlaceBlock(AbstractBlock.Settings settings) {
      super(settings);
   }

   @Override
   protected TickPriority getTickPriority() {
      return TickPriority.EXTREMELY_LOW;
   }

   @Override
   public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      if (VoteRules.PLACE_BLOCK.isActive()) {
         Direction direction = state.get(FACING);
         Direction direction2 = direction.getOpposite();
         BlockPos blockPos = pos.offset(direction2);
         BlockPos blockPos2 = pos.offset(direction);
         method_50878(
            world,
            blockPos,
            direction2,
            itemStack -> {
               if (itemStack.isEmpty()) {
                  return false;
               } else {
                  boolean bl = itemStack.getItem() instanceof BlockItem blockItem
                     && blockItem.place(
                           new PlaceBlockContext(world, Hand.MAIN_HAND, itemStack, new BlockHitResult(blockPos2.toCenterPos(), direction2, blockPos2, false))
                        )
                        .isAccepted();
                  if (!bl) {
                     double d = EntityType.ITEM.getHeight() / 2.0;
                     double e = blockPos2.getX() + 0.5;
                     double f = blockPos2.getY() + 0.5 - d;
                     double g = blockPos2.getZ() + 0.5;
                     ItemEntity itemEntity = new ItemEntity(world, e, f, g, itemStack);
                     itemEntity.setToDefaultPickupDelay();
                     world.spawnEntity(itemEntity);
                  }

                  return true;
               }
            }
         );
      }
   }

   public static boolean method_50881(World world, BlockPos blockPos, Direction direction, ItemStack itemStack) {
      for (Inventory inventory : method_50877(world, blockPos)) {
         ItemStack itemStack2 = HopperBlockEntity.transfer(null, inventory, itemStack, direction);
         if (itemStack2.isEmpty()) {
            return true;
         }
      }

      return false;
   }

   public static boolean method_50878(World world, BlockPos blockPos, Direction direction, Function<ItemStack, Boolean> function) {
      for (Inventory inventory : method_50877(world, blockPos)) {
         boolean bl = IntStream.of(getAvailableSlots(inventory, direction)).anyMatch(i -> {
            ItemStack itemStackx = inventory.removeStack(i, 1);
            if (!itemStackx.isEmpty()) {
               boolean blx = function.apply(itemStackx.copy());
               if (blx) {
                  inventory.markDirty();
               } else {
                  inventory.setStack(i, itemStackx);
               }

               return true;
            } else {
               return false;
            }
         });
         if (bl) {
            return true;
         }
      }

      ItemEntity itemEntity = method_50880(world, blockPos);
      if (itemEntity != null) {
         ItemStack itemStack = itemEntity.getStack();
         if (!itemStack.isEmpty()) {
            boolean bl = function.apply(itemStack.copyWithCount(1));
            if (bl) {
               itemStack.decrement(1);
               if (itemStack.getCount() <= 0) {
                  itemEntity.discard();
               }
            }

            return true;
         }
      }

      return false;
   }

   public static List<Inventory> method_50877(World world, BlockPos blockPos) {
      BlockState blockState = world.getBlockState(blockPos);
      Block block = blockState.getBlock();
      if (block instanceof InventoryProvider) {
         SidedInventory sidedInventory = ((InventoryProvider)block).getInventory(blockState, world, blockPos);
         if (sidedInventory != null) {
            return List.of(sidedInventory);
         }
      } else if (blockState.hasBlockEntity()) {
         BlockEntity blockEntity = world.getBlockEntity(blockPos);
         if (blockEntity instanceof Inventory) {
            if (!(blockEntity instanceof ChestBlockEntity) || !(block instanceof ChestBlock)) {
               return List.of((Inventory)blockEntity);
            }

            Inventory inventory = ChestBlock.getInventory((ChestBlock)block, blockState, world, blockPos, true);
            if (inventory != null) {
               return List.of(inventory);
            }
         }
      }

      List<Inventory> list = new ArrayList<>();

      for (Entity entity : world.getOtherEntities((Entity)null, getBox(blockPos), EntityPredicates.VALID_INVENTORIES)) {
         if (entity instanceof Inventory inventory2) {
            list.add(inventory2);
         }
      }
      return list;
   }

   @Nullable
   public static ItemEntity method_50880(World world, BlockPos blockPos) {
      List<ItemEntity> list = world.getEntitiesByClass(ItemEntity.class, getBox(blockPos), EntityPredicates.VALID_ENTITY);
      return list.size() < 1 ? null : list.get(0);
   }

   private static Box getBox(BlockPos blockPos) {
//      double d = 0.9999999;
      return Box.of(blockPos.toCenterPos(), 0.9999999, 0.9999999, 0.9999999);
   }

   @Override
   protected MapCodec<? extends FacingBlock> getCodec() {
	   return null;
   }
   
   private static int[] indexArray(int size) {
		int[] is = new int[size];
		int i = 0;

		while (i < is.length) {
			is[i] = i++;
		}

		return is;
	}
   
   private static int[] getAvailableSlots(Inventory inventory, Direction side) {
		if (inventory instanceof SidedInventory sidedInventory) {
			return sidedInventory.getAvailableSlots(side);
		} else {
			int i = inventory.size();
			if (i < AVAILABLE_SLOTS_CACHE.length) {
				int[] is = AVAILABLE_SLOTS_CACHE[i];
				if (is != null) {
					return is;
				} else {
					int[] js = indexArray(i);
					AVAILABLE_SLOTS_CACHE[i] = js;
					return js;
				}
			} else {
				return indexArray(i);
			}
		}
	}
}
