package net.zhengzhengyiyi.mine;

import net.minecraft.server.network.ServerPlayerEntity;
import net.zhengzhengyiyi.accessor.MineServerWorldAccessor;

/**
 * class_11068 - Unlock Requirement
 * Tests whether a player meets the requirements to unlock something.
 */
@FunctionalInterface
public interface UnlockRequirement {
    /** test() - Checks if player meets requirements */
    boolean test(ServerPlayerEntity player);

    /** Returns a requirement that checks if the player has a specific mine effect unlocked */
    static UnlockRequirement hasEffect(MineEffect effect) {
        return player -> ((MineServerWorldAccessor)(Object)player.getEntityWorld()).hasMineEffect(effect);
    }

    /** Returns a requirement that checks if the player has completed a mine */
    static UnlockRequirement hasCompletedMine() {
        return player -> ((MineServerWorldAccessor)(Object)player.getEntityWorld()).isMineCompleted();
    }

    /** Returns a requirement that always passes */
    static UnlockRequirement always() {
        return player -> true;
    }

    /** Returns a requirement that always fails */
    static UnlockRequirement never() {
        return player -> false;
    }

    /** Combines two requirements with AND logic */
    default UnlockRequirement and(UnlockRequirement other) {
        return player -> this.test(player) && other.test(player);
    }

    /** Combines two requirements with OR logic */
    default UnlockRequirement or(UnlockRequirement other) {
        return player -> this.test(player) || other.test(player);
    }
}
