package net.zhengzhengyiyi;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.zhengzhengyiyi.item.ByteItem;
import net.zhengzhengyiyi.item.ModItems;
import net.zhengzhengyiyi.item.Name;
import net.zhengzhengyiyi.rules.VoteRules;

import org.jetbrains.annotations.Nullable;

public class NbtCraftingRecipe extends SpecialCraftingRecipe {
   public NbtCraftingRecipe(CraftingRecipeCategory craftingRecipeCategory) {
      super(craftingRecipeCategory);
   }

   private static List<ItemStack> getInputs(CraftingInventory craftingInventory) {
      List<ItemStack> list = new ArrayList<>();

      for (int i = 0; i < craftingInventory.size(); i++) {
         ItemStack itemStack = craftingInventory.getStack(i);
         if (!itemStack.isEmpty()) {
            list.add(itemStack);
         }
      }

      return list;
   }

   public boolean matches(CraftingInventory craftingInventory, World world) {
//      return !VoteRules.NBT_CRAFTING.isActive() ? false : !getOutput(getInputs(craftingInventory)).isEmpty();
	   return !getOutput(getInputs(craftingInventory)).isEmpty();
   }

   public ItemStack craft(CraftingInventory craftingInventory, DynamicRegistryManager dynamicRegistryManager) {
      return !VoteRules.NBT_CRAFTING.isActive() ? ItemStack.EMPTY : getOutput(getInputs(craftingInventory));
   }

   private static int removeAndCount(List<ItemStack> list, Predicate<ItemStack> predicate, boolean useStackCount) {
      int i = 0;
      Iterator<ItemStack> iterator = list.iterator();

      while (iterator.hasNext()) {
         ItemStack itemStack = iterator.next();
         if (predicate.test(itemStack)) {
            iterator.remove();
            i += useStackCount ? itemStack.getCount() : 1;
         }
      }

      return i;
   }

   @Nullable
   private static <T> T findAndRemoveSingle(List<ItemStack> list, Function<ItemStack, T> function) {
      T object = null;
      Iterator<ItemStack> iterator = list.iterator();

      while (iterator.hasNext()) {
         ItemStack itemStack = iterator.next();
         T object2 = function.apply(itemStack);
         if (object2 != null) {
            if (object != null) {
               return null;
            }

            iterator.remove();
            object = object2;
         }
      }

      return object;
   }

   private static <T extends NbtElement> ItemStack createNbtItem(Item item, T nbtElement) {
      ItemStack itemStack = new ItemStack(item);
      ((ByteItem<?>)item).applyNbt(itemStack, nbtElement);
      return itemStack;
   }

   private static ItemStack createErrorItem(String string) {
	    ItemStack itemStack = new ItemStack(ModItems.SYNTAX_ERROR);
	    
	    Text loreText = Text.literal(string);
	    LoreComponent loreComponent = new LoreComponent(List.of(loreText));
	    
	    itemStack.set(DataComponentTypes.LORE, loreComponent);
	    
	    return itemStack;
	}

   public static ItemStack getOutput(List<ItemStack> list) {
      if (list.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         int i = removeAndCount(list, itemStackx -> itemStackx.isOf(ModItems.TAG), false);
         if (i > 0) {
            return craftTag(list, i);
         } else {
            int j = removeAndCount(list, itemStackx -> itemStackx.isIn(ItemTags.BOATS), false);
            if (j > 0) {
               return craftFloatOrDouble(list, j);
            } else {
               ItemStack itemStack = list.get(0);
               if (itemStack.isOf(ModItems.LEFT_CURLY) && list.size() > 1) {
                  return craftCompound(list);
               } else if (itemStack.isOf(ModItems.LEFT_SQUARE) && list.size() > 1) {
                  return craftList(list);
               } else if (!itemStack.isOf(ModItems.RIGHT_CURLY) && !itemStack.isOf(ModItems.RIGHT_SQUARE) && !itemStack.isOf(ModItems.NAME)) {
                  byte[] bs = getBytesFromTags(list);
                  return bs != null ? craftNumberFromBytes(bs) : craftConcatenation(list);
               } else {
                  return createErrorItem("Expected { or [");
               }
            }
         }
      }
   }

   private static ItemStack craftTag(List<ItemStack> list, int i) {
      if (i != 1) {
         return ItemStack.EMPTY;
      } else {
         int j = removeAndCount(list, itemStack -> itemStack.isOf(Items.STRING), false);
         if (j > 0) {
            return j == 1 && list.isEmpty() ? createNbtItem(ModItems.STRING_TAG, NbtString.of("")) : ItemStack.EMPTY;
         } else {
            int k = removeAndCount(list, itemStack -> itemStack.isOf(Items.STICK), true);
            return !list.isEmpty() ? ItemStack.EMPTY : createNbtItem(ModItems.BYTE_TAG, NbtByte.of((byte)k));
         }
      }
   }

