package net.zhengzhengyiyi.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtType;
import org.jetbrains.annotations.Nullable;

public class ByteItem<T extends NbtElement> extends Item {
   public static final String KEY = "value";
   private final NbtType<T> nbtType;

   public ByteItem(Item.Settings settings, NbtType<T> nbtType) {
      super(settings);
      this.nbtType = nbtType;
   }

   public NbtType<T> getNbtType() {
      return this.nbtType;
   }

   @SuppressWarnings("unchecked")
   @Nullable
   public T method_50804(ItemStack itemStack) {
//      NbtCompound nbtCompound = itemStack.getNbt();
	  NbtCompound nbtCompound = itemStack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
      if (nbtCompound == null) {
         return null;
      } else if (this.nbtType != NbtCompound.TYPE) {
         NbtElement nbtElement = nbtCompound.get("value");
         return (T)(nbtElement != null && nbtElement.getNbtType() == this.nbtType ? nbtElement : null);
      } else {
         return (T)nbtCompound;
      }
   }

   public void applyNbt(ItemStack itemStack, NbtElement nbtElement) {
	    NbtCompound customData = itemStack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();

	    if (nbtElement instanceof NbtCompound nbtCompound2) {
	        itemStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbtCompound2));
	    } else {
	        customData.put("value", nbtElement);
	        itemStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(customData));
	    }
	}
}
