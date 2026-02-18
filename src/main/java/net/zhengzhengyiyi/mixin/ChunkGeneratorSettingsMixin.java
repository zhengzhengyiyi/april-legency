package net.zhengzhengyiyi.mixin;

import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.zhengzhengyiyi.accessor.ChunkSettingsAccessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChunkGeneratorSettings.class)
public abstract class ChunkGeneratorSettingsMixin implements ChunkSettingsAccessor {
   @Override
   public Builder getBuilder() {
      return new Builder((ChunkGeneratorSettings) (Object) this);
   }
   
   @Unique
   private long customSalt = 0L;

   @Override
   public long getCustomSalt() {
       return this.customSalt;
   }

   @Override
   public void setCustomSalt(long salt) {
       this.customSalt = salt;
   }
}
