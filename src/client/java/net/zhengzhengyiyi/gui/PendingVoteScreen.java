package net.zhengzhengyiyi.gui;

import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.zhengzhengyiyi.accessor.VoteClientPlayNetworkHandler;
import net.zhengzhengyiyi.gui.widget.VoteListWidget;
import net.zhengzhengyiyi.vote.ClientVoteManager;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;

@Environment(EnvType.CLIENT)
public class PendingVoteScreen extends Screen {
    protected static final Identifier VOTES_TEXTURE = Identifier.of("zhengzhengyiyi","textures/gui/votes.png");
    @SuppressWarnings("unused")
	private static final int field_44337 = 8;
    @SuppressWarnings("unused")
	private static final int TEXTURE_WIDTH = 236;
    @SuppressWarnings("unused")
	private static final int field_44339 = 64;
    public static final int field_44336 = 72;
    @SuppressWarnings("unused")
	private static final int WINDOW_WIDTH = 238;
    @SuppressWarnings("unused")
	private static final int field_44341 = 36;
    private static final Text SHOW_RULES_TEXT = Text.translatable("vote.show_current_rules");
    private static final Text CURRENT_RULES_TEXT = Text.translatable("vote.current_rules");

    private VoteListWidget voteListWidget;

    public PendingVoteScreen() {
        super(Text.translatable("gui.pending_votes.title"));
    }

    private int getListHeight() {
        return Math.max(36, this.height - 128 - 16);
    }

    private int getButtonsY() {
        return 80 + getListHeight() - 8;
    }

    private int getBackgroundX() {
        return (this.width - 238) / 2;
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected void init() {
//        this.voteListWidget = new VoteListWidget(((VoteClientPlayNetworkHandler)this.client.getNetworkHandler()).getVoteManager(), this, this.client, this.width, this.height, 68, getButtonsY(), 33);
    	this.voteListWidget = new VoteListWidget(((VoteClientPlayNetworkHandler)this.client.getNetworkHandler()).getVoteManager(), this, this.client, this.width, 200, 20, getButtonsY());

        int i = getBackgroundX() + 3;
        int j = getButtonsY() + 8 + 4;

        addDrawableChild(ButtonWidget.builder(SHOW_RULES_TEXT, buttonWidget -> {
        	ClientVoteManager voteManager = ((VoteClientPlayNetworkHandler)this.client.getNetworkHandler()).getVoteManager();
        	Stream<Text> stream = voteManager.activeVotes.values().stream()
                    .flatMap(entry -> {
                        Text path = Text.of(entry.title().getString());
//                        Text desc = ((Vote) regEntry.value()).getDescription(VoterAction.REPEAL);
                        return Stream.<Text>of(path, entry.definition().metadata().getDisplayName());
                    })
                    .map(text -> (Text)text);
            
            this.client.setScreen(new ReportEvidenceScreen(CURRENT_RULES_TEXT, this, VoteLine.wrap(this.client.textRenderer, stream, 320).toList()));
        }).dimensions(i, j, 236, 20).build());

        addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, buttonWidget -> close()).dimensions(i, j + 20 + 2, 236, 20).build());
        
        addSelectableChild(this.voteListWidget);

//        if (!this.client.options.skipVoteTutorial) {
//            this.client.options.skipVoteTutorial = true;
//            this.client.options.write();
//        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.renderBackground(context, mouseX, mouseY, deltaTicks);
//        RenderSystem.setShaderTexture(0, VOTES_TEXTURE);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    	int i = getBackgroundX() + 3;
    	
    	context.enableScissor(60, 20, width-60, height);
    	
    	context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, PendingVoteScreen.MENU_BACKGROUND_TEXTURE, i, 64, 236, getListHeight() + 16, 8, 236, 34, 1, 1);
//        renderBackground(context, mouseX, mouseY, delta);
        this.voteListWidget.render(context, mouseX, mouseY, delta);
        context.disableScissor();
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (this.client.options.socialInteractionsKey.matchesKey(input)) {
            this.client.setScreen(null);
            return true;
        }
        return super.keyPressed(input);
    }

    public void onVotesUpdated() {
        if (this.voteListWidget.entryList.isEmpty()) {
            close();
        }
    }
}
