package net.zhengzhengyiyi.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.zhengzhengyiyi.block.ModBlocks;

/**
 * Mirrors craftmine's MiscConfiguredFeatures.field_59596 ("mine_start").
 * Places the mine spawn platform: 3x3 stone floor, MineCrafter at center,
 * ShimmeringDoor one block south.
 *
 * Called by ServerWorldMixin.method_69093 on first entry, exactly like
 * craftmine calls MiscConfiguredFeatures.field_59596.generate().
 */
public class MineStartFeature extends Feature<DefaultFeatureConfig> {

    public MineStartFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        // origin is passed as mutable.down() from method_69093, so the floor is at origin+1
        BlockPos floor = context.getOrigin().up();

        // 3x3 stone floor
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                world.setBlockState(floor.add(dx, 0, dz), Blocks.STONE.getDefaultState(), 3);
            }
        }

        // Clear 2 blocks of air above
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                world.setBlockState(floor.add(dx, 1, dz), Blocks.AIR.getDefaultState(), 3);
                world.setBlockState(floor.add(dx, 2, dz), Blocks.AIR.getDefaultState(), 3);
            }
        }

        // MineCrafter at center
        world.setBlockState(floor.up(), ModBlocks.MINE_CRAFTER.getDefaultState(), 3);

        // ShimmeringDoor one block south, facing north
        BlockPos doorBase = floor.add(0, 1, 1);
        BlockState doorLower = ModBlocks.SHIMMERING_DOOR.getDefaultState()
            .with(net.minecraft.block.DoorBlock.FACING, Direction.NORTH)
            .with(net.minecraft.block.DoorBlock.HALF, net.minecraft.block.enums.DoubleBlockHalf.LOWER)
            .with(net.minecraft.block.DoorBlock.HINGE, net.minecraft.block.enums.DoorHinge.LEFT)
            .with(net.minecraft.block.DoorBlock.OPEN, false);
        world.setBlockState(doorBase, doorLower, 3);
        world.setBlockState(doorBase.up(),
            doorLower.with(net.minecraft.block.DoorBlock.HALF, net.minecraft.block.enums.DoubleBlockHalf.UPPER), 3);

        return true;
    }
}
