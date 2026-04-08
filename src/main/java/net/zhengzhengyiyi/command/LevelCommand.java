package net.zhengzhengyiyi.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.RegistryEntryReferenceArgumentType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.zhengzhengyiyi.AprilsLegacy;
import net.zhengzhengyiyi.mine.MineEffect;
import net.zhengzhengyiyi.mine.SpecialMine;
import net.zhengzhengyiyi.mine.class_10967;

import java.util.List;
import java.util.Optional;

public class LevelCommand {

    private static final SimpleCommandExceptionType NO_WORLD_ERROR =
        new SimpleCommandExceptionType(Text.literal("Failed to create mine dimension."));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(
            CommandManager.literal("level")
                .requires(src -> true) // op-only: use /op to restrict in production
                .then(CommandManager.literal("from")
                    .then(CommandManager.literal("mine")
                        .then(CommandManager.argument("mine",
                                RegistryEntryReferenceArgumentType.registryEntry(registryAccess, AprilsLegacy.SPECIAL_MINE_KEY))
                            .executes(ctx -> executeFromMine(ctx,
                                RegistryEntryReferenceArgumentType.getRegistryEntry(ctx, "mine", AprilsLegacy.SPECIAL_MINE_KEY)
                            ))
                        )
                    )
                    .then(CommandManager.literal("effects")
                        .executes(ctx -> executeFromEffects(ctx, List.of()))
                    )
                )
        );
    }

    private static int executeFromMine(CommandContext<ServerCommandSource> ctx,
                                        RegistryEntry<SpecialMine> entry) throws CommandSyntaxException {
        ServerCommandSource src = ctx.getSource();
        List<MineEffect> effects = entry.value().generateEffects(src.getWorld());
        return createAndEnter(src, effects, Optional.of(entry.value()));
    }

    private static int executeFromEffects(CommandContext<ServerCommandSource> ctx,
                                           List<MineEffect> effects) throws CommandSyntaxException {
        return createAndEnter(ctx.getSource(), effects, Optional.empty());
    }

    private static int createAndEnter(ServerCommandSource src, List<MineEffect> effects,
                                       Optional<SpecialMine> mine) throws CommandSyntaxException {
        class_10967.class_10970 result = class_10967.method_69062(src.getServer(), effects, mine);
        ServerWorld world = result.world();

        if (world == null) throw NO_WORLD_ERROR.create();

        BlockPos spawn = world.getSpawnPoint().getPos();
        Vec3d pos = new Vec3d(spawn.getX() + 0.5, spawn.getY() + 1.0, spawn.getZ() + 0.5);

        for (ServerPlayerEntity player : src.getServer().getPlayerManager().getPlayerList()) {
            player.teleportTo(new TeleportTarget(world, pos, Vec3d.ZERO, 0f, 0f,
                java.util.Set.of(), TeleportTarget.NO_OP));
        }

        src.sendFeedback(() -> Text.literal("Created mine: " + result.id().getValue()), true);
        return 1;
    }
}
