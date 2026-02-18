package net.zhengzhengyiyi.network;

import java.util.List;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.zhengzhengyiyi.mine.MineEffect;

public record ClientPacket6(List<MineEffect> unlockedEffects) implements CustomPayload {
   public static final Id<ClientPacket6> ID = new Id<>(Identifier.of("zhengzhengyiyi", "client_packet_6"));
   public static final PacketCodec<RegistryByteBuf, ClientPacket6> CODEC = PacketCodec.tuple(
		      PacketCodecs.codec(MineEffect.CODEC).collect(PacketCodecs.toList()), 
		      ClientPacket6::unlockedEffects, 
		      ClientPacket6::new
	);

   @Override
   public Id<? extends CustomPayload> getId() {
      return ID;
   }
   
   public void apply(ModClientPlayPacketListener clientPlayPacketListener) {
	    clientPlayPacketListener.method_68897(this);
   }
}
