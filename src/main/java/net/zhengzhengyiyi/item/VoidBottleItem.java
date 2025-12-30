package net.zhengzhengyiyi.item;

import net.minecraft.item.Item;

// TODO fix this and register inside Items.java

public class VoidBottleItem extends Item {
    public VoidBottleItem(Item.Settings settings) {
        super(settings);
    }

//    @Override
//    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
//        user.emitGameEvent(GameEvent.DRINK);
//        
//        if (!world.isClient && user instanceof PlayerEntity player) {
//            player.damage(player.getDamageSources().outOfWorld(), 8.0F);
//            
//            StatusEffectInstance randomEffect = extractRandomEffect(player);
//            world.playSound(null, player.getX(), player.getY(), player.getZ(), 
//                SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
//
//            if (randomEffect != null) {
//                ItemStack potion = new ItemStack(Items.POTION);
//                potion.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(
//                    Optional.empty(), Optional.empty(), List.of(randomEffect)));
//                return ItemUsage.exchangeStack(stack, player, potion);
//            } else {
//                return captureEntityIntoBottle(player);
//            }
//        }
//        return stack;
//    }
//
//    private static StatusEffectInstance extractRandomEffect(LivingEntity entity) {
//        ArrayList<StatusEffectInstance> effects = new ArrayList<>(entity.getStatusEffects());
//        if (!effects.isEmpty()) {
//            StatusEffectInstance target = Util.getRandom(effects, entity.getRandom());
//            entity.removeStatusEffect(target.getEffectType());
//            return new StatusEffectInstance(target);
//        }
//        return null;
//    }
//
//    @Override
//    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
//        return 32;
//    }
//
//    @Override
//    public UseAction getUseAction(ItemStack stack) {
//        return UseAction.DRINK;
//    }
//
//    @Override
//    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
//        if (user.getStatusEffects().isEmpty() && !user.hasVehicle()) {
//            return TypedActionResult.fail(user.getStackInHand(hand));
//        }
//        return ItemUsage.consumeHeldItem(world, user, hand);
//    }
//
//    public static ItemStack captureEntityIntoBottle(LivingEntity player) {
//        LivingEntity target = player;
//        
//        if (player.getVehicle() instanceof LivingEntity vehicle) {
//            player.stopRiding();
//            target = vehicle;
//        }
//
//        NbtCompound entityData = new NbtCompound();
//        target.saveSelfNbt(entityData);
//
//        ItemStack entityBottle = new ItemStack(Items.VOID_BOTTLE); 
//        
//        NbtCompound customData = new NbtCompound();
//        customData.put("EntityTag", entityData);
//        entityBottle.set(DataComponentTypes.CUSTOM_DATA, net.minecraft.component.type.NbtComponent.of(customData));
//        
//        if (target == player) {
//            player.damage(player.getDamageSources().outOfWorld(), 1.0F);
//        }
//
//        return entityBottle;
//    }
}
