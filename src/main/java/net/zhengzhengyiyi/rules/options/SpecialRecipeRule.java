package net.zhengzhengyiyi.rules.options;

import com.mojang.serialization.Codec;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.zhengzhengyiyi.vote.VoteValue;
import net.zhengzhengyiyi.world.Vote;

public class SpecialRecipeRule extends SetVoteRule<Identifier> {
    private static final Set<Identifier> SPECIAL_RECIPES = Set.of(
        Identifier.of("wob"), 
        Identifier.of("m_banner_pattern"), 
        Identifier.of("string_concatenation"), 
        Identifier.of("diamond_drows")
    );

    @SuppressWarnings("unchecked")
	@Override
    public Stream<VoteValue> generateOptions(MinecraftServer server, Random random, int limit) {
        return SPECIAL_RECIPES.stream()
            .filter(id -> !this.contains(id))
            .limit(limit)
            .map(id -> new SetVoteRule.Option(id));
    }

    @Override
    protected Codec<Identifier> getElementCodec() {
        return Identifier.CODEC;
    }

    public boolean isRecipeUnlocked(Identifier id) {
        return this.contains(id) ? true : !SPECIAL_RECIPES.contains(id);
    }

    @Override
    protected Text getElementDescription(Identifier id) {
        return Text.translatable(id.toTranslationKey("rule.recipe"));
    }
}
