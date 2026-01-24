package net.zhengzhengyiyi.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class NeitherPortalBlock extends DimensionPortalBlock implements BlockEntityProvider {
   private static final Random RANDOM = Random.create();

   public NeitherPortalBlock(AbstractBlock.Settings settings) {
      super(settings);
   }

   @Environment(EnvType.CLIENT)
   @Override
   protected ParticleEffect getParticle(BlockState state, World world, BlockPos pos) {
      BlockEntity blockEntity = world.getBlockEntity(pos);
      if (blockEntity instanceof NeitherPortalEntity shinyEntity) {
         int color = shinyEntity.getDimensionId();
         float scale = 1.0F + (float)(color >> 16 & 0xFF) / 255.0F;
         return new DustParticleEffect(color, scale);
      } else {
         return super.getParticle(state, world, pos);
      }
   }

   @Nullable
   @Override
   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return new NeitherPortalEntity(pos, state, Math.abs(RANDOM.nextInt()));
   }
}
