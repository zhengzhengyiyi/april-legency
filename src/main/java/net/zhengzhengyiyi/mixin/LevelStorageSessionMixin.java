package net.zhengzhengyiyi.mixin;

import com.google.common.base.Charsets;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.level.storage.LevelStorage;
import net.zhengzhengyiyi.vote.VoteState;
import net.zhengzhengyiyi.world.VoteSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Mixin(LevelStorage.Session.class)
public abstract class LevelStorageSessionMixin implements VoteSession {
    @Shadow @Final private LevelStorage.LevelSave directory;
    @Unique private static Logger LOGGER = LoggerFactory.getLogger(LevelStorageSessionMixin.class);
    @Shadow public abstract Path getDirectory(WorldSavePath savePath);

    @Override
    public VoteState loadVotes() {
        Path path = this.getDirectory(WorldSavePathMixin.create("votes.json"));
        if (Files.exists(path)) {
            try (@SuppressWarnings("deprecation")
			BufferedReader bufferedReader = Files.newBufferedReader(path, Charsets.UTF_8)) {
                JsonReader jsonReader = new JsonReader(bufferedReader);
                JsonElement jsonElement = Streams.parse(jsonReader);
                if (!jsonElement.isJsonNull() && jsonReader.peek() != JsonToken.END_DOCUMENT) {
                    throw new JsonSyntaxException("Did not consume the entire document.");
                }
                return VoteState.CODEC.parse(new Dynamic<>(JsonOps.INSTANCE, jsonElement))
                        .resultOrPartial(Util.addPrefix("Rule decoding: ", LOGGER::error))
                        .orElseGet(VoteState::new);
            } catch (Exception exception) {
                LOGGER.warn("Failed to read votes from {}", path, exception);
            }
        }
        return new VoteState();
    }

    @SuppressWarnings("deprecation")
	@Override
    public void saveVotes(VoteState state) {
        Path path = this.getDirectory(WorldSavePathMixin.create("votes.json"));
        try {
            Path path2 = Files.createTempFile(this.directory.path(), "votes", ".json");
            try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path2, Charsets.UTF_8)) {
                JsonWriter jsonWriter = new JsonWriter(bufferedWriter);
                jsonWriter.setIndent("  ");
//                Streams.write(Util.getResult(VoteState.CODEC.encodeStart(JsonOps.INSTANCE, state), IOException::new), jsonWriter);
                DataResult<JsonElement> dataResult = VoteState.CODEC.encodeStart(JsonOps.INSTANCE, state);
                JsonElement jsonElement = dataResult.getOrThrow(IOException::new);
                Streams.write(jsonElement, jsonWriter);
            }
            Path path3 = this.getDirectory(WorldSavePathMixin.create("votes.json_old"));
            Util.backupAndReplace(path, path2, path3);
        } catch (Exception exception) {
            LOGGER.warn("Failed to write votes to {}", path, exception);
        }
    }
}
