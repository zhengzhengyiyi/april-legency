package net.zhengzhengyiyi.command;

import com.google.gson.JsonElement;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.io.PrintStream;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.NbtElementArgumentType;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;
import net.zhengzhengyiyi.world.Vote;
import net.zhengzhengyiyi.world.VoteRule;
import net.zhengzhengyiyi.vote.VoteDefinition;
import net.zhengzhengyiyi.vote.VoteState;
import net.zhengzhengyiyi.vote.VoterAction;
import net.zhengzhengyiyi.vote.VoteCategory;
import net.zhengzhengyiyi.vote.VoteManager;
import net.zhengzhengyiyi.vote.VoteServer;

import org.slf4j.Logger;

public class VoteCommands {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final SuggestionProvider<ServerCommandSource> SUGGEST_VOTE_ID = (commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(
        ((VoteServer)commandContext.getSource().getServer()).getVoteManager().getActiveVoteIds().map(UUID::toString), suggestionsBuilder
    );

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(
            CommandManager.literal("vote")
                .requires(source -> source.hasPermissionLevel(2))
                .then(
                    CommandManager.literal("pending")
                        .then(
                            CommandManager.literal("start")
                                .executes(ctx -> startPendingVote(ctx.getSource(), Optional.empty()))
                                .then(
                                    CommandManager.argument("rule", RegistryEntryArgumentType.registryEntry(registryAccess, RegistryKeys.RULES))
                                        .executes(
                                            ctx -> startPendingVote(
                                                ctx.getSource(), Optional.of(RegistryEntryArgumentType.getRegistryEntry(ctx, "rule", RegistryKeys.RULES))
                                            )
                                        )
                                )
                        )
                        .then(CommandManager.literal("repeal").executes(ctx -> startRepealVote(ctx.getSource())))
                        .then(
                            CommandManager.literal("finish")
                                .then(CommandManager.literal("*").executes(ctx -> finishAllVotes(ctx.getSource(), true)))
                                .then(
                                    CommandManager.argument("id", UuidArgumentType.uuid())
                                        .suggests(SUGGEST_VOTE_ID)
                                        .executes(ctx -> finishVote(ctx.getSource(), UuidArgumentType.getUuid(ctx, "id"), true))
                                )
                        )
                        .then(
                            CommandManager.literal("discard")
                                .then(CommandManager.literal("*").executes(ctx -> finishAllVotes(ctx.getSource(), false)))
                                .then(
                                    CommandManager.argument("id", UuidArgumentType.uuid())
                                        .suggests(SUGGEST_VOTE_ID)
                                        .executes(ctx -> finishVote(ctx.getSource(), UuidArgumentType.getUuid(ctx, "id"), false))
                                )
                        )
                        .then(
                            CommandManager.literal("vote")
                                .then(
                                    CommandManager.argument("id", UuidArgumentType.uuid())
                                        .suggests(SUGGEST_VOTE_ID)
                                        .then(
                                            CommandManager.argument("option", IntegerArgumentType.integer(0))
                                                .executes(
                                                    ctx -> castVote(
                                                        ctx.getSource(), UuidArgumentType.getUuid(ctx, "id"), IntegerArgumentType.getInteger(ctx, "option"), 1
                                                    )
                                                )
                                                .then(
                                                    CommandManager.argument("count", IntegerArgumentType.integer())
                                                        .executes(
                                                            ctx -> castVote(
                                                                ctx.getSource(),
                                                                UuidArgumentType.getUuid(ctx, "id"),
                                                                IntegerArgumentType.getInteger(ctx, "option"),
                                                                IntegerArgumentType.getInteger(ctx, "count")
                                                            )
                                                        )
                                                )
                                        )
                                )
                        )
                )
                .then(
                    CommandManager.literal("rule")
                        .then(
                            CommandManager.argument("rule", RegistryEntryArgumentType.registryEntry(registryAccess, RegistryKeys.RULES))
                                .then(
                                    CommandManager.literal("approve")
                                        .executes(
                                            ctx -> applyRuleDirectly(
                                                ctx.getSource(),
                                                RegistryEntryArgumentType.getRegistryEntry(ctx, "rule", RegistryKeys.RULES),
                                                VoteCategory.APPROVE,
                                                new NbtCompound()
                                            )
                                        )
                                        .then(
                                            CommandManager.literal("?")
                                                .executes(
                                                    ctx -> showRuleInfo(
                                                        ctx.getSource(), VoteCategory.APPROVE, RegistryEntryArgumentType.getRegistryEntry(ctx, "rule", RegistryKeys.RULES)
                                                    )
                                                )
                                        )
                                        .then(
                                            CommandManager.argument("value", NbtElementArgumentType.nbtElement())
                                                .executes(
                                                    ctx -> applyRuleDirectly(
                                                        ctx.getSource(),
                                                        RegistryEntryArgumentType.getRegistryEntry(ctx, "rule", RegistryKeys.RULES),
                                                        VoteCategory.APPROVE,
                                                        NbtElementArgumentType.getNbtElement(ctx, "value")
                                                    )
                                                )
                                        )
                                )
                                .then(
                                    CommandManager.literal("repeal")
                                        .executes(
                                            ctx -> applyRuleDirectly(
                                                ctx.getSource(),
                                                RegistryEntryArgumentType.getRegistryEntry(ctx, "rule", RegistryKeys.RULES),
                                                VoteCategory.REPEAL,
                                                new NbtCompound()
                                            )
                                        )
                                        .then(
                                            CommandManager.literal("?")
                                                .executes(
                                                    ctx -> showRuleInfo(
                                                        ctx.getSource(), VoteCategory.REPEAL, RegistryEntryArgumentType.getRegistryEntry(ctx, "rule", RegistryKeys.RULES)
                                                    )
                                                )
                                        )
                                        .then(
                                            CommandManager.literal("*")
                                                .executes(
                                                    ctx -> repealAllFromRule(ctx.getSource(), RegistryEntryArgumentType.getRegistryEntry(ctx, "rule", RegistryKeys.RULES))
                                                )
                                        )
                                        .then(
                                            CommandManager.argument("value", NbtElementArgumentType.nbtElement())
                                                .executes(
                                                    ctx -> applyRuleDirectly(
                                                        ctx.getSource(),
                                                        RegistryEntryArgumentType.getRegistryEntry(ctx, "rule", RegistryKeys.RULES),
                                                        VoteCategory.REPEAL,
                                                        NbtElementArgumentType.getNbtElement(ctx, "value")
                                                    )
                                                )
                                        )
                                )
                        )
                        .then(
                            CommandManager.literal("?")
                                .then(CommandManager.literal("approve").executes(ctx -> showRandomRuleInfo(ctx.getSource(), VoteCategory.APPROVE)))
                                .then(CommandManager.literal("repeal").executes(ctx -> showRandomRuleInfo(ctx.getSource(), VoteCategory.REPEAL)))
                        )
                        .then(CommandManager.literal("*").then(CommandManager.literal("repeal").executes(ctx -> repealEverything(ctx.getSource()))))
                )
                .then(
                    CommandManager.literal("dump_all")
                        .executes(ctx -> dumpAllRules(ctx.getSource(), false))
                        .then(CommandManager.literal("short").executes(ctx -> dumpAllRules(ctx.getSource(), true)))
                        .then(CommandManager.literal("long").executes(ctx -> dumpAllRules(ctx.getSource(), false)))
                )
        );
    }

