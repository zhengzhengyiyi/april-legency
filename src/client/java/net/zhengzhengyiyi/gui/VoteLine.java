package net.zhengzhengyiyi.gui;

import com.google.common.collect.Streams;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public record VoteLine(OrderedText contents, long index, Text original) {
    public static Stream<VoteLine> wrap(TextRenderer textRenderer, Stream<Text> textStream, int width) {
        return textStream.flatMap(text -> 
            Streams.mapWithIndex(
                textRenderer.wrapLines(text, width).stream(), 
                (orderedText, index) -> new VoteLine(orderedText, index, text)
            )
        );
    }
}
