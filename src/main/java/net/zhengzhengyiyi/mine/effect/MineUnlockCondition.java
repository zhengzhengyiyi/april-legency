package net.zhengzhengyiyi.mine.effect;

import com.mojang.datafixers.util.Function4;
import java.util.Arrays;
import java.util.Set;
import java.util.function.BiFunction;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.zhengzhengyiyi.mine.MineEffect;
import net.zhengzhengyiyi.mine.SpecialMine;

import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;

public interface MineUnlockCondition {
   default boolean method_69600(ServerWorld serverWorld, ServerPlayerEntity serverPlayerEntity, DamageSource damageSource) {
      return false;
   }

   default boolean method_69601(ServerWorld serverWorld, ServerPlayerEntity serverPlayerEntity, DamageSource damageSource, float f) {
      return false;
   }

   default boolean method_69602(ServerWorld serverWorld, ServerPlayerEntity serverPlayerEntity, Entity entity) {
      return false;
   }

   default boolean method_69598(ServerWorld serverWorld, AdvancementEntry advancementEntry) {
      return false;
   }

   default boolean method_69609(ServerWorld serverWorld, BlockState blockState, BlockPos blockPos) {
      return false;
   }

   default boolean method_69608(ServerWorld serverWorld, BlockState blockState) {
      return false;
   }

   default boolean method_69644(ServerWorld serverWorld, BlockState blockState, BlockPos blockPos) {
      return false;
   }

   default boolean method_69604(ServerWorld serverWorld, ServerPlayerEntity serverPlayerEntity, ItemStack itemStack) {
      return false;
   }

   default boolean method_69643(ServerWorld serverWorld, ServerPlayerEntity serverPlayerEntity, ItemStack itemStack) {
      return false;
   }

   default boolean method_69652(ServerWorld serverWorld, ServerPlayerEntity serverPlayerEntity, ItemStack itemStack) {
      return false;
   }

   default boolean method_69603(ServerWorld serverWorld, ServerPlayerEntity serverPlayerEntity, AnimalEntity animalEntity, ItemStack itemStack) {
      return false;
   }

   default boolean method_69599(ServerWorld serverWorld, ServerPlayerEntity serverPlayerEntity, class_10976 arg) {
      return false;
   }

   default boolean method_69606(ServerWorld serverWorld, ServerPlayerEntity serverPlayerEntity, MineEffect arg) {
      return false;
   }

   default boolean method_69607(ServerWorld serverWorld, ServerPlayerEntity serverPlayerEntity, Set<MineEffect> set, boolean bl) {
      return false;
   }

   default boolean method_69605(ServerWorld serverWorld, ServerPlayerEntity serverPlayerEntity, SpecialMine arg, boolean bl) {
      return false;
   }

   static MineUnlockCondition method_69621(Function4<ServerWorld, ServerPlayerEntity, DamageSource, Float, Boolean> function4) {
      return new MineUnlockCondition() {
         @Override
         public boolean method_69601(ServerWorld serverWorld, ServerPlayerEntity serverPlayerEntity, DamageSource damageSource, float f) {
            return (Boolean)function4.apply(serverWorld, serverPlayerEntity, damageSource, f);
         }
      };
   }

   static MineUnlockCondition method_69639(TriFunction<ServerWorld, ServerPlayerEntity, DamageSource, Boolean> triFunction) {
      return new MineUnlockCondition() {
         @Override
         public boolean method_69600(ServerWorld serverWorld, ServerPlayerEntity serverPlayerEntity, DamageSource damageSource) {
            return (Boolean)triFunction.apply(serverWorld, serverPlayerEntity, damageSource);
         }
      };
   }

   static MineUnlockCondition method_69620(EntityType<? extends Entity> entityType) {
      return method_69596(1.0F, entityType);
   }

   @SafeVarargs
   static MineUnlockCondition method_69596(float f, EntityType<? extends Entity>... entityTypes) {
      return new MineUnlockCondition() {
         @Override
         public boolean method_69602(ServerWorld serverWorld, ServerPlayerEntity serverPlayerEntity, Entity entity) {
            return Arrays.stream(entityTypes).anyMatch(entityType -> entityType == entity.getType()) && serverWorld.random.nextDouble() < f;
         }
      };
   }

