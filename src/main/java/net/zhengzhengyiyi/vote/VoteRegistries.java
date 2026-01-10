package net.zhengzhengyiyi.vote;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.zhengzhengyiyi.world.Vote;

public interface VoteRegistries {
	public static final RegistryKey<Registry<Vote>> VOTE_RULE_TYPE_KEY = 
		    RegistryKey.ofRegistry(Identifier.of("minecraft", "vote_rule_type"));

	public static final Registry<Vote> VOTE_RULE_TYPE = FabricRegistryBuilder
	    .createSimple(VOTE_RULE_TYPE_KEY)
	    .buildAndRegister();
	
	public static final RegistryKey<Boolean> DEFAULT_BOOLEAN_VALUE_KEY = 
            RegistryKey.of(RegistryKey.ofRegistry(Identifier.of("minecraft", "boolean_state")), Identifier.of("minecraft", "true"));
	
	public static void init() {
	}
}
