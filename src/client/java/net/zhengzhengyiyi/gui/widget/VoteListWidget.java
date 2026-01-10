package net.zhengzhengyiyi.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;
import net.zhengzhengyiyi.gui.PendingVoteScreen;
import net.zhengzhengyiyi.vote.ClientVoteManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Environment(EnvType.CLIENT)
public class VoteListWidget extends AlwaysSelectedEntryListWidget<VoteEntry> {
    private static final Tooltip ALREADY_VOTED_TOOLTIP = Tooltip.of(Text.translatable("vote.no_more_votes"));
    public static final int PADDING = 4;
    
    public List<VoteEntry> entryList;

    public VoteListWidget(ClientVoteManager manager, PendingVoteScreen screen, MinecraftClient client, int width, int height, int top, int bottom) {
        super(client, width, height, top, 34);

//        UUID playerUuid = client.player.getUuid();
        entryList = new ArrayList<>();

        manager.activeVotes.forEach((voteId, entry) -> {
        	boolean hasVoted = false;
            entryList.add(new VoteEntry(
                client,
                manager,
                hasVoted,
                this.getRowWidth(), 
                voteId, 
                screen, 
                entry.definition(),
                hasVoted ? null : ALREADY_VOTED_TOOLTIP
            ));
        });

        entryList.sort(VoteEntry.COMPARATOR);
        entryList.forEach(this::addEntry);
    }
    
    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
    	super.renderWidget(context, mouseX, mouseY, deltaTicks);
    }
    
    public int getRowWidth() {
    	return 220;
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
    
    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (!this.isMouseOver(click.x(), click.y())) return false;
        return super.mouseClicked(click, doubled);
    }
}
