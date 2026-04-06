package net.zhengzhengyiyi.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.zhengzhengyiyi.block.MineCrafterBlockEntity;
import net.zhengzhengyiyi.mine.MineEffectGenerator;
import net.zhengzhengyiyi.mine.MineGeneratorSlot;
import net.zhengzhengyiyi.mine.class_11041;

@Environment(EnvType.CLIENT)
public class MineCrafterScreen extends HandledScreen<MineEffectGenerator> {
   private static final Identifier SLOT_TEX          = Identifier.ofVanilla("container/slot");
   private static final Identifier DISABLED_SLOT_TEX = Identifier.ofVanilla("container/crafter/disabled_slot");
   private static final Identifier LEVEL_SLOT_TEX    = Identifier.ofVanilla("container/slot/level");
   private static final Identifier DONATE_TEX        = Identifier.ofVanilla("textures/gui/container/mine_crafter_donate.png");
   private static final Identifier BG_TEX            = Identifier.ofVanilla("textures/gui/container/mine_crafter.png");
   private static final Identifier HINTS_TEX         = Identifier.ofVanilla("textures/gui/container/mine_crafter_hints.png");
   private static final Identifier BOSS_TEX          = Identifier.ofVanilla("textures/gui/container/mine_crafter_boss.png");
   private static final Identifier BOSS_ACTIVE_TEX   = Identifier.ofVanilla("textures/gui/container/mine_crafter_boss_active.png");
   private static final Identifier ACTIVE_TEX        = Identifier.ofVanilla("textures/gui/container/mine_crafter_active.png");
   private static final Identifier WON_TEX           = Identifier.ofVanilla("textures/gui/container/mine_crafter_won.png");
   private static final Identifier FAIL_TEX          = Identifier.ofVanilla("textures/gui/container/mine_crafter_fail.png");
   private static final Identifier XP_BG_TEX         = Identifier.ofVanilla("hud/experience_bar_background");
   private static final Identifier XP_BAR_TEX        = Identifier.ofVanilla("hud/experience_bar_progress");
   private static final Identifier ALL_UNLOCKED_TEX  = Identifier.ofVanilla("container/crafter/all_unlocked");
   private static final Identifier SCROLLER_TEX      = Identifier.ofVanilla("container/loom/scroller_disabled");

   private final TextIconButtonWidget donateButton;
   private float scrollOffset;
   private int scrollRow;
   private int tick = 0;

   public MineCrafterScreen(MineEffectGenerator handler, PlayerInventory inventory, Text title) {
      super(handler, inventory, title);
      this.donateButton = TextIconButtonWidget.builder(Text.empty(), this::onDonateClick, true)
         .texture(Identifier.ofVanilla("icon/donate_experience"), 24, 24)
         .dimension(24, 24)
         .build();
      this.donateButton.setTooltip(Tooltip.of(Text.translatable("container.mine_crafter.donate", 20)));
   }

   private void onDonateClick(ButtonWidget btn) {
      this.client.player.closeHandledScreen();
   }

   @Override
   protected void init() {
      this.backgroundWidth = 193;
      this.backgroundHeight = 205;
      super.init();
      this.addSelectableChild(this.donateButton);
      this.scrollRow = 0;
      this.updateHintSlots(this.scrollRow);
   }

