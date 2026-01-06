/*    */ package net.zhengzhengyiyi.network;
import java.util.Optional;

/*    */ 
/*    */ import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
/*    */ 
/*    */ public record class_8480(int transactionId, Optional<Text> rejectReason) implements CustomPayload {
/*    */   
/*  9 */   public class_8480 {}
/*    */ 
/*    */ 	
public static final Identifier PACKET_ID = Identifier.of("aprils_legacy", "vote_response");
public static final CustomPayload.Id<class_8480> PAYLOAD_ID = new CustomPayload.Id<>(PACKET_ID);
//public static final PacketType<class_8480> TYPE = new PacketType<>(NetworkSide.CLIENTBOUND, PACKET_ID);
/*    */ 
/*    */   
/*    */   public static class_8480 method_51141(int i) {
/* 15 */     return new class_8480(i, Optional.empty());
/*    */   }
/*    */   
/*    */   public static class_8480 method_51142(int i, Text text) {
/* 19 */     return new class_8480(i, Optional.of(text));
/*    */   }
/*    */   
/*    */   public class_8480(PacketByteBuf packetByteBuf) {
/* 23 */     this(packetByteBuf
/* 24 */         .readVarInt(),
				packetByteBuf
/* 25 */         .readOptional(b -> Text.of(b.readString())));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void write(PacketByteBuf buf) {
/* 31 */     buf.writeVarInt(transactionId);
/* 32 */     buf.writeOptional(this.rejectReason, (a, b) -> a.writeString(b.getString()));
/*    */   }
/*    */ 
/*    */   
/*    */   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
/* 37 */     ((VoteClientPlayPacketListener)clientPlayPacketListener).onVoteResponse(this);
/*    */   }
/*    */
//public PacketType<? extends Packet<ClientPlayPacketListener>> getPacketType() {
//	return TYPE;
//}
@Override
public Id<? extends CustomPayload> getId() {
	return PAYLOAD_ID;
} }
