package net.zhengzhengyiyi.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactories;
//import net.minecraft.client.render.entity.EntityRendererFactories;
import net.zhengzhengyiyi.entity.ModEntities;

@Environment(EnvType.CLIENT)
public class ModEntityRenderers {

    public static void register() {
    	EntityRendererFactories.register(ModEntities.MOON_COW, MoonCowEntityRenderer::new);
    }
}
