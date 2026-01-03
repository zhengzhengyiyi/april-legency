/*    */ package net.zhengzhengyiyi.network;

/*    */ 
/*    */ import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.zhengzhengyiyi.vote.VoteOptionId;
/*    */ 
/*    */ public final record class_8482(VoteOptionId id, VoterData voters) implements Packet<ClientPlayPacketListener> {
/*    */   
/*  9 */   public class_8482 {}

/*    */   
/*    */   public class_8482(PacketByteBuf packetByteBuf) {
/* 15 */     this(VoteOptionId.read(packetByteBuf), VoterData.read(packetByteBuf));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void write(PacketByteBuf buf) {
/* 23 */     VoteOptionId.write(buf, null);
/* 24 */     voters.write(buf);
/*    */   }
/*    */ 
/*    */   
/*    */   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
/* 29 */     ((VoteClientPlayPacketListener)clientPlayPacketListener).method_51014(this);
/*    */   }
/*    */

@Override
public PacketType<? extends Packet<ClientPlayPacketListener>> getPacketType() {
	return null;
}
}
