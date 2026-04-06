package net.zhengzhengyiyi.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.FeatureConfig;

/**
 * class_11086 - Feature Configuration Record
 * Stores template identifiers and rotation settings for structure generation.
 */
public record StructureFeatureConfig(Identifier templateId, BlockRotation rotation, BlockMirror mirror) implements FeatureConfig {
    public static final Codec<StructureFeatureConfig> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Identifier.CODEC.fieldOf("template").forGetter(StructureFeatureConfig::templateId),
            BlockRotation.CODEC.optionalFieldOf("rotation", BlockRotation.NONE).forGetter(StructureFeatureConfig::rotation),
            BlockMirror.CODEC.optionalFieldOf("mirror", BlockMirror.NONE).forGetter(StructureFeatureConfig::mirror)
        ).apply(instance, StructureFeatureConfig::new)
    );

    public StructureFeatureConfig(Identifier templateId) {
        this(templateId, BlockRotation.NONE, BlockMirror.NONE);
    }
}
