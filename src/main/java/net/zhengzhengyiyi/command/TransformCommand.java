package net.zhengzhengyiyi.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

// THIS FILE IS INCOMPLITED

public class TransformCommand {
    public static final SimpleCommandExceptionType NOT_LIVING_ENTITY_EXCEPTION = new SimpleCommandExceptionType(Text.literal("Target is not a living entity"));
    public static final SimpleCommandExceptionType MULTIPLE_PLAYERS_EXCEPTION = new SimpleCommandExceptionType(Text.literal("Expected only one player for target skin"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
//        dispatcher.register(
//            CommandManager.literal("transform")
////                .requires(source -> source.hasPermissionLevel(2))
//                .then(
//                    CommandManager.literal("into")
//                        .then(
//                            CommandManager.argument("entity", RegistryEntryArgumentType.registryEntry(registryAccess, RegistryKeys.ENTITY_TYPE))
//                                .executes(context -> transformInto(
//                                    context.getSource(),
//                                    RegistryEntryArgumentType.getRegistryEntry(context, "entity", RegistryKeys.ENTITY_TYPE),
//                                    null
//                                ))
//                                .then(
//                                    CommandManager.argument("nbt", NbtCompoundArgumentType.nbtCompound())
//                                        .executes(context -> transformInto(
//                                            context.getSource(),
//                                            RegistryEntryArgumentType.getRegistryEntry(context, "entity", RegistryKeys.ENTITY_TYPE),
//                                            NbtCompoundArgumentType.getNbtCompound(context, "nbt")
//                                        ))
//                                )
//                        )
//                        .then(
//                            CommandManager.literal("player")
//                                .then(
//                                    CommandManager.argument("player", GameProfileArgumentType.gameProfile())
//                                        .executes(context -> applyPlayerSkin(
//                                            context.getSource(), 
//                                            GameProfileArgumentType.getProfileArgument(context, "player")
//                                        ))
//                                )
//                        )
//                )
//                .then(
//                    CommandManager.literal("scale")
//                        .then(
//                            CommandManager.argument("scale", FloatArgumentType.floatArg(0.1F, 16.0F))
//                                .executes(context -> setScale(
//                                    context.getSource(), 
//                                    FloatArgumentType.getFloat(context, "scale")
//                                ))
//                        )
//                )
//                .then(
//                    CommandManager.literal("clear")
//                        .executes(context -> clearTransform(context.getSource()))
//                )
//        );
    }

//    private static int transformInto(ServerCommandSource source, RegistryEntry.Reference<EntityType<?>> entityType, @Nullable NbtCompound nbt) throws CommandSyntaxException {
//        if (source.getEntityOrThrow() instanceof LivingEntity livingEntity) {
//            livingEntity.setTransformData(data -> data.setEntityType(entityType.value(), Optional.ofNullable(nbt)));
//            source.sendFeedback(Text.literal("Transformed into ").append(entityType.value().getName()), false);
//            return 1;
//        } else {
//            throw NOT_LIVING_ENTITY_EXCEPTION.create();
//        }
//    }
//
//    private static int applyPlayerSkin(ServerCommandSource source, Collection<GameProfile> profiles) throws CommandSyntaxException {
//        if (profiles.size() != 1) {
//            throw MULTIPLE_PLAYERS_EXCEPTION.create();
//        } else {
//            ServerPlayerEntity player = source.getPlayerOrThrow();
//            GameProfile targetProfile = profiles.iterator().next();
//            SkullBlockEntity.fetchProfileByName(targetProfile.getName()).thenAccept(optionalProfile -> {
//                optionalProfile.ifPresent(profile -> {
//                    player.setTransformData(data -> data.setPlayerProfile(Optional.of(profile)));
//                    source.sendFeedback(Text.literal("Applied skin of " + profile.getName()), false);
//                });
//            });
//            return 1;
//        }
//    }
//
//    private static int setScale(ServerCommandSource source, float scale) throws CommandSyntaxException {
//        if (source.getEntityOrThrow() instanceof LivingEntity livingEntity) {
//            livingEntity.setTransformData(data -> data.setScale(scale));
//            source.sendFeedback(Text.literal("Transformed scale by " + String.format("%.2f", scale) + "x"), false);
//            return 1;
//        } else {
//            throw NOT_LIVING_ENTITY_EXCEPTION.create();
//        }
//    }
//
//    private static int clearTransform(ServerCommandSource source) throws CommandSyntaxException {
//        if (source.getEntityOrThrow() instanceof LivingEntity livingEntity) {
//            livingEntity.clearTransformData();
//            source.sendFeedback(Text.literal("Cleared transform"), false);
//            return 1;
//        } else {
//            throw NOT_LIVING_ENTITY_EXCEPTION.create();
//        }
//    }
}
