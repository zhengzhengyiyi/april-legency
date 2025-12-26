package net.zhengzhengyiyi.vote;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Uuids;
import java.util.Comparator;
import java.util.UUID;

/**
 * A unique identifier for a specific option within a specific vote.
 * <p>
 * Official Name: bgm
 * Intermediary Name: net.minecraft.class_8373
 */
public record VoteOptionId(UUID voteId, int index) {

    /**
     * Standard sorting: first by Vote UUID, then by Option Index.
     */
    public static final Comparator<VoteOptionId> COMPARATOR = Comparator
            .comparing(VoteOptionId::voteId)
            .thenComparingInt(VoteOptionId::index);

    /**
     * Full Record Codec for structured data (like NBT/JSON).
     */
    public static final Codec<VoteOptionId> CODEC = RecordCodecBuilder.create(instance -> 
        instance.group(
            Uuids.INT_STREAM_CODEC.fieldOf("uuid").forGetter(VoteOptionId::voteId),
            Codec.INT.fieldOf("index").forGetter(VoteOptionId::index)
        ).apply(instance, VoteOptionId::new)
    );

    /**
     * String-based Codec (Format: "uuid:index").
     * Used for Map keys in JSON where objects aren't allowed as keys.
     */
    public static final Codec<VoteOptionId> STRING_CODEC = Codec.STRING.comapFlatMap(s -> {
        int colonIndex = s.indexOf(':');
        if (colonIndex == -1) {
            return DataResult.error(() -> "Missing colon in VoteOptionId: " + s);
        }
        try {
            UUID uuid = UUID.fromString(s.substring(0, colonIndex));
            int index = Integer.parseInt(s.substring(colonIndex + 1));
            return DataResult.success(new VoteOptionId(uuid, index));
        } catch (Exception e) {
            return DataResult.error(() -> "Invalid VoteOptionId format: " + s);
        }
    }, id -> id.voteId.toString() + ":" + id.index);

    /**
     * Network Reader (PacketByteBuf.PacketReader).
     */
    public static VoteOptionId read(PacketByteBuf buf) {
        return new VoteOptionId(buf.readUuid(), buf.readVarInt());
    }

    /**
     * Network Writer (PacketByteBuf.PacketWriter).
     */
    public static void write(PacketByteBuf buf, VoteOptionId id) {
        buf.writeUuid(id.voteId());
        buf.writeVarInt(id.index());
    }

    @Override
    public String toString() {
        return this.voteId.toString() + ":" + this.index;
    }
}