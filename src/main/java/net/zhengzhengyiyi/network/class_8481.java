/*    */ package net.zhengzhengyiyi.network;

import java.util.UUID;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;

/*    */ public final record class_8481(UUID id) implements Packet<ClientPlayPacketListener> {
/*    */   
/*  8 */   public class_8481 {}
		public class_8481(PacketByteBuf packetByteBuf) {
/* 13 */     this(packetByteBuf
/* 14 */         .readUuid());
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void write(PacketByteBuf buf) {
/* 20 */     buf.writeUuid(this.id);
/*    */   }
/*    */ 
/*    */   
/*    */   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
/* 25 */     ((VoteClientPlayPacketListener)clientPlayPacketListener).method_51013(this);
/*    */   }
@Override
public PacketType<? extends Packet<ClientPlayPacketListener>> getPacketType() {
	return null;
} }
