/*    */ package net.zhengzhengyiyi.network;
/*    */ 
/*    */ import java.util.UUID;

import com.mojang.serialization.DynamicOps;

import net.minecraft.nbt.NbtOps;
/*    */ import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.zhengzhengyiyi.vote.VoteDefinition;
/*    */ 
/*    */ public final record class_8483(UUID id, VoteDefinition voteData) implements Packet<ClientPlayPacketListener> {
/*    */   
/* 10 */   public class_8483 {}
/*    */ 
/*    */ 
/*    */   
/*    */   @SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
public class_8483(PacketByteBuf packetByteBuf) {
/* 15 */     this(packetByteBuf
/* 16 */         .readUuid(), (VoteDefinition)packetByteBuf
/* 17 */         .decode((DynamicOps)NbtOps.INSTANCE, VoteDefinition.CODEC));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   @SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
public void write(PacketByteBuf buf) {
/* 23 */     buf.writeUuid(this.id);
/* 24 */     buf.encode((DynamicOps)NbtOps.INSTANCE, VoteDefinition.CODEC, this.voteData);
/*    */   }
/*    */ 
/*    */   
/*    */   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
/* 29 */     ((VoteClientPlayPacketListener)clientPlayPacketListener).method_51015(this);
/*    */   }
/*    */
@Override
public PacketType<? extends Packet<ClientPlayPacketListener>> getPacketType() {
	return null;
} }
