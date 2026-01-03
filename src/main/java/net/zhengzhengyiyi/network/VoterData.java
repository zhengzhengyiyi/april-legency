package net.zhengzhengyiyi.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Uuids;
import net.zhengzhengyiyi.vote.VoteChoice;

import java.util.Map;
import java.util.UUID;

public record VoterData(Map<UUID, VoteChoice> voters) {
    public static final VoterData EMPTY = new VoterData(Map.of());

    public static final Codec<VoterData> CODEC = RecordCodecBuilder.create(instance -> 
        instance.group(
            Codec.unboundedMap(Uuids.CODEC, VoteChoice.CODEC)
                .fieldOf("voters")
                .forGetter(VoterData::voters)
        ).apply(instance, VoterData::new)
    );

    public static VoterData read(PacketByteBuf buf) {
//        return new VoterData(buf.readMap(PacketByteBuf::readUuid, VoteChoice::read));
    	return new VoterData(buf.readMap(b -> b.readUuid(), VoteChoice::read));
    }

    public void write(PacketByteBuf buf) {
//        buf.writeMap(this.voters, VoteChoice::writeUuid, (b, entry) -> entry.write(b));
    	buf.writeMap(this.voters, (b, uuid) -> b.writeUuid(uuid), (b, choice) -> VoteChoice.write(b, choice));
    }

    public int getVoterCount() {
        return this.voters.size();
    }

    public int getTotalVotes() {
        return this.voters.values().stream()
                .mapToInt(VoteChoice::voteCount)
                .sum();
    }

    public static VoterData createSingle(UUID uuid, VoteChoice entry) {
        return new VoterData(Map.of(uuid, entry));
    }
}
