package net.zhengzhengyiyi.advancement;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.TickCriterion;

public class VoteCriteria {

    public static final TickCriterion VOTE = Criteria.register("zhengzhengyiyi:voted", new TickCriterion());
    
    public static void init() {
    }
}
