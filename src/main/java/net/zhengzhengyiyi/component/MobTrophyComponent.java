package net.zhengzhengyiyi.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Consumer;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public record MobTrophyComponent(RegistryEntry<EntityType<?>> type, boolean shiny) implements TooltipAppender {
   public static final Codec<MobTrophyComponent> CODEC = Codec.withAlternative(
      RecordCodecBuilder.create(
         instance -> instance.group(
               RegistryFixedCodec.of(RegistryKeys.ENTITY_TYPE).fieldOf("type").forGetter(MobTrophyComponent::type),
               Codec.BOOL.fieldOf("shiny").forGetter(MobTrophyComponent::shiny)
            )
            .apply(instance, MobTrophyComponent::new)
      ),
      RegistryFixedCodec.of(RegistryKeys.ENTITY_TYPE),
      registryEntry -> new MobTrophyComponent(registryEntry, false)
   );
   public static final PacketCodec<RegistryByteBuf, MobTrophyComponent> PACKET_CODEC = PacketCodec.tuple(
      PacketCodecs.registryEntry(RegistryKeys.ENTITY_TYPE), MobTrophyComponent::type,
      PacketCodecs.BOOLEAN, MobTrophyComponent::shiny,
      MobTrophyComponent::new
   );

   @Override
   public void appendTooltip(
      Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components
   ) {
      textConsumer.accept(Text.translatable("item.minecraft.mob_trophy.entity", this.type.value().getName()));
      if (this.shiny) {
         textConsumer.accept(Text.translatable("item.minecraft.mob_trophy.shiny").formatted(Formatting.LIGHT_PURPLE));
      }
   }
}
