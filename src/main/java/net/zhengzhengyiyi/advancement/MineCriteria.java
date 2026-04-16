package net.zhengzhengyiyi.advancement;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.TickCriterion;

/**
 * Custom advancement criteria for April Fools mine advancements.
 * Each criterion is a simple TickCriterion triggered imperatively at the right moment.
 */
public class MineCriteria {

    /** Triggered when a player enters a mine dimension for the first time. */
    public static final TickCriterion ENTERED_MINE =
        Criteria.register("aprils-legacy:entered_mine", new TickCriterion());

    /** Triggered when a player wins (completes) a mine. */
    public static final TickCriterion MINE_WON =
        Criteria.register("aprils-legacy:mine_won", new TickCriterion());

    /** Triggered when a player loses a mine. */
    public static final TickCriterion MINE_LOST =
        Criteria.register("aprils-legacy:mine_lost", new TickCriterion());

    /** Triggered when a player picks up a shimmering key. */
    public static final TickCriterion GOT_SHIMMERING_KEY =
        Criteria.register("aprils-legacy:got_shimmering_key", new TickCriterion());

    public static void init() {
        // static initialiser — calling this class loads the fields
    }
}
