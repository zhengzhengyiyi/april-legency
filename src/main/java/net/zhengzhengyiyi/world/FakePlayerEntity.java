package net.zhengzhengyiyi.world;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.minecraft.world.rule.GameRules;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FakePlayerEntity extends PathAwareEntity {
    private static final String PLAYER_NAME = "Ray Tracing";
    
    private static final List<Text> DEATH_EXCUSES = Stream.of(
            "That was just a warm-up, next time I'll be ready!",
            "I got caught off-guard. I won't make that mistake again",
            "It's not my fault, the lag made me miss my jump!",
            "I was distracted by that beautiful sunset",
            "I was practicing my speedrun strats and got a bit carried away"
        ).map(s -> Text.translatable("chat.type.text", PLAYER_NAME, s)).collect(Collectors.toList());

    private static final List<Text> IDLE_MESSAGES = Stream.of(
            "I just found diamonds! Wait, no, it's just coal. Again.",
            "I'm a master builder. I built a dirt house once",
            "Creepers? Never heard of 'em",
            "If at first you don't succeed, dig straight down"
        ).map(s -> Text.translatable("chat.type.text", PLAYER_NAME, s)).collect(Collectors.toList());

    private static final List<Text> JOIN_MESSAGES = Stream.of(
            "Greetings, fellow Minecrafters! Let's build some amazing things together",
            "Did someone say cake? I'm here for the cake!"
        ).map(s -> Text.translatable("chat.type.text", PLAYER_NAME, s)).collect(Collectors.toList());

    private static final List<Text> LEAVE_MESSAGES = Stream.of(
            "I have to go take care of my real-life sheep. See you all later!",
            "My mom is calling me for dinner. Gotta run!"
        ).map(s -> Text.translatable("chat.type.text", PLAYER_NAME, s)).collect(Collectors.toList());

//    private static final Text FRENCH_MESSAGE = Text.translatable("chat.type.text", PLAYER_NAME, "Omelette du fromage");
    
    public boolean isJoining = true;
    private long nextChatTime;

    public FakePlayerEntity(EntityType<? extends FakePlayerEntity> type, World world) {
        super(type, world);
        this.nextChatTime = world.getTime() + world.random.nextBetweenExclusive(80, 600);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new FleeEntityGoal<>(this, ZombieEntity.class, 8.0F, 1.0, 1.2));
        this.goalSelector.add(1, new FleeEntityGoal<>(this, HostileEntity.class, 12.0F, 1.0, 1.2));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.2));
        this.goalSelector.add(2, new TemptGoal(this, 1.1, Ingredient.ofItems(Items.DIAMOND), false));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(4, new LookAroundGoal(this));
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 20.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.23F);
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_PLAYER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PLAYER_DEATH;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getEntityWorld().isClient() && this.nextChatTime <= this.getEntityWorld().getTime()) {
            this.nextChatTime = this.getEntityWorld().getTime() + this.getEntityWorld().random.nextBetweenExclusive(600, 3600);
            
            if (this.isJoining) {
                this.broadcastRandomMessage(JOIN_MESSAGES);
                this.isJoining = false;
            } else if (this.getEntityWorld().random.nextFloat() < 0.1F) {
                this.broadcastRandomMessage(LEAVE_MESSAGES);
                this.discard();
                if (this.getEntityWorld() instanceof ServerWorld serverWorld) {
                    serverWorld.getServer().getPlayerManager().broadcast(
                        Text.translatable("multiplayer.player.left", PLAYER_NAME).formatted(Formatting.YELLOW), false
                    );
                }
            } else {
                this.broadcastRandomMessage(IDLE_MESSAGES);
            }
        }
    }

    @Override
    protected void updatePostDeath() {
        super.updatePostDeath();
        if (this.deathTime == 20 && !this.getEntityWorld().isClient()) {
            this.broadcastRandomMessage(DEATH_EXCUSES);
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        if (!this.getEntityWorld().isClient() && this.getEntityWorld() instanceof ServerWorld serverWorld) {
            PlayerManager pm = serverWorld.getServer().getPlayerManager();
            if (GameRules.SHOW_DEATH_MESSAGES.getDefaultValue()) {
                pm.broadcast(this.getDamageTracker().getDeathMessage(), false);
            }
        }
        super.onDeath(damageSource);
    }

    private void broadcastRandomMessage(List<Text> messages) {
        if (!this.getEntityWorld().isClient() && this.getEntityWorld() instanceof ServerWorld serverWorld) {
            Text message = messages.get(this.random.nextInt(messages.size()));
            serverWorld.getServer().getPlayerManager().broadcast(message, false);
        }
    }
}