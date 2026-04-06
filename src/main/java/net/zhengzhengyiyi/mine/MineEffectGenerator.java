package net.zhengzhengyiyi.mine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.level.ServerWorldProperties;
import net.zhengzhengyiyi.AprilsLegacy;
import net.zhengzhengyiyi.accessor.LevelPropertiesAccessor;
import net.zhengzhengyiyi.accessor.MineServerWorldAccessor;
import net.zhengzhengyiyi.block.MineCrafterBlockEntity;
import net.zhengzhengyiyi.block.ModBlocks;
import net.zhengzhengyiyi.component.ModDataComponentTypes;
import net.zhengzhengyiyi.item.ModItems;
import net.zhengzhengyiyi.mine.effect.MineEffectGroup;
import net.zhengzhengyiyi.mine.effect.class_11113;

public class MineEffectGenerator extends ScreenHandler {
   public static final List<Vec2f> field_58807 = List.of(
      new Vec2f(93.0F, 82.0F),
      new Vec2f(117.0F, 69.0F),
      new Vec2f(115.0F, 41.0F),
      new Vec2f(85.0F, 31.0F),
      new Vec2f(57.0F, 54.0F),
      new Vec2f(63.0F, 89.0F),
      new Vec2f(128.0F, 96.0F),
      new Vec2f(150.0F, 68.0F),
      new Vec2f(148.0F, 30.0F),
      new Vec2f(111.0F, 15.0F),
      new Vec2f(60.0F, 15.0F),
      new Vec2f(31.0F, 32.0F),
      new Vec2f(22.0F, 57.0F),
      new Vec2f(27.0F, 89.0F)
   );
   private final class_11039 field_58810 = new class_11039(field_58807);
   private final ScreenHandlerContext field_58811;
   private final Consumer<RegistryKey<DimensionOptions>> field_58812;
   private final List<MineGeneratorSlot> field_58813 = new ArrayList<>();
   private final class_11044 field_58814;
   private final List<class_11041> field_58815 = new ArrayList<>();
   private final List<class_11042> field_58816 = new ArrayList<>();
   public static final int field_58808 = 9;
   public static final int field_58809 = 4;
   private static final int field_58817 = 6;
   private static final int field_58818 = 10;
   private int field_58819;
   private int field_58820;
   private int field_58802;
   private int field_58803;
   private final Optional<ServerWorld> field_58804;
   private final Optional<ServerPlayerEntity> field_58805;
   private final Optional<BlockPos> field_58806;

   public MineEffectGenerator(int i, PlayerInventory playerInventory, List<Integer> list) {
      this(i, playerInventory, ScreenHandlerContext.EMPTY, new SimpleInventory(99), registryKey -> {}, list);
   }

   public MineEffectGenerator(
      int i,
      PlayerInventory playerInventory,
      ScreenHandlerContext screenHandlerContext,
      Inventory inventory,
      Consumer<RegistryKey<DimensionOptions>> consumer,
      List<Integer> list
   ) {
      super(net.zhengzhengyiyi.screen.ModScreenHandlerType.MINE_CRAFTER, i);
      this.method_69537(list);
      checkSize(inventory, 99);
      this.field_58811 = screenHandlerContext;
      this.field_58812 = consumer;
      inventory.onOpen(playerInventory.player);
      if (playerInventory.player instanceof ServerPlayerEntity serverPlayerEntity) {
         this.field_58805 = Optional.of(serverPlayerEntity);
      } else {
         this.field_58805 = Optional.empty();
      }

      this.field_58806 = screenHandlerContext.get((world, blockPos) -> blockPos);
      this.field_58804 = screenHandlerContext.get((world, blockPos) -> world instanceof ServerWorld serverWorld ? serverWorld : null);
      this.field_58814 = (class_11044)this.addSlot(new class_11044(inventory, 0, 88, 58, this));
      this.method_69538(inventory);
      this.method_69551();
      this.method_69552();
      this.method_69520();
      this.method_69521();
      this.addPlayerInventory(playerInventory);
      this.onContentChanged(inventory);
   }

