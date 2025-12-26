package net.zhengzhengyiyi.vote;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.random.Random;

/**
 * Defines a type of rule that can be voted on.
 * It manages the creation and retrieval of specific VoteValues.
 * <p>
 * Official Name: bec
 * Intermediary Name: net.minecraft.class_8367
 */
public interface VoteRuleType<T extends VoteValue> {

    /**
     * Gets all potential values that can be generated or "repealed" for this rule.
     */
    default Stream<VoteValue> getPotentialValues() {
        return this.getActiveValues();
    }

    /**
     * Removes or resets values associated with this rule.
     * Used when cleaning up or overriding rules.
     * * @param force If true, uses getActiveValues; otherwise uses getPotentialValues.
     * @return The number of values affected.
     */
    default int clear(boolean force) {
        List<VoteValue> values = (force ? this.getActiveValues() : this.getPotentialValues()).toList();
        // bed.b refers to VoteAction.REPEAL
        values.forEach(value -> value.apply(VoterAction.REPEAL));
        return values.size();
    }

    /**
     * Helper to wrap a specific codec into a VoteValue codec.
     */
    @SuppressWarnings("unchecked")
	static <T extends VoteValue> Codec<VoteValue> codec(Codec<T> codec) {
        return (Codec<VoteValue>) codec;
    }

    /**
     * Returns the codec used to serialize values of this rule type.
     */
    Codec<VoteValue> getCodec();

    /**
     * Gets a stream of currently active values for this rule in the world.
     */
    Stream<VoteValue> getActiveValues();

    /**
     * Generates a stream of new, random VoteValues for a new vote proposal.
     * * @param server The Minecraft server.
     * @param random The random source (apj refers to net.minecraft.util.math.random.Random).
     * @param count  The number of options requested.
     * @return A stream of generated values.
     */
    Stream<VoteValue> generate(MinecraftServer server, Random random, int count);
}