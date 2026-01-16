package net.zhengzhengyiyi.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.zhengzhengyiyi.block.ModBlocks;

public class ModItems {
	public static void init() {
	}
	
	public static final Item PICKAXE_BLOCK_ITEM = register(ModBlocks.PICKAXE_BLOCK);
	public static final Item PLACE_BLOCK_ITEM = register(ModBlocks.PLACE_BLOCK);
	
	private static Item register(Block block) {
		Identifier id = Registries.BLOCK.getId(block);
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
        
	    return register(new BlockItem(block, new Item.Settings().registryKey(itemKey)));
	}

	private static Item register(BlockItem item) {
	      return register(item.getBlock(), item);
	}

	protected static Item register(Block block, Item item) {
	   return register(Registries.BLOCK.getId(block), item);
	}

	private static Item register(Identifier id, Item item) {
	   if (item instanceof BlockItem) {
	     ((BlockItem)item).appendBlocks(Item.BLOCK_ITEMS, item);
	   }

	   return Registry.register(Registries.ITEM, id, item);
	}
}
