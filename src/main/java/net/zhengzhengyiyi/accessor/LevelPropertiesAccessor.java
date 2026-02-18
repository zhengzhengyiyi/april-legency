package net.zhengzhengyiyi.accessor;

import java.util.Optional;

import net.minecraft.util.math.random.Random;
import net.zhengzhengyiyi.mine.MineEffect;
import net.zhengzhengyiyi.mine.SpecialMine;

public interface LevelPropertiesAccessor {
	void method_70220(MineEffect arg);

	boolean method_70223(MineEffect arg);

	void method_70222(SpecialMine arg);

	boolean method_70219(SpecialMine arg);

	void method_70221(Optional<SpecialMine> optional, boolean bl);

	Optional<SpecialMine> method_70218(Random random);

	int method_70227();

	int method_70228();

	void method_70224(int i);

	int method_70225();

	int method_70226();
}
