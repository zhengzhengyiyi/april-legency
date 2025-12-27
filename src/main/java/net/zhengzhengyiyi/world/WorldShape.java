package net.zhengzhengyiyi.world;

import com.mojang.serialization.Codec;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;

public enum WorldShape implements StringIdentifiable {
    ONE("1", "earth_1.png"),
    A("a", "earth_a.png"),
    PRIME("prime", "earth_prime.png"),
    DEFAULT("none", "earth.png");

    public static final Codec<WorldShape> CODEC = StringIdentifiable.createCodec(WorldShape::values);
    private final String id;
    private final Identifier texture;

    private WorldShape(String id, String textureName) {
        this.id = id;
        this.texture = Identifier.of("textures/environment/" + textureName);
    }

    @Override
    public String asString() {
        return this.id;
    }

    public Identifier getTexture() {
        return this.texture;
    }
}
