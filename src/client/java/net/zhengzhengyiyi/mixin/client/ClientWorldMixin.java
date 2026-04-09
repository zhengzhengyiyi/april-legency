package net.zhengzhengyiyi.mixin.client;

import net.minecraft.client.world.ClientWorld;
import net.zhengzhengyiyi.accessor.VoteClientWorldAccessor;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientWorld.class)
public class ClientWorldMixin implements VoteClientWorldAccessor {
}
