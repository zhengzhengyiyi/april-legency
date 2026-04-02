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

	void setCurrentSpecialMine(Optional<SpecialMine> optional, boolean bl);

	Optional<SpecialMine> getRandomSpecialMine(Random random);

	int getMineLevel();

	int getMineExp();

	void addMineExp(int exp);

	int getTotalMineExp();

	int getLevelCount();
}
