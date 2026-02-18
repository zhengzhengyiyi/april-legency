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
    @Unique private int field_custom_val_a;
    @Unique private int field_custom_val_b;

    @Override
    public void method_70220(MineEffect arg) {
        this.field_custom_effect = arg;
    }

    @Override
    public boolean method_70223(MineEffect arg) {
        return this.field_custom_effect == arg;
    }

    @Override
    public void method_70222(SpecialMine arg) {
        this.field_custom_mine = arg;
    }

    @Override
    public boolean method_70219(SpecialMine arg) {
        return this.field_custom_mine == arg;
    }

    @Override
    public void method_70221(Optional<SpecialMine> optional, boolean bl) {
        this.field_custom_mine = optional.orElse(null);
    }

    @Override
    public Optional<SpecialMine> method_70218(Random random) {
        return Optional.ofNullable(this.field_custom_mine);
    }

    @Override
    public int method_70227() {
        return this.field_custom_val_a;
    }

    @Override
    public int method_70228() {
        return this.field_custom_val_b;
    }

    @Override
    public void method_70224(int i) {
        this.field_custom_val_a = i;
    }

    @Override
    public int method_70225() {
        return this.field_custom_val_b;
    }

    @Override
    public int method_70226() {
        return 0;
    }
}