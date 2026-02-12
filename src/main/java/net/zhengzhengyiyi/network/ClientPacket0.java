package net.zhengzhengyiyi.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;

public record ClientPacket0(RegistryEntry<DimensionType> dimensionType) implements CustomPayload {
   public static final PacketCodec<RegistryByteBuf, ClientPacket0> CODEC = PacketCodec.tuple(
      DimensionType.PACKET_CODEC, ClientPacket0::dimensionType, ClientPacket0::new
   );
   
   public static final Identifier PACKET_ID = Identifier.of("craftmine", "packet_0");
   public static final Id<ClientPacket0> PAYLOAD_ID = new Id<>(PACKET_ID);

   public void apply(ModClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.method_68892(this);
   }
   
   public void write(RegistryByteBuf buf) {
       buf.writeRegistryKey(dimensionType.getKey().get());
   }

   @Override
	public Id<ClientPacket0> getId() {
		return PAYLOAD_ID;
	}
}
