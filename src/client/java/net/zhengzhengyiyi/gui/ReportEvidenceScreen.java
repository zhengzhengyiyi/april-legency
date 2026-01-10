package net.zhengzhengyiyi.gui;

import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ReportEvidenceScreen extends Screen {
    private static final int HEADER_HEIGHT = 40;
    public static final int ROW_WIDTH = 320;
//    private static final int MARGIN = 8;
//    private static final int BUTTON_WIDTH = 150;
//    private static final int BUTTON_HEIGHT = 20;
    private static final int ITEM_HEIGHT = 20;
    private static final MutableText COPY_TEXT = Text.translatable("chat.copy");

    private final Screen parent;
    private final List<VoteLine> voteLines;
    private EvidenceListWidget listWidget;

    public ReportEvidenceScreen(Text title, Screen parent, List<VoteLine> voteLines) {
        super(title);
        this.parent = parent;
        this.voteLines = voteLines;
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Override
    protected void init() {
    	this.listWidget = new EvidenceListWidget(this.client, this.width, this.height, HEADER_HEIGHT, ITEM_HEIGHT, this.voteLines);
//        this.listWidget.setRenderBackground(false);

        int leftButtonX = this.width / 2 - 150 - 5;
        int rightButtonX = this.width / 2 + 5;
        int buttonY = this.height - 20 - 8;

        addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> close())
                .dimensions(rightButtonX, buttonY, 150, 20).build());

        addDrawableChild(ButtonWidget.builder(COPY_TEXT, button -> {
        	String fullText = this.voteLines.stream()
        	        .<String>map(line -> ((VoteLine)line).contents().toString())
        	        .collect(Collectors.joining("\n"));
//            String fullText = this.voteLines.stream()
//                    .map(line -> ((VoteLine)line).contents().getString())
//                    .collect(Collectors.joining("\n"));
            this.client.keyboard.setClipboard(fullText);
        }).dimensions(leftButtonX, buttonY, 150, 20).build());
        
        addSelectableChild(this.listWidget);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.listWidget.render(context, mouseX, mouseY, delta);
//        drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 16, 16777215);
        super.render(context, mouseX, mouseY, delta);
    }

    @Environment(EnvType.CLIENT)
    public static class EvidenceListWidget extends AlwaysSelectedEntryListWidget<EvidenceListWidget.EvidenceEntry> {
//        public EvidenceListWidget(MinecraftClient client, List<VoteLine> lines) {
    	public EvidenceListWidget(MinecraftClient client, int width, int height, int top, int itemHeight, List<VoteLine> lines) {
//            super(client, ReportEvidenceScreen.this.width, ReportEvidenceScreen.this.height, 40, ReportEvidenceScreen.this.height - 40, 18);
        	super(client, width, height, top, itemHeight);
            for (VoteLine line : lines) {
                addEntry(new EvidenceEntry(line));
            }
        }

        @Override
        public int getRowWidth() {
            return 320;
        }

        protected int getScrollbarPositionX() {
            return getRowRight() - 2;
        }

        @Environment(EnvType.CLIENT)
        public class EvidenceEntry extends AlwaysSelectedEntryListWidget.Entry<EvidenceEntry> {
            private final VoteLine line;

            public EvidenceEntry(VoteLine line) {
                this.line = line;
            }

            @Override
            public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
                int renderX = getX() + 1 + (this.line.index() > 0L ? 16 : 0);
                int renderY = getY() + (this.getHeight() - 9) / 2 + 1;
                context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, this.line.contents(), renderX, renderY, -1);
            }

            @Override
            public Text getNarration() {
                return Text.translatable("narrator.select", this.line.original());
            }

            @Override
            public boolean mouseClicked(Click click, boolean doubled) {
                if (click.button() == 0) {
                    EvidenceListWidget.this.setSelected(this);
                    return true;
                }
                return false;
            }
        }
    }
}
