package net.zhengzhengyiyi.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.BlockState;
import net.zhengzhengyiyi.rules.VoteRules;

//@Mixin(EndermanEntity.PickUpBlockGoal.class)
@Mixin(targets = "net.minecraft.entity.mob.EndermanEntity$PickUpBlockGoal")
public class EndermanEntityMixin {
	@Redirect(
		method = "tick",
		at = @At(
		    value = "INVOKE",
		    target = "Lnet/minecraft/block/BlockState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"
		)
	)
	private boolean redirectEndermanAbility(BlockState state, net.minecraft.registry.tag.TagKey<net.minecraft.block.Block> tag) {
		return state.isIn(tag) || (VoteRules.ENDERMEN_PICK_ANYTHING.isActive() && !state.hasBlockEntity());
	}
}
