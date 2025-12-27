package net.zhengzhengyiyi.rules.options;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;
import net.zhengzhengyiyi.rules.options.VoteEffect;
import net.zhengzhengyiyi.world.Vote;
import net.zhengzhengyiyi.vote.VoteValue;
import net.zhengzhengyiyi.vote.VoterAction;
import net.zhengzhengyiyi.world.Vote;

public class ReplaceItemRule extends VoteEffect.Weighted {
    private final Codec<ReplaceItemRule.Option> optionCodec = RecordCodecBuilder.create(
        instance -> instance.group(
                Registries.ITEM.getCodec().fieldOf("source").forGetter(opt -> opt.source),
                Registries.ITEM.getCodec().fieldOf("target").forGetter(opt -> opt.target)
            )
            .apply(instance, ReplaceItemRule.Option::new)
    );
    private final ReplaceItemRule.TargetSupplier targetSupplier;

    public ReplaceItemRule(ReplaceItemRule.TargetSupplier targetSupplier) {
        this.targetSupplier = targetSupplier;
    }

    @Override
    public Codec getOptionCodec() {
//        return Vote.createCodec(this.optionCodec);
    	return this.optionCodec;
    }

    @Override
    protected Optional<VoteValue> selectRandomOption(MinecraftServer server, Random random) {
        Registry<Item> registry = server.getRegistryManager().getOrThrow(RegistryKeys.ITEM);
        Optional<Item> source = selectSourceItem(server, random, registry);
        Optional<Item> target = this.targetSupplier.get(registry, random);
        if (source.isPresent() && target.isPresent() && !source.equals(target)) {
            return Optional.of(new ReplaceItemRule.Option(source.get(), target.get()));
        }
        return Optional.empty();
    }

    private static Optional<Item> selectSourceItem(MinecraftServer server, Random random, Registry<Item> registry) {
        if (random.nextInt(10) != 0) {
            List<Item> list = registry.stream()
                .filter(item -> {
                    for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                        ServerStatHandler stats = player.getStatHandler();
                        if (stats.getStat(Stats.PICKED_UP, item) > 0
                            || stats.getStat(Stats.USED, item) > 0
                            || stats.getStat(Stats.CRAFTED, item) > 0) {
                            return true;
                        }
                    }
                    return false;
                })
                .toList();
            if (!list.isEmpty()) {
                return Util.getRandomOrEmpty(list, random);
            }
        }
        return registry.getRandom(random).map(RegistryEntry.Reference::value).filter(item -> item != Items.AIR);
    }

    public interface TargetSupplier {
        Optional<Item> get(Registry<Item> registry, Random random);
    }

    protected class Option extends VoteEffect.Option {
        final Item source;
        final Item target;
        private final Text description;

        protected Option(Item source, Item target) {
            this.source = source;
            this.target = target;
            this.description = Text.translatable("rule.replace_items", source.getName(), target.getName());
        }

        @Override
        protected Text getDescriptionText() {
            return this.description;
        }

//        @Override
//        public void run(MinecraftServer server) {
//            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
//                player.getInventory().replace(this.source, this.target);
//                player.currentScreenHandler.sendContentUpdates();
//            }
//        }
        
        @Override
        public void run(MinecraftServer server) {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                PlayerInventory inventory = player.getInventory();
                
                for (int i = 0; i < inventory.size(); i++) {
                    ItemStack stack = inventory.getStack(i);
                    
                    if (!stack.isEmpty() && stack.isOf(this.source)) {
                        int count = stack.getCount();
                        inventory.setStack(i, new ItemStack(this.target, count));
                    }
                }
                
                player.currentScreenHandler.sendContentUpdates();
            }
        }

		@Override
		public Vote getType() {
			return null;
		}

		@Override
		public Text getDescription(VoterAction action) {
			return null;
		}
    }
}
