package net.zhengzhengyiyi.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {
	@Shadow
	protected float getShadowOpacity(S state) {
		return 0.0F;
	}
	
	@Shadow
	private void addShadowPiece(S renderState, World world, float shadowOpacity, Mutable pos, Chunk chunk) {}
	
	@Shadow
	protected float getShadowRadius(S state) {
		return 0.0F;
	}
	
	@Overwrite
	private void updateShadow(S renderState, MinecraftClient client, World world) {
	    renderState.shadowPieces.clear();
	    float radius = this.getShadowRadius(renderState);
	    
	    if (!client.options.getEntityShadows().getValue() || renderState.invisible || radius <= 0.01F || renderState.squaredDistanceToCamera > 576.0) {
	        renderState.shadowRadius = 0.0F;
	        return;
	    }

	    radius = Math.min(radius, 8.0F); 
	    renderState.shadowRadius = radius;

	    double d = renderState.squaredDistanceToCamera;
	    float opacity = (float)((1.0 - d / 576.0) * this.getShadowOpacity(renderState));
	    if (opacity <= 0.0F) return;

	    int step = (d > 256.0) ? 2 : 1; 

	    int minX = MathHelper.floor(renderState.x - radius);
	    int maxX = MathHelper.floor(renderState.x + radius);
	    int minZ = MathHelper.floor(renderState.z - radius);
	    int maxZ = MathHelper.floor(renderState.z + radius);
	    
	    BlockPos.Mutable mutable = new BlockPos.Mutable();
	    
	    for (int z = minZ; z <= maxZ; z += step) {
	        for (int x = minX; x <= maxX; x += step) {
	            mutable.set(x, 0, z);
	            Chunk chunk = world.getChunk(mutable); 
	            int topY = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG).get(x & 15, z & 15);
	            
	            if (topY <= renderState.y) {
	                mutable.setY(topY - 1);
	                addShadowPiece(renderState, world, opacity, mutable, chunk);
	            }
	        }
	    }
	}
}
