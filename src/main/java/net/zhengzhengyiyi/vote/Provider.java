package net.zhengzhengyiyi.vote;

public interface Provider {
    java.util.stream.Stream<VoteValue> getValues();
}