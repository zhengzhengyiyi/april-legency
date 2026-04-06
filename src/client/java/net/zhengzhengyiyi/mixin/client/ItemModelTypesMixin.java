package net.zhengzhengyiyi.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.model.ItemModelTypes;
import net.zhengzhengyiyi.renderer.MineIngredientItemModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ItemModelTypes.class)
public class ItemModelTypesMixin {

   @Inject(method = "bootstrap", at = @At("TAIL"))
   private static void registerMineIngredientModel(CallbackInfo ci) {
      ItemModelTypesAccessorMixin.getIdMapper().put(
         MineIngredientItemModel.Unbaked.ID,
         MineIngredientItemModel.Unbaked.CODEC
      );
   }
}
