package com.hycer.enderpearlchunkloading.mixins;

import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.world.World;


@Mixin(ThrownEntity.class)
public abstract class ThrownEntityMixin extends Entity {
    @Unique
    private static ChunkPos prevChunkPos = null;
    private ThrownEntityMixin(EntityType<?> type, World world)
    {
        super(type, world);
    }

    @Inject(method = "tick()V", at = @At(value = "HEAD"))
    private void chunkLoadNextChunk(CallbackInfo ci)
    {
        if (((Object) this) instanceof EnderPearlEntity)
        {
                World world = this.getEntityWorld();
                Vec3d velocity = this.getVelocity();

                // 非珍珠炮射出的珍珠不会触发强加载
                if (world instanceof ServerWorld &&
                        (Math.abs(velocity.x) > 20 || Math.abs(velocity.z) > 20)) {
                    Vec3d pos = this.getPos();
                    double nx = pos.x + velocity.x;
                    double nz = pos.z + velocity.z;
                    System.out.println("Adding ticket for " + nx + " " + nz);

                    ChunkPos cp = new ChunkPos(MathHelper.floor(nx) >> 4, MathHelper.floor(nz) >> 4);

//                    System.out.println("Adding chunk ticket for " + cp.x + " " + cp.z);
//                    System.out.println("before,isChunkLoaded: " + ((ServerWorld) world).getChunkManager().isChunkLoaded(cp.x, cp.z));

                    ServerChunkManager chunkManager = ((ServerWorld) world).getChunkManager();
                    // 取消之前强加载的区块
                    if (prevChunkPos != null && prevChunkPos != cp) {
                        chunkManager.setChunkForced(prevChunkPos, false);
                    }
                    // 设置新的强加载区块
                    chunkManager.setChunkForced(cp, true);
                    prevChunkPos = cp;
                    chunkManager.setChunkForced(cp, true);
//                    System.out.println("after,isChunkLoaded: " + ((ServerWorld) world).getChunkManager().isChunkLoaded(cp.x, cp.z));
                }

        }

    }
}