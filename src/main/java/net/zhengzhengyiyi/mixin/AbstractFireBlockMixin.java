package net.zhengzhengyiyi.mixin;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.world.World;
import net.zhengzhengyiyi.accessor.MineServerWorldAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFireBlock.class)
public class AbstractFireBlockMixin {

    /**
     * Allow nether portals to be lit in mine dimensions and infinite dimensions.
     * Vanilla only allows portal lighting in the overworld and nether (isOverworldOrNether).
     * We extend this to also allow it in any mine world or any infinite dimension
     * (identified by the "aprils-legacy:dim_" id prefix).
     */
    @Inject(method = "isOverworldOrNether", at = @At("RETURN"), cancellable = true)
    private static void allowPortalInMineDimensions(World world, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) return; // already allowed, skip

        // Allow in mine dimensions
        if (world instanceof net.minecraft.server.world.ServerWorld serverWorld
                && serverWorld instanceof MineServerWorldAccessor mineWorld
                && mineWorld.isMineWorld()) {
            cir.setReturnValue(true);
            return;
        }

        // Allow in infinite dimensions (id starts with "aprils-legacy:dim_")
        String dimPath = world.getRegistryKey().getValue().toString();
        if (dimPath.startsWith("aprils-legacy:dim_")) {
            cir.setReturnValue(true);
        }
    }
}
