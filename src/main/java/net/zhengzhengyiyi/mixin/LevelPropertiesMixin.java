package net.zhengzhengyiyi.mixin;

import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.ServerWorldProperties;
import net.zhengzhengyiyi.accessor.LevelPropertiesAccessor;
import net.zhengzhengyiyi.mine.MineEffect;
import net.zhengzhengyiyi.mine.SpecialMine;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import java.util.Optional;

@Mixin(LevelProperties.class)
public abstract class LevelPropertiesMixin implements ServerWorldProperties, LevelPropertiesAccessor {
    @Unique private MineEffect field_custom_effect;
    @Unique private SpecialMine field_custom_mine;
    @Unique private int field_custom_val_a;  // mine level
    @Unique private int field_custom_val_b;  // mine exp
    @Unique private int field_custom_val_c;  // total mine exp
    @Unique private int field_custom_level_count;  // number of mines created

    @Override
    public void setUnlockedMineEffect(MineEffect effect) {
        this.field_custom_effect = effect;
    }

    @Override
    public boolean hasUnlockedMineEffect(MineEffect effect) {
        return this.field_custom_effect == effect;
    }

    @Override
    public void setSpecialMine(SpecialMine mine) {
        this.field_custom_mine = mine;
    }

    @Override
    public boolean hasSpecialMine(SpecialMine mine) {
        return this.field_custom_mine == mine;
    }

    @Override
    public void setCurrentSpecialMine(Optional<SpecialMine> optional, boolean bl) {
        this.field_custom_mine = optional.orElse(null);
    }

    @Override
    public Optional<SpecialMine> getRandomSpecialMine(Random random) {
        return Optional.ofNullable(this.field_custom_mine);
    }

    @Override
    public int getMineLevel() {
        return this.field_custom_val_a;
    }

    @Override
    public int getMineExp() {
        return this.field_custom_val_b;
    }

    @Override
    public void addMineExp(int exp) {
        this.field_custom_val_b += exp;
        this.field_custom_val_c += exp;
        // Level up every 100 exp
        this.field_custom_val_a = this.field_custom_val_c / 100;
    }

    @Override
    public int getTotalMineExp() {
        return this.field_custom_val_c;
    }

    @Override
    public int getLevelCount() {
        return this.field_custom_level_count++;
    }
}