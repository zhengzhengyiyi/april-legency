package net.zhengzhengyiyi.gui.toast;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * class_11122 - Level-up toast notification.
 * Shown when the player gains a mine level.
 */
@Environment(EnvType.CLIENT)
public class LevelUpToast implements Toast {
    private static final Identifier TEXTURE = Identifier.ofVanilla("toast/advancement");
    public static final int DISPLAY_DURATION = 5000;
    private int level;
    private Toast.Visibility visibility;

    public LevelUpToast(int level) {
        this.level = level;
        this.visibility = Toast.Visibility.SHOW;
    }

    @Override
    public Toast.Visibility getVisibility() { return this.visibility; }

    @Override
    public void update(ToastManager manager, long time) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player != null) this.level = player.experienceLevel;

        if (this.visibility == Toast.Visibility.SHOW) {
            if (client.currentScreen instanceof net.zhengzhengyiyi.gui.UnlocksScreen) {
                this.visibility = Toast.Visibility.HIDE;
            } else {
                this.visibility = time >= DISPLAY_DURATION * manager.getNotificationDisplayTimeMultiplier()
                    ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
            }
        }
    }

    @Override
    public int getWidth() { return 240; }

    @Override
    public void draw(DrawContext context, TextRenderer textRenderer, long startTime) {
        context.drawGuiTexture(net.minecraft.client.gl.RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, this.getWidth(), this.getHeight());
        context.drawText(textRenderer, Text.stringifiedTranslatable("level.gained", this.level), 30, 7, -256, false);
        context.drawText(textRenderer, Text.translatable("level.unlock_hint", Text.keybind("key.unlocks")), 30, 18, -1, false);
        context.drawItemWithoutEntity(Items.EXPERIENCE_BOTTLE.getDefaultStack(), 8, 8);
    }
}
