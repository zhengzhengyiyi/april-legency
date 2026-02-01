package net.zhengzhengyiyi;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.zhengzhengyiyi.generator.generation.BiomeSourceFactory;
import net.zhengzhengyiyi.generator.generation.*;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.fantasy.RuntimeWorldHandle;

import java.util.function.BiFunction;

public class InfiniteDimensionManager {
   private static final Int2ObjectMap<BiFunction<MinecraftServer, Integer, RuntimeWorldConfig>> field_23484 = new Int2ObjectOpenHashMap<>();

   public static BiFunction<MinecraftServer, Integer, RuntimeWorldConfig> method_26506(BiFunction<MinecraftServer, Integer, ChunkGenerator> generatorFactory) {
      return (server, hash) -> new RuntimeWorldConfig()
              .setGenerator(generatorFactory.apply(server, hash))
              .setSeed(hash);
   }
   
//   @SuppressWarnings("unchecked")
//public static ServerWorld getOrCreateInfiniteDimension(MinecraftServer server, int hash) {
//	    Identifier id = Identifier.of(AprilsLegacy.MOD_ID, "dim_" + hash);
//	    BiFunction<MinecraftServer, Integer, RuntimeWorldConfig> configFactory = field_23484.get(hash);
//
//	    var registryManager = server.getRegistryManager();
//	    var dimensionTypes = registryManager.getOrThrow(RegistryKeys.DIMENSION_TYPE);
//	    var settingsRegistry = registryManager.getOrThrow(RegistryKeys.CHUNK_GENERATOR_SETTINGS);
//	    var biomeRegistry = registryManager.getOrThrow(RegistryKeys.BIOME);
//	    var blockRegistry = registryManager.getOrThrow(RegistryKeys.BLOCK);
//	    
//	    java.util.Random random = new java.util.Random(hash);
//
//	    var overworldTypeEntry = dimensionTypes.getEntry(net.minecraft.world.dimension.DimensionTypes.OVERWORLD.getValue()).orElseThrow();
//	    
//	    var allBiomes = biomeRegistry.streamEntries().toList();
//	    var fixedBiomeSource = new net.minecraft.world.biome.source.FixedBiomeSource(allBiomes.get(random.nextInt(allBiomes.size())));
//
//	    RegistryKey<ChunkGeneratorSettings>[] keys = new RegistryKey[]{
//	        ChunkGeneratorSettings.OVERWORLD,
//	        ChunkGeneratorSettings.NETHER, 
//	        ChunkGeneratorSettings.END, 
//	        ChunkGeneratorSettings.FLOATING_ISLANDS,
//	        ChunkGeneratorSettings.CAVES
//	    };
//	    
//	    var shapeTemplate = settingsRegistry.getOrThrow(keys[random.nextInt(keys.length)]).value();
//	    var routerTemplate = settingsRegistry.getOrThrow(keys[random.nextInt(keys.length)]).value();
//	    
//	    var solidBlocks = blockRegistry.stream()
//	        .filter(b -> b.getDefaultState().isFullCube(net.minecraft.world.EmptyBlockView.INSTANCE, net.minecraft.util.math.BlockPos.ORIGIN))
//	        .toList();
//	    
//	    net.minecraft.world.gen.chunk.GenerationShapeConfig crazyShape = new net.minecraft.world.gen.chunk.GenerationShapeConfig(
//	        random.nextBoolean() ? -64 : 0, 
//	        random.nextInt(256) + 128, 
//	        random.nextInt(4) + 1, 
//	        random.nextInt(4) + 1
//	    );
//
//	    ChunkGeneratorSettings modifiedSettings = new ChunkGeneratorSettings(
//	        crazyShape,
//	        solidBlocks.get(random.nextInt(solidBlocks.size())).getDefaultState(), 
//	        random.nextBoolean() ? net.minecraft.block.Blocks.LAVA.getDefaultState() : net.minecraft.block.Blocks.WATER.getDefaultState(),
//	        routerTemplate.noiseRouter(),
//	        routerTemplate.surfaceRule(),
//	        shapeTemplate.spawnTarget(),
//	        random.nextInt(128),
//	        random.nextBoolean(), random.nextBoolean(), random.nextBoolean(), random.nextBoolean()
//	    );
//
//	    RuntimeWorldConfig config = new RuntimeWorldConfig().setSeed(hash);
//	    if (configFactory != null) {
//	        RuntimeWorldConfig extraConfig = configFactory.apply(server, hash);
//	        config.setGenerator(extraConfig.getGenerator());
//	        config.setSeed(extraConfig.getSeed());
//	    } else {
//	        config.setGenerator(new NoiseChunkGenerator(fixedBiomeSource, net.minecraft.registry.entry.RegistryEntry.of(modifiedSettings)));
//	    }
//
//	    config.setDimensionType(overworldTypeEntry);
//	    config.setRaining(random.nextBoolean());
//	    config.setShouldTickTime(random.nextBoolean());
//	    config.setTimeOfDay(random.nextInt(24000));
//	    
//	    return AprilsLegacy.fantasy.getOrOpenPersistentWorld(id, config).asWorld();
//	}
   
