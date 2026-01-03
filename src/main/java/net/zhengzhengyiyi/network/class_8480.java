/*    */ package net.zhengzhengyiyi.network;
import java.util.Optional;

/*    */ 
/*    */ import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.text.Text;
/*    */ 
/*    */ public record class_8480(int transactionId, Optional<Text> rejectReason) implements Packet<ClientPlayPacketListener> {
/*    */   
/*  9 */   public class_8480 {}
/*    */ 
/*    */ 
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
/* 37 */     ((VoteClientPlayPacketListener)clientPlayPacketListener).method_51012(this);
/*    */   }
/*    */
@Override
public PacketType<? extends Packet<ClientPlayPacketListener>> getPacketType() {
	return null;
} }
