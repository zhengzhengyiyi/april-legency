package net.zhengzhengyiyi.entity.pet;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.EnumSet;

/**
 * class_10989 - Pet Look Goal
 * Makes the pet look at players holding breeding items (repurposed as treat items).
 */
public class PetLookGoal extends Goal {
    private final BasePetEntity pet;
    private PlayerEntity target;
    private int timer;

    public PetLookGoal(BasePetEntity pet) {
        this.pet = pet;
        this.setControls(EnumSet.of(Goal.Control.LOOK));
    }

    /** canStart - Checks if can start */
    @Override
    public boolean canStart() {
        this.target = this.pet.getEntityWorld().getClosestPlayer(this.pet, 8.0);
        return this.target != null && method_69327(this.target);
    }

    /** shouldContinue - Checks if should continue */
    @Override
    public boolean shouldContinue() {
        return this.timer > 0 && this.target != null && this.target.isAlive() && method_69327(this.target);
    }

    /** start - Starts looking */
    @Override
    public void start() {
        this.timer = 40 + this.pet.getRandom().nextInt(40);
    }

    /** stop - Stops looking */
    @Override
    public void stop() {
        this.target = null;
    }

    /** tick - Updates look */
    @Override
    public void tick() {
        if (this.target != null) {
            this.pet.getLookControl().lookAt(this.target, 10.0f, this.pet.getMaxLookPitchChange());
        }
        this.timer--;
    }

    /** method_69327 - Checks if player has breeding item */
    private boolean method_69327(PlayerEntity player) {
        ItemStack held = player.getMainHandStack();
        return !held.isEmpty() && this.pet.isBreedingItem(held);
    }
}
