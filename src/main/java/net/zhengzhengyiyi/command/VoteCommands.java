package net.zhengzhengyiyi.command;

import com.google.gson.JsonElement;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.NbtElementArgumentType;
import net.minecraft.command.argument.RegistryEntryReferenceArgumentType;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.zhengzhengyiyi.vote.*;
import net.zhengzhengyiyi.world.Vote;
import net.zhengzhengyiyi.world.VoteRule;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class VoteCommands {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final RegistryKey<Registry<Vote>> VOTE_RULE_REGISTRY_KEY = VoteRegistries.VOTE_RULE_TYPE_KEY;

    private static final SuggestionProvider<ServerCommandSource> SUGGEST_VOTE_ID = (context, builder) -> {
        var registryManager = context.getSource().getRegistryManager();
        var registry = registryManager.getOrThrow(VOTE_RULE_REGISTRY_KEY);
        return CommandSource.suggestMatching(registry.getIds().stream().map(Identifier::toString), builder);
    };

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(
            CommandManager.literal("vote")
//                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("pending")
                    .then(CommandManager.literal("start")
                        .executes(ctx -> startPendingVote(ctx.getSource(), Optional.empty()))
                        .then(CommandManager.argument("rule", RegistryEntryReferenceArgumentType.registryEntry(registryAccess, VOTE_RULE_REGISTRY_KEY))
                            .executes(ctx -> startPendingVote(ctx.getSource(), Optional.of(RegistryEntryReferenceArgumentType.getRegistryEntry(ctx, "rule", VOTE_RULE_REGISTRY_KEY))))
                        )
                    )
                    .then(CommandManager.literal("repeal")
                        .executes(ctx -> startRepealVote(ctx.getSource()))
                    )
                    .then(CommandManager.literal("finish")
                        .then(CommandManager.literal("*")
                            .executes(ctx -> finishAllVotes(ctx.getSource(), true))
                        )
                        .then(CommandManager.argument("id", UuidArgumentType.uuid())
                            .suggests(SUGGEST_VOTE_ID)
                            .executes(ctx -> finishVote(ctx.getSource(), UuidArgumentType.getUuid(ctx, "id"), true))
                        )
                    )
                    .then(CommandManager.literal("discard")
                        .then(CommandManager.literal("*")
                            .executes(ctx -> finishAllVotes(ctx.getSource(), false))
                        )
                        .then(CommandManager.argument("id", UuidArgumentType.uuid())
                            .suggests(SUGGEST_VOTE_ID)
                            .executes(ctx -> finishVote(ctx.getSource(), UuidArgumentType.getUuid(ctx, "id"), false))
                        )
                    )
                    .then(CommandManager.literal("vote")
                        .then(CommandManager.argument("id", UuidArgumentType.uuid())
                            .suggests(SUGGEST_VOTE_ID)
                            .then(CommandManager.argument("option", IntegerArgumentType.integer(0))
                                .executes(ctx -> castVote(ctx, 1))
                                .then(CommandManager.argument("count", IntegerArgumentType.integer())
                                    .executes(ctx -> castVote(ctx, IntegerArgumentType.getInteger(ctx, "count")))
                                )
                            )
                        )
                    )
                )
                .then(CommandManager.literal("rule")
                    .then(CommandManager.argument("rule", RegistryEntryReferenceArgumentType.registryEntry(registryAccess, VOTE_RULE_REGISTRY_KEY))
                        .then(CommandManager.literal("approve")
                            .executes(ctx -> applyRuleDirectly(ctx, VoterAction.APPROVE, new NbtCompound()))
                            .then(CommandManager.literal("?")
                                .executes(ctx -> showRuleInfo(ctx, VoterAction.APPROVE))
                            )
                            .then(CommandManager.argument("value", NbtElementArgumentType.nbtElement())
                                .executes(ctx -> applyRuleDirectly(ctx, VoterAction.APPROVE, NbtElementArgumentType.getNbtElement(ctx, "value")))
                            )
                        )
                        .then(CommandManager.literal("repeal")
                            .executes(ctx -> applyRuleDirectly(ctx, VoterAction.REPEAL, new NbtCompound()))
                            .then(CommandManager.literal("?")
                                .executes(ctx -> showRuleInfo(ctx, VoterAction.REPEAL))
                            )
                            .then(CommandManager.literal("*")
                                .executes(ctx -> repealAllFromRule(ctx.getSource(), RegistryEntryReferenceArgumentType.getRegistryEntry(ctx, "rule", VOTE_RULE_REGISTRY_KEY)))
                            )
                            .then(CommandManager.argument("value", NbtElementArgumentType.nbtElement())
                                .executes(ctx -> applyRuleDirectly(ctx, VoterAction.REPEAL, NbtElementArgumentType.getNbtElement(ctx, "value")))
                            )
                        )
                    )
                    .then(CommandManager.literal("?")
                        .then(CommandManager.literal("approve")
                            .executes(ctx -> showRandomRuleInfo(ctx.getSource(), VoterAction.APPROVE))
                        )
                        .then(CommandManager.literal("repeal")
                            .executes(ctx -> showRandomRuleInfo(ctx.getSource(), VoterAction.REPEAL))
                        )
                    )
                    .then(CommandManager.literal("*")
                        .then(CommandManager.literal("repeal")
                            .executes(ctx -> repealEverything(ctx.getSource()))
                        )
                    )
                )
                .then(CommandManager.literal("dump_all")
                    .executes(ctx -> dumpAllRules(ctx.getSource(), false))
                    .then(CommandManager.literal("short")
                        .executes(ctx -> dumpAllRules(ctx.getSource(), true))
                    )
                )
                .then(CommandManager.literal("io")
                    .then(CommandManager.literal("flush")
                        .executes(ctx -> {
                            ctx.getSource().getServer().save(false, true, true);
                            ctx.getSource().sendFeedback(() -> Text.literal("Flushed votes"), true);
                            return 1;
                        })
                    )
                    .then(CommandManager.literal("reload")
                        .executes(ctx -> {
                            ctx.getSource().sendFeedback(() -> Text.literal("Reloaded votes"), true);
                            return 1;
                        })
                    )
                )
        );
    }

    private static VoteManager getVoteManager(ServerCommandSource source) {
        return ((VoteServer) source.getServer()).getVoteManager();
    }

    public static Text createVoteHoverText(UUID id, VoteDefinition definition, ServerCommandSource source) {
        var ops = source.getRegistryManager().getOps(JsonOps.INSTANCE);
        String json = VoteDefinition.CODEC.encodeStart(ops, definition)
            .result()
            .map(JsonElement::toString)
            .orElse("Error encoding");
        
        return Text.literal(id.toString())
            .styled(style -> style.withUnderline(true)
                .withHoverEvent(new HoverEvent.ShowText(Text.literal(json)))
            );
    }
    
    private static int startPendingVote(ServerCommandSource source, Optional<RegistryEntry.Reference<Vote>> ruleEntry) {
        MinecraftServer server = source.getServer();
        VoteManager manager = getVoteManager(source);
        UUID id = UUID.randomUUID();
        
        VoteDefinition.Context context = new VoteDefinition.Context(
            source.getPlayer() != null ? source.getPlayer().getBlockPos() : BlockPos.ORIGIN,
            1.0f,
            List.of(),
            5,
            3,
            source.getWorld().getRandom(),
            false 
        );

        Optional<VoteDefinition> definition;

//        if (ruleEntry.isPresent()) {
//            Vote rule = ruleEntry.get().value();
//            Map<VoteOptionId, VoteDefinition.Option> options = new LinkedHashMap<>();
//            definition = VoteDefinition.proposeApply(id, server, context);
//        } else {
//            definition = VoteDefinition.proposeApply(id, server, context);
//        }
        
        try {
        	// TODO
        	
        	definition = VoteDefinition.proposeApply(id, server, context);
	
	        return definition.map(def -> {
	            manager.addVote(id, def);
	            source.sendFeedback(() -> Text.literal("Started vote for ").append(createVoteHoverText(id, def, source)), true);
	            return 1;
	        }).orElseGet(() -> {
	            source.sendError(Text.literal("Failed to start vote (proposals empty)"));
	            return 0;
	        });
        } catch(Exception e) {
        	e.printStackTrace();
        	return 0;
        }
    }

    private static int startRepealVote(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        VoteManager manager = getVoteManager(source);
        UUID id = UUID.randomUUID();

        VoteDefinition.Context context = new VoteDefinition.Context(
            BlockPos.ofFloored(source.getPosition()), 
            1.0f, 
            List.of(), 
            5, 
            1, 
            source.getWorld().getRandom(), 
            true
        );

        Optional<VoteDefinition> definition = VoteDefinition.proposeRevoke(id, server, context);

        return definition.map(def -> {
            manager.addVote(id, def);
            source.sendFeedback(() -> Text.literal("Starting repeal vote ").append(createVoteHoverText(id, def, source)), true);
            return 1;
        }).orElseGet(() -> {
            source.sendError(Text.literal("Failed to start repeal vote"));
            return 0;
        });
    }

    private static int finishAllVotes(ServerCommandSource source, boolean approve) {
//        List<UUID> ids = new ArrayList<>(getVoteManager(source).activeVotes.collect(Collectors.toList()));
    	List<UUID> ids = new ArrayList<>(getVoteManager(source).activeVotes.keySet());
        int count = 0;
        for (UUID id : ids) {
            count += finishVote(source, id, approve);
        }
        return count;
    }

    private static int finishVote(ServerCommandSource source, UUID id, boolean approve) {
        VoteResults state = getVoteManager(source).forceFinish(id);
        if (state != null) {
            String action = approve ? "Finished" : "Rejected";
            source.sendFeedback(() -> Text.literal(action + " vote for ").append(createVoteHoverText(id, state.vote(), source)), true);
            return 1;
        } else {
            source.sendError(Text.literal("Failed to finish vote " + id));
            return 0;
        }
    }

    private static int castVote(CommandContext<ServerCommandSource> context, int count) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        UUID id = UuidArgumentType.getUuid(context, "id");
        int optionIndex = IntegerArgumentType.getInteger(context, "option");
        var voter = source.getEntityOrThrow();

        if (!(voter instanceof net.minecraft.server.network.ServerPlayerEntity player)) {
            source.sendError(Text.literal("Only players can cast votes"));
            return 0;
        }

        VoteOptionId optionId = new VoteOptionId(id, optionIndex);
        VoteManager manager = getVoteManager(source);
        VoteManager.OptionHandle handle = manager.getOptionHandle(optionId);

        if (handle != null) {
            handle.submit(player, count);
            source.sendFeedback(() -> Text.literal(String.format("Added %d votes from %s to option %d of vote %s", 
                count, player.getDisplayName().getString(), optionIndex, id)), true);
            return 1;
        } else {
            source.sendError(Text.literal("Failed to add votes (Invalid vote or option)"));
            return 0;
        }
    }

    private static int showRandomRuleInfo(ServerCommandSource source, VoterAction category) {
        var randomRule = getVoteManager(source).getRandomRule(source.getServer(), source.getWorld().getRandom());
        return showRuleInfoLogic(source, category, randomRule);
    }

    private static int showRuleInfo(CommandContext<ServerCommandSource> context, VoterAction category) throws CommandSyntaxException {
        var rule = RegistryEntryReferenceArgumentType.getRegistryEntry(context, "rule", VOTE_RULE_REGISTRY_KEY);
        return showRuleInfoLogic(context.getSource(), category, rule);
    }

    @SuppressWarnings("rawtypes")
	private static int showRuleInfoLogic(ServerCommandSource source, VoterAction category, RegistryEntry.Reference<Vote> rule) {
        Stream<VoteRule> stream = (category == VoterAction.APPROVE) 
            ? rule.value().getRelevantOptions().map(r -> (VoteRule)r)
            : rule.value().getActiveOptions().map(r -> (VoteRule)r);

        return stream.findAny()
          .map(effect -> applyEffectToWorld(effect, category, source))
          .orElseGet(() -> {
              source.sendError(Text.literal("No applicable rule in " + rule.registryKey().getValue()));
              return 0;
          });
    }

    private static int applyRuleDirectly(CommandContext<ServerCommandSource> context, VoterAction category, NbtElement nbt) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        RegistryEntry.Reference<Vote> rule = RegistryEntryReferenceArgumentType.getRegistryEntry(context, "rule", VOTE_RULE_REGISTRY_KEY);
        
        return rule.value().getOptionCodec().parse(new Dynamic<>(NbtOps.INSTANCE, nbt)).resultOrPartial(
            error -> {
                LOGGER.warn("Failed to decode {}: {}", rule.registryKey().getValue(), error);
                source.sendError(Text.literal("Failed to decode rule"));
            }
        ).map(effect -> applyEffectToWorld((VoteRule<?>) effect, category, source)).orElse(0);
    }

    @SuppressWarnings("unchecked")
	private static int applyEffectToWorld(VoteRule<?> rule, VoterAction category, ServerCommandSource source) {
        Text feedback = rule.getDisplayText(null);
        rule.getActiveOptions().forEach(option -> {
            if (option instanceof VoteValue voteValue) {
                voteValue.apply(category);
            }
        });
        source.sendFeedback(() -> Text.literal("Applied ").append(feedback), true);
        return 1;
    }

    private static int repealEverything(ServerCommandSource source) {
        int count = source.getRegistryManager().getOrThrow(VOTE_RULE_REGISTRY_KEY).stream()
            .mapToInt(rule -> rule.applyDefault(false))
            .sum();
        source.sendFeedback(() -> Text.literal("Repealed " + count + " changes from all rules"), true);
        return count;
    }

    private static int repealAllFromRule(ServerCommandSource source, RegistryEntry.Reference<Vote> rule) {
        int count = rule.value().applyDefault(false);
        source.sendFeedback(() -> Text.literal("Repealed " + count + " changes for " + rule.registryKey().getValue()), true);
        return count;
    }

    private static int dumpAllRules(ServerCommandSource source, boolean isShort) {
        Registry<Vote> registry = source.getRegistryManager().getOrThrow(VOTE_RULE_REGISTRY_KEY);
        registry.streamEntries()
            .sorted(Comparator.comparing(ref -> ref.registryKey().getValue()))
            .forEach(ref -> {
                String key = ref.registryKey().getValue().toString();
                if (!isShort) {
                    LOGGER.info("Rule: {}", key);
                } else {
                    LOGGER.info("Rule (short): {}", key);
                }
            });
        source.sendFeedback(() -> Text.literal("Rules dumped to log!"), false);
        return 1;
    }
}
