package net.zhengzhengyiyi.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.RegistryEntryReferenceArgumentType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.zhengzhengyiyi.AprilsLegacy;
import net.zhengzhengyiyi.accessor.MineServerWorldAccessor;
import net.zhengzhengyiyi.mine.MineEffect;

import java.util.Collection;
/**
 * Mirrors craftmine class_10966 — op command to unlock a world effect globally.
 *
 * /unlock_world_effect <effect>
 */
public class UnlockWorldEffectCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                 CommandRegistryAccess registryAccess) {
        dispatcher.register(
            (LiteralArgumentBuilder<ServerCommandSource>) CommandManager.literal("unlock_world_effect")
                .then(
                    CommandManager.argument("effect",
                        RegistryEntryReferenceArgumentType.registryEntry(registryAccess, AprilsLegacy.WORLD_EFFECT_KEY))
                        .executes(ctx -> execute(
                            ctx.getSource(),
                            RegistryEntryReferenceArgumentType.getRegistryEntry(
                                ctx, "effect", AprilsLegacy.WORLD_EFFECT_KEY)
                        ))
                )
        );
    }

    private static int execute(ServerCommandSource src,
                                Collection<RegistryEntry.Reference<MineEffect>> effects) {
        MineServerWorldAccessor world = (MineServerWorldAccessor)(Object) src.getWorld();
        effects.forEach(ref -> {
            world.unlockMineEffect(ref.value());
            src.sendFeedback(() -> Text.literal("Unlocked world effect: " + ref.registryKey().getValue()), true);
        });
        return effects.size();
    }

    // Overload for single entry (used by RegistryEntryReferenceArgumentType)
    private static int execute(ServerCommandSource src,
                                RegistryEntry.Reference<MineEffect> effect) {
        MineServerWorldAccessor world = (MineServerWorldAccessor)(Object) src.getWorld();
        world.unlockMineEffect(effect.value());
        src.sendFeedback(() -> Text.literal("Unlocked world effect: " + effect.registryKey().getValue()), true);
        return 1;
    }
}
