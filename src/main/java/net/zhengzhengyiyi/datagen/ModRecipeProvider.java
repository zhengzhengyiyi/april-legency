package net.zhengzhengyiyi.datagen;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.ComplexRecipeJsonBuilder;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.zhengzhengyiyi.NbtCraftingRecipe;
import net.zhengzhengyiyi.item.ModItems;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter) {
        return new RecipeGenerator(registryLookup, exporter) {
            @Override
            public void generate() {
//                ComplexRecipeJsonBuilder.create(ModItems.NBT_CRAFTING_RECIPE)
//                    .offerTo(exporter, Identifier.ofVanilla("nbt_crafting").toString());
            	ComplexRecipeJsonBuilder.create(category -> new NbtCraftingRecipe(category))
                	.offerTo(exporter, Identifier.of("zhengzhengyiyi", "nbt_crafting").toString());
            	
            	RegistryEntryLookup<Item> registryLookup = registries.getOrThrow(RegistryKeys.ITEM);
            	
	            ShapedRecipeJsonBuilder.create(registryLookup, RecipeCategory.REDSTONE, ModItems.LEFT_SQUARE)
	                .input('x', ModItems.BIT)
	                .pattern("xx")
	                .pattern("x ")
	                .pattern("xx")
//	                .method_51103(false)
	                .criterion("has_bit", conditionsFromItem(ModItems.BIT))
	                .offerTo(exporter);
	             ShapedRecipeJsonBuilder.create(registryLookup, RecipeCategory.REDSTONE, ModItems.RIGHT_SQUARE)
	                .input('x', ModItems.BIT)
	                .pattern("xx")
	                .pattern(" x")
	                .pattern("xx")
//	                .method_51103(false)
	                .criterion("has_bit", conditionsFromItem(ModItems.BIT))
	                .offerTo(exporter);
	             ShapedRecipeJsonBuilder.create(registryLookup, RecipeCategory.REDSTONE, ModItems.LEFT_CURLY)
	                .input('x', ModItems.BIT)
	                .pattern(" x")
	                .pattern("x ")
	                .pattern(" x")
//	                .method_51103(false)
	                .criterion("has_bit", conditionsFromItem(ModItems.BIT))
	                .offerTo(exporter);
	             ShapedRecipeJsonBuilder.create(registryLookup, RecipeCategory.REDSTONE, ModItems.RIGHT_CURLY)
	                .input('x', ModItems.BIT)
	                .pattern("x ")
	                .pattern(" x")
	                .pattern("x ")
//	                .method_51103(false)
	                .criterion("has_bit", conditionsFromItem(ModItems.BIT))
	                .offerTo(exporter);
	             
	             offerStonecuttingRecipe(RecipeCategory.REDSTONE, ModItems.NAME, Items.NAME_TAG, 16);
	             offerStonecuttingRecipe(RecipeCategory.REDSTONE, ModItems.TAG, Items.NAME_TAG, 16);
	             offerStonecuttingRecipe(RecipeCategory.REDSTONE, ModItems.BIT, ModItems.NAME, 16);
	             offerStonecuttingRecipe(RecipeCategory.REDSTONE, ModItems.BIT, ModItems.TAG, 16);
            }
        };
    }

    @Override
    public String getName() {
        return "ModRecipeProvider";
    }
}