   static MineUnlockCondition method_69651(TriFunction<ServerWorld, ServerPlayerEntity, Entity, Boolean> triFunction) {
      return new MineUnlockCondition() {
         @Override
         public boolean method_69602(ServerWorld serverWorld, ServerPlayerEntity serverPlayerEntity, Entity entity) {
            return (Boolean)triFunction.apply(serverWorld, serverPlayerEntity, entity);
         }
      };
   }

   static MineUnlockCondition method_69637(BiFunction<ServerWorld, AdvancementEntry, Boolean> biFunction) {
      return new MineUnlockCondition() {
         @Override
         public boolean method_69598(ServerWorld serverWorld, AdvancementEntry advancementEntry) {
            return biFunction.apply(serverWorld, advancementEntry);
         }
      };
   }

   static MineUnlockCondition method_69650(BiFunction<ServerWorld, BlockState, Boolean> biFunction) {
      return new MineUnlockCondition() {
         @Override
         public boolean method_69609(ServerWorld serverWorld, BlockState blockState, BlockPos blockPos) {
            return biFunction.apply(serverWorld, blockState);
         }
      };
   }

   static MineUnlockCondition method_69656(TriFunction<ServerWorld, BlockState, BlockPos, Boolean> triFunction) {
      return new MineUnlockCondition() {
         @Override
         public boolean method_69609(ServerWorld serverWorld, BlockState blockState, BlockPos blockPos) {
            return (Boolean)triFunction.apply(serverWorld, blockState, blockPos);
         }
      };
   }

   static MineUnlockCondition method_69655(BiFunction<ServerWorld, BlockState, Boolean> biFunction) {
      return new MineUnlockCondition() {
         @Override
         public boolean method_69608(ServerWorld serverWorld, BlockState blockState) {
            return biFunction.apply(serverWorld, blockState);
         }
      };
   }

   static MineUnlockCondition method_69657(BiFunction<ServerWorld, BlockState, Boolean> biFunction) {
      return new MineUnlockCondition() {
         @Override
         public boolean method_69644(ServerWorld serverWorld, BlockState blockState, BlockPos blockPos) {
            return biFunction.apply(serverWorld, blockState);
         }
      };
   }

   static MineUnlockCondition itemUse(TriFunction<ServerWorld, BlockState, BlockPos, Boolean> triFunction) {
      return new MineUnlockCondition() {
         @Override
         public boolean method_69644(ServerWorld serverWorld, BlockState blockState, BlockPos blockPos) {
            return (Boolean)triFunction.apply(serverWorld, blockState, blockPos);
         }
      };
   }

   static MineUnlockCondition method_69660(TriFunction<ServerWorld, ServerPlayerEntity, ItemStack, Boolean> triFunction) {
      return new MineUnlockCondition() {
         @Override
         public boolean method_69604(ServerWorld serverWorld, ServerPlayerEntity serverPlayerEntity, ItemStack itemStack) {
            return (Boolean)triFunction.apply(serverWorld, serverPlayerEntity, itemStack);
         }
      };
   }

   static MineUnlockCondition method_69661(TriFunction<ServerWorld, ServerPlayerEntity, ItemStack, Boolean> triFunction) {
      return new MineUnlockCondition() {
         @Override
         public boolean method_69652(ServerWorld serverWorld, ServerPlayerEntity serverPlayerEntity, ItemStack itemStack) {
            return (Boolean)triFunction.apply(serverWorld, serverPlayerEntity, itemStack);
         }
      };
   }

   static MineUnlockCondition method_69662(TriFunction<ServerWorld, ServerPlayerEntity, ItemStack, Boolean> triFunction) {
      return new MineUnlockCondition() {
         @Override
         public boolean method_69643(ServerWorld serverWorld, ServerPlayerEntity serverPlayerEntity, ItemStack itemStack) {
            return (Boolean)triFunction.apply(serverWorld, serverPlayerEntity, itemStack);
         }
      };
   }

   static MineUnlockCondition method_69646(Function4<ServerWorld, ServerPlayerEntity, AnimalEntity, ItemStack, Boolean> function4) {
      return new MineUnlockCondition() {
         @Override
         public boolean method_69603(ServerWorld serverWorld, ServerPlayerEntity serverPlayerEntity, AnimalEntity animalEntity, ItemStack itemStack) {
            return (Boolean)function4.apply(serverWorld, serverPlayerEntity, animalEntity, itemStack);
         }
      };
   }