    private static Text createVoteHoverText(UUID id, VoteDefinition definition) {
        String json = VoteDefinition.CODEC.encodeStart(JsonOps.INSTANCE, definition).get().left().map(JsonElement::toString).orElse("Error!");
        return Text.literal(id.toString())
            .styled(style -> style.withUnderline(true).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(json))));
    }

    private static Integer executeVoteStart(ServerCommandSource source, UUID id, MinecraftServer server, Optional<VoteDefinition> definition) {
        return definition.map(def -> {
            server.getVoteManager().startVote(id, def);
            source.sendFeedback(() -> Text.literal("Started vote for ").append(createVoteHoverText(id, def)), true);
            return 1;
        }).orElseGet(() -> {
            source.sendError(Text.literal("Failed to start vote, maybe retry?"));
            return 0;
        });
    }

    private static int startPendingVote(ServerCommandSource source, Optional<RegistryEntry.Reference<Vote>> ruleEntry) {
        Random random = source.getWorld().random;
        UUID id = UUID.randomUUID();
        MinecraftServer server = source.getServer();
        Set<Vote> availableVotes = server.getVoteManager().getAvailableVotes();
        VoteDefinition.Type type = VoteDefinition.Type.random(random);
        
        Optional<VoteDefinition> definition = ruleEntry.isPresent() 
            ? VoteDefinition.createFixed(id, server, type, ruleEntry.get().value())
            : VoteDefinition.createRandom(id, availableVotes, server, type);
            
        return executeVoteStart(source, id, server, definition);
    }

    private static int startRepealVote(ServerCommandSource source) {
        Random random = source.getWorld().random;
        UUID id = UUID.randomUUID();
        MinecraftServer server = source.getServer();
        Set<Vote> availableVotes = server.getVoteManager().getAvailableVotes();
        VoteDefinition.Type type = VoteDefinition.Type.random(random);
        Optional<VoteDefinition> definition = VoteDefinition.createRepeal(id, availableVotes, server, type);
        return executeVoteStart(source, id, server, definition);
    }

    private static int finishAllVotes(ServerCommandSource source, boolean approve) {
        List<UUID> ids = source.getServer().getVoteManager().getActiveVoteIds().toList();
        int count = 0;
        for (UUID id : ids) {
            count += finishVote(source, id, approve);
        }
        return count;
    }

    private static int finishVote(ServerCommandSource source, UUID id, boolean approve) {
        VoteState state = source.getServer().getVoteManager().finishVote(id, approve);
        if (state != null) {
            source.sendFeedback(() -> Text.literal(approve ? "Finished vote for " : "Rejected vote for ").append(createVoteHoverText(id, state.vote())), true);
            return 1;
        } else {
            source.sendError(Text.literal("Failed to finish vote ").append(String.valueOf(id)));
            return 0;
        }
    }

    private static int castVote(ServerCommandSource source, UUID id, int option, int count) throws CommandSyntaxException {
        Entity voter = source.getEntityOrThrow();
        if (source.getServer().getVoteManager().addVotes(new VoterAction(id, option), voter, count)) {
            source.sendFeedback(() -> Text.translatable("Added %s votes from %s to option %s of vote %s", count, voter.getDisplayName(), option, id), true);
            return 1;
        } else {
            source.sendError(Text.literal("Failed to add votes to ").append(String.valueOf(id)));
            return 0;
        }
    }

    private static int showRandomRuleInfo(ServerCommandSource source, VoteCategory category) {
        RegistryEntry.Reference<Vote> randomRule = source.getServer().getVoteManager().getRandomRule(source.getWorld().getRandom());
        return showRuleInfo(source, category, randomRule);
    }

    private static int showRuleInfo(ServerCommandSource source, VoteCategory category, RegistryEntry.Reference<Vote> rule) {
        return (switch (category) {
            case APPROVE -> rule.value().generateApproveEffects(source.getServer(), source.getWorld().getRandom(), 1);
            case REPEAL -> rule.value().getActiveRepeals().toList().stream();
        }).findAny().map(effect -> applyEffectToWorld(effect, category, source)).orElseGet(() -> {
            source.sendError(Text.literal("No applicable rule in ").append(rule.registryKey().getValue().toString()));
            return 0;
        });
    }

    private static int applyRuleDirectly(ServerCommandSource source, RegistryEntry.Reference<Vote> rule, VoteCategory category, NbtElement nbt) {
        return rule.value()
            .getCodec()
            .parse(new Dynamic<>(NbtOps.INSTANCE, nbt))
            .get()
            .map(effect -> applyEffectToWorld(effect, category, source), result -> {
                LOGGER.warn("Failed to decode {}/{}: {}", rule.registryKey().getValue(), nbt, result.message());
                source.sendError(Text.literal("Failed to decode ").append(rule.registryKey().getValue().toString()));
                return 0;
            });
    }

    private static int applyEffectToWorld(VoteRule effect, VoteCategory category, ServerCommandSource source) {
        Text feedback = effect.getDisplayName(category);
        effect.apply(category, source.getServer());
        source.sendFeedback(() -> Text.literal("Applied ").append(feedback), true);
        return 1;
    }

    private static int repealEverything(ServerCommandSource source) {
        int count = source.getRegistryManager().get(RegistryKeys.RULES).stream().mapToInt(rule -> rule.repealAll(false)).sum();
        source.sendFeedback(() -> Text.literal("Repealed " + count + " changes from all rules"), true);
        return count;
    }

    private static int repealAllFromRule(ServerCommandSource source, RegistryEntry.Reference<Vote> rule) {
        int count = rule.value().repealAll(false);
        source.sendFeedback(() -> Text.literal("Repealed " + count + " changes for " + rule.registryKey().getValue()), true);
        return count;
    }

    private static int dumpAllRules(ServerCommandSource source, boolean isShort) {
        MinecraftServer server = source.getServer();
        Registry<Vote> registry = source.getRegistryManager().getOrThrow(RegistryKeys.RULES);
        PrintStream out = System.out;
        Random random = Random.create();
        registry.streamEntries()
            .sorted(Comparator.comparing(ref -> ref.registryKey().getValue()))
            .forEach(ref -> {
                if (!isShort) {
                    out.println(ref.registryKey().getValue());
                } else {
                    String examples = ref.value().generateApproveEffects(server, random, 3)
                        .map(e -> "\"" + e.getDisplayName(VoteCategory.APPROVE).getString() + "\"")
                        .collect(Collectors.joining(", "));
                    out.println(ref.registryKey().getValue() + ": " + examples);
                }
            });
        source.sendFeedback(() -> Text.literal("Rules dumped to console!"), false);
        return 1;
    }
}
