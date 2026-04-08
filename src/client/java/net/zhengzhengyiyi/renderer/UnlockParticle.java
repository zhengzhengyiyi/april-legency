package net.zhengzhengyiyi.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.random.Random;

/**
 * class_11141 - Unlock particle (enchantment-style blue/green sparkle).
 */
@Environment(EnvType.CLIENT)
public class UnlockParticle extends BillboardParticle {
    private final double originX, originY, originZ;
    private final SpriteProvider spriteProvider;

    protected UnlockParticle(ClientWorld world, double x, double y, double z,
                              double vx, double vy, double vz, SpriteProvider sprites) {
        super(world, x, y, z, vx, vy, vz, sprites.getSprite(world.random));
        this.spriteProvider = sprites;
        this.originX = x; this.originY = y; this.originZ = z;
        this.scale = 0.1F * (this.random.nextFloat() * 0.4F + 0.2F);
        float j = this.random.nextFloat() * 0.6F + 0.4F;
        if (this.random.nextBoolean()) {
            this.red = j * 0.2F; this.green = j * 0.3F; this.blue = j;
        } else {
            this.red = j * 0.2F; this.green = j; this.blue = j * 0.2F;
        }
        this.maxAge = (int)(Math.random() * 20.0) + 50;
    }

    @Override
    protected RenderType getRenderType() { return RenderType.PARTICLE_ATLAS_OPAQUE; }

    @Override
    public void move(double dx, double dy, double dz) {
        this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dz));
        this.repositionFromBoundingBox();
    }

    @Override
    public float getSize(float tickProgress) {
        float f = (this.age + tickProgress) / this.maxAge;
        f = 1.0F - f; f *= f; f = 1.0F - f;
        return this.scale * f;
    }

    @Override
    protected int getBrightness(float tint) {
        int i = super.getBrightness(tint);
        float f = (float) this.age / this.maxAge;
        f *= f; f *= f;
        int j = i & 85;
        int k = i >> 16 & 0xFF;
        k += (int)(f * 15.0F * 16.0F);
        if (k > 240) k = 240;
        return j | k << 16;
    }

    @Override
    public void tick() {
        this.lastX = this.x; this.lastY = this.y; this.lastZ = this.z;
        if (this.age++ >= this.maxAge) { this.markDead(); return; }
        this.updateSprite(this.spriteProvider);
        float f = (float) this.age / this.maxAge;
        float var3 = -f + f * f * 2.0F;
        float var4 = 1.0F - var3;
        this.x = this.originX + this.velocityX * var4;
        this.y = this.originY + this.velocityY * var4 + (1.0F - f);
        this.z = this.originZ + this.velocityZ * var4;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider sprites;
        public Factory(SpriteProvider sprites) { this.sprites = sprites; }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientWorld world,
                                       double x, double y, double z,
                                       double vx, double vy, double vz, Random random) {
            return new UnlockParticle(world, x, y, z, vx, vy, vz, this.sprites);
        }
    }
}
