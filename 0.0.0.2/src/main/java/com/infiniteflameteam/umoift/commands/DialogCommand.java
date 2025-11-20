package com.infiniteflameteam.umoift.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

public class DialogCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(DialogCommand.class);

    // 提供对话框ID的自动补全
    private static final SuggestionProvider<CommandSourceStack> DIALOG_SUGGESTIONS =
            (context, builder) -> {
                com.infiniteflameteam.umoift.dialog.DialogManager.getAvailableDialogs()
                        .forEach(builder::suggest);
                return builder.buildFuture();
            };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("dialog")
                .requires(source -> source.hasPermission(2))

                // dialog show <targets> <dialog_id>
                .then(Commands.literal("show")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("dialog_id", StringArgumentType.string())
                                        .suggests(DIALOG_SUGGESTIONS)
                                        .executes(context -> executeShow(
                                                context,
                                                EntityArgument.getPlayers(context, "targets"),
                                                StringArgumentType.getString(context, "dialog_id"),
                                                null // 默认场景
                                        ))
                                        .then(Commands.argument("scene", StringArgumentType.string())
                                                .executes(context -> executeShow(
                                                        context,
                                                        EntityArgument.getPlayers(context, "targets"),
                                                        StringArgumentType.getString(context, "dialog_id"),
                                                        StringArgumentType.getString(context, "scene")
                                                ))
                                        )
                                )
                        )
                )

                // dialog clear <targets>
                .then(Commands.literal("clear")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(context -> executeClear(
                                        context,
                                        EntityArgument.getPlayers(context, "targets")
                                ))
                        )
                )

                // dialog reload
                .then(Commands.literal("reload")
                        .executes(context -> executeReload(context))
                )

                // dialog list
                .then(Commands.literal("list")
                        .executes(context -> executeList(context))
                )
        );
    }

    private static int executeShow(CommandContext<CommandSourceStack> context,
                                   Collection<ServerPlayer> targets,
                                   String dialogId, String scene) {
        try {
            int successCount = com.infiniteflameteam.umoift.dialog.DialogManager.showDialog(targets, dialogId, scene);

            if (successCount > 0) {
                context.getSource().sendSuccess(() ->
                        Component.literal(
                                String.format("向 %d 名玩家显示对话框: %s", successCount, dialogId)
                        ), true);
            } else {
                context.getSource().sendFailure(
                        Component.literal("显示对话框失败: " + dialogId)
                );
            }
            return successCount;
        } catch (Exception e) {
            LOGGER.error("显示对话框时出错", e);
            context.getSource().sendFailure(
                    Component.literal("错误: " + e.getMessage())
            );
            return 0;
        }
    }

    private static int executeClear(CommandContext<CommandSourceStack> context,
                                    Collection<ServerPlayer> targets) {
        com.infiniteflameteam.umoift.dialog.DialogManager.clearDialog(targets);
        context.getSource().sendSuccess(() ->
                Component.literal("清除对话框"), true);
        return targets.size();
    }

    private static int executeReload(CommandContext<CommandSourceStack> context) {
        try {
            com.infiniteflameteam.umoift.dialog.DialogManager.reloadDialogs();
            context.getSource().sendSuccess(() ->
                    Component.literal("重载对话框配置成功"), true);
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(
                    Component.literal("重载失败: " + e.getMessage())
            );
            return 0;
        }
    }

    private static int executeList(CommandContext<CommandSourceStack> context) {
        List<String> dialogs = com.infiniteflameteam.umoift.dialog.DialogManager.getAvailableDialogs();
        context.getSource().sendSuccess(() ->
                Component.literal("可用对话框: " + String.join(", ", dialogs)), false);
        return dialogs.size();
    }
}