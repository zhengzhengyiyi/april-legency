package net.zhengzhengyiyi.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.CakeBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import net.zhengzhengyiyi.rules.VoteRules;

@Mixin(CakeBlock.class)
public class CakeBlockMixin {
	@Inject(method="tryEat", at=@At("HEAD"))
	private static void tryEat(WorldAccess world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfoReturnable<ActionResult> cir) {
		if (VoteRules.INFINITE_CAKES.isActive()) {
			if (!player.canConsume(false)) {
				cir.setReturnValue(ActionResult.SUCCESS);
				cir.cancel();
			}
			player.incrementStat(Stats.EAT_CAKE_SLICE);
			player.getHungerManager().add(2, 0.1F);
			world.emitGameEvent(player, GameEvent.BLOCK_DESTROY, pos);
			
			cir.setReturnValue(ActionResult.SUCCESS);
			cir.cancel();
		}
	}
}
