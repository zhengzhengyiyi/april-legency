package net.zhengzhengyiyi.rules.options;

import com.mojang.serialization.Codec;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;

public enum WeatherState implements StringIdentifiable {
    DEFAULT("default"),
    NEVER("never"),
    ALWAYS("always");

    public static final Codec<WeatherState> CODEC = StringIdentifiable.createCodec(WeatherState::values);
    
    private final String id;
    private final Text rainText;
    private final Text thunderText;

    private WeatherState(String id) {
        this.id = id;
        this.rainText = Text.translatable("rule.weather.rain." + id);
        this.thunderText = Text.translatable("rule.weather.thunder." + id);
    }

    @Override
    public String asString() {
        return this.id;
    }

    public Text getRainDescription() {
        return this.rainText;
    }

    public Text getThunderDescription() {
        return this.thunderText;
    }
}
