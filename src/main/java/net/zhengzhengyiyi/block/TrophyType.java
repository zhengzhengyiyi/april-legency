package net.zhengzhengyiyi.block;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.Consumer;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;

public enum TrophyType implements StringIdentifiable, TooltipAppender {
   GOLD("gold"),
   MEGA_SPUD("mega_spud"),
   NO_MEDAL("no_medal");

   public static final Codec<TrophyType> CODEC = StringIdentifiable.createCodec(TrophyType::values);
   public static final PacketCodec<ByteBuf, TrophyType> PACKET_CODEC = PacketCodecs.indexed(i -> values()[i], Enum::ordinal);
   private final String id;

   TrophyType(final String id) {
      this.id = id;
   }

   @Override
   public String asString() {
      return this.id;
   }

   @Override
   public void appendTooltip(
      Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components
   ) {
      textConsumer.accept(Text.translatable("trophy." + this.id).formatted(Formatting.GRAY));
   }
}
