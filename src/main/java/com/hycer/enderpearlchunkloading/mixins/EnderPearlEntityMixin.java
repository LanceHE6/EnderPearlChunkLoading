package com.hycer.enderpearlchunkloading.mixins;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.world.World;

import java.util.Objects;


@Mixin(EnderPearlEntity.class)
public abstract class EnderPearlEntityMixin extends ThrownEntity {
    @Unique
    private ChunkPos prevChunkPos = null;
    @Unique
    private int loadCount = 0;

    private EnderPearlEntityMixin(EntityType<? extends ThrownEntity> type, World world)
    {
        super(type, world);
    }

    @Inject(method = "tick()V", at = @At(value = "HEAD"))
    private void chunkLoadNextChunk(CallbackInfo ci)
    {
        if (((Object) this) instanceof EnderPearlEntity)
        {
                PlayerEntity owner = (PlayerEntity) this.getOwner();
                World world = this.getEntityWorld();
                Vec3d velocity = this.getVelocity();

                // 非珍珠炮射出的珍珠不会触发强加载
                if (world instanceof ServerWorld &&
                        (Math.abs(velocity.x) > 20 || Math.abs(velocity.z) > 20)) {
                    if (loadCount > 10){
//                        System.out.println("超过20gt未被拦截，移除珍珠实体");
                        owner.sendMessage(Text.of("[EnderPearlChunkLoading]珍珠超过10gt未被拦截，已移除珍珠实体"));
                        this.kill();
                        return;
                    }
                    Vec3d pos = this.getPos();
                    double nx = pos.x + velocity.x;
                    double nz = pos.z + velocity.z;

                    ChunkPos cp = new ChunkPos(MathHelper.floor(nx) >> 4, MathHelper.floor(nz) >> 4);

                    ServerChunkManager chunkManager = ((ServerWorld) world).getChunkManager();
                    // 取消之前强加载的区块
                    if (prevChunkPos != null && !Objects.equals(prevChunkPos, cp)) {
                        chunkManager.setChunkForced(prevChunkPos, false);
                    }
                    // 设置新的强加载区块
                    chunkManager.setChunkForced(cp, true);
                    owner.sendMessage(Text.of("[EPCL]已加载区块%d: ".formatted(loadCount + 1) + String.format("%.2f", nx) + " " + String.format("%.2f", nz)));
                    System.out.println("Set chunk forced for " + nx + " " + nz);
                    loadCount ++;
                    prevChunkPos = cp;
                    chunkManager.setChunkForced(cp, true);
//                    System.out.println("after,isChunkLoaded: " + ((ServerWorld) world).getChunkManager().isChunkLoaded(cp.x, cp.z));
                }

        }

    }
}