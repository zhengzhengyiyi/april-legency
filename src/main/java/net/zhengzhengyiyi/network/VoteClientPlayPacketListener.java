package net.zhengzhengyiyi.network;

public interface VoteClientPlayPacketListener {
    void onVoteUpdate(VoteUpdateS2CPacket packet);

    void onVoteRuleSync(VoteRuleSyncS2CPacket packet);

    void onVoteStart(class_8483 paramclass_8483);
    
    void onVoteResult(class_8481 paramclass_8481);
    
    void onVoteStop(class_8482 paramclass_8482);
    
    void onVoteResponse(voteResponsepacket paramclass_8480);
    
    void method_50043(VoteCastpacket paramclass_8258);
    
    void method_50045(class_8484 paramclass_8484);
}
