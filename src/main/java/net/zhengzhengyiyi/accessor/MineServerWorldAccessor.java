package net.zhengzhengyiyi.accessor;

import java.util.List;
import java.util.Optional;

import net.zhengzhengyiyi.mine.MineEffect;
import net.zhengzhengyiyi.mine.SpecialMine;

public interface MineServerWorldAccessor {
	public void unlockMineEffect(MineEffect effect);
	public boolean isMineWorld();
	public boolean isMineCompleted();
	public boolean isMineWon();
	/** Returns true if the player has unlocked this effect globally (overworld save data). */
	boolean hasMineEffect(MineEffect effect);
	/** Returns true if this specific mine world was created with this effect. */
	boolean isMineWorldEffect(MineEffect effect);
	List<MineEffect> getUnlockedMineEffects();
	/**
	 * method_69085 — if the mine is completed, unlock the effect globally.
	 * Otherwise, drop a mine ingredient item in the world at the given position.
	 */
	void dropOrUnlockMineEffect(net.minecraft.util.math.Vec3d pos, MineEffect effect, @org.jetbrains.annotations.Nullable net.minecraft.server.network.ServerPlayerEntity player);
	default boolean hasSpecialMine(SpecialMine mine) { return false; }
	default void unlockSpecialMine(SpecialMine mine) {}
	default Optional<SpecialMine> getCurrentSpecialMine() { return Optional.empty(); }
}
