package net.zhengzhengyiyi.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

/**
 * class_11132 - Client-side unlock tree manager.
 */
@Environment(EnvType.CLIENT)
public class UnlockTreeManager {
    @Nullable private Listener listener;

    public void setListener(@Nullable Listener listener) {
        this.listener = listener;
    }

    public void clear() {
        if (this.listener != null) this.listener.onCleared();
    }

    @Environment(EnvType.CLIENT)
    public interface Listener {
        void onRootAdded(String key);
        void onRootRemoved(String key);
        void onChildAdded(String key);
        void onChildRemoved(String key);
        void onCleared();
    }
}
