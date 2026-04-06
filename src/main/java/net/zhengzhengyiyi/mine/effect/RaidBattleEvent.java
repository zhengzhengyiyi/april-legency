package net.zhengzhengyiyi.mine.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.zhengzhengyiyi.mine.class_11099;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * class_11103 - Raid Battle
 * Manages a raid-style battle event in a mine world.
 * Note: Superseded by WaveEvent in practice; kept for completeness.
 */
public class RaidBattleEvent implements class_11099 {
    public static final MapCodec<RaidBattleEvent> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(e -> e.id),
            BlockPos.CODEC.fieldOf("pos").forGetter(e -> e.pos),
            Codec.INT.fieldOf("wave").forGetter(e -> e.currentWave),
            Codec.INT.fieldOf("total_waves").forGetter(e -> e.totalWaves),
            Uuids.INT_STREAM_CODEC.listOf().fieldOf("raiders").forGetter(e -> e.raiderUuids),
            class_11099.Status.CODEC.fieldOf("status").forGetter(e -> e.status)
        ).apply(instance, RaidBattleEvent::new)
    );

    private final Identifier id;
    private final BlockPos pos;
    private int currentWave;
    private final int totalWaves;
    private final List<UUID> raiderUuids;
    private class_11099.Status status;

    public RaidBattleEvent(Identifier id, BlockPos pos, int currentWave, int totalWaves, List<UUID> raiderUuids, class_11099.Status status) {
        this.id = id;
        this.pos = pos;
        this.currentWave = currentWave;
        this.totalWaves = totalWaves;
        this.raiderUuids = new ArrayList<>(raiderUuids);
        this.status = status;
    }

    public RaidBattleEvent(Identifier id, BlockPos pos, int totalWaves) {
        this(id, pos, 0, totalWaves, List.of(), class_11099.Status.ACTIVE);
    }

    /** method_69842 - Initializes raid */
    @Override
    public void tick(ServerWorld world) {
        if (this.status != class_11099.Status.ACTIVE) return;

        // Check if current wave is cleared
        boolean waveCleared = this.raiderUuids.stream()
            .map(world::getEntity)
            .allMatch(e -> e == null || !e.isAlive());

        if (waveCleared) {
            this.currentWave++;
            if (this.currentWave >= this.totalWaves) {
                this.status = class_11099.Status.WON;
            } else {
                spawnWave(world);
            }
        }
    }

    private void spawnWave(ServerWorld world) {
        this.raiderUuids.clear();
        int count = 5 + this.currentWave * 2;
        for (int i = 0; i < count; i++) {
            BlockPos spawnPos = this.pos.add(
                world.getRandom().nextBetween(-20, 20), 0,
                world.getRandom().nextBetween(-20, 20)
            );
            var entity = EntityType.PILLAGER.spawn(world, spawnPos, SpawnReason.EVENT);
            if (entity instanceof PillagerEntity raider) {
                raider.setPersistent();
                this.raiderUuids.add(raider.getUuid());
            }
        }
    }

    /** method_69845 - Cleans up raid */
    @Override
    public void onRemoved(ServerWorld world, boolean force) {
        this.status = force ? class_11099.Status.FAILED : class_11099.Status.WON;
        // Kill remaining raiders
        this.raiderUuids.stream()
            .map(world::getEntity)
            .filter(e -> e != null && e.isAlive())
            .forEach(e -> e.kill(world));
    }

    /** method_69841 - Gets raid location */
    @Override
    public BlockPos getPos() {
        return this.pos;
    }

    /** method_69849 - Gets raid status */
    @Override
    public class_11099.Status getStatus() {
        return this.status;
    }

    /** method_69851 - Gets codec */
    @Override
    public MapCodec<? extends class_11099> getCodec() {
        return CODEC;
    }
}
