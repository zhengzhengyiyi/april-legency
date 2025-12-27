package net.zhengzhengyiyi.rules.options;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public enum CollisionStrategy implements StringIdentifiable {
    NONE("none"),
    BREAK("break"),
    EXPLODE("explode");

    public static final Codec<CollisionStrategy> CODEC = StringIdentifiable.createCodec(CollisionStrategy::values);
    private final String id;

    private CollisionStrategy(String id) {
        this.id = id;
    }

    @Override
    public String asString() {
        return this.id;
    }
}
