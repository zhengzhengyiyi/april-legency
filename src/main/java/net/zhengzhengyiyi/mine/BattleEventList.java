package net.zhengzhengyiyi.mine;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * class_11101 - Battle Event List
 * Stores and manages a list of active battle events in a mine world.
 */
public class BattleEventList {
    public static final Codec<BattleEventList> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            class_11099.CODEC.listOf().fieldOf("events").forGetter(l -> l.events)
        ).apply(instance, BattleEventList::new)
    );

    private final List<class_11099> events;

    public BattleEventList(List<class_11099> events) {
        this.events = new ArrayList<>(events);
    }

    public BattleEventList() {
        this(List.of());
    }

    /** Adds a battle event */
    public void add(class_11099 event) {
        this.events.add(event);
    }

    /** Removes a battle event */
    public void remove(class_11099 event) {
        this.events.remove(event);
    }

    /** Ticks all active events */
    public void tick(ServerWorld world) {
        List<class_11099> toRemove = new ArrayList<>();
        for (class_11099 event : this.events) {
            event.tick(world);
            if (event.getStatus() != class_11099.Status.ACTIVE) {
                toRemove.add(event);
            }
        }
        for (class_11099 event : toRemove) {
            event.onRemoved(world, false);
            this.events.remove(event);
        }
    }

    /** Returns the first active event near a position */
    public Optional<class_11099> getNearest(BlockPos pos) {
        return this.events.stream()
            .filter(e -> e.getStatus() == class_11099.Status.ACTIVE)
            .min((a, b) -> Double.compare(
                a.getPos().getSquaredDistance(pos),
                b.getPos().getSquaredDistance(pos)
            ));
    }

    /** Returns all events */
    public List<class_11099> getEvents() {
        return List.copyOf(this.events);
    }

    /** Returns true if any event is active */
    public boolean hasActiveEvent() {
        return this.events.stream().anyMatch(e -> e.getStatus() == class_11099.Status.ACTIVE);
    }

    /** Returns true if all events are won */
    public boolean allWon() {
        return !this.events.isEmpty()
            && this.events.stream().allMatch(e -> e.getStatus() == class_11099.Status.WON);
    }

    /** Clears all events */
    public void clear(ServerWorld world) {
        this.events.forEach(e -> e.onRemoved(world, true));
        this.events.clear();
    }
}
