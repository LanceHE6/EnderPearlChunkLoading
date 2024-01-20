package com.hycer.enderpearlchunkloading;

import com.hycer.enderpearlchunkloading.config.ModConfiguration;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;


import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;


public class EnderPearlChunkLoading implements ModInitializer {
    private ModConfiguration config;
    @Override
    public void onInitialize() {
        config = new ModConfiguration();
        registerCommands();
    }
    public void registerCommands(){
        // mod启用状态
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(literal("enderPearlChunkLoading")
                        .requires(sources -> sources.hasPermissionLevel(2))
                        .then(literal("status")
                                .then(literal("set")
                                        .then(literal("true")
                                                .executes(context -> executeSetStatus(context, true)))
                                        .then(literal("false")
                                                .executes(context -> executeSetStatus(context, false)))
                                )
                                .executes(this::executeStatus)
                        )));
        // 珍珠强加载时间阈值
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(literal("enderPearlChunkLoading")
                        .requires(sources -> sources.hasPermissionLevel(2))
                        .then(literal("loadTimeThreshold")
                                .then(literal("set")
                                        .then(argument("gt", IntegerArgumentType.integer(5, 40))
                                                .executes(context -> executeSetLoadTimeThreshold(context, IntegerArgumentType.getInteger(context, "gt")))))
                                .executes(this::executeLoadTimeThreshold)
                        )));

    }

    private int executeSetStatus(CommandContext<ServerCommandSource> context, boolean status) {
        PlayerEntity player = context.getSource().getPlayer();
        config.setStatus(status);
        if (player != null) {
            player.sendMessage(Text.of("[EPCL]已%s珍珠强加载".formatted(status ? "§3启用§f" : "§4禁用§f")));
        }
        return 1;
    }
    private int executeStatus(CommandContext<ServerCommandSource> context){
        PlayerEntity player = context.getSource().getPlayer();
        if (player != null) {
            player.sendMessage(Text.of("[EPCL]珍珠强加载已%s".formatted(config.getStatus() ? "§3启用" : "§4禁用")));
        }
        return 1;
    }

    private int executeSetLoadTimeThreshold(CommandContext<ServerCommandSource> context, int threshold) {
        PlayerEntity player = context.getSource().getPlayer();
        config.setLoadTimeThreshold(threshold);
        if (player != null) {
            player.sendMessage(Text.of("[EPCL]已设置珍珠强加载时间阈值为 %d gt".formatted(threshold)));
        }
        return 1;
    }

    private int executeLoadTimeThreshold(CommandContext<ServerCommandSource> context) {
        PlayerEntity player = context.getSource().getPlayer();
        if (player != null) {
            player.sendMessage(Text.of("[EPCL]当前珍珠强加载时间阈值为 %d gt".formatted(config.getLoadTimeThreshold())));
        }
        return 1;
    }
}
