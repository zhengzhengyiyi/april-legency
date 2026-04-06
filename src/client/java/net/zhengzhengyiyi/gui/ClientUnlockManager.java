package net.zhengzhengyiyi.gui;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.registry.entry.RegistryEntry;
import net.zhengzhengyiyi.mine.effect.class_10976;
import org.jetbrains.annotations.Nullable;

/**
 * class_11139 - Client-side unlock state manager.
 */
@Environment(EnvType.CLIENT)
public class ClientUnlockManager {
    /** Visibility states for unlock entries */
    public enum Visibility { INVISIBLE, VISIBLE, HIDDEN }

    private final Object2BooleanMap<String> obtained = new Object2BooleanOpenHashMap<>();
    private final Map<String, Visibility> visibility = new HashMap<>();
    private final Object2BooleanOpenHashMap<String> isActiveExclusive = new Object2BooleanOpenHashMap<>();
    private final UnlockTreeManager tree = new UnlockTreeManager();
    @Nullable private ScreenListener screenListener;
    @Nullable private String selectedTab;

    public ClientUnlockManager() {}

    public void applyUpdate(boolean reset,
                            Map<String, Boolean> obtainedMap,
                            Map<String, Visibility> visibilityMap,
                            Map<String, Boolean> activeExclusiveMap) {
        if (reset) {
            this.obtained.clear();
            this.visibility.clear();
            this.isActiveExclusive.clear();
        }
        this.obtained.putAll(obtainedMap);
        this.visibility.putAll(visibilityMap);
        this.isActiveExclusive.putAll(activeExclusiveMap);
    }

    public void setSelectedTab(@Nullable String tab) {
        this.selectedTab = tab;
    }

    public void setScreenListener(@Nullable ScreenListener listener) {
        this.screenListener = listener;
        this.tree.setListener(listener);
    }

    public UnlockTreeManager getTree() { return this.tree; }

    public Set<String> getActiveUnlocks() {
        Set<String> active = new HashSet<>();
        this.obtained.forEach((key, bl) -> { if (bl) active.add(key); });
        return active;
    }

    public boolean isObtained(String key) { return this.obtained.getOrDefault(key, false); }
    public boolean isActive(String key) { return this.getActiveUnlocks().contains(key); }
    public Visibility getVisibility(String key) {
        return this.visibility.getOrDefault(key, Visibility.INVISIBLE);
    }

    @Environment(EnvType.CLIENT)
    public interface ScreenListener extends UnlockTreeManager.Listener {
        void onTabSelected(@Nullable String tab);
    }
}
