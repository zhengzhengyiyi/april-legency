package net.zhengzhengyiyi.vote;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Comparator;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

import org.jetbrains.annotations.Nullable;

/**
 * Represents the summary of a vote choice, including its display name and total vote count.
 * <p>
 * Official Name: bgu
 * Intermediary Name: net.minecraft.class_8371
 */
public record VoteChoice(Text displayName, int voteCount) {
	public VoteChoice {
	    if (voteCount < 0) {
	        throw new IllegalArgumentException("Vote count cannot be negative");
	    }
	}

    /**
     * Comparator to sort choices by their vote count.
     */
    public static final Comparator<VoteChoice> COMPARATOR = Comparator.comparing(VoteChoice::voteCount);

    /**
     * Codec for serializing vote choices, using "display_name" and "vote_count" fields.
     */
    public static final Codec<VoteChoice> CODEC = RecordCodecBuilder.create(instance -> 
        instance.group(
            TextCodecs.CODEC.fieldOf("display_name").forGetter(VoteChoice::displayName),
            Codec.INT.fieldOf("vote_count").forGetter(VoteChoice::voteCount)
        ).apply(instance, VoteChoice::new)
    );

    /**
     * Reads a {@link VoteChoice} from a {@link PacketByteBuf}.
     * Corresponding to sf.a (c) in bytecode.
     */
    public static VoteChoice read(PacketByteBuf buf) {
        Text text = Text.of(buf.readString());
        int count = buf.readVarInt();
        
        return new VoteChoice(text, count);
    }

    /**
     * Writes a {@link VoteChoice} to a {@link PacketByteBuf}.
     * Corresponding to sf.b (d) in bytecode.
     */
    public static void write(PacketByteBuf buf, VoteChoice choice) {
        buf.writeString(choice.displayName().toString());
        buf.writeVarInt(choice.voteCount());
    }

    /**
     * Helper method to accumulate votes.
     * If an existing choice is provided, it increments the count; otherwise, it starts from zero.
     * * @param existing The previous state of the choice.
     * @param name The display name for the choice.
     * @param addedCount The number of votes to add.
     * @return A new {@link VoteChoice} instance with the updated count.
     */
    public static VoteChoice accumulate(@Nullable VoteChoice existing, Text name, int addedCount) {
        int currentCount = (existing != null) ? existing.voteCount() : 0;
        return new VoteChoice(name, currentCount + addedCount);
    }
}