   static MineUnlockCondition method_69638(RegistryEntry<net.zhengzhengyiyi.unlock.PlayerUnlock> registryEntry) {
      return new MineUnlockCondition() {
         @Override
         public boolean method_69599(ServerWorld serverWorld, ServerPlayerEntity serverPlayerEntity, class_10976 arg) {
            return arg.key().equals(registryEntry.value().key());
         }
      };
   }

   static MineUnlockCondition method_69636(MineEffect arg) {
      return new MineUnlockCondition() {
         @Override
         public boolean method_69606(ServerWorld serverWorld, ServerPlayerEntity serverPlayerEntity, MineEffect arg) {
            return arg.key().equals(arg.key());
         }
      };
   }

   static MineUnlockCondition method_69642(boolean bl, MineEffect... args) {
      return new MineUnlockCondition() {
         @Override
         public boolean method_69607(ServerWorld serverWorld, ServerPlayerEntity serverPlayerEntity, Set<MineEffect> set, boolean bl) {
            if (bl && !bl) {
               return false;
            } else {
               for (MineEffect lv : args) {
                  if (!set.contains(lv)) {
                     return false;
                  }
               }

               return true;
            }
         }
      };
   }

   static MineUnlockCondition method_69659(BiFunction<ServerWorld, ServerPlayerEntity, Boolean> biFunction) {
      return new MineUnlockCondition() {
         @Override
         public boolean method_69607(ServerWorld serverWorld, ServerPlayerEntity serverPlayerEntity, Set<MineEffect> set, boolean bl) {
            return !bl ? false : biFunction.apply(serverWorld, serverPlayerEntity);
         }
      };
   }

   static MineUnlockCondition method_69640(boolean bl) {
      return new MineUnlockCondition() {
         @Override
         public boolean method_69605(ServerWorld serverWorld, ServerPlayerEntity serverPlayerEntity, SpecialMine arg, boolean bl) {
            return !bl || bl;
         }
      };
   }

   static MineUnlockCondition method_69641(boolean bl, SpecialMine arg) {
      return new MineUnlockCondition() {
         @Override
         public boolean method_69605(ServerWorld serverWorld, ServerPlayerEntity serverPlayerEntity, SpecialMine arg, boolean bl) {
            return bl && !bl ? false : arg == arg;
         }
      };
   }

   static void method_69624(World world, ServerPlayerEntity serverPlayerEntity, DamageSource damageSource, float f) {
      method_69630(
         world, serverPlayerEntity, serverPlayerEntity.getEntityPos(), (serverWorld, arg) -> arg.method_69601(serverWorld, serverPlayerEntity, damageSource, f)
      );
   }

   static void method_69623(World world, ServerPlayerEntity serverPlayerEntity, DamageSource damageSource) {
      method_69630(
         world, serverPlayerEntity, serverPlayerEntity.getEntityPos(), (serverWorld, arg) -> arg.method_69600(serverWorld, serverPlayerEntity, damageSource)
      );
   }

   static void method_69625(World world, ServerPlayerEntity serverPlayerEntity, Entity entity) {
      method_69630(world, serverPlayerEntity, entity.getEntityPos(), (serverWorld, arg) -> arg.method_69602(serverWorld, serverPlayerEntity, entity));
   }

   static void method_69622(World world, ServerPlayerEntity serverPlayerEntity, AdvancementEntry advancementEntry) {
      method_69630(world, serverPlayerEntity, serverPlayerEntity.getEntityPos(), (serverWorld, arg) -> arg.method_69598(serverWorld, advancementEntry));
   }

   static void method_69631(World world, ServerPlayerEntity serverPlayerEntity, BlockPos blockPos, BlockState blockState) {
      method_69630(world, serverPlayerEntity, blockPos.toCenterPos(), (serverWorld, arg) -> arg.method_69609(serverWorld, blockState, blockPos));
   }

   static void method_69610(ServerWorld serverWorld, BlockPos blockPos, BlockState blockState) {
      method_69630(serverWorld, null, blockPos.toCenterPos(), (serverWorldx, arg) -> arg.method_69608(serverWorldx, blockState));
   }

   static void method_69648(World world, ServerPlayerEntity serverPlayerEntity, BlockPos blockPos, BlockState blockState) {
      method_69630(world, serverPlayerEntity, blockPos.toCenterPos(), (serverWorld, arg) -> arg.method_69644(serverWorld, blockState, blockPos));
   }

