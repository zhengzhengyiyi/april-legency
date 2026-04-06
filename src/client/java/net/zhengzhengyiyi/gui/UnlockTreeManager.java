package net.zhengzhengyiyi.gui;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.registry.entry.RegistryEntry;
import net.zhengzhengyiyi.mine.effect.class_10976;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

/**
 * class_11132 - Client-side unlock tree manager.
 * Tracks parent/child relationships between unlocks for the UI tree.
 */
@Environment(EnvType.CLIENT)
public class UnlockTreeManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Map<RegistryEntry<class_10976>, List<RegistryEntry<class_10976>>> children = new Object2ObjectOpenHashMap<>();
    private final Set<RegistryEntry<class_10976>> roots = new ObjectLinkedOpenHashSet<>();
    private final Set<RegistryEntry<class_10976>> nonRoots = new ObjectLinkedOpenHashSet<>();
    @Nullable
    private Listener listener;

    public void addUnlocks(Collection<RegistryEntry<class_10976>> unlocks) {
        List<RegistryEntry<class_10976>> pending = new ArrayList<>(unlocks);
        while (!pending.isEmpty()) {
            if (!pending.removeIf(this::tryAdd)) {
                LOGGER.error("Couldn't load unlocks: {}", pending);
                break;
            }
        }
    }

    private boolean tryAdd(RegistryEntry<class_10976> entry) {
        Optional<RegistryEntry<class_10976>> parent = entry.value().parent();
        List<RegistryEntry<class_10976>> parentChildren = parent.map(this.children::get).orElse(null);
        if (parentChildren == null && parent.isPresent()) return false;

        if (parentChildren != null) parentChildren.add(entry);
        this.children.put(entry, new ArrayList<>());

        if (parent.isEmpty()) {
            this.roots.add(entry);
            if (this.listener != null) this.listener.onRootAdded(entry);
        } else {
            this.nonRoots.add(entry);
            if (this.listener != null) this.listener.onChildAdded(entry);
        }
        return true;
    }

    public void removeUnlocks(Set<RegistryEntry<class_10976>> toRemove) {
        for (RegistryEntry<class_10976> entry : toRemove) {
            if (!this.children.containsKey(entry)) {
                LOGGER.warn("Told to remove unlock {} but I don't know what that is", entry);
            } else {
                removeRecursive(entry);
            }
        }
    }

    private void removeRecursive(RegistryEntry<class_10976> entry) {
        for (RegistryEntry<class_10976> child : this.children.get(entry)) removeRecursive(child);
        this.children.remove(entry);
        if (entry.value().parent().isEmpty()) {
            this.roots.remove(entry);
            if (this.listener != null) this.listener.onRootRemoved(entry);
        } else {
            this.nonRoots.remove(entry);
            if (this.listener != null) this.listener.onChildRemoved(entry);
        }
    }

    public void clear() {
        this.children.clear();
        this.roots.clear();
        this.nonRoots.clear();
        if (this.listener != null) this.listener.onCleared();
    }

    public Iterable<RegistryEntry<class_10976>> getRoots() { return this.roots; }

    public List<RegistryEntry<class_10976>> getChildren(RegistryEntry<class_10976> entry) {
        List<RegistryEntry<class_10976>> list = this.children.get(entry);
        return list == null ? List.of() : list;
    }

    public void setListener(@Nullable Listener listener) {
        this.listener = listener;
        if (listener != null) {
            for (RegistryEntry<class_10976> root : this.roots) listener.onRootAdded(root);
            for (RegistryEntry<class_10976> child : this.nonRoots) listener.onChildAdded(child);
        }
    }

    @Environment(EnvType.CLIENT)
    public interface Listener {
        void onRootAdded(RegistryEntry<class_10976> entry);
        void onRootRemoved(RegistryEntry<class_10976> entry);
        void onChildAdded(RegistryEntry<class_10976> entry);
        void onChildRemoved(RegistryEntry<class_10976> entry);
        void onCleared();
    }
}
