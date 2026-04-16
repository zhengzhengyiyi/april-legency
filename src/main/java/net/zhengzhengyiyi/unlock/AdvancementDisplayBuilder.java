package net.zhengzhengyiyi.unlock;

import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.AssetInfo;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 * Builder for AdvancementDisplay since the vanilla builder doesn't exist in this version.
 * Mirrors Craftmine class_10974.
 */
public class AdvancementDisplayBuilder {
    @Nullable
    private ItemStack icon;
    @Nullable
    private Text title;
    @Nullable
    private Text description;
    private Optional<AssetInfo.TextureAssetInfo> background = Optional.empty();
    @Nullable
    private AdvancementFrame frame;
    private boolean showToast = false;
    private boolean announceToChat = false;
    private boolean hidden = false;

    public AdvancementDisplayBuilder icon(ItemStack icon) {
        this.icon = icon;
        return this;
    }

    public AdvancementDisplayBuilder title(Text title) {
        this.title = title;
        return this;
    }

    public AdvancementDisplayBuilder description(Text description) {
        this.description = description;
        return this;
    }

    public AdvancementDisplayBuilder hint(Text hint) {
        // Hint is not used in the current version's constructor
        // It's part of the description in Craftmine but not in the current API
        return this;
    }

    public AdvancementDisplayBuilder background(Identifier background) {
        this.background = Optional.of(new AssetInfo.TextureAssetInfo(background));
        return this;
    }

    public AdvancementDisplayBuilder frame(AdvancementFrame frame) {
        this.frame = frame;
        return this;
    }

    public AdvancementDisplayBuilder showToast(boolean showToast) {
        this.showToast = showToast;
        return this;
    }

    public AdvancementDisplayBuilder announceToChat(boolean announceToChat) {
        this.announceToChat = announceToChat;
        return this;
    }

    public AdvancementDisplayBuilder hidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public AdvancementDisplay build() {
        return new AdvancementDisplay(
            Objects.requireNonNull(this.icon, "icon"),
            Objects.requireNonNull(this.title, "title"),
            Objects.requireNonNull(this.description, "description"),
            this.background,
            Objects.requireNonNull(this.frame, "frame"),
            this.showToast,
            this.announceToChat,
            this.hidden
        );
    }
}
