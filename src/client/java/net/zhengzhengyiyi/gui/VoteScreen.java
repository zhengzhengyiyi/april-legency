package net.zhengzhengyiyi.gui;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.input.AbstractInput;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.zhengzhengyiyi.accessor.VoteClientPlayNetworkHandler;
import net.zhengzhengyiyi.vote.ClientVoteManager;

import org.jetbrains.annotations.Nullable;

import com.mojang.datafixers.DataFixUtils;

import net.minecraft.util.collection.DefaultedList;

import java.util.*;
import java.util.Map.Entry;

import net.zhengzhengyiyi.vote.VoteDefinition;
import net.zhengzhengyiyi.vote.VoteDefinition.Option;
import net.zhengzhengyiyi.vote.VoteOptionId;

@Environment(EnvType.CLIENT)
public class VoteScreen extends HandledScreen<VoteScreen.VoteScreenHandler> {
    private static final Identifier BACKGROUND_TEXTURE = Identifier.of("zhengzhengyiyi", "textures/gui/voting.png");
    private static final Text VOTED_TEXT = Text.translatable("vote.voted").formatted(Formatting.GREEN);
    private static final Text NO_MORE_VOTES_TEXT = Text.translatable("vote.no_more_votes");

//    private final UUID voteId;
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

//    public record OptionData(int id, Text display, VoteDefinition.Option data) {}
    public record OptionData(VoteOptionId id, Text display, VoteDefinition.Option data) {}

    public VoteScreen(PlayerScreenHandler playerScreenHandler, PlayerInventory inventory, UUID voteId, ClientVoteManager manager, ClientVoteManager.VoteEntry entry) {
        super(new VoteScreenHandler(playerScreenHandler), inventory, Text.translatable("gui.voting.title"));
//        this.voteId = voteId;
        this.manager = manager;
        this.voteEntry = entry;
        this.playerUuid = inventory.player.getUuid();
        this.backgroundWidth = 231;
        this.backgroundHeight = 219;

        List<OptionData> list = new ArrayList<>();
        int index = 0;
        Map<VoteOptionId, Option> optionMap = entry.definition().options();
        for (Entry<VoteOptionId, Option> e : optionMap.entrySet()) {
            Text title = Text.translatable("vote.option_vote_title", entry.definition().metadata().getDisplayName(), index + 1, optionMap.size());
            list.add(new OptionData(e.getKey(), title, e.getValue()));
            index++;
        }
        this.options = List.copyOf(list);
    }

    @Override
    protected void init() {
        super.init();
        
        int ix = this.x;
        int iy = this.y;

        this.prevButton = addDrawableChild(new NavButton(ix + 9, iy + 4, Text.translatable("gui.voting.prev"), 0, new NavButton.PressAction() {
            @Override
            public void onPress(NavButton button) {
                VoteScreen.this.changeOption(-1);
            }
        }));

        this.nextButton = addDrawableChild(new NavButton(ix + 205, iy + 4, Text.translatable("gui.voting.next"), 32, new NavButton.PressAction() {
            @Override
            public void onPress(NavButton button) {
                VoteScreen.this.changeOption(1);
            }
        }));

        this.submitButton = addDrawableChild(new VoteButton(ix + 26, iy + 106, Text.translatable("gui.voting.do_it"), new VoteButton.PressAction() {
            @Override
            public void onPress(VoteButton button) {
                VoteScreen.this.handleVoteSubmit();
            }
        }));

        this.updateOptionView();
    }

    private void changeOption(int delta) {
        int nextIndex = this.currentOptionIndex + delta;
        if (nextIndex >= 0 && nextIndex < this.options.size()) {
            this.currentOptionIndex = nextIndex;
            this.updateOptionView();
        }
    }

