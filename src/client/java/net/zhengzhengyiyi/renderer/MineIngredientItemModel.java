package net.zhengzhengyiyi.renderer;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HeldItemContext;
import net.minecraft.util.Identifier;
import net.zhengzhengyiyi.component.ModDataComponentTypes;
import net.zhengzhengyiyi.mine.class_11056;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Custom ItemModel for mine_ingredient items.
 * Reads WORLD_MODIFIERS, finds the first effect's itemModel identifier,
 * and delegates rendering to that model — so a "sheep" ingredient shows a sheep spawn egg.
 * Matches reference class_11163.
 */
@Environment(EnvType.CLIENT)
public class MineIngredientItemModel implements ItemModel {

   public static final MineIngredientItemModel INSTANCE = new MineIngredientItemModel();

   @Override
   public void update(
      ItemRenderState state,
      ItemStack stack,
      ItemModelManager resolver,
      ItemDisplayContext displayContext,
      @Nullable ClientWorld world,
      @Nullable HeldItemContext heldItemContext,
      int seed
   ) {
      class_11056 modifiers = stack.getOrDefault(ModDataComponentTypes.WORLD_MODIFIERS, class_11056.field_58859);
      Optional<Identifier> modelId = modifiers.effects().stream()
         .flatMap(effect -> Optional.ofNullable(effect.itemModel()).stream())
         .findFirst();

      if (modelId.isPresent()) {
         ItemModel delegate = MinecraftClient.getInstance().getBakedModelManager().getItemModel(modelId.get());
         if (delegate != null) {
            delegate.update(state, stack, resolver, displayContext, world, heldItemContext, seed);
         }
      }
      // If no itemModel, render nothing — the slot frame is still visible
   }

   @Environment(EnvType.CLIENT)
   public record Unbaked() implements ItemModel.Unbaked {
      public static final MapCodec<Unbaked> CODEC = MapCodec.unit(new Unbaked());
      public static final Identifier ID = Identifier.of("aprils-legacy", "mine_ingredient");

      @Override
      public MapCodec<Unbaked> getCodec() {
         return CODEC;
      }

      @Override
      public ItemModel bake(ItemModel.BakeContext context) {
         return INSTANCE;
      }

      @Override
      public void resolve(ResolvableModel.Resolver resolver) {
         // Dynamic — no static models to pre-resolve
      }
   }
}
