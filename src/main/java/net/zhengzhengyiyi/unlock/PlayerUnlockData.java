package net.zhengzhengyiyi.unlock;

import it.unimi.dsi.fastutil.Pair;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.BreezeEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.ItemTags;
import net.zhengzhengyiyi.mine.effect.MineUnlockCondition;
import net.zhengzhengyiyi.mine.effect.class_11113;

import java.util.List;

/**
 * Mirrors Craftmine class_10979 — defines all player unlocks.
 * These are the purchasable upgrades in the unlock tree.
 */
public abstract class PlayerUnlockData {

    private static final String EXCLUSIVE_PET = "exclusive_pet";
    private static final List<Item> BUNDLE_ITEMS = List.of(
        Items.DIRT, Items.DIAMOND, Items.WOODEN_SWORD, Items.MACE,
        Items.TNT, Items.POISONOUS_POTATO, Items.NETHERITE_PICKAXE,
        Items.REDSTONE, Items.GOLDEN_APPLE, Items.CAKE, Items.BONE, Items.MUSIC_DISC_CAT
    );

    // ─── EXPLORATION TREE ───────────────────────────────────────────────────────

    public static RegistryEntry<PlayerUnlock> EXPLORATION = PlayerUnlock.root("exploration")
        .icon(Items.LEATHER_BOOTS)
        .modifyAttribute(EntityAttributes.MOVEMENT_SPEED, 0.1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
        .modifyAttribute(EntityAttributes.SNEAKING_SPEED, 0.2, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
        .register();

    public static RegistryEntry<PlayerUnlock> ENDER_PEARL_STARTER = PlayerUnlock.child("ender_pearl_starter", EXPLORATION)
        .icon(Items.ENDER_PEARL)
        .giveStacks(Items.ENDER_PEARL.getDefaultStack().copyWithCount(5))
        .visibility(PlayerUnlock.Visibility.MYSTERY)
        .visibleWhen(MineUnlockCondition.method_69620(EntityType.ENDERMAN))
        .price(5)
        .register();

    public static RegistryEntry<PlayerUnlock> MORE_PEARLS = PlayerUnlock.child("more_pearls", ENDER_PEARL_STARTER)
        .icon(Items.ENDER_PEARL)
        .visibility(PlayerUnlock.Visibility.INVISIBLE)
        .visibleWhen(MineUnlockCondition.method_69638(ENDER_PEARL_STARTER))
        .giveStacks(Items.ENDER_PEARL.getDefaultStack().copyWithCount(10))
        .price(10)
        .register();

    public static RegistryEntry<PlayerUnlock> MORE_MORE_PEARLS = PlayerUnlock.child("more_more_pearls", MORE_PEARLS)
        .icon(Items.ENDER_PEARL)
        .visibility(PlayerUnlock.Visibility.INVISIBLE)
        .visibleWhen(MineUnlockCondition.method_69638(MORE_PEARLS))
        .giveStacks(Items.ENDER_PEARL.getDefaultStack().copyWithCount(25))
        .price(25)
        .register();

    public static RegistryEntry<PlayerUnlock> SPEED_1 = PlayerUnlock.child("speed_1", EXPLORATION)
        .icon(Items.FURNACE_MINECART)
        .modifyAttribute(EntityAttributes.MOVEMENT_SPEED, 0.1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
        .price(5)
        .register();

    public static RegistryEntry<PlayerUnlock> AMPHIBIAN = PlayerUnlock.child("amphibian", SPEED_1)
        .icon(Items.SALMON)
        .modifyAttribute(EntityAttributes.WATER_MOVEMENT_EFFICIENCY, 0.5, EntityAttributeModifier.Operation.ADD_VALUE)
        .price(5)
        .register();

    public static RegistryEntry<PlayerUnlock> SPEED_2 = PlayerUnlock.child("speed_2", SPEED_1)
        .icon(Items.MINECART)
        .modifyAttribute(EntityAttributes.MOVEMENT_SPEED, 0.2, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
        .price(10)
        .register();

    public static RegistryEntry<PlayerUnlock> SPEED_3 = PlayerUnlock.child("speed_3", SPEED_2)
        .icon(Items.CHEST_MINECART)
        .modifyAttribute(EntityAttributes.MOVEMENT_SPEED, 0.2, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
        .price(20)
        .register();

    public static RegistryEntry<PlayerUnlock> SNEAK_1 = PlayerUnlock.child("sneak_1", SPEED_1)
        .icon(Items.APPLE)
        .modifyAttribute(EntityAttributes.SNEAKING_SPEED, 0.5, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
        .price(2)
        .register();

    public static RegistryEntry<PlayerUnlock> SNEAK_2 = PlayerUnlock.child("sneak_2", SNEAK_1)
        .icon(Items.GOLDEN_APPLE)
        .modifyAttribute(EntityAttributes.SNEAKING_SPEED, 0.5, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
        .price(4)
        .register();

    public static RegistryEntry<PlayerUnlock> MOVEMENT_BOOST = PlayerUnlock.child("movement_boost", SPEED_1)
        .icon(Items.COOKIE)
        .giveEffect(StatusEffects.SPEED, 120, 1)
        .price(5)
        .register();

    public static RegistryEntry<PlayerUnlock> PATHFINDER = PlayerUnlock.child("pathfinder", EXPLORATION)
        .icon(net.zhengzhengyiyi.item.ModItems.EXIT_EYE)
        .price(10)
        .register();

    public static RegistryEntry<PlayerUnlock> LODESTONE_EXITS = PlayerUnlock.child("lodestone_exits", PATHFINDER)
        .icon(Items.LODESTONE)
        .price(15)
        .register();

    public static RegistryEntry<PlayerUnlock> STARTER_COMPASS = PlayerUnlock.child("starter_compass", LODESTONE_EXITS)
        .icon(Items.COMPASS)
        .giveItems(Items.COMPASS)
        .price(20)
        .register();

    // ─── CRAFTING TREE ──────────────────────────────────────────────────────────

    public static RegistryEntry<PlayerUnlock> CRAFTING = PlayerUnlock.root("crafting")
        .icon(Items.CRAFTING_TABLE)
        .register();

    public static RegistryEntry<PlayerUnlock> SMELTER_1 = PlayerUnlock.child("smelter_1", CRAFTING)
        .icon(Items.FURNACE)
        .giveStacks(Items.FURNACE.getDefaultStack(), Items.COAL.getDefaultStack().copyWithCount(32))
        .price(4)
        .register();

    public static RegistryEntry<PlayerUnlock> SMELTER_2 = PlayerUnlock.child("smelter_2", SMELTER_1)
        .icon(Items.BLAST_FURNACE)
        .giveStacks(Items.BLAST_FURNACE.getDefaultStack(), Items.CHARCOAL.getDefaultStack().copyWithCount(64))
        .disables(SMELTER_1)
        .price(8)
        .register();

    public static RegistryEntry<PlayerUnlock> SMELT_VALUE_1 = PlayerUnlock.child("smelt_value_1", SMELTER_2)
        .icon(Items.GOLD_INGOT)
        .expFactorForTag(ItemTags.BEACON_PAYMENT_ITEMS, 2.0F) // closest to "ingots" in this version
        .price(15)
        .register();

    public static RegistryEntry<PlayerUnlock> SMELT_VALUE_2 = PlayerUnlock.child("smelt_value_2", SMELT_VALUE_1)
        .icon(Items.NETHERITE_INGOT)
        .expFactorForTag(ItemTags.BEACON_PAYMENT_ITEMS, 4.0F)
        .price(15)
        .register();

    public static RegistryEntry<PlayerUnlock> INVENTORY_SLOTS_1 = PlayerUnlock.child("inventory_slots_1", CRAFTING)
        .icon(Items.CHEST)
        .price(2)
        .register();

    public static RegistryEntry<PlayerUnlock> INVENTORY_SLOTS_2 = PlayerUnlock.child("inventory_slots_2", INVENTORY_SLOTS_1)
        .icon(Items.CHEST)
        .price(15)
        .register();

    public static RegistryEntry<PlayerUnlock> INVENTORY_SLOTS_3 = PlayerUnlock.child("inventory_slots_3", INVENTORY_SLOTS_2)
        .icon(Items.CHEST)
        .price(30)
        .register();

    public static RegistryEntry<PlayerUnlock> BUNDLE_OF_FORTUNE = PlayerUnlock.child("bundle_of_fortune", INVENTORY_SLOTS_3)
        .icon(Items.BUNDLE)
        .price(50)
        .giveStack(player -> {
            ItemStack bundle = Items.BUNDLE.getDefaultStack().copy();
            Item item = BUNDLE_ITEMS.get(player.getRandom().nextInt(BUNDLE_ITEMS.size()));
            bundle.set(DataComponentTypes.BUNDLE_CONTENTS, new BundleContentsComponent(List.of(item.getDefaultStack().copy())));
            return bundle;
        })
        .register();

    public static RegistryEntry<PlayerUnlock> INVENTORY_CRAFTING = PlayerUnlock.child("inventory_crafting", CRAFTING)
        .icon(Items.STICK)
        .price(2)
        .register();

    public static RegistryEntry<PlayerUnlock> ENCHANTING = PlayerUnlock.child("enchanting", INVENTORY_SLOTS_1)
        .icon(Items.ENCHANTING_TABLE)
        .giveItems(Items.ENCHANTING_TABLE)
        .price(5)
        .register();

    public static RegistryEntry<PlayerUnlock> MAGIC_STARTER_KIT = PlayerUnlock.child("magic_starter_kit", ENCHANTING)
        .icon(Items.LAPIS_LAZULI)
        .giveStacks(Items.LAPIS_LAZULI.getDefaultStack().copyWithCount(3), Items.BOOKSHELF.getDefaultStack().copyWithCount(5))
        .price(15)
        .register();

    public static RegistryEntry<PlayerUnlock> MAGIC_MASTER_KIT = PlayerUnlock.child("magic_master_kit", MAGIC_STARTER_KIT)
        .icon(Items.ENCHANTED_BOOK)
        .giveStacks(Items.LAPIS_LAZULI.getDefaultStack().copyWithCount(6), Items.BOOKSHELF.getDefaultStack().copyWithCount(10))
        .disables(MAGIC_STARTER_KIT)
        .price(25)
        .register();

    public static RegistryEntry<PlayerUnlock> ALCHEMY = PlayerUnlock.child("alchemy", INVENTORY_SLOTS_1)
        .icon(Items.BREWING_STAND)
        .giveItems(Items.BREWING_STAND)
        .price(15)
        .register();

    public static RegistryEntry<PlayerUnlock> MISE_EN_PLACE = PlayerUnlock.child("mise_en_place", ALCHEMY)
        .icon(Items.GLASS_BOTTLE)
        .giveStacks(
            Items.GLASS_BOTTLE.getDefaultStack().copyWithCount(16),
            Items.NETHER_WART.getDefaultStack().copyWithCount(4),
            Items.GLISTERING_MELON_SLICE.getDefaultStack().copyWithCount(4),
            Items.SUGAR.getDefaultStack().copyWithCount(4),
            Items.BLAZE_POWDER.getDefaultStack().copyWithCount(2)
        )
        .price(30)
        .register();

    // ─── GATHERER TREE ──────────────────────────────────────────────────────────

    public static RegistryEntry<PlayerUnlock> GATHERER = PlayerUnlock.root("gatherer")
        .icon(Items.SWEET_BERRIES)
        .register();

    public static RegistryEntry<PlayerUnlock> PICKUP_AREA_SIZE = PlayerUnlock.child("pickup_area_size", GATHERER)
        .icon(Items.FISHING_ROD)
        .price(25)
        // PICKUP_RANGE attribute not available in this MC version — skip attribute modifier
        .register();

    public static RegistryEntry<PlayerUnlock> DIRT_ENJOYER_1 = PlayerUnlock.child("dirt_enjoyer_1", GATHERER)
        .icon(Items.DIRT)
        .expFactorForTag(ItemTags.DIRT, 2.0F)
        .price(2)
        .register();

    public static RegistryEntry<PlayerUnlock> MINING = PlayerUnlock.child("mining", GATHERER)
        .icon(Items.WOODEN_PICKAXE)
        .modifyAttribute(EntityAttributes.MINING_EFFICIENCY, 0.5, EntityAttributeModifier.Operation.ADD_VALUE)
        .modifyAttribute(EntityAttributes.SUBMERGED_MINING_SPEED, 1.0, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
        .price(2)
        .register();

    public static RegistryEntry<PlayerUnlock> MINING_EFFICIENCY = PlayerUnlock.child("mining_efficiency", MINING)
        .icon(Items.IRON_ORE)
        .price(5)
        .modifyAttribute(EntityAttributes.MINING_EFFICIENCY, 1.0, EntityAttributeModifier.Operation.ADD_VALUE)
        .register();

    public static RegistryEntry<PlayerUnlock> MINING_EFFICIENCY_2 = PlayerUnlock.child("mining_efficiency_2", MINING_EFFICIENCY)
        .icon(Items.GOLD_NUGGET)
        .price(10)
        .modifyAttribute(EntityAttributes.MINING_EFFICIENCY, 1.0, EntityAttributeModifier.Operation.ADD_VALUE)
        .register();

    public static RegistryEntry<PlayerUnlock> MINING_EFFICIENCY_3 = PlayerUnlock.child("mining_efficiency_3", MINING_EFFICIENCY_2)
        .icon(Items.GOLD_INGOT)
        .price(10)
        .modifyAttribute(EntityAttributes.MINING_EFFICIENCY, 1.0, EntityAttributeModifier.Operation.ADD_VALUE)
        .register();

    public static RegistryEntry<PlayerUnlock> STARTER_PICK = PlayerUnlock.child("starter_pick", MINING_EFFICIENCY_3)
        .icon(Items.IRON_PICKAXE)
        .giveItems(Items.IRON_PICKAXE)
        .price(10)
        .register();

    @SuppressWarnings("unchecked")
    public static RegistryEntry<PlayerUnlock> LUCKY_PICK = PlayerUnlock.child("lucky_pick", STARTER_PICK)
        .icon(Items.DIAMOND_PICKAXE)
        .disables(STARTER_PICK)
        .giveEnchantedItem(Items.DIAMOND_PICKAXE,
            Pair.of(Enchantments.FORTUNE, 3),
            Pair.of(Enchantments.UNBREAKING, 1),
            Pair.of(Enchantments.EFFICIENCY, 4))
        .price(20)
        .visibility(PlayerUnlock.Visibility.INVISIBLE)
        .visibleWhen(MineUnlockCondition.method_69638(STARTER_PICK))
        .register();

    public static RegistryEntry<PlayerUnlock> ORE_SEEKER_1 = PlayerUnlock.child("ore_seeker_1", LUCKY_PICK)
        .icon(Items.GOLD_ORE)
        .price(20)
        .expFactorForTag(ItemTags.GOLD_ORES, 2.0F) // use gold_ores as representative; no generic "ores" tag
        .register();

    public static RegistryEntry<PlayerUnlock> ORE_SEEKER_2 = PlayerUnlock.child("ore_seeker_2", ORE_SEEKER_1)
        .icon(Items.DIAMOND_ORE)
        .price(20)
        .expFactorForTag(ItemTags.DIAMOND_ORES, 4.0F)
        .register();

    public static RegistryEntry<PlayerUnlock> RARE_EARTH_SPECIALIST = PlayerUnlock.child("rare_earth_specialist", LUCKY_PICK)
        .icon(Items.DIAMOND)
        .price(20)
        .expFactorForItem(Items.DIAMOND, 4.0F)
        .expFactorForItem(Items.REDSTONE, 4.0F)
        .expFactorForItem(Items.LAPIS_LAZULI, 4.0F)
        .register();

    public static RegistryEntry<PlayerUnlock> FISHING = PlayerUnlock.child("fishing", GATHERER)
        .icon(Items.SALMON)
        .price(2)
        .modifyAttribute(EntityAttributes.LUCK, 1.0, EntityAttributeModifier.Operation.ADD_VALUE)
        .register();

    public static RegistryEntry<PlayerUnlock> FISHING_ROD = PlayerUnlock.child("fishing_rod", FISHING)
        .icon(Items.FISHING_ROD)
        .giveItems(Items.FISHING_ROD)
        .price(5)
        .register();

    public static RegistryEntry<PlayerUnlock> HUNTER = PlayerUnlock.child("hunter", GATHERER)
        .icon(Items.PORKCHOP)
        .register();

    public static RegistryEntry<PlayerUnlock> CAMPFIRE = PlayerUnlock.child("campfire", HUNTER)
        .icon(Items.CAMPFIRE)
        .giveItems(Items.CAMPFIRE)
        .price(5)
        .register();

    // ─── COMBATANT TREE ─────────────────────────────────────────────────────────

    public static RegistryEntry<PlayerUnlock> COMBATANT = PlayerUnlock.root("combatant")
        .icon(Items.NETHERITE_SWORD)
        .modifyAttribute(EntityAttributes.ATTACK_DAMAGE, 2.0, EntityAttributeModifier.Operation.ADD_VALUE)
        .register();

    public static RegistryEntry<PlayerUnlock> STARTER_SWORD = PlayerUnlock.child("starter_sword", COMBATANT)
        .icon(Items.WOODEN_SWORD)
        .giveItems(Items.WOODEN_SWORD)
        .price(5)
        .register();

    @SuppressWarnings("unchecked")
    public static RegistryEntry<PlayerUnlock> STARTER_SWORD_IRON = PlayerUnlock.child("starter_sword_iron", STARTER_SWORD)
        .icon(Items.IRON_SWORD)
        .price(25)
        .giveEnchantedItem(Items.IRON_SWORD,
            Pair.of(Enchantments.SHARPNESS, 3),
            Pair.of(Enchantments.SWEEPING_EDGE, 1))
        .disables(STARTER_SWORD)
        .register();

    public static RegistryEntry<PlayerUnlock> ARCHER = PlayerUnlock.child("archer", COMBATANT)
        .icon(Items.BOW)
        .giveItems(Items.BOW)
        .price(10)
        .register();

    public static RegistryEntry<PlayerUnlock> QUIVER = PlayerUnlock.child("quiver", ARCHER)
        .icon(Items.ARROW)
        .giveStacks(Items.ARROW.getDefaultStack().copyWithCount(16))
        .price(10)
        .register();

    public static RegistryEntry<PlayerUnlock> FLETCHER = PlayerUnlock.child("fletcher", QUIVER)
        .icon(Items.FLETCHING_TABLE)
        .disables(QUIVER)
        .giveStacks(Items.ARROW.getDefaultStack().copyWithCount(64))
        .price(20)
        .register();

    public static RegistryEntry<PlayerUnlock> ARMAMENTS = PlayerUnlock.child("armaments", COMBATANT)
        .icon(Items.IRON_HELMET)
        .price(10)
        .register();

    public static RegistryEntry<PlayerUnlock> DECKED_OUT = PlayerUnlock.child("decked_out", ARMAMENTS)
        .icon(Items.LEATHER_CHESTPLATE)
        .giveItems(Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS)
        .price(20)
        .register();

    public static RegistryEntry<PlayerUnlock> FULL_METAL = PlayerUnlock.child("full_metal", DECKED_OUT)
        .icon(Items.IRON_CHESTPLATE)
        .giveItems(Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS)
        .disables(DECKED_OUT)
        .price(40)
        .register();

    public static RegistryEntry<PlayerUnlock> STARTER_SHIELD = PlayerUnlock.child("starter_shield", COMBATANT)
        .icon(Items.SHIELD)
        .giveItems(Items.SHIELD)
        .price(15)
        .register();

    public static RegistryEntry<PlayerUnlock> STARTER_APPLES = PlayerUnlock.child("starter_apples", ARMAMENTS)
        .icon(Items.APPLE)
        .giveStacks(Items.APPLE.getDefaultStack().copyWithCount(5))
        .price(15)
        .register();

    public static RegistryEntry<PlayerUnlock> MORE_STARTER_APPLES = PlayerUnlock.child("more_starter_apples", STARTER_APPLES)
        .icon(Items.APPLE)
        .giveStacks(Items.APPLE.getDefaultStack().copyWithCount(10))
        .disables(STARTER_APPLES)
        .price(15)
        .register();

    public static RegistryEntry<PlayerUnlock> GOLDEN_STARTER_APPLES = PlayerUnlock.child("golden_starter_apples", MORE_STARTER_APPLES)
        .icon(Items.GOLDEN_APPLE)
        .giveStacks(Items.GOLDEN_APPLE.getDefaultStack().copyWithCount(5))
        .disables(MORE_STARTER_APPLES)
        .price(15)
        .register();

    // ─── PET UNLOCKS ────────────────────────────────────────────────────────────

    public static RegistryEntry<PlayerUnlock> CAT_PET = PlayerUnlock.root("cat_pet")
        .icon(Items.CAT_SPAWN_EGG)
        .spawnTameablePet(EntityType.CAT, net.minecraft.entity.passive.CatEntity.class, cat -> {})
        .exclusive(EXCLUSIVE_PET)
        .price(20)
        .register();

    public static RegistryEntry<PlayerUnlock> WOLF_PET = PlayerUnlock.root("wolf_pet")
        .icon(Items.WOLF_SPAWN_EGG)
        .spawnTameablePet(EntityType.WOLF, net.minecraft.entity.passive.WolfEntity.class, wolf -> {})
        .exclusive(EXCLUSIVE_PET)
        .price(20)
        .register();

    public static RegistryEntry<PlayerUnlock> PARROT_PET = PlayerUnlock.root("parrot_pet")
        .icon(Items.PARROT_SPAWN_EGG)
        .spawnTameablePet(EntityType.PARROT, net.minecraft.entity.passive.ParrotEntity.class, parrot -> {})
        .exclusive(EXCLUSIVE_PET)
        .price(15)
        .register();

    /**
     * Called during mod initialization to force static field initialization,
     * which registers all unlocks into the PLAYER_UNLOCK registry.
     */
    public static void init() {
        // Accessing any field triggers all static initializers
        // The act of referencing EXPLORATION causes the class to load
        // and all static fields to be initialized in order.
        Object unused = EXPLORATION;
    }
}
