package net.zhengzhengyiyi.gui;

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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public class ReportEvidenceScreen extends Screen {
    private static final MutableText COPY_TEXT = Text.translatable("chat.copy");
    private final Screen parent;
    private final List<ReportEntryData> entries;
    private EvidenceListWidget listWidget;

    public ReportEvidenceScreen(Text title, Screen parent, List<ReportEntryData> entries) {
        super(title);
        this.parent = parent;
        this.entries = entries;
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Override
    protected void init() {
        int buttonY = this.height - 28;
        int centerX = this.width / 2;
        
    	this.addDrawableChild(ButtonWidget.builder(COPY_TEXT, button -> {
            String content = this.entries.stream()
                    .map(data -> data.contents().getString())
                    .collect(Collectors.joining("\n"));
            this.client.keyboard.setClipboard(content);
        }).dimensions(centerX - 155, buttonY, 150, 20).build());

        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close())
                .dimensions(centerX + 5, buttonY, 150, 20).build());
        
        this.listWidget = new EvidenceListWidget(this.client, this.width, this.height - 50, 40, this.height - 50);
        this.addSelectableChild(this.listWidget);
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 16, 0xFFFFFF);
        context.enableScissor(0, 40, this.width, this.height - 40);
        this.listWidget.render(context, mouseX, mouseY, delta);
        
        context.disableScissor();
    }
    
    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
    	
    }

    @Environment(EnvType.CLIENT)
    public class EvidenceListWidget extends AlwaysSelectedEntryListWidget<EvidenceListWidget.Entry> {
        public EvidenceListWidget(MinecraftClient client, int width, int height, int top, int bottom) {
//            super(client, width, height, top, bottom);
        	super(client, width, height, top, 20);
            for (ReportEntryData data : ReportEvidenceScreen.this.entries) {
                this.addEntry(new Entry(data));
            }
        }
        
        @Override
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
    		this.drawMenuListBackground(context);
    		this.renderList(context, mouseX, mouseY, deltaTicks);
    		this.drawHeaderAndFooterSeparators(context);
    		this.drawScrollbar(context, mouseX, mouseY);
    		
//    		context.drawText(textRenderer, "HelloWorld", 20, 20, 0xFF000000, false);
        }

        @Override
        public int getRowWidth() {
            return 320;
        }

        @Environment(EnvType.CLIENT)
        public class Entry extends AlwaysSelectedEntryListWidget.Entry<Entry> {
            private final ReportEntryData data;

            public Entry(ReportEntryData data) {
                this.data = data;
            }
            
            @Override
            public void render(DrawContext context, int x, int y, boolean hovered, float deltaTicks) {
                int xOffset = this.getX();
                int yOffset = this.getY() + 10;
                
//                System.out.println(this.getX() + " " + this.getY());
                
//                context.drawTextWithShadow(ReportEvidenceScreen.this.textRenderer, this.data.contents().getString(), xOffset, yOffset, 0xFFFFFF);
                context.drawTextWithShadow(ReportEvidenceScreen.this.textRenderer, this.data.contents(), xOffset, yOffset, 0xFF000000);
            }

            @Override
            public boolean mouseClicked(Click click, boolean doubled) {
                if (click.button() == 0) {
                    EvidenceListWidget.this.setSelected(this);
                    return true;
                }
                return false;
            }

            @Override
            public Text getNarration() {
                return Text.translatable("narrator.select", this.data.original());
            }
        }
    }

    public record ReportEntryData(long index, Text contents, Text original) {
    	public static List<ReportEntryData> createFromStream(Stream<String> stream) {
            MutableText prefix = Text.literal("- ");
            return stream.map(line -> {
                Text textLine = Text.literal(line);
                return new ReportEntryData(0, prefix.copy().append(textLine), textLine);
            }).collect(Collectors.toList());
        }
    }
}
