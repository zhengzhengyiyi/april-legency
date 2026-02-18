package net.zhengzhengyiyi.mine;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

import net.zhengzhengyiyi.AprilsLegacy;
import net.zhengzhengyiyi.accessor.MineServerWorldAccessor;
import net.zhengzhengyiyi.component.BiomeMineComponent;
import net.zhengzhengyiyi.component.MineEffectComponent;
import net.zhengzhengyiyi.mine.effect.*;

public record MineEffect(
   String key,
   Text name,
   Text description,
   Text unlockHint,
   @Nullable Identifier itemModel,
   List<MineEffectComponent> components,
   UnlockMode unlockMode,
   List<MineUnlockCondition> unlockedBy,
   List<MineEffect> unlockedAfter,
   int requiredUnlockCount,
   Set<MineEffectGroup> inSets,
   Set<MineEffect> incompatibleWith,
   float experienceModifier,
   int randomWeight,
   RandomizationMode randomizationMode,
   boolean multiplayerOnly,
   Consumer<ServerWorld> onMineEnter,
   Consumer<ServerWorld> onMineLeave,
   Consumer<ServerWorld> onMineTick
) {
   public static final Codec<MineEffect> CODEC = AprilsLegacy.MINE_EFFECTS.getCodec();
   public static final PacketCodec<RegistryByteBuf, MineEffect> field_59138 = PacketCodecs.registryValue(AprilsLegacy.WORLD_EFFECT_KEY);

   public boolean method_69925(Collection<MineEffect> collection) {
      return collection.stream().noneMatch(effect -> method_69921(effect, this));
   }

   public static boolean method_69921(MineEffect a, MineEffect b) {
      return a.incompatibleWith.contains(b) || b.incompatibleWith.contains(a);
   }

   public boolean method_69919(ServerWorld world) {
      return this.method_69926(world)
         && (
            this.requiredUnlockCount == 0
               || AprilsLegacy.MINE_EFFECTS.stream().filter(v -> ((MineServerWorldAccessor)(Object)world).method_69104(v)).limit(this.requiredUnlockCount).count() == (long)this.requiredUnlockCount
         );
   }

   public boolean method_69926(ServerWorld world) {
      return this.multiplayerOnly && world.getServer().isSingleplayer() ? false : this.unlockMode != UnlockMode.NEVER_UNLOCKED;
   }

   public boolean method_69927(ServerWorld world) {
      if (this.multiplayerOnly && world.getServer().isSingleplayer()) {
         return false;
      } else if (this.randomizationMode == RandomizationMode.NEVER) {
         return false;
      } else if (((MineServerWorldAccessor)world).method_69104(this)) {
         return true;
      } else {
         return this.randomizationMode == RandomizationMode.WHEN_UNLOCKABLE ? this.method_69919(world) : false;
      }
   }

   public static MineEffect.Builder builder(String id) {
      return new MineEffect.Builder(id);
   }

   public <T extends MineEffectComponent> Stream<T> method_69922(Class<T> type) {
      return this.components.stream()
         .filter(type::isInstance)
         .map(type::cast);
   }

   public static class Builder {
      private final String field_59139;
      private Style field_59140 = Style.EMPTY.withColor(Formatting.BLUE);
      @Nullable
      private Identifier field_59141;
      private final List<MineEffectComponent> field_59142 = new ArrayList<>();
      private final List<MineUnlockCondition> field_59143 = new ArrayList<>();
      private final List<MineEffect> field_59144 = new ArrayList<>();
      private int field_59145;
      private final Set<MineEffectGroup> field_59146 = new ObjectArraySet<>();
      private float field_59147 = 1.0F;
      private int field_59148 = 100;
      private RandomizationMode field_59149 = RandomizationMode.WHEN_UNLOCKED;
      private UnlockMode field_59150 = UnlockMode.UNLOCKED_ON_WIN;
      private boolean field_59151 = false;
      private final List<Consumer<ServerWorld>> field_59152 = new ArrayList<>();
      private final List<Consumer<ServerWorld>> field_59153 = new ArrayList<>();
      private final List<Consumer<ServerWorld>> field_59154 = new ArrayList<>();
      private final Set<MineEffect> field_59155 = new ObjectArraySet<>();

      public Builder(String id) {
         this.field_59139 = id;
      }

      public MineEffect.Builder style(Style style) {
         this.field_59140 = style;
         return this;
      }

      private MineEffect.Builder method_69931(Identifier id) {
         this.field_59141 = id;
         return this;
      }

      public MineEffect.Builder item(Item item) {
         return this.method_69931(item.getComponents().get(DataComponentTypes.ITEM_MODEL));
      }

      public MineEffect.Builder method_69937(String id) {
         return this.method_69931(Identifier.of(id));
      }

      public MineEffect.Builder method_69930(int weight) {
         this.field_59148 = weight;
         return this;
      }

      public MineEffect.Builder method_69928() {
         this.field_59149 = RandomizationMode.WHEN_UNLOCKABLE;
         return this;
      }

      public MineEffect.Builder method_69946() {
         this.field_59148 = 0;
         this.field_59149 = RandomizationMode.NEVER;
         return this;
      }

      public MineEffect.Builder method_69953() {
         this.field_59148 = 0;
         this.field_59149 = RandomizationMode.NEVER;
         this.field_59150 = UnlockMode.NEVER_UNLOCKED;
         return this;
      }

      public MineEffect.Builder method_69958() {
         this.field_59151 = true;
         return this;
      }

      public MineEffect.Builder method_69945(MineEffectComponent... components) {
         this.field_59142.addAll(List.of(components));
         return this;
      }

      @SafeVarargs
      public final MineEffect.Builder method_69942(RegistryKey<Biome>... biomes) {
         for (RegistryKey<Biome> key : biomes) {
            this.field_59142.add(new BiomeMineComponent.BiomeEntry(key));
         }
         return this;
      }

      public final MineEffect.Builder method_69939(Consumer<DimensionSettingsBuilder> consumer) {
         this.field_59142.add(consumer::accept);
         return this;
      }

      public MineEffect.Builder condition(MineUnlockCondition... conditions) {
         this.field_59143.addAll(List.of(conditions));
         this.field_59150 = UnlockMode.UNLOCKED_BY_CONDITION;
         return this;
      }

      public MineEffect.Builder method_69960() {
         this.field_59150 = UnlockMode.UNLOCKED_ON_WIN;
         return this;
      }

      public MineEffect.Builder method_69962() {
         this.field_59150 = UnlockMode.ALWAYS_UNLOCKED;
         return this;
      }

      public MineEffect.Builder method_69929(float exp) {
         this.field_59147 = exp;
         return this;
      }

      public MineEffect.Builder method_69944(MineEffect... effects) {
         this.field_59144.addAll(List.of(effects));
         return this;
      }

      public MineEffect.Builder method_69947(int count) {
         this.field_59145 = count;
         return this;
      }

      public MineEffect.Builder group(MineEffectGroup group) {
         this.field_59146.add(group);
         return this;
      }

      public MineEffect.Builder method_69952(MineEffect... effects) {
         this.field_59155.addAll(List.of(effects));
         return this;
      }

      public MineEffect.Builder method_69951(Consumer<ServerWorld> action) {
         this.field_59152.add(action);
         return this;
      }

      public MineEffect.Builder method_69957(Consumer<ServerWorld> action) {
         this.field_59153.add(action);
         return this;
      }

      public MineEffect.Builder method_69959(Consumer<ServerPlayerEntity> action) {
         this.field_59152.add(world -> world.getPlayers().forEach(action));
         return this;
      }

      public MineEffect.Builder method_69961(Consumer<ServerWorld> action) {
         this.field_59154.add(action);
         return this;
      }

      public MineEffect build() {
         return Registry.register(AprilsLegacy.MINE_EFFECTS, Identifier.ofVanilla(this.field_59139), this.method_69964());
      }

      private MineEffect method_69964() {
         Text name = Text.translatable("world.effect." + this.field_59139 + ".name").fillStyle(this.field_59140);
         Text desc = Text.translatable("world.effect." + this.field_59139 + ".description");
         Text hint = Text.translatable("world.effect." + this.field_59139 + ".hint");
         
         Consumer<ServerWorld> enterAction = combine(this.field_59152);
         Consumer<ServerWorld> leaveAction = combine(this.field_59153);
         Consumer<ServerWorld> tickAction = combine(this.field_59154);

         MineEffect effect = new MineEffect(
            this.field_59139, name, desc, hint, this.field_59141,
            List.copyOf(this.field_59142), this.field_59150,
            List.copyOf(this.field_59143), List.copyOf(this.field_59144),
            this.field_59145, Set.copyOf(this.field_59146),
            Set.copyOf(this.field_59155), this.field_59147,
            this.field_59148, this.field_59149, this.field_59151,
            enterAction, leaveAction, tickAction
         );
         this.field_59146.forEach(group -> group.add(effect));
         return effect;
      }

      private static Consumer<ServerWorld> combine(List<Consumer<ServerWorld>> consumers) {
         if (consumers.isEmpty()) return world -> {};
         List<Consumer<ServerWorld>> copy = List.copyOf(consumers);
         return world -> copy.forEach(c -> c.accept(world));
      }
   }
}