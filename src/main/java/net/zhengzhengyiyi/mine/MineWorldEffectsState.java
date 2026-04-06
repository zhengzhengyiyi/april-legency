package net.zhengzhengyiyi.mine;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores the MineEffect list for a Fantasy mine world.
 * Since Fantasy worlds don't use DimensionOptions from the registry,
 * we persist the effects here so they survive world reloads.
 */
public class MineWorldEffectsState extends PersistentState {

   public static final Codec<MineWorldEffectsState> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
         MineEffect.CODEC.listOf().fieldOf("effects").forGetter(s -> s.effects)
      ).apply(instance, MineWorldEffectsState::new)
   );

   public static final PersistentStateType<MineWorldEffectsState> TYPE = new PersistentStateType<>(
      "mine_effects", MineWorldEffectsState::new, CODEC, DataFixTypes.SAVED_DATA_RANDOM_SEQUENCES
   );

   private final List<MineEffect> effects;

   public MineWorldEffectsState() {
      this.effects = new ArrayList<>();
   }

   public MineWorldEffectsState(List<MineEffect> effects) {
      this.effects = new ArrayList<>(effects);
   }

   public List<MineEffect> getEffects() {
      return effects;
   }

   public void setEffects(List<MineEffect> newEffects) {
      this.effects.clear();
      this.effects.addAll(newEffects);
      this.markDirty();
   }
}
