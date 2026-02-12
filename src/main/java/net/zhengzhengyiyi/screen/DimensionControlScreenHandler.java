package net.zhengzhengyiyi.screen;

import java.util.Optional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.dimension.DimensionType;
import net.zhengzhengyiyi.block.ModBlocks;
import net.zhengzhengyiyi.component.ModDataComponentTypes;
import net.zhengzhengyiyi.item.ModItems;
import net.zhengzhengyiyi.mixin.ServerWorldMixin;
import net.zhengzhengyiyi.render.DimensionEffects;
import net.zhengzhengyiyi.render.DimensionEffects.SkyType;
import net.zhengzhengyiyi.render.DimensionEffects.class_11079;

public class DimensionControlScreenHandler extends ScreenHandler {
   public static final int field_58755 = 0;
   @SuppressWarnings("unused")
private static final int field_58756 = 1;
   @SuppressWarnings("unused")
private static final int field_58757 = 28;
   @SuppressWarnings("unused")
private static final int field_58758 = 28;
   @SuppressWarnings("unused")
private static final int field_58759 = 37;
   private final Inventory field_58760 = new SimpleInventory(1) {
      @Override
      public void markDirty() {
         super.markDirty();
         DimensionControlScreenHandler.this.onContentChanged(this);
      }
   };
   private final ScreenHandlerContext field_58761;

   public DimensionControlScreenHandler(int i, PlayerInventory playerInventory) {
      this(i, playerInventory, ScreenHandlerContext.EMPTY);
   }

   public DimensionControlScreenHandler(int i, PlayerInventory playerInventory, ScreenHandlerContext screenHandlerContext) {
      super(ModScreenHandlerType.DIMENSION_CONTROL, i);
      this.field_58761 = screenHandlerContext;
      this.addSlot(new Slot(this.field_58760, 0, 129, 34) {
         @Override
         public boolean canInsert(ItemStack stack) {
            return stack.contains(ModDataComponentTypes.SKY);
         }
      });
      this.addPlayerSlots(playerInventory, 8, 84);
      screenHandlerContext.run((world, blockPos) -> {
    	    EnvironmentAttributeMap attributes = world.getDimension().attributes();

    	    DimensionEffects currentEffects = new DimensionEffects(
    	        Optional.ofNullable(attributes.apply(EnvironmentAttributes.CLOUD_HEIGHT_VISUAL, null)),
    	        true,
    	        Optional.of(new DimensionEffects.class_11082() {
					@Override
					public SkyType method_69775() {
						return SkyType.END;
					}

					@Override
					public Text method_69776() {
						return Text.of("null, none");
					}
    	        	
    	        }),
    	        true,
    	        false,
    	        class_11079.OVERWORLD,
    	        true,
    	        false
    	    );

    	    currentEffects.sky().ifPresent(skyEffect -> {
    	        ItemStack itemStack = new ItemStack(ModItems.SKY_BOX);
    	        itemStack.set(ModDataComponentTypes.SKY, skyEffect);
    	        this.field_58760.setStack(0, itemStack);
    	    });
    	});
//      screenHandlerContext.run((world, blockPos) -> world.getDimension().attributes().sky().ifPresent(arg -> {
//         ItemStack itemStack = new ItemStack(Items.SKY_BOX);
//         itemStack.set(DataComponentTypes.SKY, arg);
//         this.field_58760.setStack(0, itemStack);
//      }));
   }

   @Override
   public void onContentChanged(Inventory inventory) {
      super.onContentChanged(inventory);
      if (inventory == this.field_58760) {
         this.method_69463();
      }
   }

   private void method_69463() {
      ItemStack itemStack = this.field_58760.getStack(0);
      @SuppressWarnings("unused")
	  DimensionEffects.class_11082 lv = itemStack.get(ModDataComponentTypes.SKY);
      this.field_58761.run((world, blockPos) -> {
         RegistryEntry<DimensionType> registryEntry = world.getDimensionEntry();
         DimensionType dimensionType = registryEntry.value();
         ((ServerWorldMixin)(Object)world).method_69089(RegistryEntry.of(dimensionType));
      });
   }

   @Override
   public boolean canUse(PlayerEntity player) {
      return canUse(this.field_58761, player, ModBlocks.DIMENSION_CONTROL);
   }

   @Override
   public ItemStack quickMove(PlayerEntity player, int slot) {
      ItemStack itemStack = ItemStack.EMPTY;
      Slot slot2 = this.slots.get(slot);
      if (slot2 != null && slot2.hasStack()) {
         ItemStack itemStack2 = slot2.getStack();
         itemStack = itemStack2.copy();
         if (slot == 0) {
            if (!this.insertItem(itemStack2, 1, 37, false)) {
               return ItemStack.EMPTY;
            }
         } else {
            if (!this.insertItem(itemStack2, 0, 1, false)) {
               return ItemStack.EMPTY;
            }

            if (slot >= 1 && slot < 28) {
               if (!this.insertItem(itemStack2, 28, 37, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (slot >= 28 && slot < 37 && !this.insertItem(itemStack2, 1, 28, false)) {
               return ItemStack.EMPTY;
            }
         }

         if (itemStack2.isEmpty()) {
            slot2.setStack(ItemStack.EMPTY);
         } else {
            slot2.markDirty();
         }

         if (itemStack2.getCount() == itemStack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot2.onTakeItem(player, itemStack2);
      }

      return itemStack;
   }
}
