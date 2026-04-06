package net.zhengzhengyiyi.feature;

import com.mojang.serialization.Codec;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

/**
 * class_11085 - Feature Generator for Structures
 * Generates structure features in the world using NBT structure templates.
 */
public class StructureFeature extends Feature<StructureFeatureConfig> {
    public StructureFeature(Codec<StructureFeatureConfig> codec) {
        super(codec);
    }

    public StructureFeature() {
        this(StructureFeatureConfig.CODEC);
    }

    /** generate() - Generates structure features in world */
    @Override
    public boolean generate(FeatureContext<StructureFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();
        StructureFeatureConfig config = context.getConfig();

        StructureTemplateManager manager = world.toServerWorld().getServer().getStructureTemplateManager();
        StructureTemplate template = manager.getTemplateOrBlank(config.templateId());

        return method_69801(world, origin, template, config.rotation(), config.mirror());
    }

    /** method_69801 - Places structure template at position */
    public boolean method_69801(
        StructureWorldAccess world,
        BlockPos pos,
        StructureTemplate template,
        BlockRotation rotation,
        BlockMirror mirror
    ) {
        StructurePlacementData placementData = new StructurePlacementData()
            .setRotation(rotation)
            .setMirror(mirror)
            .setIgnoreEntities(false);

        template.place(world, pos, pos, placementData, world.toServerWorld().getRandom(), 2);
        return true;
    }
}
