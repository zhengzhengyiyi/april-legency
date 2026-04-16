package net.zhengzhengyiyi.unlock;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Mirrors Craftmine class_10975 — manages pet registration and spawning.
 *
 * Pets are registered when a PlayerUnlock is activated (onActivate),
 * then spawned when the player enters a mine (onMineEnter).
 */
public class PetSpawner {

    /** Maps entity type → list of configurators to apply after spawning. */
    private static final Map<EntityType<?>, List<Consumer<TameableEntity>>> REGISTERED_PETS = new HashMap<>();

    /**
     * Mirrors class_10975.method_69179 — register a basic pet type.
     * Called from PlayerUnlock.Builder.spawnPet() onActivate.
     */
    public static void registerPet(EntityType<?> entityType) {
        REGISTERED_PETS.put(entityType, List.of());
    }

    /**
     * Mirrors class_10975.method_69182 — register a tameable pet with a configurator.
     * Called from PlayerUnlock.Builder.spawnTameablePet() onActivate.
     */
    public static void registerTameablePet(EntityType<?> entityType, Consumer<TameableEntity> configurator) {
        List<Consumer<TameableEntity>> existing = REGISTERED_PETS.getOrDefault(entityType, new ArrayList<>());
        List<Consumer<TameableEntity>> updated = new ArrayList<>(existing);
        updated.add(configurator);
        REGISTERED_PETS.put(entityType, updated);
    }

    /**
     * Mirrors class_10975.method_69180 — spawn a basic pet near the player.
     * Called from PlayerUnlock.Builder.spawnPet() onMineEnter.
     */
    public static void spawnPet(EntityType<?> entityType, ServerPlayerEntity player) {
        ServerWorld world = (ServerWorld) player.getEntityWorld();
        BlockPos playerPos = player.getBlockPos();
        BlockPos spawnPos = findSafeSpawnPos(world, playerPos);

        TameableEntity pet = (TameableEntity) entityType.spawn(world, spawnPos, SpawnReason.EVENT);
        if (pet != null) {
            pet.setTamed(true, false);
            pet.setInvulnerable(true);
            pet.setOwner(player);
            List<Consumer<TameableEntity>> configurators = REGISTERED_PETS.getOrDefault(entityType, List.of());
            configurators.forEach(c -> c.accept(pet));
        }
    }

    /**
     * Mirrors class_10975.method_69181 — spawn a typed tameable pet near the player.
     * Called from PlayerUnlock.Builder.spawnTameablePet() onMineEnter.
     */
    public static <T extends TameableEntity> void spawnTameablePet(
            EntityType<?> entityType, Class<T> entityClass, ServerPlayerEntity player) {
        ServerWorld world = (ServerWorld) player.getEntityWorld();
        BlockPos playerPos = player.getBlockPos();
        BlockPos.Mutable mutable = playerPos.mutableCopy().move(Direction.WEST);

        // Walk up until we find a non-solid block — mirrors Craftmine exactly
        while (!world.getBlockState(mutable).getCollisionShape(world, mutable).isEmpty()) {
            mutable.move(Direction.UP);
        }

        TameableEntity pet = (TameableEntity) entityType.spawn(world, mutable.toImmutable(), SpawnReason.EVENT);
        if (pet != null) {
            pet.setTamed(true, false);
            pet.setInvulnerable(true);
            pet.setOwner(player);
            List<Consumer<TameableEntity>> configurators = REGISTERED_PETS.getOrDefault(entityType, List.of());
            configurators.forEach(c -> c.accept(pet));
        }
    }

    /**
     * Find a safe spawn position near the player (solid ground, air above).
     */
    private static BlockPos findSafeSpawnPos(ServerWorld world, BlockPos center) {
        BlockPos.Mutable mutable = center.mutableCopy().move(Direction.WEST);
        while (!world.getBlockState(mutable).getCollisionShape(world, mutable).isEmpty()) {
            mutable.move(Direction.UP);
        }
        return mutable.toImmutable();
    }
}
