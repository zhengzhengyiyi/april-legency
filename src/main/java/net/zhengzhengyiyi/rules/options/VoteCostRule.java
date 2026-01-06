package net.zhengzhengyiyi.rules.options;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;
import net.zhengzhengyiyi.vote.VoteCost;
import net.zhengzhengyiyi.world.Vote;
import net.zhengzhengyiyi.vote.VoteValue;
import net.zhengzhengyiyi.vote.VoterAction;
import org.jetbrains.annotations.Nullable;

public class VoteCostRule implements Vote {
    private static final List<VoteCost.Instance> DEFAULT_COSTS = List.of(
        new VoteCost.Instance(VoteCost.Constants.PER_PROPOSAL, 1), 
        new VoteCost.Instance(VoteCost.Constants.PER_OPTION, 1)
    );
    
    final List<VoteCost.Instance> defaultEntries = DEFAULT_COSTS;
    List<VoteCost.Instance> currentEntries = DEFAULT_COSTS;
    
    private final Codec<VoteValue> optionCodec = VoteCost.Instance.CODEC.listOf().xmap(
        list -> new VoteCostRule.Option(list), 
        option -> ((VoteCostRule.Option)option).entries
    );

    @Override
    public Codec getOptionCodec() {
        return this.optionCodec;
    }

    @Override
    public Stream getActiveOptions() {
        return this.currentEntries.equals(this.defaultEntries) ? Stream.empty() : Stream.of(new VoteCostRule.Option(this.currentEntries));
    }

    @Override
    public Stream generateOptions(MinecraftServer server, Random random, int limit) {
        List<List<VoteCost.Instance>> possibleCombinations = createCombinations(random, this.currentEntries);
        Util.shuffle(possibleCombinations, random);
        return possibleCombinations.stream().limit(limit).map(entries -> new VoteCostRule.Option(entries));
    }

    public static List<List<VoteCost.Instance>> createCombinations(Random random, List<VoteCost.Instance> current) {
        List<VoteCost.Instance> pool1 = sample(current, List.of(VoteCost.Type.PER_PROPOSAL), 0, random);
        List<VoteCost.Instance> pool2 = sample(current, List.of(VoteCost.Type.PER_OPTION), 1, random);
        List<VoteCost.Instance> pool3 = sample(current, List.of(VoteCost.Type.ITEM, VoteCost.Type.RESOURCE), 2, random);
        
        List<List<VoteCost.Instance>> results = new ObjectArrayList<>();
        populateCombinations(results, pool1, pool2, pool3, current);
        return results;
    }

    private static List<VoteCost.Instance> sample(@Nullable VoteCost.Instance existing, List<VoteCost.Type> types, Random random) {
        List<VoteCost.Instance> pool = new ArrayList<>();
        return pool;
    }

    private static List<VoteCost.Instance> sample(List<VoteCost.Instance> list, List<VoteCost.Type> types, int index, Random random) {
        VoteCost.Instance existing = index < list.size() ? list.get(index) : null;
        return sample(existing, types, random);
    }

    private static void populateCombinations(
        List<List<VoteCost.Instance>> results,
        List<VoteCost.Instance> pool1,
        List<VoteCost.Instance> pool2,
        List<VoteCost.Instance> pool3,
        List<VoteCost.Instance> current
    ) {
        ObjectArrayList<VoteCost.Instance> stack = new ObjectArrayList<>();
        for (VoteCost.Instance e1 : pool1) {
            if (e1 != null) stack.push(e1);
            for (VoteCost.Instance e2 : pool2) {
                if (!isSameMaterial(e1, e2)) {
                    if (e2 != null) stack.push(e2);
                    for (VoteCost.Instance e3 : pool3) {
                        if (!isSameMaterial(e1, e3) && !isSameMaterial(e2, e3)) {
                            if (e3 != null) stack.push(e3);
                            if (!stack.isEmpty() && !stack.equals(current)) {
                                results.add(List.copyOf(stack));
                            }
                            if (e3 != null) stack.pop();
                        }
                    }
                    if (e2 != null) stack.pop();
                }
            }
            if (e1 != null) stack.pop();
        }
    }

    private static boolean isSameMaterial(@Nullable VoteCost.Instance a, @Nullable VoteCost.Instance b) {
        return a != null && b != null ? a.cost().equals(b.cost()) : false;
    }

    public List<VoteCost.Instance> getCurrentCosts() {
        return this.currentEntries;
    }

    class Option implements VoteValue {
        private static final Text SEPARATOR = Text.literal(", ");
        final List<VoteCost.Instance> entries;
        public final Text description;

        Option(List<VoteCost.Instance> entries) {
            this.entries = entries;
            List<MutableText> entryTexts = entries.stream()
                .map(e -> Text.empty().append(String.valueOf(e.amount())).append("x ").append(e.cost().getDescription()))
                .toList();
            this.description = Text.translatable("rule.new_vote_cost", Texts.join(entryTexts, SEPARATOR));
        }

        @Override
        public void apply(VoterAction action) {
            VoteCostRule.this.currentEntries = switch (action) {
                case APPROVE -> this.entries;
                case REPEAL -> VoteCostRule.this.defaultEntries;
			default -> null;
            };
        }

		 

		@Override
		public Text getDescription(VoterAction action) {
			return null;
		}
    }
}