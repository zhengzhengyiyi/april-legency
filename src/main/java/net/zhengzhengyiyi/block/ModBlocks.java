package net.zhengzhengyiyi.block;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class ModBlocks {
    public static final RegistryKey<Block> CHEESE_BLOCK_KEY = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of("minecraft", "cheese"));
    public static final RegistryKey<Block> PICKAXE_BLOCK_KEY = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of("minecraft", "pickaxe_block"));
    public static final RegistryKey<Block> PLACE_BLOCK_KEY = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of("minecraft", "place_block"));
    public static final RegistryKey<Block> BOOK_BOX_KEY = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of("minecraft", "book_box"));
    public static final RegistryKey<Block> CURSOR_KEY = RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("cursor"));
    public static final RegistryKey<Block> ANT_KEY = RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("ant"));

    public static final Block DIMENSION_CONTROL = register(
        RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("dimension_control")),
        new DimensionControlBlock(AbstractBlock.Settings.create().mapColor(MapColor.BLUE).instrument(NoteBlockInstrument.BANJO).strength(2.5F).sounds(BlockSoundGroup.WOOD).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("dimension_control"))))
    );
    public static final Block ANT = register(
        ANT_KEY, new AntBlock(AbstractBlock.Settings.copy(Blocks.ANVIL).sounds(BlockSoundGroup.WET_GRASS).strength(-1.0F, 3600000.0F).dropsNothing().registryKey(ANT_KEY))
    );
    public static final Block NEITHER_PORTAL = register(
        RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("neither_portal")),
        new NeitherPortalBlock(AbstractBlock.Settings.copy(Blocks.NETHER_PORTAL).noCollision().strength(-1.0F).sounds(BlockSoundGroup.GLASS).dropsNothing().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("neither_portal"))))
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
    public static final Block MINE_CRAFTER = register(
        RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("mine_crafter")),
        new MineCrafterBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASS).sounds(BlockSoundGroup.WOOD).strength(2.5F).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("mine_crafter"))))
    );
    public static final Block MINING_PORTAL = register(
        RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("mine_travelling_block")),
        new MiningPortalBlock(AbstractBlock.Settings.create().noCollision().strength(-1.0F).dropsNothing().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("mine_travelling_block"))))
    );
    public static final Block REVISIT_BLOCK = register(
        RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("mine_revisit")),
        new RevisitBlock(AbstractBlock.Settings.copy(Blocks.STONE).strength(1.5F).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("mine_revisit"))))
    );
    
    // April Fools blocks
    public static final Block LEAF_LITTER = register(
        RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("leaf_litter")),
        new LeafLitterBlock(AbstractBlock.Settings.copy(Blocks.MOSS_CARPET).strength(0.1F).sounds(BlockSoundGroup.GRASS).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("leaf_litter"))))
    );
    public static final Block FIREFLY_BUSH = register(
        RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("firefly_bush")),
        new FireflyBushBlock(AbstractBlock.Settings.create().strength(0.0F).sounds(BlockSoundGroup.GRASS).noCollision().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("firefly_bush"))))
    );
    public static final Block CACTUS_FLOWER = register(
        RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("cactus_flower")),
        new CactusFlowerBlock(AbstractBlock.Settings.copy(Blocks.POPPY).strength(0.0F).sounds(BlockSoundGroup.GRASS).noCollision().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("cactus_flower"))))
    );
    public static final Block TROPHY_BLOCK = register(
        RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("trophy")),
        new TrophyBlock(AbstractBlock.Settings.create().strength(1.0F).sounds(BlockSoundGroup.WOOD).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("trophy"))))
    );
    public static final Block MOB_TROPHY_BLOCK = register(
        RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("mob_trophy")),
        new MobTrophyBlock(AbstractBlock.Settings.create().strength(1.0F).sounds(BlockSoundGroup.WOOD).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("mob_trophy"))))
    );
    public static final Block SHIMMERING_DOOR = register(
        RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("shimmering_door")),
        new ShimmeringDoorBlock(net.minecraft.block.BlockSetType.OAK, AbstractBlock.Settings.copy(Blocks.OAK_DOOR).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("shimmering_door"))))
    );

    public static final BlockEntityType<MineCrafterBlockEntity> MINE_CRAFTER_BLOCKENTITY =
        createBlockEntityType("mine_crafter", MineCrafterBlockEntity::new, MINE_CRAFTER);
    public static final BlockEntityType<TravellingBlockEntity> TRAVELLING_BLOCK_ENTITY =
        createBlockEntityType("mine_travelling_block", TravellingBlockEntity::new, MINING_PORTAL);
    public static final BlockEntityType<NeitherPortalEntity> NEITHER_PORTAL_ENTITY =
        register("neither_portal", NeitherPortalEntity::new, NEITHER_PORTAL);
    public static final BlockEntityType<MobTrophyBlockEntity> MOB_TROPHY_BLOCK_ENTITY =
        register("mob_trophy", MobTrophyBlockEntity::new, MOB_TROPHY_BLOCK);

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

    private static <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(String id, FabricBlockEntityTypeBuilder.Factory<? extends T> factory, Block... blocks) {
        Util.getChoiceType(TypeReferences.BLOCK_ENTITY, id);
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.<T>create(factory, blocks).build());
    }

    public static void init() {
    }
}
