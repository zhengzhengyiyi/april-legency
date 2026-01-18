package net.zhengzhengyiyi.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class Name extends Item {
   public static final String NAME = "name";

   public Name(Item.Settings settings) {
      super(settings);
   }

   public static String method_50802(ItemStack itemStack) {
      NbtComponent component = itemStack.get(DataComponentTypes.CUSTOM_DATA);
      if (component != null) {
         NbtCompound nbt = component.copyNbt();
         if (nbt.contains(NAME)) {
            return nbt.getString(NAME).get();
         }
      }
      return null;
   }

   public static void method_50801(ItemStack itemStack, String string) {
      NbtCompound nbt = itemStack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
      nbt.putString(NAME, string);
      itemStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
   }
}
