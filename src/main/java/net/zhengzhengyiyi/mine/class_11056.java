package net.zhengzhengyiyi.mine;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.component.ComponentsAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.zhengzhengyiyi.AprilsLegacy;
import net.zhengzhengyiyi.component.ModDataComponentTypes;

import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public record class_11056(List<MineEffect> effects, boolean includeDescription) implements TooltipAppender {
   public static final Codec<class_11056> field_58857 = RecordCodecBuilder.create(
      instance -> instance.group(
            MineEffect.CODEC.listOf().fieldOf("effects").forGetter(class_11056::effects),
            Codec.BOOL.fieldOf("include_description").forGetter(class_11056::includeDescription)
         )
         .apply(instance, class_11056::new)
   );
   public static final PacketCodec<RegistryByteBuf, class_11056> field_58858 = PacketCodec.tuple(
      PacketCodecs.registryValue(AprilsLegacy.WORLD_EFFECT_KEY).collect(PacketCodecs.toList()),
      class_11056::effects,
      PacketCodecs.BOOLEAN,
      class_11056::includeDescription,
      class_11056::new
   );
   public static final class_11056 field_58859 = new class_11056(List.of(), false);
   private static final Text field_58860 = Text.literal("???");
   private static final Style field_58863 = Style.EMPTY.withColor(Formatting.GRAY);

   private Text method_69592(@Nullable PlayerEntity playerEntity, MineEffect arg, boolean bl, boolean bl2) {
      boolean bl3 = false;
      // TODO
      if (!bl3 && bl2) {
         return field_58860;
      } else if (bl) {
         Text text = Texts.setStyleIfAbsent(arg.unlockHint().copy(), field_58863);
         return Text.translatable("unlocks.screen.hint", text);
      } else {
         return arg.description();
      }
   }

   private static Text method_69593(MineEffect arg, Text text) {
      float f = arg.experienceModifier();
      return (Text)(f != 1.0F ? Text.translatable("world.effect.experience_modifier", text, f) : text);
   }

   @Override
   public void appendTooltip(TooltipContext context, Consumer<Text> textConsumer, TooltipType type,
         ComponentsAccess components) {
      Boolean isCompleted = components.get(ModDataComponentTypes.MINE_COMPLETED);
      if (isCompleted != null) {
         Text statusText = isCompleted ? Text.translatable("mine.status.completed").formatted(Formatting.GREEN)
                                       : Text.translatable("mine.status.failed").formatted(Formatting.RED);
         textConsumer.accept(Text.literal("Status: ").append(statusText).formatted(Formatting.GRAY));
         textConsumer.accept(ScreenTexts.EMPTY);
      }

      boolean showHint = components.get(ModDataComponentTypes.WORLD_EFFECT_UHINT) != null;
      boolean isUnlocked = components.get(ModDataComponentTypes.WORLD_EFFECT_UNLOCK) != null;

      if (this.includeDescription) {
         for (MineEffect lv : this.effects) {
            textConsumer.accept(method_69593(lv, this.method_69592(AprilsLegacy.server.getPlayerManager().getPlayerList().get(0), lv, showHint, isUnlocked)));
         }
      } else {
         textConsumer.accept(Text.literal("Effects:").formatted(Formatting.GOLD));
         for (MineEffect lv : this.effects) {
            Text text = method_69593(lv, lv.name());
            textConsumer.accept(Text.literal("  ").append(text));
         }
      }
   }
}
