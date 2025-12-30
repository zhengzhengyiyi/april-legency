package net.zhengzhengyiyi.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.DataFixUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.zhengzhengyiyi.vote.ClientVoteManager;

import org.jetbrains.annotations.Nullable;

import java.util.*;

@Environment(EnvType.CLIENT)
public class VoteScreen extends HandledScreen<VoteScreen.VoteScreenHandler> {
    private static final Identifier BACKGROUND_TEXTURE = Identifier.of("minecraft", "textures/gui/voting.png");
    private static final Text VOTED_TEXT = Text.translatable("vote.voted").formatted(Formatting.GREEN);
    private static final Text NO_MORE_VOTES_TEXT = Text.translatable("vote.no_more_votes");

    private final UUID voteId;
    private final ClientVoteManager manager;
    private ClientVoteManager.VoteEntry voteEntry;
    private final List<OptionData> options;
    private final UUID playerUuid;
    private int currentOptionIndex;

    private Text currentOptionTitle = Text.empty();
    private Text statusText = Text.empty();
    private MultilineText descriptionText = MultilineText.EMPTY;

    @Nullable private OptionData selectedOption;
    private NavButton prevButton;
    private NavButton nextButton;
    private VoteButton submitButton;

    public record OptionData(int id, Text display, VoteDefinition.Option data) {}

    public VoteScreen(PlayerInventory inventory, UUID voteId, ClientVoteManager manager, ClientVoteManager.VoteEntry entry) {
        super(new VoteScreenHandler(inventory.player.playerScreenHandler), inventory, Text.translatable("gui.voting.title"));
        this.voteId = voteId;
        this.manager = manager;
        this.voteEntry = entry;
        this.playerUuid = inventory.player.getUuid();
        this.backgroundWidth = 231;
        this.backgroundHeight = 219;

        List<OptionData> list = new ArrayList<>();
        int index = 0;
        for (Map.Entry<Integer, VoteDefinition.Option> e : entry.definition().getOptions().entrySet()) {
            Text title = Text.translatable("vote.option_vote_title", entry.definition().getName(), index + 1, entry.definition().getOptions().size());
            list.add(new OptionData(e.getKey(), title, e.getValue()));
            index++;
        }
        this.options = List.copyOf(list);
    }

    @Override
    protected void init() {
        super.init();
        this.prevButton = addDrawableChild(new NavButton(this.x + 9, this.y + 4, Text.translatable("gui.voting.prev"), 0, (button) -> {
            if (this.currentOptionIndex > 0) {
                this.currentOptionIndex--;
                this.updateOptionView();
            }
        }));
        this.nextButton = addDrawableChild(new NavButton(this.x + 205, this.y + 4, Text.translatable("gui.voting.next"), 32, (button) -> {
            if (this.currentOptionIndex < this.options.size() - 1) {
                this.currentOptionIndex++;
                this.updateOptionView();
            }
        }));
        this.submitButton = addDrawableChild(new VoteButton(this.x + 26, this.y + 106, Text.translatable("gui.voting.do_it"), (button) -> {
            if (this.selectedOption != null) {
                int requestId = this.manager.registerCallback((id, result) -> {
                    this.statusText = result.orElse(VOTED_TEXT);
                });
            }
        }));
        this.updateOptionView();
    }

    private void updateOptionView() {
        this.selectedOption = this.options.get(this.currentOptionIndex);
        this.prevButton.active = (this.currentOptionIndex > 0);
        this.nextButton.active = (this.currentOptionIndex < this.options.size() - 1);

        boolean canVote = this.voteEntry.canVote(this.playerUuid);
        this.statusText = canVote ? Text.empty() : NO_MORE_VOTES_TEXT;
        this.currentOptionTitle = this.selectedOption.display();
        this.descriptionText = MultilineText.create(this.textRenderer, this.selectedOption.data().displayName(), 205);
        this.submitButton.active = canVote;
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        int titleWidth = this.textRenderer.getWidth(this.currentOptionTitle);
        context.drawTextWithShadow(this.textRenderer, this.currentOptionTitle, 26 + (180 - titleWidth) / 2, 8, -1);
        context.drawTextWithShadow(this.textRenderer, this.statusText, 118, 110, -1);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        int textHeight = this.descriptionText.getLineCount() * 9;
//        this.descriptionText.drawCenterWithShadow(context, this.x + 115, this.y + 27 + (68 - textHeight) / 2);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    public boolean shouldPause() {
        return true;
    }

    @Environment(EnvType.CLIENT)
    private static class NavButton extends PressableWidget {
        private final int vOffset;
        private final PressAction action;

        public NavButton(int x, int y, Text message, int vOffset, PressAction action) {
            super(x, y, 16, 16, message);
            this.vOffset = vOffset;
            this.action = action;
        }

        @Override
        public void onPress() {
            this.action.onPress(this);
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            int u = 231;
            int v = this.vOffset;
            if (!this.active) v += this.width;
            else if (this.isSelected()) v += 0; 
            context.drawTexture(BACKGROUND_TEXTURE, this.getX(), this.getY(), u, v, this.width, this.height);
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {
            this.appendDefaultNarrations(builder);
        }

        public interface PressAction { void onPress(NavButton button); }
    }

    @Environment(EnvType.CLIENT)
    private static class VoteButton extends PressableWidget {
        private final PressAction action;

        public VoteButton(int x, int y, Text message, PressAction action) {
            super(x, y, 89, 22, message);
            this.action = action;
        }

        @Override
        public void onPress() {
            this.action.onPress(this);
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            int v = 219;
            int u = this.active ? (this.isSelected() ? 89 : 0) : 0;
            context.drawTexture(BACKGROUND_TEXTURE, this.getX(), this.getY(), u, v, this.width, this.height);
            context.drawCenteredTextWithShadow(RenderSystem.getShaderTextRenderer(), this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, this.active ? 16777215 : 10526880);
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {
            this.appendDefaultNarrations(builder);
        }

        public interface PressAction { void onPress(VoteButton button); }
    }

    @Environment(EnvType.CLIENT)
    public static class VoteScreenHandler extends ScreenHandler {
        private final ScreenHandler playerHandler;

        public VoteScreenHandler(PlayerScreenHandler playerHandler) {
            super(null, 0);
            this.playerHandler = playerHandler;
        }

        @Override
        public ItemStack quickMove(Object player, int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean canUse(Object player) {
            return true;
        }

        @Override
        public ItemStack getCursorStack() {
            return this.playerHandler.getCursorStack();
        }

        @Override
        public void setCursorStack(ItemStack stack) {
            this.playerHandler.setCursorStack(stack);
        }
    }
}
