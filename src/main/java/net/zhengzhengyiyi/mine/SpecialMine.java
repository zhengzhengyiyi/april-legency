package net.zhengzhengyiyi.mine;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;
import net.zhengzhengyiyi.mine.effect.MineUnlockCondition;

public record SpecialMine(
   String key,
   Text name,
   Text description,
   List<MineEffect> requiredEffects,
   List<List<MineEffect>> randomEffectPools,
   List<MineUnlockCondition> unlockedBy,
   List<SpecialMine> unlockedAfter,
   int extraRandomCount
) {
   public static final Codec<SpecialMine> CODEC = Registries.SPECIAL_MINE.getCodec();
   public static final PacketCodec<RegistryByteBuf, SpecialMine> PACKET_CODEC = PacketCodecs.registryValue(RegistryKeys.SPECIAL_MINE);

   public static SpecialMine.Builder builder(String id) {
      return new SpecialMine.Builder(id);
   }

   public List<MineEffect> generateEffects(ServerWorld world) {
      Random random = world.getRandom();
      List<MineEffect> result = new ArrayList<>(this.requiredEffects);

      for (List<MineEffect> pool : this.randomEffectPools) {
         ArrayList<MineEffect> candidates = new ArrayList<>(pool);
         MineEffect selected = null;

         while (selected == null && !candidates.isEmpty()) {
            selected = Util.getRandom(candidates, random);
            if (!selected.canApplyWith(result)) {
               candidates.remove(selected);
               selected = null;
            }
         }

         if (selected != null) {
            result.add(selected);
         }
      }

      for (int i = 0; i < this.extraRandomCount; i++) {
         MineEffectGenerator.pickRandomEffect(world, result, Set.of()).ifPresent(result::add);
      }

      return result;
   }

   public static class Builder {
      private final String id;
      private Style nameStyle = Style.EMPTY.withColor(Formatting.BLUE);
      private final List<MineEffect> requiredEffects = new ArrayList<>();
      private final List<List<MineEffect>> randomEffectPools = new ArrayList<>();
      private final List<MineUnlockCondition> unlockConditions = new ArrayList<>();
      private final List<SpecialMine> parentMines = new ArrayList<>();
      private int extraRandomCount = 0;

      public Builder(String id) {
         this.id = id;
      }

      public SpecialMine.Builder style(Style style) {
         this.nameStyle = style;
         return this;
      }

      public SpecialMine.Builder required(MineEffect... effects) {
         this.requiredEffects.addAll(List.of(effects));
         return this;
      }

      public SpecialMine.Builder pool(MineEffect... effects) {
         this.randomEffectPools.add(List.of(effects));
         return this;
      }

      public SpecialMine.Builder pool(MineEffectGroup group) {
         this.randomEffectPools.add(group.getEffects());
         return this;
      }

      public SpecialMine.Builder extraRandom(int count) {
         this.extraRandomCount = count;
         return this;
      }

      public SpecialMine.Builder condition(MineUnlockCondition... conditions) {
         this.unlockConditions.addAll(List.of(conditions));
         return this;
      }

      public SpecialMine.Builder after(SpecialMine... mines) {
         this.parentMines.addAll(List.of(mines));
         return this;
      }

      public SpecialMine register() {
         SpecialMine mine = this.build();
         if (mine.unlockedBy().isEmpty()) {
            if (!this.parentMines.isEmpty()) {
               throw new IllegalStateException("Missing unlock condition for special mine " + this.id);
            }
            SpecialMineData.DEFAULT_MINES.add(mine);
         }

         return Registry.register(Registries.SPECIAL_MINE, mine.key(), mine);
      }

      private SpecialMine build() {
         Text name = Text.translatable("mine." + this.id + ".name").fillStyle(this.nameStyle);
         Text description = Text.translatable("mine." + this.id + ".description");
         return new SpecialMine(this.id, name, description, this.requiredEffects, this.randomEffectPools, this.unlockConditions, this.parentMines, this.extraRandomCount);
      }
   }
}
