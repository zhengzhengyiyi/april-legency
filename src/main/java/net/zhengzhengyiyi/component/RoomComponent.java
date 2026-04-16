package net.zhengzhengyiyi.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

/**
 * Data component stored on a Shimmering Key that identifies which hub room
 * structure it unlocks. Mirrors craftmine class_11055.
 *
 * The id is stored as a short vanilla identifier (e.g. minecraft:barrels).
 * getFullId() prefixes it with "hub/room/" for structure template lookup
 * (e.g. minecraft:hub/room/barrels).
 */
public record RoomComponent(Identifier id) {

    public static final Codec<RoomComponent> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Identifier.CODEC.fieldOf("structure").forGetter(RoomComponent::id)
        ).apply(instance, RoomComponent::new)
    );

    public static final PacketCodec<RegistryByteBuf, RoomComponent> PACKET_CODEC =
        PacketCodec.tuple(Identifier.PACKET_CODEC, RoomComponent::id, RoomComponent::new);

    public static final String ROOM_PATH_PREFIX = "hub/room/";

    /** Returns the full structure identifier with the hub/room/ prefix. Mirrors class_11055.method_69586(). */
    public Identifier getFullId() {
        return toFullId(this.id);
    }

    /** Adds the hub/room/ prefix. Mirrors class_11055.method_69590(). */
    public static Identifier toFullId(Identifier shortId) {
        return shortId.withPrefixedPath(ROOM_PATH_PREFIX);
    }

    /** Strips the hub/room/ prefix. Mirrors class_11055.method_69587(). */
    public static Identifier toShortId(Identifier fullId) {
        String path = fullId.getPath();
        if (path.startsWith(ROOM_PATH_PREFIX)) {
            return fullId.withPath(path.substring(ROOM_PATH_PREFIX.length()));
        }
        return fullId;
    }
}
