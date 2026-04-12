package net.zhengzhengyiyi.mine;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.zhengzhengyiyi.AprilsLegacy;
import net.zhengzhengyiyi.mine.effect.WaveEvent;

public interface class_11099 {
   Codec<class_11099> CODEC = AprilsLegacy.field_59578.getCodec().dispatch(class_11099::getCodec, Function.identity());

   void tick(ServerWorld world);

   void onRemoved(ServerWorld world, boolean force);

   BlockPos getPos();

   class_11099.Status getStatus();

   MapCodec<? extends class_11099> getCodec();

   @SuppressWarnings({ "unchecked", "rawtypes" })
static MapCodec<? extends class_11099> register(Registry<MapCodec<? extends class_11099>> registry) {
      Registry.register(registry, "raid", class_11103.field_59106);
      return Registry.register((Registry)registry, "battle", WaveEvent.CODEC);
   }

   public static enum Status implements StringIdentifiable {
      ACTIVE,
      WON,
      FAILED;

      public static final Codec<class_11099.Status> CODEC = StringIdentifiable.createCodec(class_11099.Status::values);

      @Override
      public String asString() {
         return this.name().toLowerCase();
      }
   }
}