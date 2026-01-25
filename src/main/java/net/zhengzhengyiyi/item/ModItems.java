package net.zhengzhengyiyi.item;

import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.nbt.*;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.zhengzhengyiyi.NbtCraftingRecipe;
import net.zhengzhengyiyi.block.ModBlocks;
import net.zhengzhengyiyi.entity.ModEntities;

public class ModItems {
	public static void init() {
	}
	
	public static final Item PICKAXE_BLOCK_ITEM = register(ModBlocks.PICKAXE_BLOCK);
	public static final Item PLACE_BLOCK_ITEM = register(ModBlocks.PLACE_BLOCK);
	public static final Item CHEESE_ITEM = register(ModBlocks.CHEESE);
	
	public static final Item MOON_COW_SPAWN_EGG = registerSpawnEgg(ModEntities.MOON_COW);
	
	public static final Item TAG = register("tag", new Item(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", "tag")))));
	public static final Item STRING_TAG = register("string_tag", new ByteItem<>(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", "string_tag"))), NbtString.TYPE));
	public static final Item BYTE_TAG = register("byte_tag", new ByteItem<>(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", "byte_tag"))), NbtByte.TYPE));
	public static final Item SHORT_TAG = register("short_tag", new ByteItem<>(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", "short_tag"))), NbtShort.TYPE));
	public static final Item INT_TAG = register("int_tag", new ByteItem<>(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", "int_tag"))), NbtInt.TYPE));
	public static final Item LONG_TAG = register("long_tag", new ByteItem<>(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", "long_tag"))), NbtLong.TYPE));
	public static final Item FLOAT_TAG = register("float_tag", new ByteItem<>(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", "float_tag"))), NbtFloat.TYPE));
	public static final Item DOUBLE_TAG = register("double_tag", new ByteItem<>(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", "double_tag"))), NbtDouble.TYPE));
	public static final Item COMPOUND_TAG = register("compound_tag", new ByteItem<>(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", "compound_tag"))), NbtCompound.TYPE));
	public static final Item LIST_TAG = register("list_tag", new ByteItem<>(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", "list_tag"))), NbtList.TYPE));
	
	public static final Item LEFT_SQUARE = register("left_square", new Item(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", "left_square")))));
	public static final Item RIGHT_SQUARE = register("right_square", new Item(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", "right_square")))));
	public static final Item LEFT_CURLY = register("left_curly", new Item(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", "left_curly")))));
	public static final Item RIGHT_CURLY = register("right_curly", new Item(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", "right_curly")))));
	public static final Item SYNTAX_ERROR = register("syntax_error", new Item(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", "syntax_error")))));
	
	public static final Item BIT = register("bit", new Item(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", "bit")))));
	public static final Item LE_TRICOLORE = register("le_tricolore", new Item(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", "le_tricolore")))));
	public static final Item LA_BAGUETTE = register("la_baguette", new Item(new Item.Settings().sword(ToolMaterial.STONE, 3.0F, -2.4F).registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", "la_baguette")))));

	public static final Item NAME = register("name", new Name(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", "name")))));
	
	public static final Item STRING2 = register("string2", new Item(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", "string2")))));
	
	// INFINITY 20W14INFINITY ITEMS
	public static final Item CURSOR_ITEM = register(ModBlocks.CURSOR);
	public static final Item FINE_ITEM = register("fine_item", new Item(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.ofVanilla("fine_item")))));
	
	public static RecipeSerializer<NbtCraftingRecipe> NBT_CRAFTING_RECIPE = RecipeSerializer.register("nbt_crafting_recipe", new SpecialCraftingRecipe.SpecialRecipeSerializer<>(NbtCraftingRecipe::new));
	
	private static Item register(Block block) {
		Identifier id = Registries.BLOCK.getId(block);
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
        
	    return register(new BlockItem(block, new Item.Settings().registryKey(itemKey)));
	}

	private static Item register(BlockItem item) {
	      return register(item.getBlock(), item);
	}
	
	private static Item register(String id, Item item) {
		return register(Identifier.of("minecraft", id), item);
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
	
	public static Item registerSpawnEgg(EntityType<?> type) {
		return register(
			RegistryKey.of(RegistryKeys.ITEM, EntityType.getId(type).withSuffixedPath("_spawn_egg")), SpawnEggItem::new, new Item.Settings().spawnEgg(type)
		);
	}
	
	public static Item register(RegistryKey<Item> key, Function<Item.Settings, Item> factory, Item.Settings settings) {
		Item item = (Item)factory.apply(settings.registryKey(key));
		if (item instanceof BlockItem blockItem) {
			blockItem.appendBlocks(Item.BLOCK_ITEMS, item);
		}

		return Registry.register(Registries.ITEM, key, item);
	}
}
