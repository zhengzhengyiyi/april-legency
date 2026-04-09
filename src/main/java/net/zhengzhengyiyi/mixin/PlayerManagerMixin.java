package net.zhengzhengyiyi.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.ClientConnection;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.zhengzhengyiyi.network.VoteRuleSyncS2CPacket;
import net.zhengzhengyiyi.rules.VoteRules;
import net.zhengzhengyiyi.vote.VoteValue;
import net.zhengzhengyiyi.vote.VoterAction;
import net.zhengzhengyiyi.world.VoteRule;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
	@Shadow
	@Final
	private CombinedDynamicRegistries<ServerDynamicRegistryType> registryManager;

	/**
	 * Mirrors craftmine GameInstance.initHub + PlayerManager.onPlayerConnect spawn logic.
	 *
	 * In craftmine, the overworld IS the mine control dimension. New players (no saved data)
	 * spawn at HUB_SPAWN_POS (13, 2, 8) with angle 90.0F — set by initHub.
	 * Players who logged out in a mine world are left there (vanilla NBT restore handles it).
	 * Only players with no saved dimension data get redirected to the overworld hub.
	 *
	 * method_69097() in ServerWorld = TeleportTarget to overworld spawn (the hub exit target).
	 * method_69093() = teleports players INTO a mine world on first entry.
	 */
	@Inject(method="onPlayerConnect", at=@At("TAIL"))
	public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData data, CallbackInfo ci) {
		// Only redirect brand-new players with no saved position data to the overworld hub.
		// Players who logged out in a mine world stay there (their NBT dimension is restored by vanilla).
		// Mirrors craftmine: overworld spawn is set to HUB_SPAWN_POS (13, 2, 8) by initHub.
		if (player.getEntityWorld() instanceof ServerWorld currentWorld) {
			boolean isInMineWorld = ((net.zhengzhengyiyi.accessor.MineServerWorldAccessor) currentWorld).isMineWorld();
			if (isInMineWorld) {
				// Player's saved data pointed to a mine world — send them to overworld hub instead.
				// Mirrors craftmine method_69097(): TeleportTarget to overworld spawn.
				net.minecraft.server.MinecraftServer server = currentWorld.getServer();
				net.minecraft.server.world.ServerWorld overworld = server.getOverworld();
				net.minecraft.util.math.Vec3d spawnPos = overworld.getSpawnPoint().getPos().toBottomCenterPos();
				net.minecraft.world.TeleportTarget target = new net.minecraft.world.TeleportTarget(
					overworld, spawnPos, net.minecraft.util.math.Vec3d.ZERO,
					90.0F, 0.0F,
					net.minecraft.world.TeleportTarget.NO_OP
				);
				player.teleportTo(target);
			}
		}
//		List<VoteValue> list = registryManager.getCombinedRegistryManager().getEntryOrThrow(VoteRegistries.VOTE_RULE_TYPE_KEY).value().stream().<Vote>flatMap(rule -> rule.getActiveOptions()).map(vote -> (VoteValue) vote).toList();
		
		@SuppressWarnings("unchecked")
		List<VoteValue> list = VoteRules.getPool().stream()
//			    .map(WeightedList.Entry::getElement)
			    .map(RegistryEntry::value)
			    .filter(vote -> vote instanceof VoteRule)
			    .map(vote -> (VoteRule<?>) vote)
			    .<VoteValue>flatMap(rule -> rule.getActiveOptions())
			    .toList();

		ServerPlayNetworking.getSender(player).sendPacket(new VoteRuleSyncS2CPacket(true, VoterAction.APPROVE, list));
	}
}
