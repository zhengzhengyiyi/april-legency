package net.zhengzhengyiyi.rules.options;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;

public abstract class EntityVoteRule extends SetVoteRule<EntityReference> {
    private final List<UUID> affectedIdList = new ArrayList<>();
    private final Set<UUID> affectedIdSet = new HashSet<>();

    @Override
    protected boolean add(EntityReference ref) {
        boolean added = super.add(ref);
        if (added) {
            this.affectedIdSet.add(ref.id());
            this.affectedIdList.add(ref.id());
        }
        return added;
    }

    @Override
    protected Codec getElementCodec() {
        return EntityReference.CODEC;
    }

    @Override
    protected boolean remove(EntityReference ref) {
        boolean removed = super.remove(ref);
        if (removed) {
            this.affectedIdSet.remove(ref.id());
            this.affectedIdList.remove(ref.id());
        }
        return removed;
    }

    public boolean isEntityAffected(UUID uuid) {
        return this.affectedIdSet.contains(uuid);
    }

    public List<UUID> getAffectedIds() {
        return this.affectedIdList;
    }

    @SuppressWarnings("rawtypes")
	@Override
    public Stream generateOptions(MinecraftServer server, Random random, int limit) {
        ObjectArrayList<ServerPlayerEntity> players = new ObjectArrayList<>(server.getPlayerManager().getPlayerList());
        Util.shuffle(players, random);
        
        return players.stream()
            .limit(limit)
            .map(player -> new SetVoteRule.Option(EntityReference.fromPlayer(player)));
    }
}