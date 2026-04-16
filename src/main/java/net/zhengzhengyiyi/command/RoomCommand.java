package net.zhengzhengyiyi.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.block.Blocks;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.zhengzhengyiyi.component.ModDataComponentTypes;
import net.zhengzhengyiyi.component.RoomComponent;
import net.zhengzhengyiyi.item.ModItems;

/**
 * Mirrors craftmine class_10964 — dev commands for building and saving hub rooms.
 *
 * /room start <id> <xSize> <zSize> <ySize>  — scaffolds a bedrock box + structure block
 * /room save  <id>                           — saves the structure to disk
 * /room key   <id>                           — gives a Shimmering Key for that room
 */
public class RoomCommand {

    /** Suggests room IDs that already exist in the world save (short form, no hub/room/ prefix). */
    private static final SuggestionProvider<ServerCommandSource> SUGGEST_SAVED =
        (ctx, builder) -> CommandSource.suggestIdentifiers(
            ctx.getSource().getWorld().getStructureTemplateManager()
                .streamTemplates()
                .filter(id -> id.getPath().startsWith(RoomComponent.ROOM_PATH_PREFIX))
                .map(RoomComponent::toShortId),
            builder
        );

    /** Suggests room IDs from the data pack (short form). */
    private static final SuggestionProvider<ServerCommandSource> SUGGEST_PACK =
        (ctx, builder) -> CommandSource.suggestIdentifiers(
            ctx.getSource().getWorld().getStructureTemplateManager()
                .streamTemplates()
                .filter(id -> id.getPath().startsWith(RoomComponent.ROOM_PATH_PREFIX))
                .map(RoomComponent::toShortId),
            builder
        );

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            (LiteralArgumentBuilder<ServerCommandSource>) CommandManager.literal("room")
                .then(CommandManager.literal("start")
                    .then(CommandManager.argument("structure", IdentifierArgumentType.identifier())
                        .then(CommandManager.argument("xSize", IntegerArgumentType.integer(1))
                            .then(CommandManager.argument("zSize", IntegerArgumentType.integer(1))
                                .then(CommandManager.argument("ySize", IntegerArgumentType.integer(1))
                                    .executes(ctx -> executeStart(
                                        ctx.getSource(),
                                        IdentifierArgumentType.getIdentifier(ctx, "structure"),
                                        IntegerArgumentType.getInteger(ctx, "xSize"),
                                        IntegerArgumentType.getInteger(ctx, "zSize"),
                                        IntegerArgumentType.getInteger(ctx, "ySize")
                                    ))
                                )
                            )
                        )
                    )
                )
                .then(CommandManager.literal("save")
                    .then(CommandManager.argument("structure", IdentifierArgumentType.identifier())
                        .suggests(SUGGEST_SAVED)
                        .executes(ctx -> executeSave(
                            ctx.getSource(),
                            IdentifierArgumentType.getIdentifier(ctx, "structure")
                        ))
                    )
                )
                .then(CommandManager.literal("key")
                    .then(CommandManager.argument("structure", IdentifierArgumentType.identifier())
                        .suggests(SUGGEST_PACK)
                        .executes(ctx -> executeKey(
                            ctx.getSource(),
                            IdentifierArgumentType.getIdentifier(ctx, "structure")
                        ))
                    )
                )
        );
    }

    /**
     * Scaffolds a bedrock box at 6 blocks in front of the player and places a structure block
     * below it pre-configured to save the room structure.
     */
    private static int executeStart(ServerCommandSource src, Identifier shortId,
                                     int xSize, int zSize, int ySize) {
        // Place origin 6 blocks in front of the executor (mirrors LookingPosArgument(0,0,6))
        BlockPos origin;
        if (src.getEntity() != null) {
            Vec3d look = src.getEntity().getRotationVec(1.0f);
            origin = BlockPos.ofFloored(src.getPosition().add(look.multiply(6.0)));
        } else {
            origin = BlockPos.ofFloored(src.getPosition());
        }        ServerWorld world = src.getWorld();

        // Build bedrock walls/floor (open top)
        for (int x = 0; x < xSize; x++) {
            for (int z = 0; z < zSize; z++) {
                for (int y = 0; y < ySize; y++) {
                    boolean isWall = x == 0 || x == xSize - 1 || z == 0 || z == zSize - 1 || y == 0;
                    if (isWall) {
                        world.setBlockState(origin.add(x, y, z), Blocks.BEDROCK.getDefaultState(), 3);
                    }
                }
            }
        }

        // Place structure block one below origin, pre-configured for saving
        BlockPos structurePos = origin.down();
        world.setBlockState(structurePos,
            Blocks.STRUCTURE_BLOCK.getDefaultState().with(StructureBlock.MODE, StructureBlockMode.SAVE), 3);
        world.getBlockEntity(structurePos, BlockEntityType.STRUCTURE_BLOCK).ifPresent(be -> {
            be.setTemplateName(RoomComponent.toFullId(shortId));
            be.setIgnoreEntities(false);
            be.setSize(new Vec3i(xSize, ySize, zSize));
        });

        String saveCmd = "/room save " + shortId;
        src.sendFeedback(() -> Text.literal("Room scaffolded. When done, save the structure block and run ")
            .append(Text.literal(saveCmd).formatted(Formatting.GRAY))
            .append(" or click ")
            .append(Text.literal("here").styled(s -> s
                .withColor(Formatting.GRAY)
                .withUnderline(true)
                .withClickEvent(new ClickEvent.SuggestCommand(saveCmd))
            )), true);
        return xSize * ySize * zSize;
    }

    /**
     * Saves the structure template to disk and gives the player a key for it.
     * Finds the structure block that was placed by /room start, triggers its save,
     * then writes the template to disk as an NBT file.
     */
    private static int executeSave(ServerCommandSource src, Identifier shortId) {
        Identifier fullId = RoomComponent.toFullId(shortId);
        ServerWorld world = src.getWorld();
        
        // Find the structure block with this template name
        // Search in a reasonable radius around the player
        BlockPos playerPos = BlockPos.ofFloored(src.getPosition());
        StructureBlockBlockEntity structureBlock = null;
        
        for (BlockPos pos : BlockPos.iterateOutwards(playerPos, 50, 50, 50)) {
            if (world.getBlockState(pos).getBlock() == Blocks.STRUCTURE_BLOCK) {
                var be = world.getBlockEntity(pos);
                if (be instanceof StructureBlockBlockEntity sb && 
                    fullId.equals(sb.getTemplateName())) {
                    structureBlock = sb;
                    break;
                }
            }
        }
        
        if (structureBlock == null) {
            src.sendError(Text.literal("Could not find structure block for " + shortId + 
                ". Make sure the structure block is configured with the correct name."));
            return 0;
        }
        
        // Trigger the structure block's save (captures blocks from world into template)
        if (!structureBlock.saveStructure(true)) {
            src.sendError(Text.literal("Failed to save structure from structure block"));
            return 0;
        }
        
        src.sendFeedback(() -> Text.literal("Saved room structure: " + shortId + " as NBT file"), true);
        // Also give the key
        executeKey(src, shortId);
        return 1;
    }

    /**
     * Gives the executing player a Shimmering Key with the INSTANT_ROOM component set.
     * Mirrors class_10964.method_69046 — uses Identifier.ofVanilla() to match the
     * loot table format (e.g. minecraft:barrels).
     */
    private static int executeKey(ServerCommandSource src, Identifier shortId) {
        ServerPlayerEntity player = src.getPlayer();
        if (player == null) {
            src.sendError(Text.literal("Must be run by a player"));
            return 0;
        }
        // Use ofVanilla to match the reference: Identifier.ofVanilla("barrels") = minecraft:barrels
        Identifier vanillaId = Identifier.ofVanilla(shortId.getPath());
        ItemStack key = new ItemStack(ModItems.SHIMMERING_KEY);
        key.set(ModDataComponentTypes.INSTANT_ROOM, new RoomComponent(vanillaId));
        player.getInventory().insertStack(key);
        src.sendFeedback(() -> Text.literal("\uD83D\uDD11 ")
            .append(Text.translatable(vanillaId.toTranslationKey("room"))), false);
        return 1;
    }
}
