package net.zhengzhengyiyi.rules.options;

import com.mojang.serialization.Codec;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;

public enum CapesState implements StringIdentifiable {
    NONE("blonk"),
    AWESOM("awesom"),
    SQUID("squid"),
    VETERINARIAN("veterinarian"),
    NO_CIRCLE("no_circle"),
    NYAN("nyan");

    public static final Codec<CapesState> CODEC = StringIdentifiable.createCodec(CapesState::values);
    private final String id;
    private final Text displayName;
    private final Identifier texture;

    private CapesState(String id) {
        this.id = id;
        this.displayName = Text.translatable("rule.caep." + id);
        this.texture = Identifier.of("textures/entity/player/caeps/" + id + ".png");
    }

    @Override
    public String asString() {
        return this.id;
    }

    public Text getDisplayName() {
        return this.displayName;
    }

    public Identifier getTexture() {
        return this.texture;
    }
}
