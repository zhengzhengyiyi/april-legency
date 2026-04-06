package net.zhengzhengyiyi.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.EntityRendererFactories;
import net.zhengzhengyiyi.block.ModBlocks;
import net.zhengzhengyiyi.entity.ModEntities;
import net.zhengzhengyiyi.renderer.pet.*;

@Environment(EnvType.CLIENT)
public class ModEntityRenderers {

    public static void register() {
        // Existing
        EntityRendererFactories.register(ModEntities.MOON_COW, MoonCowEntityRenderer::new);

        // Angry Ghast
        EntityRendererFactories.register(ModEntities.ANGRY_GHAST, AngryGhastRenderer::new);

        // Pet entities
        EntityRendererFactories.register(ModEntities.PET_ARMADILLO, PetArmadilloRenderer::new);
        EntityRendererFactories.register(ModEntities.PET_AXOLOTL, PetAxolotlRenderer::new);
        EntityRendererFactories.register(ModEntities.PET_BEE, PetBeeRenderer::new);
        EntityRendererFactories.register(ModEntities.PET_CAT, PetCatRenderer::new);
        EntityRendererFactories.register(ModEntities.PET_CHICKEN, PetChickenRenderer::new);
        EntityRendererFactories.register(ModEntities.PET_COW, PetCowRenderer::new);
        EntityRendererFactories.register(ModEntities.PET_CREEPER, PetCreeperRenderer::new);
        EntityRendererFactories.register(ModEntities.PET_FOX, PetFoxRenderer::new);
        EntityRendererFactories.register(ModEntities.PET_FROG, PetFrogRenderer::new);
        EntityRendererFactories.register(ModEntities.PET_MOOSHROOM, PetMooshroomRenderer::new);
        EntityRendererFactories.register(ModEntities.PET_POLAR_BEAR, PetPolarBearRenderer::new);
        EntityRendererFactories.register(ModEntities.PET_SLIME, PetSlimeRenderer::new);
        EntityRendererFactories.register(ModEntities.PET_TURTLE, PetTurtleRenderer::new);
        EntityRendererFactories.register(ModEntities.PET_WOLF, PetWolfRenderer::new);

        // Block entity renderers
        BlockEntityRendererFactories.register(ModBlocks.TRAVELLING_BLOCK_ENTITY, TravellingBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlocks.MOB_TROPHY_BLOCK_ENTITY, MobTrophyBlockEntityRenderer::new);
    }
}
