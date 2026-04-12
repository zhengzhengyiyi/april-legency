package net.zhengzhengyiyi.mine;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.raid.Raid;
import net.minecraft.village.raid.RaidManager;

/**
 * Mirrors craftmine's class_11103 — the Raid battle event.
 * Wraps a vanilla Raid and tracks its win/fail status as a class_11099 event.
 */
public class class_11103 implements class_11099 {
    public static final MapCodec<class_11103> field_59106 = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            BlockPos.CODEC.fieldOf("pos").forGetter(e -> e.pos),
            class_11099.Status.CODEC.fieldOf("win_status").forGetter(e -> e.status)
        ).apply(instance, class_11103::new)
    );

    private BlockPos pos;
    private class_11099.Status status;

    public class_11103(BlockPos pos, class_11099.Status status) {
        this.pos = pos;
        this.status = status;
    }

    public class_11103() {
        this(BlockPos.ORIGIN, class_11099.Status.ACTIVE);
    }

    /** Mirrors method_69842 — ticks the raid, updates status from vanilla RaidManager */
    @Override
    public void tick(ServerWorld world) {
        RaidManager raidManager = world.getRaidManager();
        Raid raid = raidManager.getRaidAt(this.pos, 100000);
        if (raid != null) {
            this.pos = raid.getCenter();
            if (raid.isFinished()) {
                this.status = raid.hasWon() ? class_11099.Status.WON : class_11099.Status.FAILED;
            }
        }
    }

    /** Mirrors method_69845 — no cleanup needed for raids */
    @Override
    public void onRemoved(ServerWorld world, boolean force) {
    }

    /** Mirrors method_69841 */
    @Override
    public BlockPos getPos() {
        return this.pos;
    }

    /** Mirrors method_69849 */
    @Override
    public class_11099.Status getStatus() {
        return this.status;
    }

    /** Mirrors method_69851 */
    @Override
    public MapCodec<? extends class_11099> getCodec() {
        return field_59106;
    }
}
