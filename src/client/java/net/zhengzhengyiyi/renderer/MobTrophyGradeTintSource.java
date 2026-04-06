package net.zhengzhengyiyi.renderer;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.tint.TintSource;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.ColorHelper;
import net.zhengzhengyiyi.block.MobTrophyBlock;
import org.jetbrains.annotations.Nullable;

/**
 * class_11121 - Tint source for mob trophy block state color (grade-based).
 */
@Environment(EnvType.CLIENT)
public record MobTrophyGradeTintSource() implements TintSource {
    public static final MapCodec<MobTrophyGradeTintSource> CODEC = MapCodec.unit(new MobTrophyGradeTintSource());

    @Override
    public int getTint(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user) {
        BlockStateComponent bsc = stack.get(DataComponentTypes.BLOCK_STATE);
        if (bsc != null) {
            MobTrophyBlock.Grade grade = bsc.getValue(MobTrophyBlock.GRADE);
            if (grade != null) return ColorHelper.fullAlpha(grade.getColor());
        }
        return ColorHelper.fullAlpha(MobTrophyBlock.Grade.GRASS.getColor());
    }

    @Override
    public MapCodec<MobTrophyGradeTintSource> getCodec() { return CODEC; }
}