   static void method_69627(World world, ServerPlayerEntity serverPlayerEntity, ItemStack itemStack) {
      method_69630(world, serverPlayerEntity, serverPlayerEntity.getEntityPos(), (serverWorld, arg) -> arg.method_69604(serverWorld, serverPlayerEntity, itemStack));
   }

   static void method_69647(World world, ServerPlayerEntity serverPlayerEntity, ItemStack itemStack) {
      method_69630(world, serverPlayerEntity, serverPlayerEntity.getEntityPos(), (serverWorld, arg) -> arg.method_69652(serverWorld, serverPlayerEntity, itemStack));
   }

   static void method_69654(World world, ServerPlayerEntity serverPlayerEntity, ItemStack itemStack) {
      method_69630(world, serverPlayerEntity, serverPlayerEntity.getEntityPos(), (serverWorld, arg) -> arg.method_69643(serverWorld, serverPlayerEntity, itemStack));
   }

   static void method_69626(World world, ServerPlayerEntity serverPlayerEntity, AnimalEntity animalEntity, ItemStack itemStack) {
      method_69630(
         world,
         serverPlayerEntity,
         serverPlayerEntity.getEntityPos(),
         (serverWorld, arg) -> arg.method_69603(serverWorld, serverPlayerEntity, animalEntity, itemStack)
      );
   }

   static void method_69633(World world, ServerPlayerEntity serverPlayerEntity, RegistryEntry<class_10976> registryEntry) {
      method_69630(
         world, serverPlayerEntity, serverPlayerEntity.getEntityPos(), (serverWorld, arg) -> arg.method_69599(serverWorld, serverPlayerEntity, registryEntry.value())
      );
   }

   static void method_69629(World world, ServerPlayerEntity serverPlayerEntity, MineEffect arg) {
      method_69630(world, serverPlayerEntity, serverPlayerEntity.getEntityPos(), (serverWorld, arg2) -> arg2.method_69606(serverWorld, serverPlayerEntity, arg));
   }

   static void method_69632(World world, ServerPlayerEntity serverPlayerEntity, Set<MineEffect> set, boolean bl) {
      method_69630(world, serverPlayerEntity, serverPlayerEntity.getEntityPos(), (serverWorld, arg) -> arg.method_69607(serverWorld, serverPlayerEntity, set, bl));
   }

   static void method_69628(World world, ServerPlayerEntity serverPlayerEntity, SpecialMine arg, boolean bl) {
      method_69630(world, serverPlayerEntity, serverPlayerEntity.getEntityPos(), (serverWorld, arg2) -> arg2.method_69605(serverWorld, serverPlayerEntity, arg, bl));
   }

   private static void method_69630(
      World world, @Nullable ServerPlayerEntity serverPlayerEntity, Vec3d vec3d, BiFunction<ServerWorld, MineUnlockCondition, Boolean> biFunction
   ) {
      if (world instanceof ServerWorld serverWorld) {
         net.zhengzhengyiyi.accessor.MineServerWorldAccessor mineWorld = (net.zhengzhengyiyi.accessor.MineServerWorldAccessor)(Object)serverWorld;
         for (MineEffect lv : net.zhengzhengyiyi.AprilsLegacy.MINE_EFFECT) {
            for (MineUnlockCondition lv2 : lv.unlockedBy()) {
               if (!mineWorld.hasMineEffect(lv)) {
                  boolean bl = true;

                  for (MineEffect lv3 : lv.unlockedAfter()) {
                     if (!mineWorld.hasMineEffect(lv3)) {
                        bl = false;
                        break;
                     }
                  }

                  if (bl && biFunction.apply(serverWorld, lv2)) {
                     mineWorld.dropOrUnlockMineEffect(vec3d, lv, serverPlayerEntity);
                  }
               }
            }
         }

         for (SpecialMine lv4 : net.zhengzhengyiyi.AprilsLegacy.SPECIAL_MINE) {
            for (MineUnlockCondition lv2x : lv4.unlockedBy()) {
               if (!mineWorld.hasSpecialMine(lv4)) {
                  boolean bl = true;

                  for (SpecialMine lv5 : lv4.unlockedAfter()) {
                     if (!mineWorld.hasSpecialMine(lv5)) {
                        bl = false;
                        break;
                     }
                  }

                  if (bl && biFunction.apply(serverWorld, lv2x)) {
                     mineWorld.unlockSpecialMine(lv4);
                  }
               }
            }
         }
      }
   }
}
