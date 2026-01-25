package net.zhengzhengyiyi.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.serialization.JsonOps;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class DebugDimensionCommand {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("debugdim")
//            .requires(source -> source.hasPermissionLevel(2))
            .executes(context -> execute(context.getSource()))
        );
    }

    private static int execute(ServerCommandSource source) {
        ServerWorld world = source.getWorld();
        Path debugPath = source.getServer().getSavePath(WorldSavePath.ROOT).resolve("debug");
        
        try {
            Files.createDirectories(debugPath);
        } catch (IOException e) {
            source.sendError(Text.literal("Failed to create debug directory"));
            return 0;
        }

        DimensionType dimensionType = world.getDimension();
        var dimensionRegistry = world.getRegistryManager().getOrThrow(RegistryKeys.DIMENSION_TYPE);
        int dimId = dimensionRegistry.getRawId(dimensionType);
        
        DimensionType.CODEC.encodeStart(JsonOps.INSTANCE, dimensionType)
            .resultOrPartial(LOGGER::error)
            .ifPresent(json -> {
                Path dimFile = debugPath.resolve("dim-" + dimId + ".json");
                try (Writer writer = Files.newBufferedWriter(dimFile)) {
                    GSON.toJson(json, writer);
                } catch (IOException e) {
                    LOGGER.warn("Failed to save dimension file {}", dimFile, e);
                }
            });

        var biomeRegistry = world.getRegistryManager().getOrThrow(RegistryKeys.BIOME);
        world.getChunkManager().getChunkGenerator().getBiomeSource().getBiomes().forEach(biomeEntry -> {
            Biome biome = biomeEntry.value();
            int biomeId = biomeRegistry.getRawId(biome);
            
            Biome.CODEC.encodeStart(JsonOps.INSTANCE, biome)
                .resultOrPartial(LOGGER::error)
                .ifPresent(json -> {
                    Path biomeFile = debugPath.resolve("biome-" + biomeId + ".json");
                    try (Writer writer = Files.newBufferedWriter(biomeFile)) {
                        GSON.toJson(json, writer);
                    } catch (IOException e) {
                        LOGGER.warn("Failed to save biome file {}", biomeFile, e);
                    }
                });
        });

        source.sendFeedback(() -> Text.literal("Saved debug data to: " + debugPath), false);
        return 1;
    }
}
