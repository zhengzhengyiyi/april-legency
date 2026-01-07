package net.zhengzhengyiyi.vote;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.stream.Stream;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.zhengzhengyiyi.network.VoteRuleSyncS2CPacket;
import net.zhengzhengyiyi.rules.VoteRules;
import net.zhengzhengyiyi.world.Vote;

/**
 * Represents a specific value or effect of a vote.
 * This is a dispatched codec-based interface.
 * <p>
 * Official Name: bee
 * Intermediary Name: net.minecraft.class_8371
 */
public interface VoteValue extends Provider {
    /**
     * Dispatches the codec based on the VoteRuleType.
     * ja.ao refers to Registries.VOTE_RULE_TYPE.
     */
//    Codec<VoteValue> CODEC = VoteRegistries.VOTE_RULE_TYPE.getCodec()
//        .dispatch(VoteValue::getType, VoteRuleType::getCodec);
	
	public static final Codec<VoteValue> CODEC = VoteRegistries.VOTE_RULE_TYPE.getCodec()
		.dispatch(
	    VoteValue::getType,
			type -> type.getOptionCodec().fieldOf("value")
		);
	
	static java.util.Optional<VoteValue> getRandomValue(net.minecraft.server.MinecraftServer server, net.minecraft.util.math.random.Random random) {
        return server.getRegistryManager()
            .getOrThrow(VoteRegistries.VOTE_RULE_TYPE_KEY)
            .getRandom(random)
            .flatMap(entry -> entry.value().generateOptions(server, random, 1).findFirst());
    }

    /**
     * Gets the type/category of this vote value.
     */
    default Vote getType() {
    	return VoteRules.AI_ATTACK;
    }

    /**
     * Applies this value to the server and broadcasts the change to all players.
     * * @param action The action (APPROVE/REPEAL).
     * @param server The Minecraft server instance.
     */
    default void applyAndBroadcast(VoterAction action, MinecraftServer server) {
        this.apply(action);
        // xi refers to VoteRuleSyncS2CPacket (class_8361)
//        server.getPlayerManager().sendToAll(new VoteRuleSyncS2CPacket(false, action, List.of(this)));
        VoteRuleSyncS2CPacket syncPacket = new VoteRuleSyncS2CPacket(false, action, List.of(this));

	    server.getPlayerManager().getPlayerList().forEach(player -> {
	        ServerPlayNetworking.send(player, syncPacket);
//	    	player.networkHandler.sendPacket(syncPacket);
	    });
    }
    
    default Stream<VoteValue> getValues() {
    	return Stream.of(this);
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
}
