package net.zhengzhengyiyi.gui.toast;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class VotingToast implements Toast {
//    private static final Identifier TEXTURE = Identifier.ofVanilla("toast/advancement");
//	private static final Identifier TEXTURE = Identifier.of("zhengzhengyiyi", "gui/toasts");
	private static final Identifier TEXTURE = Identifier.of("zhengzhengyiyi", "textures/gui/toasts.png");
    private static final Text TITLE = Text.literal("New proposal received!").formatted(Formatting.LIGHT_PURPLE);
    public final Priority priority;
    private final Text title;
    private final List<OrderedText> lines;
    private final int width;
    private long startTime;

    public static Optional<VotingToast> create(MinecraftClient client, Random random, Priority priority) {
        TextRenderer textRenderer = client.textRenderer;
        List<Text> messages = MESSAGES.getOrDefault(priority, List.of());
        return Util.getRandomOrEmpty(messages, random).map(text -> {
            List<OrderedText> wrappedLines = textRenderer.wrapLines(text, 200);
            int maxWidth = Math.max(200, wrappedLines.stream().mapToInt(textRenderer::getWidth).max().orElse(200));
            return new VotingToast(priority, TITLE, wrappedLines, maxWidth + 30);
        });
    }

    private VotingToast(Priority priority, Text title, List<OrderedText> lines, int width) {
        this.priority = priority;
        this.title = title;
        this.lines = lines;
        this.width = width;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return 20 + Math.max(this.lines.size(), 1) * 12;
    }

    @Override
    public void draw(DrawContext context, TextRenderer textRenderer, long startTime) {
        int w = getWidth();
        int h = getHeight();

        if (w == 160 && this.lines.size() <= 1) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, w, h);
        } else {
            int l = Math.min(4, h - 28);
            this.drawPart(context, w, 0, 0, 28);
            for (int m = 28; m < h - l; m += 10) {
                this.drawPart(context, w, 16, m, Math.min(16, h - m - l));
            }
            this.drawPart(context, w, 32 - l, h - l, l);
        }

        context.drawText(MinecraftClient.getInstance().textRenderer, this.title, 18, 7, -256, false);
        for (int j = 0; j < this.lines.size(); j++) {
            context.drawText(MinecraftClient.getInstance().textRenderer, this.lines.get(j), 18, 18 + j * 12, -1, false);
        }
        
        this.startTime = startTime;

//        return (startTime > (50L * this.priority.delayFactor)) ? Visibility.HIDE : Visibility.SHOW;
    }
    
    private void drawPart(DrawContext context, int width, int vOffset, int y, int height) {
        int uLeftWidth = (vOffset == 0) ? 20 : 5;
        int uRightWidth = Math.min(60, width - uLeftWidth);
        int texW = 256;
        int texH = 256;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, y, 0.0F, 64.0F + vOffset, uLeftWidth, height, texW, texH);
        
        for (int x = uLeftWidth; x < width - uRightWidth; x += 64) {
            int currentPartWidth = Math.min(64, width - x - uRightWidth);
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 32.0F, 64.0F + vOffset, currentPartWidth, height, texW, texH);
        }
        
//       System.out.println("width: " + width + ", uLeftWidth: " + uLeftWidth + ", uRightWidth: " + uRightWidth);
       context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, width - uRightWidth, y, 160.0F - uRightWidth, 64.0F + vOffset, uRightWidth, height, texW, texH);
    }

    @Environment(EnvType.CLIENT)
    public enum Priority {
        LOW(300, 2400, 100),
        MEDIUM(200, 2400, 60),
        CONCERNING(100, 1200, 40),
        WHY_ARE_YOU_NOT_DOING_IT(10, 99999, 20);

        final int interval;
        final int maxTime;
        final int delayFactor;

        Priority(int interval, int maxTime, int delayFactor) {
            this.interval = interval;
            this.maxTime = maxTime;
            this.delayFactor = delayFactor;
        }

        public static Optional<Priority> fromTime(int time) {
            Priority p = getByTime(time);
            return (time % p.interval == 0) ? Optional.of(p) : Optional.empty();
        }

        private static Priority getByTime(int time) {
            for (Priority p : values()) {
                time -= p.maxTime;
                if (time < 0) return p;
            }
            return WHY_ARE_YOU_NOT_DOING_IT;
        }
    }

    public static final Text VOTE_KEY = Text.keybind("key.voting").formatted(Formatting.BOLD);

    private static final Map<Priority, List<Text>> MESSAGES = Map.of(
            Priority.LOW, List.of(
                    Text.translatable("Press %s to open voting screen", VOTE_KEY),
                    Text.translatable("To open voting screen, press %s", VOTE_KEY),
                    Text.translatable("New vote started, press %s to cast your vote", VOTE_KEY)
            ),
            Priority.MEDIUM, List.of(
                    Text.translatable("A new proposal is waiting for your vote, press %s", VOTE_KEY),
                    Text.translatable("Others are having fun while you are not pressing %s", VOTE_KEY),
                    Text.translatable("Time to change some rules, press %s", VOTE_KEY),
                    Text.translatable("You have new vote proposals to review, press %s to access!", VOTE_KEY)
            ),
            Priority.CONCERNING, List.of(
                    Text.translatable("Ok, so the whole idea of this release is to vote, so press %s", VOTE_KEY),
                    Text.translatable("Somebody wants to tell you what you can and can not do, press %s to prevent that", VOTE_KEY),
                    Text.translatable("If you can't find %s, it's probably on your keyboard", VOTE_KEY),
                    Text.translatable("Do you want more phantoms? That's how you get more phantoms! Press %s", VOTE_KEY),
                    Text.translatable("Please, just press %s and be done with it!", VOTE_KEY)
            ),
            Priority.WHY_ARE_YOU_NOT_DOING_IT, List.of(
                    Text.translatable("PRESS %s ", VOTE_KEY).append(Text.translatable("PRESS %s ", VOTE_KEY)),
                    Text.translatable("WHYYYYYYYYYYYYYYYYYYYYYYYYY NO %s", VOTE_KEY),
                    Text.translatable("DO YOU HAVE NO IDEA WHERE %s IS!?", VOTE_KEY),
                    Text.translatable("AAAAAAA %s AAAAAAA!", VOTE_KEY).formatted(Formatting.OBFUSCATED)
            )
    );

	@Override
	public Visibility getVisibility() {
		return (startTime > (50L * this.priority.delayFactor)) ? Visibility.HIDE : Visibility.SHOW;
	}

	@Override
	public void update(ToastManager manager, long time) {
		
	}
}
