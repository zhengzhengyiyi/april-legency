package net.zhengzhengyiyi.accessor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import net.zhengzhengyiyi.mine.MineEffect;
import net.zhengzhengyiyi.mine.SpecialMine;

public interface MineServerWorldAccessor {
	public void unlockMineEffect(MineEffect effect);
	public boolean isMineWorld();
	public boolean isMineCompleted();
	public boolean isMineWon();
	boolean hasMineEffect(MineEffect effect);
	boolean isMineWorldEffect(MineEffect effect);
	List<MineEffect> getUnlockedMineEffects();
	void dropOrUnlockMineEffect(net.minecraft.util.math.Vec3d pos, MineEffect effect, @org.jetbrains.annotations.Nullable net.minecraft.server.network.ServerPlayerEntity player);
	default boolean hasSpecialMine(SpecialMine mine) { return false; }
	default void unlockSpecialMine(SpecialMine mine) {}
	default Optional<SpecialMine> getCurrentSpecialMine() { return Optional.empty(); }

	/** Mirrors craftmine ServerWorld.method_69093 — places spawn platform on first entry, teleports players. */
	void method_69093(boolean revisit, Optional<UUID> playerUuid);
}
