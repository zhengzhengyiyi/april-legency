package net.zhengzhengyiyi.command;

import com.google.common.collect.ImmutableSet;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Comparator;
import java.util.stream.IntStream;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.WorldChunk;
import net.zhengzhengyiyi.InfiniteDimensionManager;
import net.zhengzhengyiyi.generator.DimensionHasher;

public class WarpCommand {
   public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
      commandDispatcher.register(
         CommandManager.literal("warp")
//            .requires(source -> source.hasPermissionLevel(2))
            .then(
               CommandManager.argument("target", StringArgumentType.greedyString())
                  .executes(
                     commandContext -> method_26726(commandContext.getSource(), StringArgumentType.getString(commandContext, "target"))
                  )
            )
      );
   }

   private static int method_26726(ServerCommandSource serverCommandSource, String string) throws CommandSyntaxException {
      int hash = DimensionHasher.hash(string);
      ServerWorld serverWorld = InfiniteDimensionManager.getOrCreateInfiniteDimension(serverCommandSource.getServer(), hash);
      
      WorldChunk worldChunk = serverWorld.getChunk(0, 0);
      BlockPos blockPos = IntStream.range(0, 16).boxed().flatMap(x -> IntStream.range(0, 16).mapToObj(z -> {
         int y = worldChunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING, x, z);
         return new BlockPos(x, y, z);
      })).filter(pos -> pos.getY() > 0).max(Comparator.comparing(Vec3i::getY)).orElse(new BlockPos(0, 100, 0));

      serverCommandSource.getEntityOrThrow().teleport(
         serverWorld,
         blockPos.getX() + 0.5,
         blockPos.getY() + 1.0,
         blockPos.getZ() + 0.5,
         ImmutableSet.of(),
         0.0F,
         0.0F,
         true
      );
      
      return 1;
   }
}
