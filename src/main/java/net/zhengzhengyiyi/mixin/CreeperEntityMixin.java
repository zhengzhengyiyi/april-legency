package net.zhengzhengyiyi.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.zhengzhengyiyi.rules.VoteRules;

@Mixin(CreeperEntity.class)
public class CreeperEntityMixin extends MobEntity {
	@Shadow
	@Final
	private static TrackedData<Boolean> CHARGED;
	
	protected CreeperEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public EntityData initialize(ServerWorldAccess world,
			 LocalDifficulty difficulty,
			 SpawnReason spawnReason,
			 @Nullable EntityData entityData) {
		if (VoteRules.CHARGED_CREEPERS.isActive()) {
			this.dataTracker.set(CHARGED, true);
		}
		
		return super.initialize(world, difficulty, spawnReason, entityData);
	}
}
