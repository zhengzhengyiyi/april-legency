package net.zhengzhengyiyi.rules;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

/**
 * Represents the text style applied by a vote rule in the April Fools 2023 "The Vote Update".
 * This enum governs colors, formatting, and special fonts like SGA or Illager.
 * <p>
 * Official Obfuscated Class: bet$a
 */
public enum TextStyle implements StringIdentifiable {
    HIDE("hide"),
    BLANK("blank"),
    BLACK("black"),
    DARK_BLUE("dark_blue"),
    DARK_GREEN("dark_green"),
    DARK_AQUA("dark_aqua"),
    DARK_RED("dark_red"),
    DARK_PURPLE("dark_purple"),
    GOLD("gold"),
    GRAY("gray"),
    DARK_GRAY("dark_gray"),
    BLUE("blue"),
    GREEN("green"),
    AQUA("aqua"),
    RED("red"),
    LIGHT_PURPLE("light_purple"),
    YELLOW("yellow"),
    WHITE("white"),
    OBFUSCATED("obfuscated"),
    BOLD("bold"),
    STRIKETHROUGH("strikethrough"),
    UNDERLINE("underline"),
    ITALIC("italic"),
    THIN("thin"),
    SGA("sga"),
    ILLAGER("illager");

    /**
     * Codec for serializing and deserializing the text style.
     */
    public static final Codec<TextStyle> CODEC = StringIdentifiable.createCodec(TextStyle::values);

    /**
     * The internal identifier for the style.
     */
    private final String id;

    /**
     * The translation key used for displaying the style's name in the UI.
     */
    private final String translationKey;

    /**
     * @param id The style name used for serialization and translation lookup.
     */
    TextStyle(String id) {
        this.id = id;
        this.translationKey = "rule.text_style." + id;
    }

    /**
     * @return The unique string identifier of this style.
     */
    @Override
    public String asString() {
        return this.id;
    }

    /**
     * @return The translation key for localized display.
     */
    public String getTranslationKey() {
        return this.translationKey;
    }
}
