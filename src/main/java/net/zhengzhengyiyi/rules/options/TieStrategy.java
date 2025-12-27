package net.zhengzhengyiyi.rules.options;

import com.mojang.serialization.Codec;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;

public enum TieStrategy implements StringIdentifiable {
    PICK_LOW("pick_low"),
    PICK_HIGH("pick_high"),
    PICK_RANDOM("pick_random"),
    PICK_ALL("pick_all"),
    PICK_NONE("pick_none"),
    FAIL("fail");

    public static final Codec<TieStrategy> CODEC = StringIdentifiable.createCodec(TieStrategy::values);
    
    private final String id;
    private final Text description;

    private TieStrategy(String id) {
        this.id = id;
        this.description = Text.translatable("rule.tie_strategy." + id);
    }

    @Override
    public String asString() {
        return this.id;
    }

    public Text getDescription() {
        return this.description;
    }
}
