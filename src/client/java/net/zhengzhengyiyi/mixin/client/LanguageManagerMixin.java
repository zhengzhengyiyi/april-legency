package net.zhengzhengyiyi.mixin.client;

import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.resource.ResourceManager;
import net.zhengzhengyiyi.rules.VoteRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(LanguageManager.class)
public class LanguageManagerMixin {

    @Inject(
        method = "reload", 
        at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0, shift = At.Shift.AFTER),
        locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void onReload(ResourceManager manager, CallbackInfo ci, List<String> list) {
        if (VoteRules.FRENCH_MODE.isActive()) {
            list.add("fr_fr");
        }
    }
}
