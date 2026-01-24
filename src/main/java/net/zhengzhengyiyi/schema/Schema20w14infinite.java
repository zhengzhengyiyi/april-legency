package net.zhengzhengyiyi.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class Schema20w14infinite extends IdentifierNormalizingSchema {
   public Schema20w14infinite(int version, Schema parent) {
      super(version, parent);
   }

   @Override
   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
      Map<String, Supplier<TypeTemplate>> blockEntities = super.registerBlockEntities(schema);
      schema.register(blockEntities, "minecraft:neither", DSL::remainder);
      return blockEntities;
   }
}
