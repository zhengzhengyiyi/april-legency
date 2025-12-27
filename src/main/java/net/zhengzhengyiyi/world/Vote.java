package net.zhengzhengyiyi.world;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.random.Random;
import net.zhengzhengyiyi.vote.VoteValue;
import net.zhengzhengyiyi.vote.VoterAction;

/**
 * Defines a type of rule that can be voted on.
 * It manages the creation and retrieval of specific VoteValues.
 * <p>
 * Official Name: bec
 * Intermediary Name: net.minecraft.class_8367
 */
public interface Vote {
    /**
     * Gets a stream of all currently relevant options for this vote.
     * * @return A stream of VoteOption.
     */
    default Stream<?> getRelevantOptions() {
        return this.getActiveOptions();
    }

    /**
     * Forcibly applies a result to the vote options.
     * Used by the system to auto-complete or clear votes.
     * * @param useActiveOnly If true, only active options are processed.
     * @return The number of options processed.
     */
    default int applyDefault(boolean useActiveOnly) {
        List<?> options = (useActiveOnly ? this.getActiveOptions() : this.getRelevantOptions()).toList();
        // bed.b maps to VoteResult.FAIL or DISCARD logic
//        options.forEach(option -> option.apply(VoterAction.APPROVE));
        return options.size();
    }

    /**
     * Helper to wrap a specific codec into a generic VoteOption codec.
     */
    static Codec<?> createCodec(Codec<?> codec) {
        return codec;
    }

    /**
     * Gets the codec used for serializing this vote's options.
     */
    Codec<VoteValue> getOptionCodec();

    /**
     * Gets the current active options for this vote (options currently winning or selected).
     */
    Stream<Vote> getActiveOptions();

    /**
     * Generates a new set of random options for this vote.
     * * @param server The server instance.
     * @param random The random source (mapped from apj).
     * @param limit The maximum number of options to generate.
     * @return A stream of newly generated VoteOptions.
     */
    Stream<VoteValue> generateOptions(MinecraftServer server, Random random, int limit);
}