package net.zhengzhengyiyi.vote;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;

/**
 * Defines a type of cost required to cast a vote.
 * <p>
 * Official Name: bgw
 */
public interface VoteCost {
    /**
     * Codec that dispatches to the correct implementation based on the cost type.
     */
	public static final MapCodec<VoteCost> CODEC = Type.CODEC.dispatchMap(
		    VoteCost::getType, 
		    arg -> {
		        Codec<? extends VoteCost> subCodec = arg.codecSupplier.get();
		        return MapCodec.assumeMapUnsafe(subCodec);
		    }
		);
	
//	Codec<VoteCost> CODEC = Type.CODEC.dispatch(
//		    VoteCost::getType, 
//		    type -> {
//		        Codec<? extends VoteCost> codec = type.codecSupplier.get();
//		        return (Codec<? extends VoteCost>) codec;
//		    }
//		);

    /**
     * Checks if the player can afford the cost and optionally deducts it.
     *
     * @param player  The player attempting to vote.
     * @param amount  The quantity of the cost.
     * @param simulate If true, the cost is only checked, not deducted.
     * @return True if the player can afford the cost.
     */
    boolean canAfford(ServerPlayerEntity player, int amount, boolean simulate);

    /**
     * Gets the category of this cost.
     */
    Type getType();

    /**
     * Gets the description text of this cost.
     */
    Text getDescription();

    /**
     * Represents the specific types of costs available.
     */
    enum Type implements StringIdentifiable {
//        PER_PROPOSAL("per_proposal", () -> Codec.unit(Constants.PER_PROPOSAL)),
//        PER_OPTION("per_option", () -> Codec.unit(Constants.PER_OPTION)),
        PER_PROPOSAL("per_proposal", () -> MapCodec.unit(Constants.PER_PROPOSAL).codec()),
        PER_OPTION("per_option", () -> MapCodec.unit(Constants.PER_OPTION).codec()),
        ITEM("item", () -> ItemCost.CODEC),
        RESOURCE("resource", () -> ResourceCost.CODEC);
//        CUSTOM("custom", () -> CustomCost.CODEC);

        public static final Codec<Type> CODEC = StringIdentifiable.createCodec(Type::values);
        private final String id;
        final Supplier<Codec<? extends VoteCost>> codecSupplier;

        Type(String id, Supplier<Codec<? extends VoteCost>> codecSupplier) {
            this.id = id;
            this.codecSupplier = codecSupplier;
        }

        @Override
        public String asString() {
            return this.id;
        }
    }

    /**
     * Internal constants for basic cost behaviors.
     */
    class Constants {
        public static final VoteCost PER_PROPOSAL = new VoteCost() {
            public boolean canAfford(ServerPlayerEntity p, int a, boolean s) { return true; }
            public Type getType() { return Type.PER_PROPOSAL; }
            public Text getDescription() { return Text.translatable("vote.count_per_proposal.description"); }
        };

        public static final VoteCost PER_OPTION = new VoteCost() {
            public boolean canAfford(ServerPlayerEntity p, int a, boolean s) { return true; }
            public Type getType() { return Type.PER_OPTION; }
            public Text getDescription() { return Text.translatable("vote.count_per_option.description"); }
        };
    }

    /**
     * Implementation for item-based voting costs.
     */
    record ItemCost(Item item, Optional<String> translationKey) implements VoteCost {
        public static final Codec<ItemCost> CODEC = RecordCodecBuilder.create(instance -> 
            instance.group(
                Registries.ITEM.getCodec().fieldOf("item").forGetter(ItemCost::item),
                Codec.STRING.optionalFieldOf("translation_key").forGetter(ItemCost::translationKey)
            ).apply(instance, ItemCost::new)
        );

        @Override
        public Type getType() { return Type.ITEM; }

        @Override
        public boolean canAfford(ServerPlayerEntity player, int amount, boolean simulate) {
//            int currentCount = 0;
            // logic to count and remove items from inventory
            // ... (Simulated based on your decompiled logic)
            return true; 
        }

        @Override
        public Text getDescription() {
            return Text.translatable(translationKey.orElseGet(() -> item.getTranslationKey()));
        }
    }

    /**
     * Implementation for resource-based costs like XP and Health.
     */
    enum ResourceCost implements VoteCost, StringIdentifiable {
        XP("xp") {
            public boolean canAfford(ServerPlayerEntity player, int amount, boolean simulate) {
                if (player.experienceLevel < amount) return false;
                if (!simulate) player.addExperienceLevels(-amount);
                return true;
            }
            public Text getDescription() { return Text.translatable("vote.cost.xp"); }
        },
        HEALTH("health") {
            public boolean canAfford(ServerPlayerEntity player, int amount, boolean simulate) {
                if (player.getHealth() < amount) return false;
                if (!simulate) player.setHealth(player.getHealth() - amount);
                return true;
            }
            public Text getDescription() { return Text.translatable("vote.cost.health"); }
        };

        public static final Codec<ResourceCost> CODEC = StringIdentifiable.createCodec(ResourceCost::values);
        private final String id;

        ResourceCost(String id) { this.id = id; }
        @Override
        public String asString() { return this.id; }
        @Override
        public Type getType() { return Type.RESOURCE; }
    }

    /**
     * Wrapper for a specific cost and its required amount.
     * Official Name: bgw$a
     */
    record Instance(VoteCost cost, int amount) {
        public static final Codec<Instance> CODEC = RecordCodecBuilder.create(instance -> 
            instance.group(
                VoteCost.CODEC.fieldOf("cost").forGetter(Instance::cost),
                Codec.INT.fieldOf("count").forGetter(Instance::amount)
            ).apply(instance, Instance::new)
        );

        public boolean apply(ServerPlayerEntity player, boolean simulate) {
            return this.cost.canAfford(player, this.amount, simulate);
        }
    }
}
