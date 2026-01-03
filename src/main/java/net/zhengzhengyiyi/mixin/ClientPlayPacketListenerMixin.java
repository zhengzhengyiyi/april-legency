package net.zhengzhengyiyi.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.network.listener.ClientPlayPacketListener;
import net.zhengzhengyiyi.network.VoteClientPlayPacketListener;

@Mixin(ClientPlayPacketListener.class)
public interface ClientPlayPacketListenerMixin extends VoteClientPlayPacketListener {

}
