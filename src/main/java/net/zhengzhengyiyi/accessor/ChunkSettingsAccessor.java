package net.zhengzhengyiyi.accessor;

import java.util.List;
import java.util.function.UnaryOperator;

import org.spongepowered.asm.mixin.Unique;

import net.minecraft.block.BlockState;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.noise.NoiseRouter;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;

public interface ChunkSettingsAccessor extends ISaltSettings {
	public Builder getBuilder();
	
	@Unique
	   class Builder {
	      private GenerationShapeConfig shapeConfig;
	      private BlockState defaultBlock;
	      private BlockState defaultFluid;
	      private NoiseRouter noiseRouter;
	      private MaterialRules.MaterialRule surfaceRule;
	      private List<MultiNoiseUtil.NoiseHypercube> spawnTarget;
	      private int seaLevel;
	      private boolean disableMobGen;
	      private boolean aquifers;
	      private boolean oreVeins;
	      private boolean legacyRandom;
	      private long salt;

	      @SuppressWarnings("deprecation")
		public Builder(ChunkGeneratorSettings settings) {
	         this.shapeConfig = settings.generationShapeConfig();
	         this.defaultBlock = settings.defaultBlock();
	         this.defaultFluid = settings.defaultFluid();
	         this.noiseRouter = settings.noiseRouter();
	         this.surfaceRule = settings.surfaceRule();
	         this.spawnTarget = settings.spawnTarget();
	         this.seaLevel = settings.seaLevel();
	         this.disableMobGen = settings.mobGenerationDisabled();
	         this.aquifers = settings.aquifers();
	         this.oreVeins = settings.oreVeins();
	         this.legacyRandom = settings.usesLegacyRandom();
	         this.salt = ((ISaltSettings)(Object)settings).getCustomSalt();
	      }

	      public Builder method_69808(GenerationShapeConfig config) {
	         this.shapeConfig = config;
	         return this;
	      }

	      public Builder method_69806(BlockState state) {
	         this.defaultBlock = state;
	         return this;
	      }

	      public Builder method_69814(BlockState state) {
	         this.defaultFluid = state;
	         return this;
	      }

	      public Builder method_69807(NoiseRouter router) {
	         this.noiseRouter = router;
	         return this;
	      }

	      public Builder method_69809(MaterialRules.MaterialRule rule) {
	         this.surfaceRule = rule;
	         return this;
	      }

	      public Builder method_69811(UnaryOperator<MaterialRules.MaterialRule> operator) {
	         this.surfaceRule = operator.apply(this.surfaceRule);
	         return this;
	      }

	      public Builder method_69810(List<MultiNoiseUtil.NoiseHypercube> list) {
	         this.spawnTarget = list;
	         return this;
	      }

	      public Builder method_69804(int seaLevel) {
	         this.seaLevel = seaLevel;
	         return this;
	      }

	      public int method_69803() {
	         return this.seaLevel;
	      }

	      public Builder method_69812(boolean bl) {
	         this.disableMobGen = bl;
	         return this;
	      }

	      public Builder method_69815(boolean bl) {
	         this.aquifers = bl;
	         return this;
	      }

	      public Builder method_69816(boolean bl) {
	         this.oreVeins = bl;
	         return this;
	      }

	      public Builder method_69817(boolean bl) {
	         this.legacyRandom = bl;
	         return this;
	      }

	      public Builder method_69805(long salt) {
	         this.salt = salt;
	         return this;
	      }

	      public ChunkGeneratorSettings build() {
	         ChunkGeneratorSettings chunkSettings = new ChunkGeneratorSettings(
	            this.shapeConfig, this.defaultBlock, this.defaultFluid, this.noiseRouter,
	            this.surfaceRule, this.spawnTarget, this.seaLevel, this.disableMobGen,
	            this.aquifers, this.oreVeins, this.legacyRandom
	         );
	         
	         ((ISaltSettings)(Object)chunkSettings).setCustomSalt(salt);
	         
	         return chunkSettings;
	      }
	   }
}
