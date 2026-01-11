package net.zhengzhengyiyi.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final RegistryKey<Block> CHEESE_BLOCK_KEY = RegistryKey.of(
        RegistryKeys.BLOCK, 
        Identifier.of("minecraft", "cheese")
    );

    public static final Block CHEESE = register(
        CHEESE_BLOCK_KEY,
        new CheeseBlock(AbstractBlock.Settings.create().registryKey(CHEESE_BLOCK_KEY))
    );

    private static Block register(RegistryKey<Block> key, Block block) {
        return Registry.register(Registries.BLOCK, key, block);
    }

    public static void init() {
    }
}
