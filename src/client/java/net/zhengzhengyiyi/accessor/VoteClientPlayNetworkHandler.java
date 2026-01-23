package net.zhengzhengyiyi.accessor;

import net.zhengzhengyiyi.network.*;
import net.zhengzhengyiyi.vote.ClientVoteManager;
import net.zhengzhengyiyi.vote.VoteOptionId;

public interface VoteClientPlayNetworkHandler extends VoteClientPlayPacketListener {
	public ClientVoteManager getVoteManager();
	void onVoteRuleSync(VoteRuleSyncS2CPacket packet);
	void onVoteStart(class_8483 packet);
	void onVoteResult(class_8481 packet);
	void onVoteStop(class_8482 packet);
	void onVoteResponse(voteResponsepacket packet);
	void onVoteUpdate(VoteUpdateS2CPacket packet);
	int method_51006(VoteOptionId arg, ClientVoteManager.ResponseHandler arg2);
	void method_50043(VoteCastpacket paramclass_8258);
    void method_50045(class_8484 paramclass_8484);
}
