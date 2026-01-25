package net.zhengzhengyiyi.block;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final RegistryKey<Block> CHEESE_BLOCK_KEY = RegistryKey.of(
        RegistryKeys.BLOCK, 
        Identifier.of("minecraft", "cheese")
    );
    public static final RegistryKey<Block> PICKAXE_BLOCK_KEY = RegistryKey.of(
        RegistryKeys.BLOCK, 
        Identifier.of("minecraft", "pickaxe_block")
    );
    public static final RegistryKey<Block> PLACE_BLOCK_KEY = RegistryKey.of(
        RegistryKeys.BLOCK, 
        Identifier.of("minecraft", "place_block")
    );
    public static final RegistryKey<Block> BOOK_BOX_KEY = RegistryKey.of(
            RegistryKeys.BLOCK, 
            Identifier.of("minecraft", "book_box")
        );
    public static final RegistryKey<Block> CURSOR_KEY = RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("cursor"));
    public static final RegistryKey<Block> ANT_KEY = RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("ant"));
    
    public static final Block ANT = register(
    		ANT_KEY, new AntBlock(AbstractBlock.Settings.copy(Blocks.ANVIL).sounds(BlockSoundGroup.WET_GRASS).strength(-1.0F, 3600000.0F).dropsNothing().registryKey(ANT_KEY))
    );
    public static final Block NEITHER_PORTAL = register(
    	      RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("neither_portal")),
    	      new NeitherPortalBlock(
    	         AbstractBlock.Settings.copy(Blocks.NETHER_PORTAL).noCollision().strength(-1.0F).sounds(BlockSoundGroup.GLASS).dropsNothing().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("neither_portal")))
    	      )
    	   );
    public static final Block CURSOR = register(CURSOR_KEY, new Block(AbstractBlock.Settings.copy(Blocks.STONE).strength(1.8F).registryKey(CURSOR_KEY)));
    
    public static final Block BOOK_BOX = register(
    		BOOK_BOX_KEY, new BookBox(AbstractBlock.Settings.copy(Blocks.OAK_WOOD).strength(1.5F).sounds(BlockSoundGroup.WOOD).registryKey(BOOK_BOX_KEY))
    	   );
    
    public static final Block PICKAXE_BLOCK = register(
    	PICKAXE_BLOCK_KEY,
    	new PickaxeBlock(AbstractBlock.Settings.copy(Blocks.STONE).strength(1.0F).solidBlock(Blocks::always).registryKey(PICKAXE_BLOCK_KEY))
    );
    public static final Block PLACE_BLOCK = register(
    	PLACE_BLOCK_KEY,
        new PickaxeBlock(AbstractBlock.Settings.copy(Blocks.STONE).strength(1.0F).solidBlock(Blocks::always).registryKey(PLACE_BLOCK_KEY))
    );
    public static final Block CHEESE = register(
        CHEESE_BLOCK_KEY,
        new CheeseBlock(AbstractBlock.Settings.create().hardness(0.5F).registryKey(CHEESE_BLOCK_KEY))
    );
    
    public static final BlockEntityType<NeitherPortalEntity> NEITHER_PORTAL_ENTITY =
    		register("neither_portal", NeitherPortalEntity::new, ModBlocks.NEITHER_PORTAL);

    private static Block register(RegistryKey<Block> key, Block block) {
        return Registry.register(Registries.BLOCK, key, block);
    }
    
    private static <T extends BlockEntity> BlockEntityType<T> register(
    		String name,
    		FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory,
    		Block... blocks
    ) {
    	Identifier id = Identifier.ofVanilla(name);
    	return Registry.register(Registries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build());
    }

    public static void init() {
    }
}
