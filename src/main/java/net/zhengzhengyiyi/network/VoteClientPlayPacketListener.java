package net.zhengzhengyiyi.network;

public interface VoteClientPlayPacketListener {
    void onVoteUpdate(VoteUpdateS2CPacket packet);

    void onVoteRuleSync(VoteRuleSyncS2CPacket packet);

    void method_51015(class_8483 paramclass_8483);
    
    void method_51013(class_8481 paramclass_8481);
    
    void method_51014(class_8482 paramclass_8482);
    
    void method_51012(class_8480 paramclass_8480);
    
    void method_50043(class_8258 paramclass_8258);
    
    void method_50045(class_8484 paramclass_8484);
}
