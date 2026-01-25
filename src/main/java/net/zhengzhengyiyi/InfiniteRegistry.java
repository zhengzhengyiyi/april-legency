package net.zhengzhengyiyi;

import com.mojang.serialization.Lifecycle;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntFunction;

public class InfiniteRegistry<T> extends SimpleRegistry<T> {
    private final IntFunction<T> entryGenerator;

    public InfiniteRegistry(RegistryKey<? extends Registry<T>> registryKey, IntFunction<T> entryGenerator) {
        super(registryKey, Lifecycle.stable());
        this.entryGenerator = entryGenerator;
    }

    @Nullable
    @Override
    public T get(@Nullable Identifier id) {
        if (id == null) return null;
        
        T existingEntry = super.get(id);
        if (existingEntry != null) {
            return existingEntry;
        }

        if ("_generated".equals(id.getNamespace())) {
            try {
                int index = Integer.parseInt(id.getPath());
                if (index >= 0) {
                    T newEntry = this.entryGenerator.apply(index);
                    RegistryKey<T> entryKey = RegistryKey.of(this.getKey(), id);
                    this.add(entryKey, newEntry, RegistryEntryInfo.DEFAULT);
                    return newEntry;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }

    @Nullable
    @Override
    public T get(int index) {
        T existingEntry = super.get(index);
        if (existingEntry != null) {
            return existingEntry;
        }

        if (index >= 0) {
            T newEntry = this.entryGenerator.apply(index);
            Identifier generatedId = Identifier.of("_generated", Integer.toString(index));
            RegistryKey<T> entryKey = RegistryKey.of(this.getKey(), generatedId);
            this.add(entryKey, newEntry, RegistryEntryInfo.DEFAULT);
            return newEntry;
        }
        return null;
    }
}
