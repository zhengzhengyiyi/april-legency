package net.zhengzhengyiyi.vote;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;
import java.util.List;
import java.util.Random;

/**
 * Defines the strategy for selecting a single winner when multiple options have the same highest score.
 * <p>
 * This is crucial for maintaining deterministic outcomes in the 23w13a voting system.
 */
public enum TieBreaker implements StringIdentifiable {
    /**
     * Randomly picks one winner from the tied options.
     */
    RANDOM("random"),
    /**
     * Consistently picks the first option in the list.
     */
    FIRST("first"),
	
	LAST("last"),
	
	ALL("all"),
	
	NONE("none");

    public static final Codec<TieBreaker> CODEC = StringIdentifiable.createCodec(TieBreaker::values);
    private final String id;

    TieBreaker(String id) {
        this.id = id;
    }

    @Override
    public String asString() {
        return this.id;
    }

    /**
     * Resolves a tie based on the current strategy.
     *
     * @param tiedOptionIds A list of options that share the same maximum score.
     * @param random The random source (usually provided by the server world).
     * @return The selected {@link VoteOptionId} to be applied.
     */
    public VoteOptionId resolve(List<VoteOptionId> tiedOptionIds, Random random) {
        if (tiedOptionIds.isEmpty()) {
            return null;
        }
        if (tiedOptionIds.size() == 1) {
            return tiedOptionIds.get(0);
        }

        return switch (this) {
            case RANDOM -> tiedOptionIds.get(random.nextInt(tiedOptionIds.size()));
            case FIRST -> tiedOptionIds.get(0);
            case LAST -> tiedOptionIds.get(tiedOptionIds.size() - 1);
            case ALL -> tiedOptionIds.get(0);
            case NONE -> null;
        };
    }
}