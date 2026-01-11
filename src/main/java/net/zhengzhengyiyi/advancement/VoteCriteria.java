package net.zhengzhengyiyi.advancement;

import java.util.Map;
import com.google.common.collect.Maps;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class VoteCriteria {
    private static final Map<Identifier, Criterion<?>> VALUES = Maps.newHashMap();

    public static final TickCriterion VOTE = register("minecraft:voted", new TickCriterion());

    private static <T extends Criterion<?>> T register(String id, T object) {
        Identifier identifier = Identifier.of(id);
        if (VALUES.containsKey(identifier)) {
            throw new IllegalArgumentException("Duplicate criterion id " + identifier);
        }
        VALUES.put(identifier, object);
        return object;
    }

    public static Iterable<? extends Criterion<?>> getCriteria() {
        return VALUES.values();
    }
    
    public static void init() {
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends CriterionConditions> Criterion<T> getById(Identifier id) {
        return (Criterion<T>) VALUES.get(id);
    }
}