   private void method_69537(List<Integer> list) {
      this.field_58802 = list.get(0);
      this.field_58803 = list.get(1);
      this.field_58819 = MineCrafterBlockEntity.getMaxInventorySize(this.field_58802);
      this.field_58820 = MineCrafterBlockEntity.getRewardCount(this.field_58802);
   }

   public int method_69539() {
      return this.field_58802;
   }

   public int method_69540() {
      return this.field_58803;
   }

   private void method_69538(Inventory inventory) {
      for (int i = 0; i < 50; i++) {
    	  MineGeneratorSlot lv = (MineGeneratorSlot)this.addSlot(new MineGeneratorSlot(inventory, 1 + i, 0, 0, this, true));
         lv.method_69556(false);
         this.field_58813.add(lv);
      }
   }

   private void method_69551() {
      float f = (float)(field_58807.size() - 1) / this.field_58819;
      int i = 0;

      for (MineGeneratorSlot lv : this.field_58813) {
         if (i >= this.field_58819) {
            lv.locked = false;
            lv.method_69556(false);
         } else {
            float g = f * i;
            Vec2f vec2f = field_58807.get((int)Math.floor(g));
            Vec2f vec2f2 = field_58807.get((int)Math.floor(g) + 1);
            Vec2f vec2f3 = vec2f.add(vec2f2.add(vec2f.negate()).multiply(g - MathHelper.floor(g)));
            boolean bl = i < this.field_58820;
            if (!lv.locked && bl) {
               lv.setStackNoCallbacks(ItemStack.EMPTY);
            }

            lv.locked = bl;
            lv.setPos((int)vec2f3.x, (int)vec2f3.y);
            lv.method_69556(true);
            i++;
         }
      }
   }

   private void method_69552() {
      if (this.field_58804.isPresent()) {
         if (!this.method_69541()) {
            boolean bl = this.method_69543().findAny().isEmpty();
            if (bl && this.field_58814.getStack().isEmpty()) {
               Optional<SpecialMine> optional = ((net.zhengzhengyiyi.accessor.MineServerWorldAccessor)(Object)this.field_58804.get()).getCurrentSpecialMine();
               if (optional.isPresent()) {
                  ItemStack itemStack = new ItemStack(ModItems.MINE_ITEM);
                  itemStack.set(ModDataComponentTypes.WORLD_MODIFIERS, new class_11056(new ArrayList<>(), false));
                  itemStack.set(DataComponentTypes.ITEM_NAME, optional.get().name());
                  itemStack.set(DataComponentTypes.LORE, new LoreComponent(List.of(optional.get().description())));
                  itemStack.set(ModDataComponentTypes.SPECIAL_MINE, optional.get());
                  this.field_58814.setStackNoCallbacks(itemStack);
                  this.sendContentUpdates();
                  return;
               }
            }

            List<MineEffect> list = this.method_69518();
            List<MineEffect> list2 = new ArrayList<>();

            for (MineGeneratorSlot lv : this.field_58813) {
               if (lv.locked && !lv.hasStack()) {
                  if (!list.isEmpty()) {
                     int i = this.method_69519();
                     lv.setStackNoCallbacks(class_11113.method_70023(Text.stringifiedTranslatable("world.mine.base", i), false, List.copyOf(list)));
                     list2.addAll(list);
                     list.clear();
                  } else {
                     Optional<MineEffect> optional2 = method_69525(this.field_58804.get(), list2, this.method_69543().collect(Collectors.toSet()));
                     optional2.ifPresent(arg2 -> {
                        lv.setStackNoCallbacks(class_11113.method_70013(arg2, false));
                        list2.add(arg2);
                     });
                  }
               }
            }
         }
      }
   }

   public boolean method_69541() {
      return this.field_58814.hasStack() && this.field_58814.getStack().contains(ModDataComponentTypes.SPECIAL_MINE);
   }

