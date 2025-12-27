package net.zhengzhengyiyi.vote;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.zhengzhengyiyi.network.VoteRuleSyncS2CPacket;
import net.zhengzhengyiyi.world.Vote;

/**
 * Represents a specific value or effect of a vote.
 * This is a dispatched codec-based interface.
 * <p>
 * Official Name: bee
 * Intermediary Name: net.minecraft.class_8371
 */
public interface VoteValue {
    /**
     * Dispatches the codec based on the VoteRuleType.
     * ja.ao refers to Registries.VOTE_RULE_TYPE.
     */
//    Codec<VoteValue> CODEC = VoteRegistries.VOTE_RULE_TYPE.getCodec()
//        .dispatch(VoteValue::getType, VoteRuleType::getCodec);
	
	@SuppressWarnings("unchecked")
	public static final Codec<VoteValue> CODEC = VoteRegistries.VOTE_RULE_TYPE.getCodec()
		.dispatch(
	    VoteValue::getType,
			type -> type.getOptionCodec().fieldOf("value")
		);

    /**
     * Gets the type/category of this vote value.
     */
    Vote getType();

    /**
     * Applies this value to the server and broadcasts the change to all players.
     * * @param action The action (APPROVE/REPEAL).
     * @param server The Minecraft server instance.
     */
    default void applyAndBroadcast(VoterAction action, MinecraftServer server) {
        this.apply(action);
        // xi refers to VoteRuleSyncS2CPacket (class_8361)
        server.getPlayerManager().sendToAll(new VoteRuleSyncS2CPacket(false, action, List.of(this)));
    }

    /**
     * Internal logic to apply or undo the effect.
     */
    void apply(VoterAction action);

    /**
     * Gets the display text for this value based on the action.
     */
    Text getDescription(VoterAction action);

    /**
     * A sub-interface for values that provide a static description regardless of the action.
     */
    interface ConstantDescription extends VoteValue {
        Text getStaticDescription();

        @Override
        default Text getDescription(VoterAction action) {
            // In the original bytecode, the switch logic was simplified to return the same text
            return this.getStaticDescription();
        }
    }
    
    public interface Provider {
        java.util.stream.Stream<VoteValue> getValues();
    }
}
