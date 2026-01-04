package net.zhengzhengyiyi.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.zhengzhengyiyi.accessor.VoteClientPlayNetworkHandler;
import net.zhengzhengyiyi.gui.widget.VoteListWidget;
import net.zhengzhengyiyi.vote.VoteRegistries;

import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public class PendingVoteScreen extends Screen {
    public static final Identifier BACKGROUND_TEXTURE = Identifier.of("zhengzhengyiyi", "textures/gui/votes.png");
    public static final Text SHOW_RULES_TEXT = Text.translatable("vote.show_current_rules");
    public static final Text CURRENT_RULES_TEXT = Text.translatable("vote.current_rules");
    public static final Text TITLE = Text.translatable("gui.pending_votes.title");

    private VoteListWidget voteList;

    public PendingVoteScreen() {
        super(TITLE);
    }

    private int getBackgroundHeight() {
        return Math.max(36, this.height - 128 - 16);
    }

    private int getListBottom() {
        return 80 + getBackgroundHeight() - 8;
    }

    private int getBackgroundX() {
        return (this.width - 238) / 2;
    }

    @Override
    protected void init() {
        int x = getBackgroundX() + 3;
        int y = getListBottom() + 12;
        
    	this.addDrawableChild(ButtonWidget.builder(SHOW_RULES_TEXT, button -> {
            Stream<String> rulesStream = VoteRegistries.VOTE_RULE_TYPE.streamEntries()
//                    .sorted(Comparator.comparing(entry -> entry.registryKey().getValue()))
//                    .flatMap(entry -> entry.value().getAppliedRules())
                    .map(rule -> rule.getIdAsString());
            
            java.util.List<VoteLine> ruleLines = rulesStream.map(str -> {
                Text textContent = Text.literal(str);
                net.minecraft.text.OrderedText ordered = textContent.asOrderedText();
                
                return new VoteLine(ordered, 0L, textContent);
            }).collect(java.util.stream.Collectors.toList());
            
            this.client.setScreen(new ReportEvidenceScreen(CURRENT_RULES_TEXT, this, ruleLines));
            			    
        }).dimensions(x, y, 236, 20).build());

        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.close())
                .dimensions(x, y + 22, 236, 20).build());
//        this.voteList = new VoteListWidget(new ClientVoteManager(), this, this.client, this.width, this.height, 68, 0, getListBottom());
        this.voteList = new VoteListWidget(((VoteClientPlayNetworkHandler)this.client.player.networkHandler).getVoteManager(), this, this.client, this.width, this.height, 68, getListBottom(), 33);
        this.addSelectableChild(this.voteList);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        int x = getBackgroundX() + 3;
        
//        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, x, 64, 236, getBackgroundHeight() + 16);
        context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                BACKGROUND_TEXTURE,
                x,
                64,
                0.0F,
                0.0F,
                236,
                getBackgroundHeight() + 16,
                256,
                256
            );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
//        this.renderBackground(context, mouseX, mouseY, delta);
    	
        this.voteList.render(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (this.client.options.socialInteractionsKey.matchesKey(input)) {
            this.close();
            return true;
        }
        return super.keyPressed(input);
    }

    public void onVotesUpdated() {
        if (this.voteList.entryList.isEmpty()) {
            this.close();
        }
    }
}