   public static Optional<MineEffect> method_69525(ServerWorld serverWorld, List<MineEffect> list, Set<MineEffect> set) {
      Pool.Builder<MineEffect> builder = Pool.builder();

      for (MineEffect lv : AprilsLegacy.MINE_EFFECT) {
         if (!lv.inSets().stream().anyMatch(MineEffectGroup::method_69967)
            && lv.randomWeight() > 0
            && lv.method_69925(list)
            && !set.contains(lv)
            && lv.method_69927(serverWorld)) {
            boolean bl = ((MineServerWorldAccessor)(Object)serverWorld).hasMineEffect(lv);
            if (bl) {
               builder.add(lv, lv.randomWeight());
            } else {
               builder.add(lv, (int)(lv.randomWeight() * 0.1F));
            }
         }
      }

      return builder.build().getOrEmpty(serverWorld.getRandom());
   }

   private List<MineEffect> method_69518() {
      ServerWorld serverWorld = this.field_58804.get();
      Random random = serverWorld.getRandom();
      List<MineEffect> list = new ArrayList<>();

      for (MineEffectGroup lv : AprilsLegacy.MINE_EFFECTS) {
         List<MineEffect> eligible = lv.method_69965().stream().filter(arg -> arg.method_69927(serverWorld)).toList();
         if (!eligible.isEmpty()) {
            MineEffect picked = this.method_69533(eligible, list, random);
            if (picked != null) list.add(picked);
         }
      }

      Collections.shuffle(list);
      return list;
   }

   private MineEffect method_69533(List<MineEffect> list, List<MineEffect> list2, Random random) {
      if (list.isEmpty()) {
         return null;
      } else {
         MineEffect lv = Util.getRandom(list, random);
         return lv.method_69925(list2) ? lv : this.method_69533(list.stream().filter(arg2 -> arg2 != lv).toList(), list2, random);
      }
   }

   private int method_69519() {
      if (this.field_58804.isEmpty()) {
         return 0;
      } else {
         ServerWorldProperties serverWorldProperties = this.field_58804.get().getServer().getSaveProperties().getMainWorldProperties();
         return ((LevelPropertiesAccessor) serverWorldProperties).getTotalMineExp() + 1;
      }
   }

   private void method_69520() {
      int i = MathHelper.ceil(AprilsLegacy.MINE_EFFECT.size() / 9.0F);
      SimpleInventory simpleInventory = new SimpleInventory(i * 9);
      if (this.field_58804.isPresent()) {
         for (MineEffect lv : AprilsLegacy.MINE_EFFECT) {
            ItemStack itemStack = class_11113.method_70013(lv, false);
            if (lv.method_69926(this.field_58804.get()) && ((MineServerWorldAccessor) this.field_58804.get()).hasMineEffect(lv)) {
               simpleInventory.addStack(itemStack);
            }
         }
      }

      for (int j = 0; j < i; j++) {
         for (int k = 0; k < 9; k++) {
            this.field_58815.add((class_11041)this.addSlot(new class_11041(simpleInventory, j * 9 + k, 8 + k * 18, 124 + j * 18, this)));
         }
      }
   }

   private void method_69521() {
      SimpleInventory simpleInventory = new SimpleInventory(60);
      if (this.field_58804.isPresent()) {
         boolean bl = this.field_58804.get().getServer().isSingleplayer();
         List<MineEffect> list = new ArrayList<>(AprilsLegacy.MINE_EFFECT.stream().toList());
         Collections.shuffle(list);

         for (MineEffect lv : list) {
            if (!lv.multiplayerOnly() || !bl) {
               ItemStack itemStack = class_11113.method_70013(lv, false);
               if (simpleInventory.canInsert(itemStack) && !((MineServerWorldAccessor)this.field_58804.get()).hasMineEffect(lv) && lv.method_69919(this.field_58804.get())) {
                  itemStack.set(ModDataComponentTypes.WORLD_EFFECT_UHINT, Unit.INSTANCE);
                  simpleInventory.addStack(itemStack);
               }
            }
         }
      }

      for (int i = 0; i < 10; i++) {
         for (int j = 0; j < 6; j++) {
            this.field_58816.add((class_11042)this.addSlot(new class_11042(simpleInventory, i * 6 + j, 205 + j * 18, 20 + i * 18, this)));
         }
      }
   }

