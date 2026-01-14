package net.zhengzhengyiyi.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LightningRodBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class LunarBaseFeature extends Feature<DefaultFeatureConfig> {
   public LunarBaseFeature(Codec<DefaultFeatureConfig> codec) {
      super(codec);
   }

   @Override
   public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
      StructureWorldAccess world = context.getWorld();
      BlockPos origin = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, context.getOrigin());
      
      this.setBlockState(world, origin, Blocks.IRON_TRAPDOOR.getDefaultState().with(TrapdoorBlock.HALF, BlockHalf.TOP));
      
      BlockState basalt = Blocks.POLISHED_BASALT.getDefaultState().with(PillarBlock.AXIS, Direction.Axis.X);
      BlockState chain = Blocks.IRON_CHAIN.getDefaultState().with(PillarBlock.AXIS, Direction.Axis.X);
      
      this.setBlockState(world, origin.north().east(), basalt);
      this.setBlockState(world, origin.north(), chain);
      this.setBlockState(world, origin.north().west(), basalt);
      this.setBlockState(world, origin.south().east(), basalt);
      this.setBlockState(world, origin.south(), chain);
      this.setBlockState(world, origin.south().west(), basalt);
      
      BlockPos dropperPos = origin.up();
      this.setBlockState(world, dropperPos, Blocks.DROPPER.getDefaultState());
      this.setBlockState(world, dropperPos.up(), Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE.getDefaultState());
      this.setBlockState(world, dropperPos.north(), Blocks.SMOOTH_QUARTZ_SLAB.getDefaultState());
      this.setBlockState(world, dropperPos.south(), Blocks.SMOOTH_QUARTZ_SLAB.getDefaultState());
      
      this.setBlockState(world, dropperPos.east(), Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, true));
      this.setBlockState(world, dropperPos.west(), Blocks.IRON_BARS.getDefaultState().with(PaneBlock.EAST, true));
      
      this.setBlockState(world, dropperPos.east().up(), Blocks.END_ROD.getDefaultState());
      this.setBlockState(world, dropperPos.west().up(), Blocks.LIGHTNING_ROD.getDefaultState().with(LightningRodBlock.FACING, Direction.DOWN));
      
      System.out.println(dropperPos);
      
//      if (world.getBlockEntity(dropperPos) instanceof DropperBlockEntity dropper) {
//         dropper.setSpecialRocketFlag(); 
//         dropper.setCustomName(Text.translatable("block.minecraft.dropper.lunar"));
//      }

      return true;
   }
}