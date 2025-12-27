package net.zhengzhengyiyi.rules.options;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.UUID;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Uuids;
import net.minecraft.text.TextCodecs;

public record EntityReference(UUID id, String name, Text displayName) {
    public static final Codec<EntityReference> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
                Uuids.INT_STREAM_CODEC.fieldOf("uuid").forGetter(EntityReference::id),
                Codec.STRING.fieldOf("name").forGetter(EntityReference::name),
                TextCodecs.CODEC.fieldOf("display_name").forGetter(EntityReference::displayName)
            )
            .apply(instance, EntityReference::new)
    );

    public static EntityReference fromPlayer(ServerPlayerEntity player) {
        GameProfile gameProfile = player.getGameProfile();
        return new EntityReference(gameProfile.id(), gameProfile.name(), player.getDisplayName());
    }

    public GameProfile toGameProfile() {
        return new GameProfile(this.id, this.name);
    }
}
