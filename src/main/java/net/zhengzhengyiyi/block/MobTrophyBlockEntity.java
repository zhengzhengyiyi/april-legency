package net.zhengzhengyiyi.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.zhengzhengyiyi.component.MobTrophyComponent;
import net.zhengzhengyiyi.component.ModDataComponentTypes;
import org.jetbrains.annotations.Nullable;

public class MobTrophyBlockEntity extends BlockEntity {
   public MobTrophyBlockEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.MOB_TROPHY_BLOCK_ENTITY, pos, state);
   }

   @Nullable
   public BlockEntityUpdateS2CPacket toUpdatePacket() {
      return BlockEntityUpdateS2CPacket.create(this);
   }

   @Override
   public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
      return this.createNbt(registries);
   }

   @Nullable
   public MobTrophyComponent getMobTrophy() {
      return this.getComponents().get(ModDataComponentTypes.TYPE_MOB_TROPHY);
   }
}