   private static ItemStack craftFloatOrDouble(List<ItemStack> list, int i) {
      AbstractNbtNumber abstractNbtNumber = findAndRemoveSingle(list, NbtCraftingRecipe::getNbtNumber);
      if (abstractNbtNumber == null) {
         return ItemStack.EMPTY;
      } else {
         boolean bl = i == 1;
         boolean bl2 = i == 2;
         if (!bl && !bl2) {
            return createErrorItem("Expected either single or double");
         } else {
            boolean bl3 = removeAndCount(list, itemStack -> itemStack.isOf(ModItems.BIT), false) > 0;
            if (!list.isEmpty()) {
               return createErrorItem("Unexpected entries in when casting to float");
            } else if (bl) {
               float f = bl3 ? Float.intBitsToFloat(abstractNbtNumber.intValue()) : abstractNbtNumber.floatValue();
               return createNbtItem(ModItems.FLOAT_TAG, NbtFloat.of(f));
            } else {
               double d = bl3 ? Double.longBitsToDouble(abstractNbtNumber.longValue()) : abstractNbtNumber.doubleValue();
               return createNbtItem(ModItems.DOUBLE_TAG, NbtDouble.of(d));
            }
         }
      }
   }

   private static ItemStack craftList(List<ItemStack> list) {
      NbtList nbtList = new NbtList();
      boolean bl = false;

      for (int i = 1; i < list.size(); i++) {
         if (bl) {
            return createErrorItem("Unexpected value after closing bracket");
         }

         ItemStack itemStack = list.get(i);
         if (itemStack.isOf(ModItems.RIGHT_SQUARE)) {
            bl = true;
         } else {
            if (!(itemStack.getItem() instanceof ByteItem lv)) {
               return createErrorItem("Unexpected value in list: expected either tag or closing bracket");
            }

            NbtElement nbtElement = lv.method_50804(itemStack);
            if (nbtElement == null) {
               return createErrorItem("OH NO INTERNAL ERROR");
            }

            if (!nbtList.addElement(nbtList.size(), nbtElement)) {
               return createErrorItem("Can't add element of type " + nbtElement.asString() + " to list " + nbtList.asString());
            }
         }
      }

      return !bl ? createErrorItem("Expected closing bracket") : createNbtItem(ModItems.LIST_TAG, nbtList);
   }

   private static ItemStack craftCompound(List<ItemStack> list) {
      NbtCompound nbtCompound = new NbtCompound();
      boolean bl = false;
      String string = null;

      for (int i = 1; i < list.size(); i++) {
         if (bl) {
            return createErrorItem("Unexpected value after closing bracket");
         }

         ItemStack itemStack = list.get(i);
         if (itemStack.isOf(ModItems.RIGHT_CURLY)) {
            bl = true;
         } else if (itemStack.isOf(ModItems.NAME)) {
            if (string != null) {
               return createErrorItem("Expected tag after name");
            }

            string = Name.method_50802(itemStack);
         } else {
            if (!(itemStack.getItem() instanceof ByteItem lv)) {
               return createErrorItem("Unexpected value in compound tag: expected either name, tag or closing bracket");
            }

            if (string == null) {
               return createErrorItem("Expected name");
            }

            NbtElement nbtElement = lv.method_50804(itemStack);
            if (nbtElement == null) {
               return createErrorItem("INTERNAL ERROR OH NO");
            }

            nbtCompound.put(string, nbtElement);
            string = null;
         }
      }

      if (string != null) {
         return createErrorItem("Expected tag after name");
      } else {
         return !bl ? createErrorItem("Expected closing bracket") : createNbtItem(ModItems.COMPOUND_TAG, nbtCompound);
      }
   }

   private static ItemStack craftNumberFromBytes(byte[] bs) {
      ByteArrayDataInput byteArrayDataInput = ByteStreams.newDataInput(bs);

      return switch (bs.length) {
         case 0 -> createNbtItem(ModItems.BYTE_TAG, NbtByte.ZERO);
         case 1 -> createNbtItem(ModItems.BYTE_TAG, NbtByte.of(byteArrayDataInput.readByte()));
         case 2 -> createNbtItem(ModItems.SHORT_TAG, NbtShort.of(byteArrayDataInput.readShort()));
         case 3, 5, 6, 7 -> createErrorItem("Number of bytes (" + bs.length + ") is not power of 2");
         case 4 -> createNbtItem(ModItems.INT_TAG, NbtInt.of(byteArrayDataInput.readInt()));
         case 8 -> createNbtItem(ModItems.LONG_TAG, NbtLong.of(byteArrayDataInput.readLong()));
         default -> createErrorItem("Total number of bytes (" + bs.length + " exceeds 8");
      };
   }

