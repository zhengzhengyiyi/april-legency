package net.zhengzhengyiyi.mixin;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.ServerWorldProperties;
import net.zhengzhengyiyi.accessor.LevelPropertiesAccessor;
import net.zhengzhengyiyi.mine.MineEffect;
import net.zhengzhengyiyi.mine.SpecialMine;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import java.util.Optional;

@Mixin(LevelProperties.class)
public abstract class LevelPropertiesMixin implements ServerWorldProperties, LevelPropertiesAccessor {
    private static final String KEY_LEVEL_COUNT    = "aprils_level_count";
    private static final String KEY_MINE_LEVEL     = "aprils_mine_level";
    private static final String KEY_MINE_EXP       = "aprils_mine_exp";
    private static final String KEY_MINE_EXP_TOTAL = "aprils_mine_exp_total";

    @Unique private final java.util.Set<MineEffect> field_custom_unlocked_effects = new java.util.HashSet<>();
    @Unique private SpecialMine field_custom_mine;
    @Unique private int field_custom_val_a;  // mine level
    @Unique private int field_custom_val_b;  // mine exp
    @Unique private int field_custom_val_c;  // total mine exp
    @Unique private int field_custom_level_count;  // number of mines created (persisted)

    // ── Persist to NBT ──────────────────────────────────────────────────────────

    /**
     * Inject into cloneWorldNbt to write our custom fields.
     * The returned NbtCompound is the root level.dat compound.
     */
    @Inject(method = "cloneWorldNbt", at = @At("RETURN"))
    private void writeCustomData(DynamicRegistryManager registryManager,
                                  NbtCompound playerNbt,
                                  CallbackInfoReturnable<NbtCompound> cir) {
        NbtCompound nbt = cir.getReturnValue();
        nbt.putInt(KEY_LEVEL_COUNT,    this.field_custom_level_count);
        nbt.putInt(KEY_MINE_LEVEL,     this.field_custom_val_a);
        nbt.putInt(KEY_MINE_EXP,       this.field_custom_val_b);
        nbt.putInt(KEY_MINE_EXP_TOTAL, this.field_custom_val_c);
    }

    /**
     * Inject into readProperties (the static load method) to read our custom fields.
     * The Dynamic<T> contains the full level.dat data.
     */
    @Inject(method = "readProperties", at = @At("RETURN"))
    private static <T> void readCustomData(Dynamic<T> dynamic,
                                            LevelInfo levelInfo,
                                            LevelProperties.SpecialProperty specialProperty,
                                            GeneratorOptions generatorOptions,
                                            Lifecycle lifecycle,
                                            CallbackInfoReturnable<LevelProperties> cir) {
        LevelProperties props = cir.getReturnValue();
        // Read persisted values back into the mixin fields via the accessor
        ((LevelPropertiesMixin)(Object) props).field_custom_level_count =
            dynamic.get(KEY_LEVEL_COUNT).asInt(0);
        ((LevelPropertiesMixin)(Object) props).field_custom_val_a =
            dynamic.get(KEY_MINE_LEVEL).asInt(0);
        ((LevelPropertiesMixin)(Object) props).field_custom_val_b =
            dynamic.get(KEY_MINE_EXP).asInt(0);
        ((LevelPropertiesMixin)(Object) props).field_custom_val_c =
            dynamic.get(KEY_MINE_EXP_TOTAL).asInt(0);
    }

    // ── LevelPropertiesAccessor implementation ───────────────────────────────

    @Override
    public void setUnlockedMineEffect(MineEffect effect) {
        this.field_custom_unlocked_effects.add(effect);
    }

    @Override
    public boolean hasUnlockedMineEffect(MineEffect effect) {
        return this.field_custom_unlocked_effects.contains(effect);
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
        this.field_custom_val_a = this.field_custom_val_c / 100;
    }

    @Override
    public int getTotalMineExp() {
        return this.field_custom_val_c;
    }

    /**
     * Mirrors craftmine method_70226: pre-increment so each call returns a new unique ID.
     * Craftmine: return ++this.field_59285
     */
    @Override
    public int getLevelCount() {
        return ++this.field_custom_level_count;
    }

    @Override
    public int peekLevelCount() {
        return this.field_custom_level_count;
    }
}
