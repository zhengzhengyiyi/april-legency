package net.zhengzhengyiyi.entity;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.zhengzhengyiyi.block.ModBlocks;

import org.jetbrains.annotations.Nullable;

public class MoonCowEntity extends CowEntity {
    public MoonCowEntity(EntityType<? extends MoonCowEntity> entityType, World world) {
        super(entityType, world);
        
        this.moveControl = new MoonCowMoveControl(this);
    }

    public static boolean canMoonCowSpawn(
        EntityType<? extends AnimalEntity> entityType, 
        WorldAccess worldAccess, 
        SpawnReason spawnReason, 
        BlockPos blockPos, 
        Random random
    ) {
        return worldAccess.getBlockState(blockPos.down()).isOf(ModBlocks.CHEESE);
    }

    @Override
    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        return world.getBlockState(pos.down()).isOf(ModBlocks.CHEESE) ? 10.0F : super.getPathfindingFavor(pos, world);
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        this.equipStack(EquipmentSlot.HEAD, new ItemStack(Blocks.GLASS));
        return super.initialize(world, difficulty, spawnReason, entityData);
    }

    @Nullable
    @Override
    public MoonCowEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
    	MoonCowEntity child = (MoonCowEntity) this.getType().create(
    		    serverWorld, 
    		    (entity) -> {
//    		        entity.equipStack(EquipmentSlot.HEAD, new ItemStack(Blocks.GLASS));
    		    }, 
    		    this.getBlockPos(), 
    		    SpawnReason.BREEDING, 
    		    true, 
    		    false
    		);
    	
        if (child != null) {
            child.equipStack(EquipmentSlot.HEAD, new ItemStack(Blocks.GLASS));
        }
        return child;
    }

    static class MoonCowMoveControl extends MoveControl {
        public MoonCowMoveControl(MobEntity mobEntity) {
            super(mobEntity);
        }

        @Override
        public void tick() {
            MoveControl.State state = this.state;
            super.tick();
            if (state == MoveControl.State.MOVE_TO || state == MoveControl.State.JUMPING) {
                this.entity.setForwardSpeed(-this.entity.getMovementSpeed());
            }
        }

        @Override
        protected float wrapDegrees(float from, float to, float max) {
            return super.wrapDegrees(from, to, max) - 180.0F;
        }
    }
}