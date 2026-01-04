package net.zhengzhengyiyi;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.handler.PacketBundleHandler;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.BundleSplitterPacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Util;
import net.zhengzhengyiyi.network.*;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class NetworkRegistries {
    private static final PacketBundleHandler NOOP_BUNDLER = new PacketBundleHandler() {
        @Override
        public void forEachPacket(Packet<?> packet, Consumer<Packet<?>> consumer) {
            consumer.accept(packet);
        }

        @Nullable
        @Override
        public PacketBundleHandler.Bundler createBundler(Packet<?> splitter) {
            return null;
        }
    };

    public static final NetworkRegistries PLAY = new NetworkRegistries(0, createPacketHandlerInitializer()
            .setup(NetworkSide.CLIENTBOUND, new PacketHandler<ClientPlayPacketListener>()
                    .register(VoteRuleSyncS2CPacket.class, VoteRuleSyncS2CPacket::new)
                    .register(class_8483.class, class_8483::new)
                    .register(class_8481.class, class_8481::new)
                    .register(class_8482.class, class_8482::new)
                    .register(class_8480.class, class_8480::new)
                    .register(VoteUpdateS2CPacket.class, VoteUpdateS2CPacket::new))
            .setup(NetworkSide.SERVERBOUND, new PacketHandler<ServerPlayPacketListener>()
                    .register(class_8258.class, class_8258::new)
                    .register(class_8484.class, class_8484::new)));

    public final int stateId;
    private final Map<NetworkSide, PacketHandler<?>> packetHandlers;

    NetworkRegistries(int id, PacketHandlerInitializer initializer) {
        this.stateId = id;
        this.packetHandlers = initializer.packetHandlers;
    }

    public static class PacketHandler<T extends PacketListener> {
        private static final Logger LOGGER = LogUtils.getLogger();
        final Object2IntMap<Class<? extends Packet<T>>> packetIds;
        private final List<Function<PacketByteBuf, ? extends Packet<T>>> packetFactories;
        private PacketBundleHandler bundler;
        private final Set<Class<? extends Packet<T>>> bundlePacketTypes;

        public PacketHandler() {
            this.packetIds = Util.make(new Object2IntOpenHashMap<>(), map -> map.defaultReturnValue(-1));
            this.packetFactories = Lists.newArrayList();
            this.bundler = NOOP_BUNDLER;
            this.bundlePacketTypes = new HashSet<>();
        }

        public <P extends Packet<T>> PacketHandler<T> register(Class<P> type, Function<PacketByteBuf, P> packetFactory) {
            int i = this.packetFactories.size();
            int j = this.packetIds.put(type, i);
            if (j != -1) {
                String string = "Packet " + type + " is already registered to ID " + j;
                LOGGER.error(LogUtils.FATAL_MARKER, string);
                throw new IllegalArgumentException(string);
            }
            this.packetFactories.add(packetFactory);
            return this;
        }

        public int getId(Class<?> packet) {
            return this.packetIds.getInt(packet);
        }

        @Nullable
        public Packet<?> createPacket(int id, PacketByteBuf buf) {
            if (id < 0 || id >= this.packetFactories.size()) return null;
            Function<PacketByteBuf, ? extends Packet<T>> function = this.packetFactories.get(id);
            return (function != null) ? function.apply(buf) : null;
        }

        public void forEachPacketType(Consumer<Class<? extends Packet<?>>> consumer) {
//            this.packetIds.keySet().stream().filter(type -> type != BundleSplitterPacket.class).forEach(consumer);
//            this.bundlePacketTypes.forEach(consumer);
        	this.packetIds.keySet().stream()
	            .filter(type -> !BundleSplitterPacket.class.equals(type))
	            .forEach(consumer);
	        this.bundlePacketTypes.forEach(consumer);
        }

        public PacketBundleHandler getBundler() {
            return this.bundler;
        }
    }

    private static PacketHandlerInitializer createPacketHandlerInitializer() {
        return new PacketHandlerInitializer();
    }

    private static class PacketHandlerInitializer {
        final Map<NetworkSide, PacketHandler<?>> packetHandlers = Maps.newEnumMap(NetworkSide.class);

        public <T extends PacketListener> PacketHandlerInitializer setup(NetworkSide side, PacketHandler<T> handler) {
            this.packetHandlers.put(side, handler);
            return this;
        }
    }

    @Nullable
    public Packet<?> getPacketHandler(NetworkSide side, int packetId, PacketByteBuf buf) {
        PacketHandler<?> handler = this.packetHandlers.get(side);
        return handler != null ? handler.createPacket(packetId, buf) : null;
    }
}
