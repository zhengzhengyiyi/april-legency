package net.zhengzhengyiyi.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.network.listener.ClientPlayPacketListener;
import net.zhengzhengyiyi.network.VoteClientPlayPacketListener;
import net.zhengzhengyiyi.network.VoteRuleSyncS2CPacket;
import net.zhengzhengyiyi.network.VoteUpdateS2CPacket;
import net.zhengzhengyiyi.network.VoteCastpacket;
import net.zhengzhengyiyi.network.voteResponsepacket;
import net.zhengzhengyiyi.network.class_8481;
import net.zhengzhengyiyi.network.class_8482;
import net.zhengzhengyiyi.network.class_8483;
import net.zhengzhengyiyi.network.class_8484;

@Mixin(ClientPlayPacketListener.class)
public interface ClientPlayPacketListenerMixin extends VoteClientPlayPacketListener {
	void onVoteUpdate(VoteUpdateS2CPacket packet);
	
    void onVoteRuleSync(VoteRuleSyncS2CPacket packet);

    void method_51015(class_8483 paramclass_8483);
    
    void method_51013(class_8481 paramclass_8481);
    
    void method_51014(class_8482 paramclass_8482);
    
    void method_51012(voteResponsepacket paramclass_8480);
    
    void method_50043(VoteCastpacket paramclass_8258);
    
    void method_50045(class_8484 paramclass_8484);
}
