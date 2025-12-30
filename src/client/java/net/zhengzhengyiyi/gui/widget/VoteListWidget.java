package net.zhengzhengyiyi.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;
import net.zhengzhengyiyi.gui.PendingVoteScreen;
import net.zhengzhengyiyi.vote.ClientVoteManager;
import net.zhengzhengyiyi.vote.VoteManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class VoteListWidget extends AlwaysSelectedEntryListWidget<VoteEntry> {
    private static final Tooltip ALREADY_VOTED_TOOLTIP = Tooltip.of(Text.translatable("vote.no_more_votes"));
    public static final int PADDING = 4;

    public VoteListWidget(ClientVoteManager manager, PendingVoteScreen screen, MinecraftClient client, int width, int height, int top, int bottom, int entryHeight) {
        super(client, width, height, top, bottom);
//        this.setRenderSelection(false);
//        this.setRenderBackground(false);

        UUID playerUuid = client.player.getUuid();
        List<VoteEntry> entryList = new ArrayList<>();

        manager.activeVotes.forEach((voteId, definition) -> {
            boolean hasVoted = definition.hasVoted(playerUuid);
            entryList.add(new VoteEntry(
                client, 
                manager, 
                hasVoted, 
                this.getRowWidth(), 
                voteId, 
                screen, 
                definition, 
                hasVoted ? null : ALREADY_VOTED_TOOLTIP
            ));
        });

        entryList.sort(VoteEntry.COMPARATOR);
        entryList.forEach(this::addEntry);
    }

    public boolean updateVotes() {
        Iterator<VoteEntry> iterator = this.children().iterator();
        while (iterator.hasNext()) {
            VoteEntry entry = iterator.next();
            if (!entry.isActive()) {
                iterator.remove();
            }
        }
        return !this.children().isEmpty();
    }

    @Override
    protected void enableScissor(DrawContext context) {
        context.enableScissor(this.getX(), this.getY() + PADDING, this.getRight(), this.getBottom());
    }
}
