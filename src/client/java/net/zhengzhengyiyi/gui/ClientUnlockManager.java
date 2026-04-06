package net.zhengzhengyiyi.gui;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.zhengzhengyiyi.mine.effect.class_10976;
import org.jetbrains.annotations.Nullable;

/**
 * class_11139 - Client-side unlock state manager.
 * Tracks which unlocks are obtained, visible, and active-exclusive.
 */
@Environment(EnvType.CLIENT)
public class ClientUnlockManager {
    private final Object2BooleanMap<RegistryEntry<class_10976>> obtained = new Object2BooleanOpenHashMap<>();
    private final Map<RegistryEntry<class_10976>, class_10976.class_10978> visibility = new HashMap<>();
    private final Object2BooleanOpenHashMap<RegistryEntry<class_10976>> isActiveExclusive = new Object2BooleanOpenHashMap<>();
    private final UnlockTreeManager tree = new UnlockTreeManager();
    @Nullable
    private ScreenListener screenListener;
    @Nullable
    private RegistryEntry<class_10976> selectedTab;

    public ClientUnlockManager() {
        Set<RegistryEntry<class_10976>> visited = new HashSet<>();
        Registries.field_59579.streamEntries().forEach(ref -> initEntry(ref, visited));
    }

    private void initEntry(RegistryEntry<class_10976> entry, Set<RegistryEntry<class_10976>> visited) {
        if (!visited.contains(entry)) {
            entry.value().parent().ifPresent(p -> initEntry(p, visited));
            this.tree.addUnlocks(List.of(entry));
            visited.add(entry);
        }
    }

    public void applyUpdate(boolean reset,
                            Map<RegistryEntry<class_10976>, Boolean> obtainedMap,
                            Map<RegistryEntry<class_10976>, class_10976.class_10978> visibilityMap,
                            Map<RegistryEntry<class_10976>, Boolean> activeExclusiveMap) {
        if (reset) {
            this.obtained.clear();
            this.visibility.clear();
            this.isActiveExclusive.clear();
        }
        this.obtained.putAll(obtainedMap);
        this.visibility.putAll(visibilityMap);
        this.isActiveExclusive.putAll(activeExclusiveMap);

        if (this.screenListener != null) {
            obtainedMap.forEach(this.screenListener::onObtainedChanged);
            visibilityMap.forEach(this.screenListener::onVisibilityChanged);
            activeExclusiveMap.forEach(this.screenListener::onActiveExclusiveChanged);
        }
    }

    public void setSelectedTab(@Nullable RegistryEntry<class_10976> tab) {
        if (this.selectedTab != tab) {
            this.selectedTab = tab;
            if (this.screenListener != null) this.screenListener.onTabSelected(tab);
        }
    }

    public void setScreenListener(@Nullable ScreenListener listener) {
        this.screenListener = listener;
        this.tree.setListener(listener);
        if (listener != null) {
            this.obtained.forEach(listener::onObtainedChanged);
            this.visibility.forEach(listener::onVisibilityChanged);
            this.isActiveExclusive.forEach(listener::onActiveExclusiveChanged);
            listener.onTabSelected(this.selectedTab);
        }
    }

    public UnlockTreeManager getTree() { return this.tree; }

    public Set<RegistryEntry<class_10976>> getActiveUnlocks() {
        Set<RegistryEntry<class_10976>> active = new HashSet<>();
        this.obtained.forEach((entry, bl) -> { if (bl) active.add(entry); });
        this.obtained.forEach((entry, bl) -> {
            if (bl) {
                entry.value().disables().forEach(active::remove);
                if (!entry.value().exclusiveKey().isEmpty() && !this.isActiveExclusive.getOrDefault(entry, false)) {
                    active.remove(entry);
                }
            }
        });
        return active;
    }

    public boolean isObtained(RegistryEntry<class_10976> entry) { return this.obtained.getOrDefault(entry, false); }
    public boolean isActive(RegistryEntry<class_10976> entry) { return this.getActiveUnlocks().contains(entry); }
    public class_10976.class_10978 getVisibility(RegistryEntry<class_10976> entry) {
        return this.visibility.getOrDefault(entry, class_10976.class_10978.INVISIBLE);
    }
    public boolean isActiveExclusive(RegistryEntry<class_10976> entry) { return this.isActiveExclusive.getOrDefault(entry, false); }

    public boolean isVisible(RegistryEntry<class_10976> entry) {
        if (getVisibility(entry) == class_10976.class_10978.INVISIBLE) return false;
        Optional<RegistryEntry<class_10976>> parent = entry.value().parent();
        return parent.isEmpty() || isVisible(parent.get());
    }

    public boolean isFullyVisible(RegistryEntry<class_10976> entry) {
        return getVisibility(entry) == class_10976.class_10978.VISIBLE;
    }

    @Environment(EnvType.CLIENT)
    public interface ScreenListener extends UnlockTreeManager.Listener {
        void onObtainedChanged(RegistryEntry<class_10976> entry, boolean obtained);
        void onVisibilityChanged(RegistryEntry<class_10976> entry, class_10976.class_10978 visibility);
        void onActiveExclusiveChanged(RegistryEntry<class_10976> entry, boolean active);
        void onTabSelected(@Nullable RegistryEntry<class_10976> tab);
    }
}
