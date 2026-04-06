package net.zhengzhengyiyi.mine.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.Codec;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.zhengzhengyiyi.mine.class_11099;

/**
 * class_11098 - End Dragon Battle
 * Manages the Ender Dragon boss fight as a mine event.
 */
public class EndDragonBattleEvent implements class_11099 {
    public static final MapCodec<EndDragonBattleEvent> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            Codec.BOOL.fieldOf("completed").forGetter(e -> e.completed),
            BlockPos.CODEC.fieldOf("pos").forGetter(e -> e.portalPos)
        ).apply(instance, EndDragonBattleEvent::new)
    );

    private boolean completed;
    private BlockPos portalPos;

    public EndDragonBattleEvent(boolean completed, BlockPos portalPos) {
        this.completed = completed;
        this.portalPos = portalPos;
    }

    public EndDragonBattleEvent() {
        this(false, BlockPos.ORIGIN);
    }

    /** method_69890 - Marks as completed */
    public void method_69890() {
        this.completed = true;
    }

    /** method_69842 - Initializes battle */
    @Override
    public void tick(ServerWorld world) {
        // Dragon fight initialization handled by vanilla EnderDragonFight
    }

    /** method_69845 - Cleans up battle */
    @Override
    public void onRemoved(ServerWorld world, boolean force) {
        this.completed = !force;
    }

    /** method_69841 - Gets portal location */
    @Override
    public BlockPos getPos() {
        return this.portalPos;
    }

    /** method_69849 - Gets battle status */
    @Override
    public class_11099.Status getStatus() {
        return this.completed ? class_11099.Status.WON : class_11099.Status.ACTIVE;
    }

    /** method_69851 - Gets codec */
    @Override
    public MapCodec<? extends class_11099> getCodec() {
        return CODEC;
    }
}


