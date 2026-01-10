package net.zhengzhengyiyi.rules.options;

import com.mojang.serialization.Codec;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.zhengzhengyiyi.world.Vote;
import net.zhengzhengyiyi.vote.VoteValue;
import net.zhengzhengyiyi.vote.VoterAction;

public abstract class SetVoteRule<T> implements Vote {
    private final Set<T> activeElements = new HashSet<>();

    protected abstract Codec<T> getElementCodec();

    protected abstract Text getElementDescription(T element);

    @SuppressWarnings("unchecked")
	@Override
    public Stream getActiveOptions() {
        return this.activeElements.stream().map(element -> new SetVoteRule.Option(element));
    }

    public boolean contains(T element) {
        return this.activeElements.contains(element);
    }

    protected boolean remove(T element) {
        return this.activeElements.remove(element);
    }

    protected boolean add(T element) {
        return this.activeElements.add(element);
    }

    public Collection<T> getActiveElements() {
        return Collections.unmodifiableCollection(this.activeElements);
    }

    protected void onUpdate(VoterAction action, MinecraftServer server) {
    }

    protected class Option implements VoteValue {
        final T element;

        public Option(T element) {
            this.element = element;
        }

        @Override
        public void apply(VoterAction action) {
            T target = this.element;
            if (action == VoterAction.APPROVE) {
                SetVoteRule.this.add(target);
            } else {
                SetVoteRule.this.remove(target);
            }
        }

//        @Override
//        public void applyWithServer(VoterAction action, MinecraftServer server) {
//            VoteValue.super.applyWithServer(action, server);
//            SetVoteRule.this.onUpdate(action, server);
//        }

		@Override
		public Vote getType() {
			return SetVoteRule.this;
		}

		@Override
		public Text getDescription(VoterAction action) {
			return Text.of("needs a short description");
		}
    }
}