    private void handleVoteSubmit() {
//        if (this.selectedOption != null) {
//            this.manager.registerCallback((id, result) -> VoteScreen.this.statusText = result.orElse(VOTED_TEXT));
//        }
    	if (this.selectedOption != null) {
            @SuppressWarnings("unused")
			int callbackId = ((VoteClientPlayNetworkHandler)client.getNetworkHandler()).method_51006(this.selectedOption.id(), (id, response) -> {
                this.statusText = (Text) DataFixUtils.orElse(response, VOTED_TEXT);
            });
        }
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
        context.drawTexture(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight, this.width, this.height);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        if (this.descriptionText != null) {
//            int textHeight = this.descriptionText.getLineCount() * 9;
//            this.descriptionText.drawCenterWithShadow(context, this.x + 115, this.y + 27 + (68 - textHeight) / 2);
        }
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
        public void onPress(AbstractInput input) {
            this.action.onPress(this);
        }

        @Override
        protected void drawIcon(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
            int u = 231;
            int v = this.vOffset;
            if (!this.active) v += this.width;
            context.drawTexture(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, this.getX(), this.getY(), u, v, this.width, this.height, this.width, this.height);
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
        public void onPress(AbstractInput input) {
            this.action.onPress(this);
        }

        @Override
        protected void drawIcon(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
            int v = 219;
            int u = this.active ? (this.isSelected() ? 89 : 0) : 0;
            context.drawTexture(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, this.getX(), this.getY(), u, v, this.width, this.height, this.width, this.height);
            context.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, this.active ? 16777215 : 10526880);
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {
            this.appendDefaultNarrations(builder);
        }

        public interface PressAction { void onPress(VoteButton button); }
    }

    @Environment(EnvType.CLIENT)
    public static class VoteScreenHandler extends ScreenHandler {
        private final ScreenHandler parentHandler;
        public final DefaultedList<ItemStack> itemList = DefaultedList.of();

        public VoteScreenHandler(PlayerScreenHandler playerScreenHandler) {
            super(null, 0);
            this.parentHandler = playerScreenHandler;

            for (int i = 0; i < playerScreenHandler.slots.size(); i++) {
                int posX, posY;

                if ((i >= 5 && i < 9) || (i >= 0 && i < 5) || i == 45) {
                    posX = -2000;
                    posY = -2000;
                } else {
                    int index = i - 9;
                    int column = index % 9;
                    int row = index / 9;

                    posX = 36 + column * 18;

                    if (i >= 36) {
                        posY = 195;
                    } else {
                        posY = 137 + row * 18;
                    }
                }

                Slot originalSlot = playerScreenHandler.slots.get(i);
                this.slots.add(new CreativeSlot(originalSlot, i, posX, posY));
            }
        }

        @Override
        public boolean canUse(PlayerEntity player) {
            return true;
        }

        @Override
        public ItemStack quickMove(PlayerEntity player, int index) {
            if (index >= this.slots.size() - 9 && index < this.slots.size()) {
                Slot slot = this.slots.get(index);
                if (slot != null && slot.hasStack()) {
                    slot.setStack(ItemStack.EMPTY);
                }
            }
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack getCursorStack() {
            return this.parentHandler.getCursorStack();
        }

        @Override
        public void setCursorStack(ItemStack stack) {
            this.parentHandler.setCursorStack(stack);
        }
    }
    
    @Environment(EnvType.CLIENT)
    static class CreativeSlot extends Slot {
        final Slot slot;

        public CreativeSlot(Slot slot, int invSlot, int x, int y) {
            super(slot.inventory, invSlot, x, y);
            this.slot = slot;
        }

        @Override
        public void onTakeItem(PlayerEntity player, ItemStack stack) {
            this.slot.onTakeItem(player, stack);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return this.slot.canInsert(stack);
        }

        @Override
        public ItemStack getStack() {
            return this.slot.getStack();
        }

        @Override
        public boolean hasStack() {
            return this.slot.hasStack();
        }

        @Override
        public void setStack(ItemStack stack, ItemStack previousStack) {
            this.slot.setStack(stack, previousStack);
        }

        @Override
        public void setStackNoCallbacks(ItemStack stack) {
            this.slot.setStackNoCallbacks(stack);
        }

        @Override
        public void markDirty() {
            this.slot.markDirty();
        }

        @Override
        public int getMaxItemCount() {
            return this.slot.getMaxItemCount();
        }

        @Override
        public int getMaxItemCount(ItemStack stack) {
            return this.slot.getMaxItemCount(stack);
        }

        @Override
        @Nullable
        public Identifier getBackgroundSprite() {
            return this.slot.getBackgroundSprite();
        }

        @Override
        public ItemStack takeStack(int amount) {
            return this.slot.takeStack(amount);
        }

        @Override
        public boolean isEnabled() {
            return this.slot.isEnabled();
        }

        @Override
        public boolean canTakeItems(PlayerEntity playerEntity) {
            return this.slot.canTakeItems(playerEntity);
        }
    }

    @Environment(EnvType.CLIENT)
    static class LockableSlot extends Slot {
        public LockableSlot(Inventory inventory, int i, int j, int k) {
            super(inventory, i, j, k);
        }

        @Override
        public boolean canTakeItems(PlayerEntity playerEntity) {
            ItemStack itemStack = this.getStack();
            return super.canTakeItems(playerEntity) && !itemStack.isEmpty()
                    ? itemStack.isItemEnabled(playerEntity.getEntityWorld().getEnabledFeatures()) 
                      && !itemStack.contains(DataComponentTypes.CREATIVE_SLOT_LOCK)
                    : itemStack.isEmpty();
        }
    }
}
