package net.zhengzhengyiyi.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * class_11130 - Unlocks screen (advancement-style tree for the unlock system).
 */
@Environment(EnvType.CLIENT)
public class UnlocksScreen extends Screen {
    private static final Identifier WINDOW_TEX = Identifier.ofVanilla("widget/window");
    public static final Text TITLE = Text.translatable("gui.unlocks");

    @Nullable
    private final Screen parent;
    private final ClientUnlockManager unlockManager;

    public UnlocksScreen(ClientUnlockManager unlockManager) {
        this(unlockManager, null);
    }

    public UnlocksScreen(ClientUnlockManager unlockManager, @Nullable Screen parent) {
        super(TITLE);
        this.parent = parent;
        this.unlockManager = unlockManager;
    }

    @Override
    protected void init() {
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, btn -> this.close())
            .dimensions(this.width / 2 - 100, this.height - 28, 200, 20)
            .build());
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        this.renderInGameBackground(context);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        int w = Math.max(this.width * 3 / 4, 80);
        int h = Math.max(this.height * 3 / 4, 80);
        int x = (this.width - w) / 2;
        int y = (this.height - h) / 2;

        context.drawGuiTexture(net.minecraft.client.gl.RenderPipelines.GUI_TEXTURED, WINDOW_TEX, x, y, w, h);
        context.drawText(this.textRenderer, TITLE, x + 8, y + 6, 4210752, false);

        int level = this.client.player != null ? this.client.player.experienceLevel : 0;
        context.drawText(this.textRenderer,
            Text.translatable("unlocks.screen.points", level),
            x + 8, y + 18, 47872, false);
    }

    @Override
    public boolean shouldPause() { return false; }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyInput input) {
        if (this.client.options.inventoryKey.matchesKey(input)) {
            this.client.setScreen(null);
            this.client.mouse.lockCursor();
            return true;
        }
        return super.keyPressed(input);
    }
}
