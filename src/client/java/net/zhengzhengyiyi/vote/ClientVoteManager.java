package net.zhengzhengyiyi.vote;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class ClientVoteManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final VoteSeparator SEPARATOR = new VoteSeparator(46, Style.EMPTY.withColor(Formatting.WHITE));

    public final Map<UUID, VoteEntry> activeVotes = new HashMap<>();
    private final Int2ObjectMap<ResponseHandler> pendingRequests = new Int2ObjectOpenHashMap<>();
    private int requestCounter;

    @Environment(EnvType.CLIENT)
    private record VoteSeparator(int codepoint, Style style) {}

    private static StringVisitable createVisitableTitle(List<VoteSeparator> separators) {
        return new StringVisitable() {
            @Override
            public <T> Optional<T> visit(Visitor<T> visitor) {
                for (VoteSeparator separator : separators) {
                    Optional<T> optional = visitor.accept(Character.toString(separator.codepoint()));
                    if (optional.isPresent()) return optional;
                }
                return Optional.empty();
            }

            @Override
            public <T> Optional<T> visit(StyledVisitor<T> styledVisitor, Style style) {
                for (VoteSeparator separator : separators) {
                    Optional<T> optional = styledVisitor.accept(style.withParent(separator.style()), Character.toString(separator.codepoint()));
                    if (optional.isPresent()) return optional;
                }
                return Optional.empty();
            }
        };
    }

    public void addVote(UUID uuid, VoteDefinition definition) {
        Text titleText = definition.metadata().getDisplayName();
        List<List<VoteSeparator>> optionSeparators = definition.options().values().stream()
                .map(option -> {
                    List<VoteSeparator> list = new ArrayList<>();
                    Text.translatable("vote.option_display", titleText, option.displayName()).asOrderedText().accept((index, style, codepoint) -> {
                        list.add(new VoteSeparator(codepoint, style));
                        return true;
                    });
                    return list;
                }).collect(Collectors.toList());

        int maxLength = getMaxListSize(optionSeparators.stream().mapToInt(List::size).toArray());
        StringVisitable visitableTitle;

        if (maxLength == 0) {
            visitableTitle = StringVisitable.EMPTY;
        } else {
            List<VoteSeparator> base = new ArrayList<>(optionSeparators.get(0));
            int end = maxLength - 1;
            while (end >= 0 && Character.isSpaceChar(base.get(end).codepoint())) {
                end--;
            }
            List<VoteSeparator> trimmed = new ArrayList<>(base.subList(0, end + 1));
            trimmed.add(SEPARATOR);
            trimmed.add(SEPARATOR);
            trimmed.add(SEPARATOR);
            visitableTitle = createVisitableTitle(trimmed);
        }

        this.activeVotes.put(uuid, new VoteEntry(definition, visitableTitle));
    }

    public void removeVote(UUID uuid) {
        this.activeVotes.remove(uuid);
    }

    public boolean hasVotes() {
        return !this.activeVotes.isEmpty();
    }

    public void updateVoterData(UUID voteId, int optionIndex, Map<UUID, Integer> voters) {
        VoteEntry entry = this.activeVotes.get(voteId);
        if (entry != null) {
            entry.voterMap().computeIfAbsent(optionIndex, k -> new HashMap<>()).putAll(voters);
        }
    }

    public void forEachVote(BiConsumer<UUID, VoteEntry> action) {
        this.activeVotes.forEach(action);
    }

    @Nullable
    public VoteEntry getVote(UUID uuid) {
        return this.activeVotes.get(uuid);
    }

    @Environment(EnvType.CLIENT)
    public record VoteEntry(VoteDefinition definition, StringVisitable title, Map<Integer, Map<UUID, Integer>> voterMap) {
        public VoteEntry(VoteDefinition definition, StringVisitable title) {
            this(definition, title, new Int2ObjectOpenHashMap<>());
        }

        public long getRemainingTicks(long worldTime) {
            long elapsed = worldTime - this.definition.metadata().getStartTime();
            return Math.max(0L, this.definition.metadata().getDuration() - elapsed);
        }

        public boolean canVote(UUID playerUuid) {
//            int totalVotes = this.voterMap.values().stream()
//                    .mapToInt(m -> m.getOrDefault(playerUuid, 0))
//                    .sum();
//            return totalVotes < this.definition.getMaxVotesPerPlayer();
        	return true;
        }
    }

    @Environment(EnvType.CLIENT)
    public record VoteStatus(int count, OptionalInt limit) {
        public boolean isWithinLimit() {
            return this.limit.isEmpty() || this.count < this.limit.getAsInt();
        }
    }

    public int registerCallback(ResponseHandler handler) {
        int id = this.requestCounter++;
        this.pendingRequests.put(id, handler);
        return id;
    }

    public void handleResponse(int id, Optional<Text> result) {
        ResponseHandler handler = this.pendingRequests.remove(id);
        if (handler != null) {
            handler.run(id, result);
        } else {
            LOGGER.warn("Received response for unknown vote request: {}", id);
        }
    }

    @FunctionalInterface
    @Environment(EnvType.CLIENT)
    public interface ResponseHandler {
        void run(int id, Optional<Text> result);
    }
    
    public static int getMaxListSize(int[] array) {
        if (array == null || array.length == 0) {
            return 0;
        }
        int max = array[0];
        for (int i : array) {
            if (i > max) {
                max = i;
            }
        }
        return max;
    }
}
