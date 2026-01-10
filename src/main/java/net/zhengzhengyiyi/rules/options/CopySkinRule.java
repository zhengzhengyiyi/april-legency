package net.zhengzhengyiyi.rules.options;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
//import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;
//import net.zhengzhengyiyi.rules.options.VoteEffect;
import net.zhengzhengyiyi.world.Vote;
import net.zhengzhengyiyi.vote.VoterAction;

public class CopySkinRule extends VoteEffect {
    private final Codec<CopySkinRule.Option> optionCodec = EntityReference.CODEC
        .optionalFieldOf("player")
        .codec()
        .xmap(CopySkinRule.Option::new, opt -> opt.playerEntry);

    @Override
    public Codec getOptionCodec() {
//        return Vote.createOptionCodec(this.optionCodec);
    	return this.optionCodec;
    }

    @Override
    public Stream generateOptions(MinecraftServer server, Random random, int limit) {
        boolean anyPlayerHasSkin = server.getPlayerManager()
            .getPlayerList()
            .stream()
            .anyMatch(player -> player.getGameProfile().properties().containsKey("textures"));

        ObjectArrayList<Optional<ServerPlayerEntity>> playerPool = server.getPlayerManager()
            .getPlayerList()
            .stream()
            .map(Optional::of)
            .collect(Collectors.toCollection(ObjectArrayList::new));

        if (anyPlayerHasSkin) {
            playerPool.add(Optional.empty());
        }

        Util.shuffle(playerPool, random);
        
        return playerPool.stream()
            .limit(limit)
            .map(optPlayer -> new CopySkinRule.Option(optPlayer.map(EntityReference::fromPlayer)));
    }

    protected class Option extends VoteEffect.Option {
        final Optional<EntityReference> playerEntry;
        private final Text description;

        protected Option(Optional<EntityReference> playerEntry) {
            this.playerEntry = playerEntry;
            this.description = playerEntry
                .map(entry -> Text.translatable("rule.copy_skin", entry.displayName()))
                .orElse(Text.translatable("rule.reset_skin"));
        }

        @Override
        protected Text getDescriptionText() {
            return this.description;
        }

        @Override
        public void run(MinecraftServer server) {
        	// TODO: implement skin copying
//            if (this.playerEntry.isPresent()) {
//                SkullBlockEntity.fetchProfileByName(this.playerEntry.get().name(), gameProfile -> {
//                    for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
//                        player.getGameProfile().getProperties().putAll(gameProfile.getProperties());
//                        server.getPlayerManager().sendToAll(new net.minecraft.network.packet.s2c.play.PlayerListS2CPacket(
//                            net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, player));
//                    }
//                });
//            } else {
//                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
//                    player.getGameProfile().getProperties().removeAll("textures");
//                }
//            }
        }

		@Override
		public Vote getType() {
			return CopySkinRule.this;
		}

		@Override
		public Text getDescription(VoterAction action) {
			return Text.of("no description");
		}
    }
}