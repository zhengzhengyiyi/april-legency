package net.zhengzhengyiyi.generator;

import com.mojang.serialization.Codec;

import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.world.gen.carver.CarverDebugConfig;
import net.minecraft.world.gen.YOffset;
import net.minecraft.block.Block;

public abstract class ProbabilityCarver extends Carver<ProbabilityCarver.CustomProbabilityConfig> {
    public ProbabilityCarver(Codec<CustomProbabilityConfig> configCodec) {
        super(configCodec);
    }

    public CustomProbabilityConfig method_26583(Random random) {
        return new CustomProbabilityConfig(random.nextFloat() / 2.0F);
    }

    public static class CustomProbabilityConfig extends CarverConfig {
        public final float probability;

        public CustomProbabilityConfig(float probability, HeightProvider y, FloatProvider yScale, YOffset lavaLevel, CarverDebugConfig debugConfig, RegistryEntryList<Block> replaceable) {
            super(probability, y, yScale, lavaLevel, debugConfig, replaceable);
            this.probability = probability;
        }

        public CustomProbabilityConfig(float probability) {
        	this(
        	        probability,
        	        net.minecraft.world.gen.heightprovider.ConstantHeightProvider.create(net.minecraft.world.gen.YOffset.fixed(0)), 
        	        net.minecraft.util.math.floatprovider.ConstantFloatProvider.create(1.0F), 
        	        net.minecraft.world.gen.YOffset.fixed(-64), 
        	        net.minecraft.world.gen.carver.CarverDebugConfig.DEFAULT, 
        	        net.minecraft.registry.entry.RegistryEntryList.of() 
        	    );
        }
    }
}