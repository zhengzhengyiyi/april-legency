package net.zhengzhengyiyi.render;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntFunction;

import net.minecraft.component.ComponentsAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public record DimensionEffects(
   Optional<Float> cloudsHeight,
   boolean alternateSkyColor,
   Optional<DimensionEffects.class_11082> sky,
   boolean brightenLighting,
   boolean darkened,
   DimensionEffects.class_11079 fogScaler,
   boolean isAlwaysFoggy,
   boolean hasSunriseAndSunset
) {
   public static final Codec<DimensionEffects> field_58978 = RecordCodecBuilder.create(
      instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("cloud_level").forGetter(DimensionEffects::cloudsHeight),
            Codec.BOOL.fieldOf("has_ground").forGetter(DimensionEffects::alternateSkyColor),
            DimensionEffects.class_11082.field_59008.optionalFieldOf("sky_type").forGetter(DimensionEffects::sky),
            Codec.BOOL.fieldOf("force_bright_lightmap").forGetter(DimensionEffects::brightenLighting),
            Codec.BOOL.fieldOf("constant_ambient_light").forGetter(DimensionEffects::darkened),
            DimensionEffects.class_11079.field_58994.fieldOf("fog_scaler").forGetter(DimensionEffects::fogScaler),
            Codec.BOOL.fieldOf("is_always_foggy").forGetter(DimensionEffects::isAlwaysFoggy),
            Codec.BOOL.fieldOf("has_sunrise_and_sunset").forGetter(DimensionEffects::hasSunriseAndSunset)
         )
         .apply(instance, DimensionEffects::new)
   );
   public static final PacketCodec<RegistryByteBuf, DimensionEffects> field_58979 = PacketCodec.tuple(
      PacketCodecs.FLOAT.collect(PacketCodecs::optional),
      DimensionEffects::cloudsHeight,
      PacketCodecs.BOOLEAN,
      DimensionEffects::alternateSkyColor,
      DimensionEffects.class_11082.field_59009.collect(PacketCodecs::optional),
      DimensionEffects::sky,
      PacketCodecs.BOOLEAN,
      DimensionEffects::brightenLighting,
      PacketCodecs.BOOLEAN,
      DimensionEffects::darkened,
      DimensionEffects.class_11079.field_58995,
      DimensionEffects::fogScaler,
      PacketCodecs.BOOLEAN,
      DimensionEffects::isAlwaysFoggy,
      PacketCodecs.BOOLEAN,
      DimensionEffects::hasSunriseAndSunset,
      DimensionEffects::new
   );
   public static final float field_58980 = 0.4F;

   public DimensionEffects method_69774(Optional<DimensionEffects.class_11082> optional) {
      return new DimensionEffects(
         this.cloudsHeight,
         this.alternateSkyColor,
         optional,
         this.brightenLighting,
         this.darkened,
         this.fogScaler,
         this.isAlwaysFoggy,
         this.hasSunriseAndSunset
      );
   }

   public boolean isSunRisingOrSetting(float skyAngle) {
      if (!this.hasSunriseAndSunset) {
         return false;
      } else {
         float f = MathHelper.cos(skyAngle * (float) (Math.PI * 2));
         return f >= -0.4F && f <= 0.4F;
      }
   }

   public int getSkyColor(float skyAngle) {
      if (!this.hasSunriseAndSunset) {
         return 0;
      } else {
         float f = MathHelper.cos(skyAngle * (float) (Math.PI * 2));
         float g = f / 0.4F * 0.5F + 0.5F;
         float h = MathHelper.square(1.0F - (1.0F - MathHelper.sin(g * (float) Math.PI)) * 0.99F);
         return ColorHelper.fromFloats(h, g * 0.3F + 0.7F, g * g * 0.7F + 0.2F, 0.2F);
      }
   }

   public float getCloudsHeight() {
      return this.cloudsHeight.orElse(Float.NaN);
   }

   public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
      return this.fogScaler.method_69780(color, sunHeight);
   }

   public boolean useThickFog(int camX, int camY) {
      return this.isAlwaysFoggy;
   }

   public static enum SkyType implements StringIdentifiable {
      NORMAL(0, "overworld", DimensionEffects.class_11080.field_59001, DimensionEffects.class_11080.field_59002),
      END(1, "end", DimensionEffects.class_11078.field_58989, DimensionEffects.class_11078.field_58990),
      CUBE(2, "cube", DimensionEffects.class_11077.field_58985, DimensionEffects.class_11077.field_58986),
      PANORAMA(3, "panorama", DimensionEffects.class_11081.field_59006, DimensionEffects.class_11081.field_59007),
      CODE(4, "code", DimensionEffects.class_11076.field_58983, DimensionEffects.class_11076.field_58984);

      private static final IntFunction<DimensionEffects.SkyType> field_59015 = ValueLists.createIndexToValueFunction(
         skyType -> skyType.field_59016, values(), ValueLists.OutOfBoundsHandling.ZERO
      );
      public static final Codec<DimensionEffects.SkyType> field_59013 = StringIdentifiable.createCodec(DimensionEffects.SkyType::values);
      public static final PacketCodec<RegistryByteBuf, DimensionEffects.SkyType> field_59014 = PacketCodecs.<DimensionEffects.SkyType>indexed(
            field_59015, skyType -> skyType.field_59016
         )
         .cast();
      private final int field_59016;
      private final String field_59017;
      final MapCodec<? extends DimensionEffects.class_11082> field_59018;
      final PacketCodec<? super RegistryByteBuf, ? extends DimensionEffects.class_11082> field_59019;

      private <T extends DimensionEffects.class_11082> SkyType(
         final int j, final String string2, final MapCodec<T> mapCodec, final PacketCodec<RegistryByteBuf, T> packetCodec
      ) {
         this.field_59016 = j;
         this.field_59017 = string2;
         this.field_59018 = mapCodec;
         this.field_59019 = packetCodec;
      }

      @Override
      public String asString() {
         return this.field_59017;
      }
   }

   public static class class_11076 implements DimensionEffects.class_11082 {
      public static final Text field_58981 = Text.translatable("sky.code");
      public static final DimensionEffects.class_11076 field_58982 = new DimensionEffects.class_11076();
      public static final MapCodec<DimensionEffects.class_11076> field_58983 = MapCodec.unit(field_58982);
      public static final PacketCodec<RegistryByteBuf, DimensionEffects.class_11076> field_58984 = PacketCodec.unit(field_58982);

      @Override
      public DimensionEffects.SkyType method_69775() {
         return DimensionEffects.SkyType.CODE;
      }

      @Override
      public Text method_69776() {
         return field_58981;
      }

	  @Override
	  public void appendTooltip(TooltipContext context, Consumer<Text> textConsumer, TooltipType type,
			ComponentsAccess components) {
		
	  }
   }

   public record class_11077(Identifier textureId, int repeats, float size, Text title) implements DimensionEffects.class_11082 {
      public static final MapCodec<DimensionEffects.class_11077> field_58985 = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               Identifier.CODEC.fieldOf("texture").forGetter(DimensionEffects.class_11077::textureId),
               Codecs.POSITIVE_INT.fieldOf("repeats").forGetter(DimensionEffects.class_11077::repeats),
               Codec.FLOAT.fieldOf("size").forGetter(DimensionEffects.class_11077::size),
               TextCodecs.CODEC.fieldOf("name").forGetter(DimensionEffects.class_11077::title)
            )
            .apply(instance, DimensionEffects.class_11077::new)
      );
      public static final PacketCodec<RegistryByteBuf, DimensionEffects.class_11077> field_58986 = PacketCodec.tuple(
         Identifier.PACKET_CODEC,
         DimensionEffects.class_11077::textureId,
         PacketCodecs.VAR_INT,
         DimensionEffects.class_11077::repeats,
         PacketCodecs.FLOAT,
         DimensionEffects.class_11077::size,
         TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC,
         DimensionEffects.class_11077::title,
         DimensionEffects.class_11077::new
      );

      @Override
      public DimensionEffects.SkyType method_69775() {
         return DimensionEffects.SkyType.CUBE;
      }

      @Override
      public Text method_69776() {
         return this.title;
      }

	  @Override
	public void appendTooltip(TooltipContext context, Consumer<Text> textConsumer, TooltipType type,
			ComponentsAccess components) {
	}
   }

   public static class class_11078 implements DimensionEffects.class_11082 {
      public static final Text field_58987 = Text.translatable("sky.end");
      public static final DimensionEffects.class_11078 field_58988 = new DimensionEffects.class_11078();
      public static final MapCodec<DimensionEffects.class_11078> field_58989 = MapCodec.unit(field_58988);
      public static final PacketCodec<RegistryByteBuf, DimensionEffects.class_11078> field_58990 = PacketCodec.unit(field_58988);

      @Override
      public DimensionEffects.SkyType method_69775() {
         return DimensionEffects.SkyType.END;
      }

      @Override
      public Text method_69776() {
         return field_58987;
      }

	  @Override
	  public void appendTooltip(TooltipContext context, Consumer<Text> textConsumer, TooltipType type,
			ComponentsAccess components) {
	  }
   }

   public static enum class_11079 implements StringIdentifiable {
      UNSCALED(0, "unscaled") {
         @Override
         public Vec3d method_69780(Vec3d vec3d, float f) {
            return vec3d;
         }
      },
      OVERWORLD(1, "overworld") {
         @Override
         public Vec3d method_69780(Vec3d vec3d, float f) {
            return vec3d.multiply(f * 0.94F + 0.06F, f * 0.94F + 0.06F, f * 0.91F + 0.09F);
         }
      },
      END(2, "end") {
         @Override
         public Vec3d method_69780(Vec3d vec3d, float f) {
            return vec3d.multiply(0.15F);
         }
      };

      private static final IntFunction<DimensionEffects.class_11079> field_58996 = ValueLists.createIndexToValueFunction(
         arg -> arg.field_58997, values(), ValueLists.OutOfBoundsHandling.ZERO
      );
      public static final Codec<DimensionEffects.class_11079> field_58994 = StringIdentifiable.createCodec(DimensionEffects.class_11079::values);
      public static final PacketCodec<ByteBuf, DimensionEffects.class_11079> field_58995 = PacketCodecs.indexed(field_58996, arg -> arg.field_58997);
      private final int field_58997;
      private final String field_58998;

      class_11079(final int j, final String string2) {
         this.field_58997 = j;
         this.field_58998 = string2;
      }

      @Override
      public String asString() {
         return this.field_58998;
      }

      public abstract Vec3d method_69780(Vec3d vec3d, float f);
   }

   public static class class_11080 implements DimensionEffects.class_11082 {
      public static final DimensionEffects.class_11080 field_59000 = new DimensionEffects.class_11080();
      public static final MapCodec<DimensionEffects.class_11080> field_59001 = MapCodec.unit(field_59000);
      public static final PacketCodec<RegistryByteBuf, DimensionEffects.class_11080> field_59002 = PacketCodec.unit(field_59000);
      public static final Text field_59003 = Text.translatable("sky.overworld");

      @Override
      public DimensionEffects.SkyType method_69775() {
         return DimensionEffects.SkyType.NORMAL;
      }

      @Override
      public Text method_69776() {
         return field_59003;
      }

	  @Override
	  public void appendTooltip(TooltipContext context, Consumer<Text> textConsumer, TooltipType type,
			ComponentsAccess components) {
		
	  }
   }

   public static class class_11081 implements DimensionEffects.class_11082 {
      public static final Text field_59004 = Text.translatable("sky.panorama");
      public static final DimensionEffects.class_11081 field_59005 = new DimensionEffects.class_11081();
      public static final MapCodec<DimensionEffects.class_11081> field_59006 = MapCodec.unit(field_59005);
      public static final PacketCodec<RegistryByteBuf, DimensionEffects.class_11081> field_59007 = PacketCodec.unit(field_59005);

      @Override
      public DimensionEffects.SkyType method_69775() {
         return DimensionEffects.SkyType.PANORAMA;
      }

      @Override
      public Text method_69776() {
         return field_59004;
      }

	  @Override
	  public void appendTooltip(TooltipContext context, Consumer<Text> textConsumer, TooltipType type,
			ComponentsAccess components) {
		
	  }
   }

   public interface class_11082 extends TooltipAppender {
      Codec<DimensionEffects.class_11082> field_59008 = DimensionEffects.SkyType.field_59013
         .dispatch(DimensionEffects.class_11082::method_69775, skyType -> skyType.field_59018);
      PacketCodec<RegistryByteBuf, DimensionEffects.class_11082> field_59009 = DimensionEffects.SkyType.field_59014
         .<RegistryByteBuf>cast()
         .dispatch(DimensionEffects.class_11082::method_69775, skyType -> skyType.field_59019);

      DimensionEffects.SkyType method_69775();

      Text method_69776();

      @Override
      default void appendTooltip(Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components) {
         textConsumer.accept(Text.translatable("sky.tooltip", this.method_69776()));
      }
   }
}