   @Nullable
   private static AbstractNbtNumber getNbtNumber(ItemStack itemStack) {
      return itemStack.getItem() instanceof ByteItem lv && lv.method_50804(itemStack) instanceof AbstractNbtNumber abstractNbtNumber
         ? abstractNbtNumber
         : null;
   }

   @Nullable
   private static byte[] getBytesFromTags(List<ItemStack> list) {
      ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();

      for (ItemStack itemStack : list) {
         if (!(itemStack.getItem() instanceof ByteItem lv)) {
            return null;
         }

         NbtElement nbtElement = lv.method_50804(itemStack);
         if (nbtElement instanceof NbtByte nbtByte) {
            byteArrayDataOutput.writeByte(nbtByte.byteValue());
         } else if (nbtElement instanceof NbtShort nbtShort) {
            byteArrayDataOutput.writeShort(nbtShort.shortValue());
         } else if (nbtElement instanceof NbtInt nbtInt) {
            byteArrayDataOutput.writeInt(nbtInt.intValue());
         } else {
            if (!(nbtElement instanceof NbtLong nbtLong)) {
               return null;
            }

            byteArrayDataOutput.writeLong(nbtLong.longValue());
         }
      }

      return byteArrayDataOutput.toByteArray();
   }

   private static ItemStack craftConcatenation(List<ItemStack> list) {
      List<NbtElement> list2 = new ArrayList<>();

      for (ItemStack itemStack : list) {
         if (!(itemStack.getItem() instanceof ByteItem lv)) {
            return ItemStack.EMPTY;
         }

         list2.add(lv.method_50804(itemStack));
      }

      if (list2.size() < 2) {
         return ItemStack.EMPTY;
      } else {
         NbtElement nbtElement = list2.get(0);

         for (int i = 1; i < list2.size(); i++) {
            NbtElement nbtElement2 = list2.get(i);
            NbtElement nbtElement3 = concatenateNbt(nbtElement, nbtElement2);
            if (nbtElement3 == null) {
               return createErrorItem("Can't concatenate " + nbtElement.asString() + " with " + nbtElement2.asString());
            }

            nbtElement = nbtElement3;
         }

         if (nbtElement instanceof NbtCompound nbtCompound) {
            return createNbtItem(ModItems.COMPOUND_TAG, nbtCompound);
         } else if (nbtElement instanceof NbtList nbtList) {
            return createNbtItem(ModItems.LIST_TAG, nbtList);
         } else {
            return nbtElement instanceof NbtString nbtString ? createNbtItem(ModItems.STRING_TAG, nbtString) : ItemStack.EMPTY;
         }
      }
   }

   @Nullable
   private static NbtElement concatenateNbt(NbtElement nbtElement, NbtElement nbtElement2) {
      if (nbtElement instanceof NbtCompound nbtCompound && nbtElement2 instanceof NbtCompound nbtCompound2) {
         return nbtCompound.copy().copyFrom(nbtCompound2);
      } else if (nbtElement instanceof NbtList nbtList && nbtElement2 instanceof NbtList nbtList2) {
         NbtList nbtList3 = nbtList.copy();

         for (NbtElement nbtElement3 : nbtList2) {
            if (!nbtList3.addElement(nbtList3.size(), nbtElement3)) {
               return null;
            }
         }

         return nbtList3;
      } else {
         return nbtElement instanceof NbtString nbtString && nbtElement2 instanceof NbtString nbtString2
            ? NbtString.of(nbtString.asString().get() + nbtString2.asString().get())
            : null;
      }
   }

   @Override
   public boolean matches(CraftingRecipeInput input, World world) {
	return true;
   }

   @Override
   public ItemStack craft(CraftingRecipeInput input, WrapperLookup registries) {
	   // TODO reversed NBT_CRAFTING
       if (VoteRules.NBT_CRAFTING.isActive()) {
           return ItemStack.EMPTY;
       }
       
       List<ItemStack> stacks = new ArrayList<>();
       for (int i = 0; i < input.size(); i++) {
           ItemStack stack = input.getStackInSlot(i);
           if (!stack.isEmpty()) {
               stacks.add(stack);
           }
       }
       
       return getOutput(stacks);
   }

   @Override
   public RecipeSerializer<? extends SpecialCraftingRecipe> getSerializer() {
	   return ModItems.NBT_CRAFTING_RECIPE;
   }
}