   @SuppressWarnings("unchecked")
   public static ServerWorld getOrCreateInfiniteDimension(MinecraftServer server, int hash) {
       Identifier id = Identifier.of(AprilsLegacy.MOD_ID, "dim_" + hash);
       BiFunction<MinecraftServer, Integer, RuntimeWorldConfig> configFactory = field_23484.get(hash);

       var registryManager = server.getRegistryManager();
       var dimensionTypes = registryManager.getOrThrow(RegistryKeys.DIMENSION_TYPE);
       var settingsRegistry = registryManager.getOrThrow(RegistryKeys.CHUNK_GENERATOR_SETTINGS);
       var biomeRegistry = registryManager.getOrThrow(RegistryKeys.BIOME);
       var blockRegistry = registryManager.getOrThrow(RegistryKeys.BLOCK);
       var structureSetRegistry = registryManager.getOrThrow(RegistryKeys.STRUCTURE_SET);
       
       java.util.Random random = new java.util.Random(hash);
       var overworldTypeEntry = dimensionTypes.getEntry(net.minecraft.world.dimension.DimensionTypes.OVERWORLD.getValue()).orElseThrow();
       
       var allBiomes = biomeRegistry.streamEntries().toList();
       var fixedBiomeSource = new net.minecraft.world.biome.source.FixedBiomeSource(allBiomes.get(random.nextInt(allBiomes.size())));

       RegistryKey<ChunkGeneratorSettings>[] keys = new RegistryKey[]{
           ChunkGeneratorSettings.OVERWORLD,
           ChunkGeneratorSettings.NETHER, 
           ChunkGeneratorSettings.END, 
           ChunkGeneratorSettings.FLOATING_ISLANDS,
           ChunkGeneratorSettings.CAVES
       };
       
       var shapeTemplate = settingsRegistry.getOrThrow(keys[random.nextInt(keys.length)]).value();
       var routerTemplate = settingsRegistry.getOrThrow(keys[random.nextInt(keys.length)]).value();
       
       var solidBlocks = blockRegistry.stream()
           .filter(b -> b.getDefaultState().isFullCube(net.minecraft.world.EmptyBlockView.INSTANCE, net.minecraft.util.math.BlockPos.ORIGIN))
           .toList();
       
       int minY = (random.nextInt(4) - 2) * 16; 
       int height = (random.nextInt(16) + 16) * 16; 

       net.minecraft.world.gen.chunk.GenerationShapeConfig crazyShape = new net.minecraft.world.gen.chunk.GenerationShapeConfig(
           minY, 
           height, 
           random.nextInt(2) + 1, 
           random.nextInt(2) + 1  
       );

       ChunkGeneratorSettings modifiedSettings = new ChunkGeneratorSettings(
           crazyShape,
           solidBlocks.get(random.nextInt(solidBlocks.size())).getDefaultState(), 
           random.nextBoolean() ? net.minecraft.block.Blocks.LAVA.getDefaultState() : net.minecraft.block.Blocks.WATER.getDefaultState(),
           routerTemplate.noiseRouter(),
           routerTemplate.surfaceRule(),
           shapeTemplate.spawnTarget(),
           random.nextInt(128),
           false, 
           random.nextBoolean(), 
           false, 
           false
       );

       RuntimeWorldConfig config = new RuntimeWorldConfig().setSeed(hash);
       if (configFactory != null) {
           RuntimeWorldConfig extraConfig = configFactory.apply(server, hash);
           config.setGenerator(extraConfig.getGenerator());
           config.setSeed(extraConfig.getSeed());
       } else {
    	   var randomStructureSets = structureSetRegistry.streamEntries()
    	            .filter(entry -> random.nextBoolean())
    	            .toList();
    	   
           config.setGenerator(new NoiseChunkGenerator(fixedBiomeSource, net.minecraft.registry.entry.RegistryEntry.of(modifiedSettings)));
       }

       config.setDimensionType(overworldTypeEntry);
       config.setRaining(random.nextBoolean());
       config.setShouldTickTime(random.nextBoolean());
       config.setTimeOfDay(random.nextInt(24000));
       
       return AprilsLegacy.fantasy.getOrOpenPersistentWorld(id, config).asWorld();
   }

