package net.zhengzhengyiyi.world;

import net.zhengzhengyiyi.vote.VoteState;

public interface VoteSession {
    VoteState loadVotes();
    void saveVotes(VoteState state);
}
