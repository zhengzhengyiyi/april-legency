package net.zhengzhengyiyi.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.EnumSet;

/**
 * class_10990 - Angry Ghast Entity
 * A more aggressive ghast variant with enhanced attack behavior.
 */
public class AngryGhastEntity extends GhastEntity {

    private boolean charging = false;

    public AngryGhastEntity(EntityType<? extends AngryGhastEntity> entityType, World world) {
        super(entityType, world);
        this.lookControl = new AngryGhastLookControl(this);
        this.moveControl = new AngryGhastMoveControl(this);
    }

    /** method_69333 - Creates attributes */
    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
            .add(EntityAttributes.MAX_HEALTH, 20.0)
            .add(EntityAttributes.FOLLOW_RANGE, 100.0)
            .add(EntityAttributes.MOVEMENT_SPEED, 0.3);
    }

    /** method_69331 - Checks if charging */
    public boolean isCharging() {
        return this.charging;
    }

    /** method_69334 - Sets charging state */
    public void setCharging(boolean charging) {
        this.charging = charging;
    }

    /** method_69332 - Gets explosion power */
    public int getAngryExplosionPower() {
        return 2;
    }

    /** method_69330 - Checks if hit by fireball */
    public boolean isHitByFireball() {
        return this.getRecentDamageSource() != null
            && this.getRecentDamageSource().isOf(net.minecraft.entity.damage.DamageTypes.FIREBALL);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(5, new AngryGhastAttackGoal(this));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    /** method_69335 - Checks if path clear */
    public boolean isPathClear(Vec3d from, Vec3d to) {
        return this.getEntityWorld().raycastBlock(from, to, this.getBlockPos(),
            net.minecraft.util.shape.VoxelShapes.fullCube(),
            net.minecraft.block.Blocks.AIR.getDefaultState()) == null;
    }

    /**
     * Inner class class_10991 - Look control
     */
    static class AngryGhastLookControl extends LookControl {
        public AngryGhastLookControl(AngryGhastEntity entity) {
            super(entity);
        }

        @Override
        public void tick() {
            // Ghast look control — no head rotation clamping
        }
    }

    /**
     * Inner class class_10992 - Move control
     */
    static class AngryGhastMoveControl extends MoveControl {
        private final AngryGhastEntity ghast;
        private int collisionTimer;

        public AngryGhastMoveControl(AngryGhastEntity ghast) {
            super(ghast);
            this.ghast = ghast;
        }

        @Override
        public void tick() {
            if (this.state == MoveControl.State.MOVE_TO) {
                if (this.collisionTimer-- <= 0) {
                    this.collisionTimer += this.ghast.getRandom().nextInt(5) + 2;
                    Vec3d target = new Vec3d(this.targetX - this.ghast.getX(),
                        this.targetY - this.ghast.getY(),
                        this.targetZ - this.ghast.getZ());
                    double len = target.length();
                    if (len < 1.0) {
                        this.state = MoveControl.State.WAIT;
                        this.ghast.setVelocity(this.ghast.getVelocity().multiply(0.5));
                    } else {
                        this.ghast.setVelocity(this.ghast.getVelocity().add(
                            target.multiply(this.speed * 0.05 / len)));
                        LivingEntity tgt = this.ghast.getTarget();
                        if (tgt == null) {
                            Vec3d vel = this.ghast.getVelocity();
                            this.ghast.setYaw(-((float) MathHelper.atan2(vel.x, vel.z)) * (180f / (float) Math.PI));
                        } else {
                            this.ghast.setYaw(-((float) MathHelper.atan2(
                                tgt.getX() - this.ghast.getX(),
                                tgt.getZ() - this.ghast.getZ())) * (180f / (float) Math.PI));
                        }
                        this.ghast.bodyYaw = this.ghast.getYaw();
                    }
                }
            }
        }
    }

    /**
     * Inner class class_10993 - Attack goal
     */
    static class AngryGhastAttackGoal extends Goal {
        private final AngryGhastEntity ghast;
        private int attackTimer;

        public AngryGhastAttackGoal(AngryGhastEntity ghast) {
            this.ghast = ghast;
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            return this.ghast.getTarget() != null;
        }

        @Override
        public void tick() {
            LivingEntity target = this.ghast.getTarget();
            if (target == null) return;

            boolean canSee = this.ghast.getVisibilityCache().canSee(target);

            if (canSee) {
                this.attackTimer++;
                if (this.attackTimer == 10) {
                    this.ghast.setCharging(true);
                    this.ghast.playSound(SoundEvents.ENTITY_GHAST_WARN, 10.0f, 1.0f);
                }
                if (this.attackTimer == 20) {
                    method_69336(target);
                    this.attackTimer = 0;
                    this.ghast.setCharging(false);
                }
            } else {
                this.attackTimer = Math.max(0, this.attackTimer - 1);
                this.ghast.setCharging(false);
            }

            this.ghast.getMoveControl().moveTo(target.getX(), target.getY(), target.getZ(), 1.0);
        }

        /** method_69336 - Fires fireball */
        private void method_69336(LivingEntity target) {
            Vec3d ghastPos = this.ghast.getEyePos();
            Vec3d dir = target.getEyePos().subtract(ghastPos).normalize()
                .multiply(this.ghast.getAngryExplosionPower());
            FireballEntity fireball = new FireballEntity(
                this.ghast.getEntityWorld(), this.ghast, dir, this.ghast.getAngryExplosionPower()
            );
            fireball.setPosition(ghastPos.x, ghastPos.y, ghastPos.z);
            this.ghast.getEntityWorld().spawnEntity(fireball);
            this.ghast.playSound(SoundEvents.ENTITY_GHAST_SHOOT, 10.0f, 1.0f);
        }

        @Override
        public void stop() {
            this.ghast.setCharging(false);
            this.attackTimer = 0;
        }
    }
}
