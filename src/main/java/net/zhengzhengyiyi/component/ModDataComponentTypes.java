package net.zhengzhengyiyi.component;

import java.util.function.UnaryOperator;

import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.zhengzhengyiyi.render.DimensionEffects;

public class ModDataComponentTypes {
	public static final ComponentType<DimensionEffects.class_11082> SKY = register(
		      "sky", builder -> builder.codec(DimensionEffects.class_11082.field_59008).packetCodec(DimensionEffects.class_11082.field_59009)
		   );
	
	private static <T> ComponentType<T> register(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
		return Registry.register(Registries.DATA_COMPONENT_TYPE, id, (builderOperator.apply(ComponentType.builder())).build());
	}
	
	public static void init() {
		
	}
}
