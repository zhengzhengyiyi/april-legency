package net.zhengzhengyiyi.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;
import net.zhengzhengyiyi.vote.ClientVoteManager;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class PendingVoteEntry extends AlwaysSelectedEntryListWidget.Entry<PendingVoteEntry> {
    private static final Comparator<PendingVoteEntry> BY_UUID = Comparator.comparing(entry -> entry.voteUuid);
    public static final Comparator<PendingVoteEntry> BY_TIME = Comparator.comparing(PendingVoteEntry::getRemainingTicks);
    private static final Comparator<PendingVoteEntry> BY_TITLE = Comparator.comparing(entry -> entry.titleString);
    private static final Comparator<PendingVoteEntry> BY_AVAILABILITY = Comparator.comparing(entry -> entry.available ? 0 : 1);

    public static final Comparator<PendingVoteEntry> DEFAULT_COMPARATOR = BY_AVAILABILITY
            .thenComparing(BY_TITLE)
            .thenComparing(BY_UUID);

    private final MinecraftClient client;
    private final PendingVoteScreen parentScreen;
    private final UUID voteUuid;
    private final ClientVoteManager voteManager;
    @Nullable
    private ClientVoteManager.VoteEntry voteEntry;
    private final boolean available;
    @Nullable
    private final Tooltip tooltip;
    public final MultilineText multilineTitle;
    private final String titleString;

    public PendingVoteEntry(MinecraftClient client, PendingVoteScreen parentScreen, boolean available, int width, UUID voteUuid, ClientVoteManager voteManager, ClientVoteManager.VoteEntry voteEntry, @Nullable Tooltip tooltip) {
        this.client = client;
        this.parentScreen = parentScreen;
        this.available = available;
        this.voteUuid = voteUuid;
        this.voteManager = voteManager;
        this.voteEntry = voteEntry;
        this.tooltip = tooltip;
        
        this.titleString = voteEntry.title().getString();
        String timeStr = StringHelper.formatTicks((int)getRemainingTicks(), client.getRenderTickCounter().getDynamicDeltaTicks());
        int timeWidth = client.textRenderer.getWidth(timeStr);
        int availableWidth = width - timeWidth - 8;
        
        this.multilineTitle = MultilineText.create(client.textRenderer, (Text) voteEntry.title(), availableWidth);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        int vOffset;
        if (!this.available || this.voteEntry == null) {
            vOffset = 66;
        } else if (this.parentScreen.getFocused() == this || hovered) {
            vOffset = 33;
        } else {
            vOffset = 0;
        }

        context.drawTexture(RenderPipelines.GUI_TEXTURED, PendingVoteScreen.MENU_BACKGROUND_TEXTURE, getX(), getY(), 0, 36 + vOffset, 220, 33, 256, 256);

//        int textX = getX() + 4;
        int textY = getY() + 4;

//        this.multilineTitle.drawWithShadow(context, textX, textY, 9, 0xFFFFFF);

        String timeStr = StringHelper.formatTicks((int)getRemainingTicks(), client.getRenderTickCounter().getDynamicDeltaTicks());
        int timeWidth = this.client.textRenderer.getWidth(timeStr);
        context.drawTextWithShadow(this.client.textRenderer, timeStr, getX() + getWidth() - timeWidth - 8, textY, 0xFFFFFF);

        if (hovered && this.tooltip != null) {
            this.parentScreen.renderWithTooltip(context, mouseX, mouseY, deltaTicks);
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (click.button() == 0) {
            if (this.voteEntry != null) {
                this.client.setScreen(new VoteScreen(this.client.player.playerScreenHandler, this.client.player.getInventory(), this.voteUuid, this.voteManager, this.voteEntry));
            }
            return true;
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public Text getNarration() {
        return Text.literal(this.titleString);
    }

    public boolean updateData() {
        this.voteEntry = this.voteManager.getVote(this.voteUuid);
        return this.voteEntry != null;
    }

    private long getRemainingTicks() {
        if (this.voteEntry == null) {
            return 0L;
        }
        long worldTime = (this.client.world != null) ? this.client.world.getTime() : 0L;
        return this.voteEntry.getRemainingTicks(worldTime);
    }
}