   private void addPlayerInventory(PlayerInventory playerInventory) {
      // Player inventory (3 rows of 9)
      for (int row = 0; row < 3; row++) {
         for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 140 + row * 18));
         }
      }

      // Player hotbar
      for (int col = 0; col < 9; col++) {
         this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 198));
      }
   }

   public class_11039 method_69542() {
      return this.field_58810;
   }

   public void method_69454(List<Integer> list) {
      this.method_69537(list);
      this.method_69551();
      this.method_69552();
   }

   protected void method_69526(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack) {
      class_11056 lv = itemStack.getOrDefault(ModDataComponentTypes.WORLD_MODIFIERS, class_11056.field_58859);
      if (!lv.effects().isEmpty() || this.method_69541()) {
         List<MineEffect> list = lv.effects();
         Optional<SpecialMine> optional = Optional.ofNullable(itemStack.get(ModDataComponentTypes.SPECIAL_MINE));
         if (optional.isPresent()) {
            list = optional.get().generateEffects(serverPlayerEntity.getEntityWorld());
         }

         System.out.println("[MineEffectGenerator] Creating mine dimension...");
         class_10967.class_10970 lv2 = class_10967.method_69062(serverPlayerEntity.getEntityWorld().getServer(), list, optional);
         RegistryKey<DimensionOptions> registryKey = lv2.id();
         System.out.println("[MineEffectGenerator] Dimension key: " + registryKey);
         
         System.out.println("[MineEffectGenerator] Calling synchronize to create Fantasy dimension...");
         lv2.synchronize(); // actually create the Fantasy dimension
         System.out.println("[MineEffectGenerator] Fantasy dimension created");
         
         // Verify the world is accessible
         RegistryKey<World> worldKey = RegistryKeys.toWorldKey(registryKey);
         ServerWorld createdWorld = serverPlayerEntity.getServer().getWorld(worldKey);
         System.out.println("[MineEffectGenerator] Verifying world exists: " + (createdWorld != null));
         if (createdWorld != null) {
            System.out.println("[MineEffectGenerator] World dimension key: " + createdWorld.getRegistryKey());
         } else {
            System.out.println("[MineEffectGenerator] WARNING: World not found immediately after creation!");
         }
         
         itemStack.set(ModDataComponentTypes.DIMENSION_ID, registryKey);
         itemStack.set(ModDataComponentTypes.MINE_ACTIVE, Unit.INSTANCE);
         this.field_58812.accept(registryKey);
         serverPlayerEntity.closeHandledScreen();
      }
   }

   @Override
   public boolean canUse(PlayerEntity player) {
      return canUse(this.field_58811, player, ModBlocks.MINE_CRAFTER);
   }

   @Override
   public void onContentChanged(Inventory inventory) {
      if (!this.method_69547() && !this.method_69548() && !this.method_69541() && !this.field_58804.isEmpty()) {
         if (this.method_69545().stream().noneMatch(arg -> arg.hasStack() && !arg.locked)
            && this.method_69546().stream().anyMatch(arg -> arg.hasStack() && arg.method_69517())) {
            this.field_58814.setStackNoCallbacks(ItemStack.EMPTY);
         } else {
            List<MineEffect> list = this.method_69543().distinct().toList();
            if (list.isEmpty()) {
               this.field_58814.setStackNoCallbacks(ItemStack.EMPTY);
            } else {
               ItemStack itemStack = new ItemStack(ModItems.MINE_ITEM);
               itemStack.set(ModDataComponentTypes.WORLD_MODIFIERS, new class_11056(list, false));
               itemStack.set(DataComponentTypes.ITEM_NAME, this.method_69522());
               this.field_58814.setStackNoCallbacks(itemStack);
               this.sendContentUpdates();
            }
         }
      }
   }

   private Text method_69522() {
      int i = this.method_69519();
      return i == 0 ? Text.translatable("world.mine.next") : Text.stringifiedTranslatable("world.mine", i);
   }

   protected Stream<MineEffect> method_69543() {
      Map<MineEffectGroup, MineEffect> map = new HashMap<>();
      List<MineEffect> list = this.field_58813
         .stream()
         .map(Slot::getStack)
         .map(itemStack -> itemStack.getOrDefault(ModDataComponentTypes.WORLD_MODIFIERS, class_11056.field_58859).effects())
         .flatMap(Collection::stream)
         .toList();

      for (MineEffect lv : list) {
         for (MineEffectGroup lv2 : lv.inSets()) {
            if (lv2.method_69967()) {
               map.put(lv2, lv);
            }
         }
      }

      return list.stream().filter(arg -> !method_69532(arg, map));
   }

   private static boolean method_69532(MineEffect arg, Map<MineEffectGroup, MineEffect> map) {
      for (MineEffectGroup lv : arg.inSets()) {
         if (lv.method_69967() && map.get(lv) != arg) {
            return true;
         }
      }

      return false;
   }

   @Override
   public ItemStack quickMove(PlayerEntity player, int slot) {
      ItemStack itemStack = ItemStack.EMPTY;
      Slot slot2 = this.slots.get(slot);
      if (slot2.hasStack()) {
         ItemStack itemStack2 = slot2.getStack();
         itemStack = itemStack2.copy();
         
         // If clicking the output slot
         if (slot == this.field_58814.id) {
            return ItemStack.EMPTY;
         }

         // If clicking ingredient slots
         if (slot > 0 && slot <= this.field_58813.getLast().id) {
            slot2.setStackNoCallbacks(ItemStack.EMPTY);
            return ItemStack.EMPTY;
         }

         // If clicking discovered effects slots
         if (slot >= this.field_58815.getFirst().id && slot <= this.field_58815.getLast().id) {
            this.insertItem(itemStack2.copy(), this.field_58820, this.field_58819 + 1, false);
            return ItemStack.EMPTY;
         }

         // If clicking player inventory, try to move to ingredient slots
         if (itemStack2.isOf(ModItems.MINE_INGREDIENT)) {
            if (!this.insertItem(itemStack2, 1, this.field_58819 + 1, false)) {
               return ItemStack.EMPTY;
            }
         }
      }

      return itemStack;
   }

   @Override
   public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
      if ((actionType == SlotActionType.PICKUP || actionType == SlotActionType.QUICK_MOVE) && (button == 0 || button == 1) && slotIndex == -999) {
         this.setCursorStack(ItemStack.EMPTY);
      }

      super.onSlotClick(slotIndex, button, actionType, player);
   }

   public class_11044 method_69544() {
      return this.field_58814;
   }

   public List<MineGeneratorSlot> method_69545() {
      return this.field_58813;
   }

   public List<class_11041> method_69546() {
      return this.field_58815;
   }

   public boolean method_69547() {
      return this.field_58814.hasStack() && this.field_58814.getStack().get(ModDataComponentTypes.MINE_ACTIVE) != null;
   }

   public boolean method_69548() {
      return this.field_58814.hasStack() && this.field_58814.getStack().get(ModDataComponentTypes.MINE_COMPLETED) != null;
   }

   public boolean method_69549() {
      return this.field_58814.hasStack() && this.field_58814.getStack().getOrDefault(ModDataComponentTypes.MINE_COMPLETED, false);
   }

   public boolean method_69550() {
      return this.field_58816.stream().noneMatch(Slot::hasStack);
   }

   @Override
   public void onClosed(PlayerEntity player) {
      if (player instanceof ServerPlayerEntity) {
         ItemStack itemStack = this.getCursorStack();
         if (!itemStack.isEmpty()) {
            this.setCursorStack(ItemStack.EMPTY);
         }
      }
   }
}
