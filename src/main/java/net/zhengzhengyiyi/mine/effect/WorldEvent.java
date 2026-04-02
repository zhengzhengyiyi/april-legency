package net.zhengzhengyiyi.mine.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;

public interface WorldEvent {
   void tick(ServerWorld world);

   void finish(ServerWorld world, boolean success);

   BlockPos getPos();

   WorldEvent.Status getStatus();

   MapCodec<? extends WorldEvent> getCodec();

   @SuppressWarnings({"unchecked", "rawtypes"})
   static MapCodec<? extends WorldEvent> register(Registry<MapCodec<? extends WorldEvent>> registry) {
      return Registry.register((Registry)registry, Identifier.of("battle"), WaveEvent.CODEC);
   }

   public static enum Status implements StringIdentifiable {
      ACTIVE,
      WON,
      FAILED;

      public static final Codec<WorldEvent.Status> CODEC = StringIdentifiable.createCodec(WorldEvent.Status::values);

      @Override
      public String asString() {
         return this.name().toLowerCase();
      }
   }
}