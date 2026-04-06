package net.zhengzhengyiyi.entity.pet;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * class_10975 - Pet Entity Manager
 * Manages registration, spawning, and customization of pet entities.
 */
public class PetManager {
    private static final Map<String, EntityType<? extends BasePetEntity>> PET_TYPES = new HashMap<>();
    private static final Map<String, List<Consumer<BasePetEntity>>> CUSTOMIZATIONS = new HashMap<>();

    /** method_69179 - Registers pet type */
    public static void method_69179(String id, EntityType<? extends BasePetEntity> type) {
        PET_TYPES.put(id, type);
    }

    /** method_69182 - Adds pet customization */
    public static void method_69182(String id, Consumer<BasePetEntity> customization) {
        CUSTOMIZATIONS.computeIfAbsent(id, k -> new ArrayList<>()).add(customization);
    }

    /** method_69180 - Spawns pet for player */
    public static BasePetEntity method_69180(ServerWorld world, PlayerEntity player, String petTypeId) {
        EntityType<? extends BasePetEntity> type = PET_TYPES.get(petTypeId);
        if (type == null) return null;

        BlockPos spawnPos = player.getBlockPos().add(1, 0, 0);
        BasePetEntity pet = type.spawn(world, spawnPos, SpawnReason.COMMAND);
        if (pet != null) {
            if (player instanceof net.minecraft.server.network.ServerPlayerEntity serverPlayer) {
                pet.setOwner(serverPlayer);
            }
            pet.setTamed(true, false);
            // Apply customizations
            List<Consumer<BasePetEntity>> customs = CUSTOMIZATIONS.get(petTypeId);
            if (customs != null) {
                customs.forEach(c -> c.accept(pet));
            }
        }
        return pet;
    }

    /** method_69181 - Customizes nearby pet */
    public static void method_69181(ServerWorld world, PlayerEntity player, Consumer<BasePetEntity> customizer) {
        world.getEntitiesByClass(BasePetEntity.class, player.getBoundingBox().expand(5.0), pet ->
            player.equals(pet.getOwner())
        ).forEach(customizer);
    }

    /** Returns all registered pet type IDs */
    public static Iterable<String> getRegisteredTypes() {
        return PET_TYPES.keySet();
    }
}
