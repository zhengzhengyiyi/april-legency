package net.zhengzhengyiyi.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.Alignment;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;
import net.zhengzhengyiyi.gui.PendingVoteScreen;
import net.zhengzhengyiyi.vote.ClientVoteManager;
import net.zhengzhengyiyi.vote.VoteDefinition;
import net.zhengzhengyiyi.vote.VoteManager;
import net.zhengzhengyiyi.vote.VoteScreen;

import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class VoteEntry extends AlwaysSelectedEntryListWidget.Entry<VoteEntry> {
    public static final Comparator<VoteEntry> BY_ID = Comparator.comparing(entry -> entry.voteId);
    public static final Comparator<VoteEntry> BY_TIME = Comparator.comparing(VoteEntry::getRemainingTicks);
    public static final Comparator<VoteEntry> BY_NAME = Comparator.comparing(entry -> entry.ruleName);
    public static final Comparator<VoteEntry> BY_STATUS = Comparator.comparing(entry -> entry.hasVoted ? 0 : 1);
    
    public static final Comparator<VoteEntry> COMPARATOR = BY_STATUS.thenComparing(BY_NAME).thenComparing(BY_ID);

    private final MinecraftClient client;
    private final PendingVoteScreen parentScreen;
    private final UUID voteId;
    private final ClientVoteManager manager;
    @Nullable
    private VoteDefinition definition;
    private final boolean hasVoted;
    @Nullable
    private final Tooltip tooltip;
    private final MultilineText multilineText;
    private final String ruleName;

    public VoteEntry(MinecraftClient client, ClientVoteManager manager, boolean hasVoted, int width, UUID voteId, PendingVoteScreen screen, VoteDefinition definition, @Nullable Tooltip tooltip) {
        this.client = client;
        this.manager = manager;
        this.hasVoted = hasVoted;
        this.voteId = voteId;
        this.parentScreen = screen;
        this.definition = definition;
        this.tooltip = tooltip;
        this.ruleName = definition.metadata().getDisplayName().getString();

        String timeString = this.getTimeDisplay();
        int timeWidth = client.textRenderer.getWidth(timeString);
        int maxTextWidth = width - timeWidth - 8;

        this.multilineText = MultilineText.create(client.textRenderer, definition.metadata().getDisplayName(), maxTextWidth);
    }

    @Override
    public void render(DrawContext context, int x, int y, boolean hovered, float deltaTicks) {
        int vOffset;
        if (!this.hasVoted || this.definition == null) {
            vOffset = 66;
        } else if (this.isFocused() || hovered) {
            vOffset = 33;
        } else {
            vOffset = 0;
        }

        context.drawTexture(RenderPipelines.GUI_TEXTURED, PendingVoteScreen.BACKGROUND_TEXTURE, x, y, 0.0F, 36.0F + vOffset, 220, 33, 256, 256);
        
        int textX = x + 4;
        int textY = y + 4;

        String timeString = this.getTimeDisplay();
        int timeWidth = this.client.textRenderer.getWidth(timeString);
        context.drawTextWithShadow(this.client.textRenderer, timeString, (textX + this.getWidth() - timeWidth - 8), textY, 0xFFFFFF);

//        if (hovered && this.tooltip != null) {
//            this.parentScreen.setTooltip(this.tooltip);
//        }
    }

    private String getTimeDisplay() {
        return StringHelper.formatTicks((int) this.getRemainingTicks(), this.client.world.getTickManager().getTickRate());
    }

    private long getRemainingTicks() {
        if (this.definition == null) return 0L;
        long currentTime = (this.client.world != null) ? this.client.world.getTime() : 0L;
        return Math.max(0L, this.definition.metadata().getEndTime() - currentTime);
    }

    @Override
    public Text getNarration() {
        return Text.literal("Vote entry for " + this.ruleName);
    }

    public boolean isActive() {
        this.definition = this.manager.getVote(this.voteId).definition();
        return this.definition != null;
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (click.button() == 0) {
            if (this.definition != null) {
                this.client.setScreen(new VoteScreen(this.voteId, this.manager, this.definition));
            }
            return true;
        }
        return super.mouseClicked(click, doubled);
    }
}
