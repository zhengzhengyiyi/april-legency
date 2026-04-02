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
	boolean hasMineEffect(MineEffect effect);
	List<MineEffect> getUnlockedMineEffects();
	default boolean hasSpecialMine(SpecialMine mine) { return false; }
	default void unlockSpecialMine(SpecialMine mine) {}
	default Optional<SpecialMine> getCurrentSpecialMine() { return Optional.empty(); }
}
