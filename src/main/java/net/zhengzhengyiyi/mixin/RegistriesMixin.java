package net.zhengzhengyiyi.mixin;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.zhengzhengyiyi.world.Vote;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(net.minecraft.registry.RegistryKeys.class)
public class RegistriesMixin {
    @Unique
    RegistryKey<Registry<Vote>> VOTE_RULE_TYPE = RegistryKey.ofRegistry(Identifier.of("minecraft", "vote_rule_type"));
}