   @Override
   protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
      super.onMouseClick(slot, slotId, button, actionType);
   }

   @Override
   protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
      if (this.handler.method_69548()) {
         if (this.handler.method_69549()) {
            context.drawText(this.textRenderer, Text.translatable("container.mine_crafter.won"), this.titleX, this.titleY, 4210752, false);
         } else {
            context.drawText(this.textRenderer, Text.translatable("container.mine_crafter.fail"), this.titleX, this.titleY, 4210752, false);
         }
      }

      if (!this.handler.method_69541() || this.handler.method_69548()) {
         if (this.handler.method_69547()) {
            context.drawText(this.textRenderer, Text.translatable("container.mine_crafter.active"), this.titleX, this.titleY, 4210752, false);
         } else if (!this.handler.method_69548()) {
            context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 4210752, false);
         }

         context.drawText(this.textRenderer, Text.translatable("container.mine_crafter.drawer"), this.playerInventoryTitleX, this.playerInventoryTitleY + 39, 4210752, false);

         MutableText hintsText = this.handler.method_69550()
            ? Text.translatable("container.mine_crafter.no_hints")
            : Text.translatable("container.mine_crafter.hints");
         int w = this.getTextRenderer().getWidth(hintsText);
         context.drawText(this.textRenderer, hintsText, this.titleX + 250 - w / 2, this.titleY, 4210752, false);
      }
   }

   @Override
   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      super.render(context, mouseX, mouseY, delta);
      if (!this.handler.method_69541() || this.handler.method_69548()) {
         this.renderXpBar(context, mouseX, mouseY, delta);
         this.renderLevelText(context);
      }
      this.drawMouseoverTooltip(context, mouseX, mouseY);
   }

   private void renderXpBar(DrawContext context, int mouseX, int mouseY, float delta) {
      int maxSize = MineCrafterBlockEntity.getMaxInventorySize(this.handler.method_69539());
      if (maxSize > 0) {
         int filled = (int)((float)this.handler.method_69540() / maxSize * 161.0F);
         int bx = this.x + 4, by = this.y - 16;
         context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, XP_BG_TEX, bx, by, 160, 11);
         if (filled > 0) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, XP_BAR_TEX, 182, 11, 0, 0, bx, by, filled, 11);
         }
      }
      this.donateButton.setPosition(this.x + this.backgroundWidth - 28, this.y - 23);
      this.donateButton.active = this.client.player.experienceLevel >= 20;
      this.donateButton.render(context, mouseX, mouseY, delta);
   }

   private void renderLevelText(DrawContext context) {
      int level = this.handler.method_69539() + 1;
      String text = Text.translatable("container.mine_crafter.level", level,
         this.handler.method_69540(), MineCrafterBlockEntity.getMaxInventorySize(this.handler.method_69539())).getString();
      int tx = this.x + this.textRenderer.getWidth(text) / 2;
      int ty = this.y - 15;
      context.drawText(this.textRenderer, text, tx + 1, ty, 0, false);
      context.drawText(this.textRenderer, text, tx - 1, ty, 0, false);
      context.drawText(this.textRenderer, text, tx, ty + 1, 0, false);
      context.drawText(this.textRenderer, text, tx, ty - 1, 0, false);
      context.drawText(this.textRenderer, text, tx, ty, 8453920, false);
   }

   @Override
   protected void handledScreenTick() {
      super.handledScreenTick();
      this.tick = (this.tick + 1) % 240;
      long activeCount = this.handler.method_69545().stream().filter(Slot::hasStack).count();
      for (Slot slot : this.handler.slots) {
         if (slot instanceof MineGeneratorSlot ms && ms.method_69555()) {
            tickSlotAnimation(ms, activeCount);
         }
      }
      this.donateButton.setFocused(false);
   }

   private void tickSlotAnimation(MineGeneratorSlot slot, long total) {
      var curve = this.handler.method_69542();
      double offset = total > 0 ? 0.5 / total * slot.getIndex() : 0;
      double t = this.tick / 240.0 + offset;
      Vec2f target = curve.method_69513((float)(t + offset));
      slot.field_58825 = target.x;
      slot.field_58826 = target.y;
   }

   @Override
   protected void drawSlot(DrawContext context, Slot slot, int x, int y) {
      // Lerp animated slot positions using dynamic delta ticks for smooth rendering
      float delta = this.client.getRenderTickCounter().getDynamicDeltaTicks();
      if (slot instanceof MineGeneratorSlot ms && ms.method_69555()) {
         ((net.zhengzhengyiyi.accessor.SlotPositionAccessor) ms).setSlotPos(
            (int)MathHelper.lerp(delta, ms.x, ms.field_58825),
            (int)MathHelper.lerp(delta, ms.y, ms.field_58826)
         );
      }
      super.drawSlot(context, slot, x, y);
   }

   @Override
   protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
      int x = this.x, y = this.y;

      if (!this.handler.method_69541() || this.handler.method_69548()) {
         context.drawTexture(RenderPipelines.GUI_TEXTURED, DONATE_TEX, x, y - 20, 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 256, 256);
      }

      if (this.handler.method_69547()) {
         Identifier activeTex = this.handler.method_69541() ? BOSS_ACTIVE_TEX : ACTIVE_TEX;
         context.drawTexture(RenderPipelines.GUI_TEXTURED, activeTex, x, y, 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 256, 256);
      } else if (this.handler.method_69548()) {
         Identifier endTex = this.handler.method_69549() ? WON_TEX : FAIL_TEX;
         context.drawTexture(RenderPipelines.GUI_TEXTURED, endTex, x, y, 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 256, 256);
      } else {
         Identifier mainTex = this.handler.method_69541() ? BOSS_TEX : BG_TEX;
         context.drawTexture(RenderPipelines.GUI_TEXTURED, mainTex, x, y, 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 256, 256);
      }

      if (!this.handler.method_69547() && !this.handler.method_69548()) {
         for (MineGeneratorSlot slot : this.handler.method_69545()) {
            if (slot.isEnabled()) {
               Identifier slotTex = (!slot.locked && !this.handler.method_69541()) ? SLOT_TEX : DISABLED_SLOT_TEX;
               context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, slotTex, x + slot.x - 1, y + slot.y - 1, 18, 18);
            }
         }
         for (class_11041 slot : this.handler.method_69546()) {
            if (slot.isEnabled() && (!slot.method_69517() || this.handler.method_69541())) {
               context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, DISABLED_SLOT_TEX, x + slot.x - 1, y + slot.y - 1, 18, 18);
            }
         }
      }

      Slot output = this.handler.method_69544();
      context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, LEVEL_SLOT_TEX, x + output.x, y + output.y, 16, 16);

      if (!this.handler.method_69541() || this.handler.method_69548()) {
         context.drawTexture(RenderPipelines.GUI_TEXTURED, HINTS_TEX, x + 200, y, 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 256, 256);
         if (this.handler.method_69550()) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, ALL_UNLOCKED_TEX, x + 230, y + 70, 50, 50);
         }
         int scrollY = (int)(55.0F * this.scrollOffset);
         context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SCROLLER_TEX, x + 174, y + 124 + scrollY, 12, 15);
      }
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double hAmount, double vAmount) {
      if (super.mouseScrolled(mouseX, mouseY, hAmount, vAmount)) return true;
      if (!this.canScroll()) return false;
      int rows = MathHelper.ceil(this.getHintCount() / 9.0F) - 4;
      if (rows > 0) {
         this.scrollOffset = MathHelper.clamp(this.scrollOffset - (float)vAmount / rows, 0f, 1f);
         this.scrollRow = Math.max((int)(this.scrollOffset * rows + 0.5F), 0);
         this.updateHintSlots(this.scrollRow);
      }
      return true;
   }

   private boolean canScroll() {
      return this.getHintCount() > 36;
   }

   private int getHintCount() {
      return (int)this.handler.method_69546().stream().filter(Slot::hasStack).count();
   }

   private void updateHintSlots(int row) {
      var hints = this.handler.method_69546();
      for (int i = 0; i < hints.size(); i++) {
         class_11041 slot = hints.get(i);
         int r = MathHelper.floor(i / 9.0F) - row;
         if (r < 0) {
            slot.method_69556(false);
         } else if (r < 4) {
            slot.method_69556(true);
            ((net.zhengzhengyiyi.accessor.SlotPositionAccessor) slot).setSlotPos(slot.x, 124 + r * 18);
         } else {
            slot.method_69556(false);
         }
      }
   }
}
