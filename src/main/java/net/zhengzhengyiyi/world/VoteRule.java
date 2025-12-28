package net.zhengzhengyiyi.world;

import java.util.Optional;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.zhengzhengyiyi.vote.VoteValue;
import net.zhengzhengyiyi.vote.VoterAction;

/**
 * The base class for a voting rule in the April Fools 2023 "The Vote Update".
 * Handles the logic for default values, current values, and generating new vote choices.
 * * @param <T> The type of the value this rule governs (e.g., Block, EntityType, etc.)
 * <p>Official Obfuscated Name: beb
 */
public abstract class VoteRule<T> implements Vote {
    /** The registry key identifying the registry this rule belongs to. */
    private final RegistryKey<? extends Registry<T>> registryKey;
    /** The original/default value for this rule. */
    final RegistryKey<T> defaultValue;
    /** The current value selected by the vote. */
    RegistryKey<T> currentValue;
    /** Codec for serializing and deserializing vote options. */
//    private final Codec<VoteValue> optionCodec;

    public VoteRule(RegistryKey<? extends Registry<T>> registryKey, RegistryKey<T> defaultValue) {
        this.registryKey = registryKey;
        this.defaultValue = defaultValue;
        this.currentValue = defaultValue;
        
        // Maps the registry key codec to create VoteOption instances
//        this.optionCodec = Vote.createCodec(RegistryKey.createCodec(registryKey)
//            .xmap(key -> new Option(key), opt -> opt.value));
    }

//    @SuppressWarnings("unchecked")
//	@Override
//    public Codec getOptionCodec() {
//        return this.optionCodec;
//    }

    public RegistryKey<T> getCurrentValue() {
        return this.currentValue;
    }

    public RegistryKey<T> getDefaultValue() {
        return this.defaultValue;
    }

    /**
     * Returns the current active vote choice if it differs from the default.
     */
    @Override
    public Stream getActiveOptions() {
        return this.currentValue.equals(this.defaultValue) 
            ? Stream.empty() 
            : Stream.of(new Option(this.currentValue));
    }

    /**
     * Generates a stream of random vote options from the registry.
     * * @param server The server instance to access registries.
     * @param random The random source for selection.
     * @param limit The number of options to generate.
     */
    @Override
    public Stream generateOptions(MinecraftServer server, Random random, int limit) {
        Registry<T> registry = server.getRegistryManager().getOrThrow(this.registryKey);
        return Stream.generate(() -> registry.getRandom(random))
            .flatMap(Optional::stream)
            .filter(entry -> !entry.matchesKey(this.defaultValue) && !entry.matchesKey(this.currentValue))
            .limit(limit)
            .map(entry -> new Option(entry.registryKey()));
    }
    
    //TODO: fix the above method

    /**
     * Abstract method implemented by child classes (like BlockVoteRule) to define
     * how the rule text is displayed.
     */
    public abstract Text getDisplayText(RegistryKey<T> value);
    
    public static final Codec<VoteRule> CODEC = Identifier.CODEC.xmap(
    	    id -> {
    	        throw new UnsupportedOperationException("Abstract class cannot be instantiated directly");
    	    },
    	    rule -> {
    	        return Identifier.of("your_mod", "dummy"); 
    	    }
    	);

    /**
     * Inner class representing a specific choice in the vote.
     */
    private class Option implements VoteValue {
        final RegistryKey<T> value;

        Option(RegistryKey<T> value) {
            this.value = value;
        }

//        @Override
//        public Vote getParentVote() {
//            return VoteRule.this;
//        }

        @Override
        public void apply(VoterAction action) {
            // Logic for applying the vote result
            // The original snippet had a broken switch; usually, it updates the currentValue
            VoteRule.this.currentValue = this.value;
        }

        @Override
        public Text getDescription(VoterAction action) {
            return VoteRule.this.getDisplayText(this.value);
        }

		@Override
		public Vote getType() {
			return null;
		}
    }
}
