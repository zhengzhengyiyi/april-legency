package net.zhengzhengyiyi.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

/**
 * Mirrors Craftmine class_11054 - Exchange value component for items.
 * Used to mark items as having no exchange value (free items from unlocks).
 */
public record ExchangeValueComponent(float value) {
    public static final Codec<ExchangeValueComponent> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.FLOAT.fieldOf("value").forGetter(ExchangeValueComponent::value)
        ).apply(instance, ExchangeValueComponent::new)
    );

    public static final PacketCodec<RegistryByteBuf, ExchangeValueComponent> PACKET_CODEC =
        PacketCodec.tuple(
            PacketCodecs.FLOAT, ExchangeValueComponent::value,
            ExchangeValueComponent::new
        );
}
