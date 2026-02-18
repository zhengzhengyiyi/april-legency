package net.zhengzhengyiyi.component;

import net.zhengzhengyiyi.mine.DimensionSettingsBuilder;

@FunctionalInterface
public interface MineEffectComponent {
	public void apply(DimensionSettingsBuilder context);
}
