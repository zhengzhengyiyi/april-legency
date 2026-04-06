package net.zhengzhengyiyi.mine;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.zhengzhengyiyi.mine.effect.MineUnlockCondition;
import net.zhengzhengyiyi.mine.effect.class_11113;

public interface SpecialMineData {
   List<SpecialMine> field_59122 = new ArrayList<>();
   SpecialMine field_59123 = SpecialMine.builder("enderman_boss")
      .required(class_11113.field_59246, class_11113.field_59265, class_11113.field_59242, class_11113.field_59257)
      .register();
   SpecialMine field_59124 = SpecialMine.builder("kuiper_belt").register();
   SpecialMine field_59125 = SpecialMine.builder("small_but_deadly_boss")
      .required(class_11113.field_59257)
      .pool(class_11113.BIOME)
      .register();
   SpecialMine field_59126 = SpecialMine.builder("spooky_scary_skeletons_boss")
      .required(class_11113.field_59265, class_11113.field_59243, class_11113.field_59257)
      .condition(MineUnlockCondition.method_69620(EntityType.SKELETON))
      .register();
   SpecialMine field_59127 = SpecialMine.builder("angry_ghast_boss")
      .required(class_11113.field_59247, class_11113.field_59239, class_11113.field_59257)
      .pool(class_11113.BIOME)
      .condition(MineUnlockCondition.method_69637((serverWorld, advancementEntry) -> advancementEntry.id().equals(Identifier.ofVanilla("feats/return_to_sender"))))
      .register();
   SpecialMine field_59128 = SpecialMine.builder("raid")
      .required(class_11113.field_59246, class_11113.field_59240, class_11113.field_59259, class_11113.field_59257)
      .condition(MineUnlockCondition.method_69620(EntityType.PILLAGER))
      .register();
   SpecialMine field_59129 = SpecialMine.builder("wither_boss")
      .required(class_11113.field_59241, class_11113.field_59257)
      .pool(class_11113.BIOME)
      .condition(MineUnlockCondition.method_69662((serverWorld, serverPlayerEntity, itemStack) -> itemStack.isOf(Items.WITHER_SKELETON_SKULL)))
      .register();
   SpecialMine field_59130 = SpecialMine.builder("ender_dragon_boss")
      .required(class_11113.field_59247)
      .pool(class_11113.BIOME)
      .condition(MineUnlockCondition.method_69642(true, class_11113.field_59162))
      .register();
   SpecialMine field_59131 = SpecialMine.builder("warden_boss")
      .required(class_11113.field_59244, class_11113.field_59257)
      .pool(class_11113.BIOME)
      .condition(
         MineUnlockCondition.method_69650(
            (serverWorld, blockState) -> blockState.isOf(Blocks.AMETHYST_BLOCK)
               || blockState.isOf(Blocks.AMETHYST_CLUSTER)
               || blockState.isOf(Blocks.BUDDING_AMETHYST)
               || blockState.isOf(Blocks.SMALL_AMETHYST_BUD)
               || blockState.isOf(Blocks.MEDIUM_AMETHYST_BUD)
               || blockState.isOf(Blocks.LARGE_AMETHYST_BUD)
         )
      )
      .register();

   static SpecialMine method_69917(Registry<SpecialMine> registry) {
      return field_59129;
   }

   /** Called during mod init to trigger static field initialization and register all SpecialMines. */
   static void init() {
      // Accessing any field forces class loading and registers all SpecialMine instances
      int size = field_59122.size();
   }
}
