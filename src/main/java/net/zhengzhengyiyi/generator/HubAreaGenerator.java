package net.zhengzhengyiyi.generator;

import net.minecraft.block.Blocks;
import net.minecraft.util.BlockRotation;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/**
 * class_11083 - Hub/Central Area Generator
 * Places structures vertically in the world to form a hub or central area.
 */
public class HubAreaGenerator {
    private final ServerWorld world;
    private final BlockPos origin;

    public HubAreaGenerator(ServerWorld world, BlockPos origin) {
        this.world = world;
        this.origin = origin;
    }

    /** method_69799 - Places structures vertically in world */
    public void method_69799(Identifier[] templateIds) {
        BlockPos current = this.origin;
        for (Identifier id : templateIds) {
            StructureTemplateManager manager = this.world.getServer().getStructureTemplateManager();
            StructureTemplate template = manager.getTemplateOrBlank(id);
            method_69801(current, template, BlockRotation.NONE);
            // Stack next structure on top
            current = current.up(template.getSize().getY());
        }
    }

    /** method_69801 - Places structure template at position */
    public boolean method_69801(BlockPos pos, StructureTemplate template, BlockRotation rotation) {
        if (template == null) return false;
        StructurePlacementData data = new StructurePlacementData()
            .setRotation(rotation)
            .setIgnoreEntities(false);
        template.place(this.world, pos, pos, data, this.world.getRandom(), 2);
        return true;
    }

    /** Places a simple platform at the origin */
    public void placePlatform(int radius) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                this.world.setBlockState(this.origin.add(dx, -1, dz), Blocks.STONE.getDefaultState());
                this.world.setBlockState(this.origin.add(dx, 0, dz), Blocks.AIR.getDefaultState());
                this.world.setBlockState(this.origin.add(dx, 1, dz), Blocks.AIR.getDefaultState());
            }
        }
    }

    /** Places a pillar from origin downward to bedrock */
    public void placePillar(Direction.Axis axis) {
        BlockPos.Mutable mutable = this.origin.mutableCopy();
        while (mutable.getY() > this.world.getBottomY()) {
            this.world.setBlockState(mutable, Blocks.STONE.getDefaultState());
            mutable.move(Direction.DOWN);
        }
    }
}
