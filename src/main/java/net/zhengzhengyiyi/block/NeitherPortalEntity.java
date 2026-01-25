package net.zhengzhengyiyi.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

public class NeitherPortalEntity extends BlockEntity {
    private int dimensionId;

    public NeitherPortalEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.NEITHER_PORTAL_ENTITY, pos, state);
    }

    public NeitherPortalEntity(BlockPos pos, BlockState state, int dimensionId) {
        this(pos, state);
        this.dimensionId = dimensionId;
    }

    @Override
    protected void writeData(WriteView data) {
        super.writeData(data);
        data.putInt("Dimension", this.dimensionId);
    }

    @Override
    protected void readData(ReadView data) {
        super.readData(data);
        this.dimensionId = data.getInt("Dimension", 0);
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return createNbt(registries);
    }

    public int getDimensionId() {
        return this.dimensionId;
    }

    public void setDimensionId(int dimensionId) {
        this.dimensionId = dimensionId;
        this.markDirty();
    }
}