   static {
	   field_23484.put(741472677, method_26506((server, h) -> 
	       new ColorGridChunkGenerator(
	           BiomeSourceFactory.createOverworldBiomeSource(server)
	       )
	   ));
	
	   field_23484.put(236157810, method_26506((server, h) -> 
	       new BlueMazeChunkGenerator(
	           BiomeSourceFactory.createOverworldBiomeSource(server)
	       )
	   ));
	   field_23484.put(1896587401, method_26506((server, h) -> 
		    new class_5021(
		        BiomeSourceFactory.createOverworldBiomeSource(server)
		    )
		));
	
		field_23484.put(726931095, method_26506((server, h) -> 
		    new class_5027(
		        BiomeSourceFactory.createOverworldBiomeSource(server)
		    )
		));
	
		field_23484.put(233542201, method_26506((server, h) -> 
		    new class_5029(
		        BiomeSourceFactory.createOverworldBiomeSource(server)
		    )
		));
	
		field_23484.put(107712651, method_26506((server, h) -> 
		    new class_5087(
		        BiomeSourceFactory.createOverworldBiomeSource(server)
		    )
		));
      
//      field_23484.put(669175628, method_26506((server, h) -> class_5031.createGenerator(server, h)));
//      field_23484.put(1929426645, method_26506((server, h) -> class_5034.createGenerator(server, h)));
//      field_23484.put(378547252, method_26506((server, h) -> class_5036.createGenerator(server, h)));
//      field_23484.put(94341406, method_26506((server, h) -> class_5037.createGenerator(server, h)));
//      field_23484.put(1174283440, method_26506((server, h) -> class_5038.createGenerator(server, h)));
//      field_23484.put(1210674279, method_26506((server, h) -> class_5039.createGenerator(server, h)));
//      field_23484.put(344885676, method_26506((server, h) -> class_5040.createGenerator(server, h)));
//      field_23484.put(31674686, method_26506((server, h) -> class_5042.createGenerator(server, h)));
//      field_23484.put(2114493792, method_26506((server, h) -> class_5044.createGenerator(server, h)));
//      field_23484.put(1143264807, method_26506((server, h) -> class_5044.createGenerator(server, h)));
//      field_23484.put(1049823113, method_26506((server, h) -> class_5044.createGenerator(server, h)));
//      field_23484.put(1011847535, method_26506((server, h) -> class_5045.createGenerator(server, h)));
//      field_23484.put(1902968744, method_26506((server, h) -> class_5047.createGenerator(server, h)));
//      field_23484.put(264458659, method_26506((server, h) -> class_5049.createGenerator(server, h)));
//      field_23484.put(1201319931, method_26506((server, h) -> class_5051.createGenerator(server, h)));
//      field_23484.put(1113696725, method_26506((server, h) -> class_5053.createGenerator(server, h)));
//      field_23484.put(1384344230, method_26506((server, h) -> class_5055.createGenerator(server, h)));
//      field_23484.put(214387762, method_26506((server, h) -> class_5057.createGenerator(server, h)));
//      field_23484.put(1098962767, method_26506((server, h) -> class_5059.createGenerator(server, h)));
//      field_23484.put(927632079, method_26506((server, h) -> class_5061.createGenerator(server, h)));
//      field_23484.put(307219718, method_26506((server, h) -> class_5063.createGenerator(server, h)));
//      field_23484.put(545072168, method_26506((server, h) -> class_5065.createGenerator(server, h)));
//      field_23484.put(1834117187, method_26506((server, h) -> class_5067.createGenerator(server, h)));
//      field_23484.put(661885389, method_26506((server, h) -> class_5069.createGenerator(server, h)));
//      field_23484.put(1036032341, method_26506((server, h) -> class_5071.createGenerator(server, h)));
//      field_23484.put(484336196, method_26506((server, h) -> class_5073.createGenerator(server, h)));
//      field_23484.put(1059552697, method_26506((server, h) -> class_5075.createGenerator(server, h)));
//      field_23484.put(907661935, method_26506((server, h) -> class_5077.createGenerator(server, h)));
//      field_23484.put(1141490659, method_26506((server, h) -> class_5079.createGenerator(server, h)));
//      field_23484.put(1028465021, method_26506((server, h) -> class_5081.createGenerator(server, h)));
//      field_23484.put(2003598857, method_26506((server, h) -> class_5083.createGenerator(server, h)));
//      field_23484.put(985130845, method_26506((server, h) -> class_5085.createGenerator(server, h)));
//      field_23484.put(251137100, method_26506((server, h) -> class_5089.createGenerator(server, h)));
//      field_23484.put(1537997313, method_26506((server, h) -> class_5091.createGenerator(server, h)));
//      field_23484.put(1916276638, method_26506((server, h) -> class_5093.createGenerator(server, h)));
//      field_23484.put(894945615, method_26506((server, h) -> class_5095.createGenerator(server, h)));
      field_23484.put(1791460938, method_26506((server, h) -> 
	    new class_5097(
	        BiomeSourceFactory.createOverworldBiomeSource(server),
	        ""
	    )
	));
   }
}
