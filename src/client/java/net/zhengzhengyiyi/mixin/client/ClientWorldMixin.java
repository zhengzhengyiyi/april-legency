package net.zhengzhengyiyi.mixin.client;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.world.ClientWorld;
import net.zhengzhengyiyi.accessor.VoteClientWorldAccessor;

@Mixin(ClientWorld.class)
public class ClientWorldMixin implements VoteClientWorldAccessor {

}
