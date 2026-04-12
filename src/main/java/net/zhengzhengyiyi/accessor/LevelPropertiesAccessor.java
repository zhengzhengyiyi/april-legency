package net.zhengzhengyiyi.accessor;

import java.util.Optional;

import net.minecraft.util.math.random.Random;
import net.zhengzhengyiyi.mine.MineEffect;
import net.zhengzhengyiyi.mine.SpecialMine;

public interface LevelPropertiesAccessor {
	void setUnlockedMineEffect(MineEffect effect);
	boolean hasUnlockedMineEffect(MineEffect effect);

	void setSpecialMine(SpecialMine mine);
	boolean hasSpecialMine(SpecialMine mine);

	/** method_70221 — called when a mine ends (win/lose), records the special mine result */
	void setCurrentSpecialMine(Optional<SpecialMine> optional, boolean bl);

	/** method_70218 — returns the next special mine to present (random selection) */
	Optional<SpecialMine> getRandomSpecialMine(Random random);

	/** method_70227 — mine level */
	int getMineLevel();

	/** method_70228 — mine exp within current level */
	int getMineExp();

	/** method_70224 — add mine exp */
	void addMineExp(int exp);

	int getTotalMineExp();

	/** method_70226 — total number of mines created (used for level IDs). Pre-increments the counter. */
	int getLevelCount();

	/** Returns the current level count without incrementing it. Used to restore existing dimensions on world load. */
	int peekLevelCount();

	/** method_70225 — current mine index (for display) */
	default int getCurrentMineIndex() { return peekLevelCount(); }
}
