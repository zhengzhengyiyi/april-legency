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
	
	@Inject(method="onPlayerConnect", at=@At("TAIL"))
	public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData data, CallbackInfo ci) {
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
