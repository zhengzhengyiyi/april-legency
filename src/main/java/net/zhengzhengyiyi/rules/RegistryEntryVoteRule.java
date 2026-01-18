package net.zhengzhengyiyi.rules;

import java.util.stream.Stream;

import com.mojang.serialization.Codec;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.zhengzhengyiyi.vote.VoteValue;
import net.zhengzhengyiyi.vote.VoterAction;
import net.zhengzhengyiyi.world.Vote;

public abstract class RegistryEntryVoteRule<T> implements Vote {
    public final RegistryKey<? extends Registry<T>> registryKey;
    final RegistryKey<T> defaultKey;
    RegistryKey<T> currentKey;
    private final Codec<VoteValue> optionCodec;

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public RegistryEntryVoteRule(RegistryKey<? extends Registry<T>> registryKey, RegistryKey<T> defaultKey) {
        this.registryKey = registryKey;
        this.defaultKey = defaultKey;
        this.currentKey = defaultKey;
        this.optionCodec = RegistryKey.createCodec(registryKey).xmap(
            key -> new RegistryEntryVoteRule.Option(key), 
            option -> ((RegistryEntryVoteRule.Option)option).key
        );
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public Codec getOptionCodec() {
        return this.optionCodec;
    }

    public RegistryKey<T> getCurrentKey() {
        return this.currentKey;
    }

    public RegistryKey<T> getDefaultKey() {
        return this.defaultKey;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Stream<VoteValue> getActiveOptions() {
        return this.currentKey.equals(this.defaultKey) ? Stream.empty() : Stream.of(new RegistryEntryVoteRule.Option(this.currentKey));
    }

//    @Override
//    public Stream generateOptions(MinecraftServer server, Random random, int limit) {
//        Registry<T> registry = server.getRegistryManager().get(this.registryKey);
//    	Registry<T> registryOptional = server.getRegistryManager().getOptional(this.registryKey).get();
//        return Stream.generate((Supplier<? extends T>) () -> registryOptional.getRandom(random))
////            .flatMap(Optional::stream)
////            .filter(entry -> !entry.matchesKey(this.defaultKey) && !entry.matchesKey(this.currentKey))
//            .limit(limit)
//            .map(entry -> new RegistryEntryVoteRule.Option(entry.registryKey()));
//    }

    protected abstract Text getOptionDescription(RegistryKey<T> key);

    class Option implements VoteValue {
        final RegistryKey<T> key;

        Option(RegistryKey<T> key) {
            this.key = key;
        }

//        @Override
//        public Vote getRule() {
//            return RegistryEntryVoteRule.this;
//        }

        @Override
        public void apply(VoterAction action) {
            RegistryEntryVoteRule.this.currentKey = switch (action) {
                case APPROVE -> this.key;
                case REPEAL -> RegistryEntryVoteRule.this.defaultKey;
			default -> null;
            };
        }

        @Override
        public Text getDescription(VoterAction action) {
            return RegistryEntryVoteRule.this.getOptionDescription(this.key);
        }
    }
}
