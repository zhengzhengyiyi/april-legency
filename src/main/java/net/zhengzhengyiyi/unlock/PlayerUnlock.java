package net.zhengzhengyiyi.unlock;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.Pair;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.AssetInfo;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.zhengzhengyiyi.AprilsLegacy;
import net.zhengzhengyiyi.component.ExchangeValueComponent;
import net.zhengzhengyiyi.component.ModDataComponentTypes;
import net.zhengzhengyiyi.mine.effect.MineUnlockCondition;

/**
 * Mirrors Craftmine class_10976 - Player unlock/achievement system.
 * Represents purchasable upgrades and unlocks that players can obtain.
 */
public record PlayerUnlock(
   String key,
   Optional<RegistryEntry<PlayerUnlock>> parent,
   List<RegistryEntry<PlayerUnlock>> disables,
   AdvancementDisplay display,
   Consumer<ServerPlayerEntity> activation,
   Consumer<ServerPlayerEntity> onMineEnter,
   List<MineUnlockCondition> madeVisibleBy,
   PlayerUnlock.Visibility defaultVisibility,
   Map<TagKey<Item>, Float> experienceFactorForItemTag,
   Map<Item, Float> experienceFactorForItem,
   int unlockPrice,
   String exclusiveKey
) {
    public static final Codec<RegistryEntry<PlayerUnlock>> CODEC = AprilsLegacy.PLAYER_UNLOCK.getEntryCodec();

    /**
     * Creates a root unlock builder (no parent).
     */
    public static PlayerUnlock.Builder root(String key) {
        return new PlayerUnlock.Builder(key, Optional.empty(), 
            Optional.of(new AssetInfo.TextureAssetInfo(Identifier.ofVanilla("unlock_backgrounds/" + key))));
    }

    /**
     * Creates a child unlock builder with a parent.
     */
    public static PlayerUnlock.Builder child(String key, RegistryEntry<PlayerUnlock> parent) {
        return new PlayerUnlock.Builder(key, Optional.of(parent), Optional.empty());
    }

    /**
     * Gets the root unlock of a tree by traversing parents.
     */
    public static RegistryEntry<PlayerUnlock> getRoot(RegistryEntry<PlayerUnlock> unlock) {
        RegistryEntry<PlayerUnlock> current = unlock;
        while (true) {
            Optional<RegistryEntry<PlayerUnlock>> parentOpt = current.value().parent();
            if (parentOpt.isEmpty()) {
                return current;
            }
            current = parentOpt.get();
        }
    }

    /**
     * Builder for creating PlayerUnlock instances.
     */
    public static class Builder {
        private final String key;
        private final Optional<RegistryEntry<PlayerUnlock>> parent;
        private final AdvancementDisplayBuilder displayBuilder;
        private final List<Consumer<ServerPlayerEntity>> activationActions = new ArrayList<>();
        private final List<Consumer<ServerPlayerEntity>> mineEnterActions = new ArrayList<>();
        private final List<MineUnlockCondition> visibilityConditions = new ArrayList<>();
        private final List<RegistryEntry<PlayerUnlock>> disables = new ArrayList<>();
        private final Map<TagKey<Item>, Float> expFactorForTag = new HashMap<>();
        private final Map<Item, Float> expFactorForItem = new HashMap<>();
        private Visibility defaultVisibility = Visibility.VISIBLE;
        private int unlockPrice = 1;
        private String exclusiveKey = "";

        public Builder(String key, Optional<RegistryEntry<PlayerUnlock>> parent, Optional<AssetInfo.TextureAssetInfo> background) {
            this.key = key;
            this.parent = parent;
            this.displayBuilder = new AdvancementDisplayBuilder()
                .title(Text.translatable("unlocks.unlock." + key + ".name"))
                .description(Text.translatable("unlocks.unlock." + key + ".description"))
                .hint(Text.translatable("unlocks.unlock." + key + ".hint"))
                .frame(AdvancementFrame.TASK)
                .showToast(true)
                .announceToChat(false)
                .hidden(false);
            background.ifPresent(bg -> displayBuilder.background(bg.id()));
        }

        public Builder icon(Supplier<ItemStack> supplier) {
            this.displayBuilder.icon(supplier.get().copy());
            return this;
        }

        public Builder icon(Item item) {
            this.displayBuilder.icon(item.getDefaultStack());
            return this;
        }

        public Builder iconModel(String modelId) {
            // ItemModelComponent doesn't exist in this version, so we'll just use a regular item
            // In the actual game, custom models would be handled differently
            ItemStack stack = new ItemStack(Items.STONE);
            // TODO: Custom model support when available
            this.displayBuilder.icon(stack);
            return this;
        }

        public Builder title(Text title) {
            this.displayBuilder.title(title);
            return this;
        }

        public Builder visibleWhen(MineUnlockCondition... conditions) {
            this.visibilityConditions.addAll(List.of(conditions));
            return this;
        }

        public Builder visibility(Visibility visibility) {
            this.defaultVisibility = visibility;
            return this;
        }

        public Builder price(int price) {
            this.unlockPrice = price;
            return this;
        }

        public Builder exclusive(String exclusiveKey) {
            this.exclusiveKey = exclusiveKey;
            return this;
        }

        public Builder onActivate(Consumer<ServerPlayerEntity> action) {
            this.activationActions.add(action);
            return this;
        }

        public Builder modifyAttribute(RegistryEntry<EntityAttribute> attribute, double value, 
                                      EntityAttributeModifier.Operation operation) {
            this.onActivate(player -> {
                EntityAttributeInstance instance = player.getAttributeInstance(attribute);
                if (instance != null) {
                    Identifier id = Identifier.ofVanilla("unlock_" + this.key);
                    instance.removeModifier(id);
                    instance.addPersistentModifier(new EntityAttributeModifier(id, value, operation));
                }
            });
            return this;
        }

        public Builder onMineEnter(Consumer<ServerPlayerEntity> action) {
            this.mineEnterActions.add(action);
            return this;
        }

        public Builder giveItems(Item... items) {
            List<Item> itemList = Arrays.asList(items);
            this.onMineEnter(player -> itemList.forEach(item -> {
                ItemStack stack = item.getDefaultStack();
                stack.set(ModDataComponentTypes.EXCHANGE_VALUE, new ExchangeValueComponent(0.0F));
                player.giveItemStack(stack);
            }));
            return this;
        }

        public Builder expFactorForTag(TagKey<Item> tag, float factor) {
            this.expFactorForTag.computeIfPresent(tag, (k, v) -> v * factor);
            this.expFactorForTag.putIfAbsent(tag, factor);
            return this;
        }

        public Builder expFactorForItem(Item item, float factor) {
            this.expFactorForItem.computeIfPresent(item, (k, v) -> v * factor);
            this.expFactorForItem.putIfAbsent(item, factor);
            return this;
        }

        public Builder giveStacks(ItemStack... stacks) {
            List<ItemStack> stackList = new ArrayList<>();
            for (ItemStack stack : stacks) {
                stackList.add(stack.copy());
            }
            this.onMineEnter(player -> stackList.forEach(stack -> {
                stack.set(ModDataComponentTypes.EXCHANGE_VALUE, new ExchangeValueComponent(0.0F));
                player.giveItemStack(stack.copy());
            }));
            return this;
        }

        public Builder giveStack(Function<ServerPlayerEntity, ItemStack> stackFunction) {
            this.onMineEnter(player -> {
                ItemStack stack = stackFunction.apply(player).copy();
                stack.set(ModDataComponentTypes.EXCHANGE_VALUE, new ExchangeValueComponent(0.0F));
                player.giveItemStack(stack);
            });
            return this;
        }

        @SafeVarargs
        public final Builder giveEnchantedItem(Item item, Pair<RegistryKey<Enchantment>, Integer>... enchantments) {
            List<Pair<RegistryKey<Enchantment>, Integer>> enchList = Arrays.asList(enchantments);
            this.onMineEnter(player -> {
                ItemStack stack = item.getDefaultStack();
                stack.set(ModDataComponentTypes.EXCHANGE_VALUE, new ExchangeValueComponent(0.0F));
                
                for (Pair<RegistryKey<Enchantment>, Integer> pair : enchList) {
                    Optional<RegistryEntry.Reference<Enchantment>> enchOpt = player.getEntityWorld()
                        .getRegistryManager()
                        .getOptionalEntry(pair.first());
                    enchOpt.ifPresent(ench -> stack.addEnchantment(ench, pair.second()));
                }
                
                player.giveItemStack(stack);
            });
            return this;
        }

        public Builder giveEffect(RegistryEntry<StatusEffect> effect, int durationSeconds) {
            return giveEffect(effect, durationSeconds, 0);
        }

        public Builder giveEffect(RegistryEntry<StatusEffect> effect, int durationSeconds, int amplifier) {
            StatusEffectInstance effectInstance = new StatusEffectInstance(
                effect, 
                durationSeconds == -1 ? -1 : durationSeconds * 20, 
                amplifier
            );
            this.onMineEnter(player -> player.addStatusEffect(effectInstance, null));
            return this;
        }

        public Builder spawnPet(EntityType<?> entityType) {
            this.onActivate(player -> PetSpawner.registerPet(entityType));
            this.onMineEnter(player -> PetSpawner.spawnPet(entityType, player));
            return this;
        }

        public Builder spawnTameablePet(EntityType<?> entityType, Class<? extends TameableEntity> entityClass, 
                                       Consumer<TameableEntity> configurator) {
            this.onActivate(player -> PetSpawner.registerTameablePet(entityType, configurator));
            this.onMineEnter(player -> PetSpawner.spawnTameablePet(entityType, entityClass, player));
            return this;
        }

        @SafeVarargs
        public final Builder disables(RegistryEntry<PlayerUnlock>... unlocks) {
            this.disables.addAll(List.of(unlocks));
            return this;
        }

        private PlayerUnlock build() {
            Consumer<ServerPlayerEntity> activation;
            if (this.activationActions.isEmpty()) {
                activation = player -> {};
            } else {
                List<Consumer<ServerPlayerEntity>> actions = List.copyOf(this.activationActions);
                activation = player -> actions.forEach(action -> action.accept(player));
            }

            Consumer<ServerPlayerEntity> mineEnter;
            if (this.mineEnterActions.isEmpty()) {
                mineEnter = player -> {};
            } else {
                List<Consumer<ServerPlayerEntity>> actions = List.copyOf(this.mineEnterActions);
                mineEnter = player -> actions.forEach(action -> action.accept(player));
            }

            return new PlayerUnlock(
                this.key,
                this.parent,
                this.disables,
                this.displayBuilder.build(),
                activation,
                mineEnter,
                this.visibilityConditions,
                this.defaultVisibility,
                this.expFactorForTag,
                this.expFactorForItem,
                this.unlockPrice,
                this.exclusiveKey
            );
        }

        public RegistryEntry<PlayerUnlock> register() {
            return Registry.registerReference(
                AprilsLegacy.PLAYER_UNLOCK, 
                Identifier.ofVanilla(this.key), 
                this.build()
            );
        }
    }

    /**
     * Visibility state for unlocks.
     */
    public enum Visibility implements StringIdentifiable {
        VISIBLE,
        INVISIBLE,
        MYSTERY;

        public static final PacketCodec<ByteBuf, Visibility> PACKET_CODEC = 
            PacketCodecs.indexed(i -> values()[i], Enum::ordinal);

        @Override
        public String asString() {
            return this.name();
        }
    }
}
