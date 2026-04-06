package net.zhengzhengyiyi.mixin.client;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.ItemModelTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(ItemModelTypes.class)
public interface ItemModelTypesAccessorMixin {
   @Accessor("ID_MAPPER")
   static Codecs.IdMapper<Identifier, MapCodec<? extends ItemModel.Unbaked>> getIdMapper() {
      throw new AssertionError();
   }
}
