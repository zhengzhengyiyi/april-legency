package net.zhengzhengyiyi.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.serialization.JsonOps;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugdimCommand {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private static final Logger LOGGER = LogManager.getLogger();

   public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
      commandDispatcher.register(
         CommandManager.literal("debugdim")
//            .requires(source -> source.hasPermissionLevel(2))
            .executes(commandContext -> execute(commandContext.getSource()))
      );
   }

   private static int execute(ServerCommandSource source) {
      ServerWorld world = source.getWorld();
      DimensionType dimensionType = world.getDimension();
      File saveDir = source.getServer().getSavePath(WorldSavePath.ROOT).toFile();
      File debugDir = new File(saveDir, "debug");
      debugDir.mkdirs();

      DimensionType.CODEC.encodeStart(JsonOps.INSTANCE, dimensionType)
         .resultOrPartial(LOGGER::error)
         .ifPresent(json -> {
            File file = new File(debugDir, "dimension_type.json");
            try (Writer writer = Files.newBufferedWriter(file.toPath())) {
               GSON.toJson(json, writer);
            } catch (IOException e) {
               LOGGER.warn("Failed to save file {}", file.getAbsolutePath(), e);
            }
         });

      world.getChunkManager().getChunkGenerator().getBiomeSource().getBiomes().forEach(biome -> {
         source.getServer().getRegistryManager().getOrThrow(RegistryKeys.BIOME).getCodec()
            .encodeStart(JsonOps.INSTANCE, biome.value())
            .resultOrPartial(LOGGER::error)
            .ifPresent(json -> {
               String name = biome.getKey().map(key -> key.getValue().toUnderscoreSeparatedString()).orElse("unknown");
               File file = new File(debugDir, "biome-" + name + ".json");
               try (Writer writer = Files.newBufferedWriter(file.toPath())) {
                  GSON.toJson(json, writer);
               } catch (IOException e) {
                  LOGGER.warn("Failed to save file {}", file.getAbsolutePath(), e);
               }
            });
      });

      source.sendFeedback(() -> Text.literal("Saved debug info to: " + debugDir.getAbsolutePath()), false);
      return 1;
   }
}
