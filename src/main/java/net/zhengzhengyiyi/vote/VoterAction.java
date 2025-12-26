package net.zhengzhengyiyi.vote;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

/**
 * Defines the type of action to perform with a vote effect.
 * <p>
 * Official Name: bed
 * Intermediary Name: net.minecraft.class_8369
 */
public enum VoterAction implements StringIdentifiable {
    APPROVE("approve"),
    REPEAL("repeal"), REVOKE("revoke"), APPLY("apply");

    /**
     * Codec for serializing and deserializing vote actions.
     */
    public static final Codec<VoterAction> CODEC = StringIdentifiable.createCodec(VoterAction::values);
    
    private final String id;

    VoterAction(String id) {
        this.id = id;
    }

    @Override
    public String asString() {
        return this.id;
    }

    /**
     * Gets the translation key prefix or simple ID for this action.
     */
    public String getId() {
        return this.id;
    }
}
