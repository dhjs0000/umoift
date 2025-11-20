package com.infiniteflameteam.umoift.client;

import com.google.gson.Gson;
import com.infiniteflameteam.umoift.client.gui.DialogScreen;
import com.infiniteflameteam.umoift.network.DialogNetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@OnlyIn(Dist.CLIENT)
public class ClientDialogHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientDialogHandler.class);
    private static final Gson GSON = new Gson();

    public static void handleDialogOpen(String dialogId, String sceneData) {
        Minecraft minecraft = Minecraft.getInstance();

        try {
            // 解析场景数据
            DialogSceneData scene = GSON.fromJson(sceneData, DialogSceneData.class);

            // 在客户端线程中显示对话框
            minecraft.execute(() -> {
                minecraft.setScreen(new DialogScreen(dialogId, scene));
            });
        } catch (Exception e) {
            LOGGER.error("处理对话框打开失败", e);
        }
    }

    public static void handleDialogClear() {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.execute(() -> {
            if (minecraft.screen instanceof DialogScreen) {
                minecraft.setScreen(null);
            }
        });
    }

    public static void sendButtonClick(String dialogId, String buttonAction) {
        DialogNetworkHandler.ServerboundDialogButtonPacket packet =
                new DialogNetworkHandler.ServerboundDialogButtonPacket(dialogId, buttonAction);
        DialogNetworkHandler.INSTANCE.sendToServer(packet);
    }

    // 客户端场景数据结构
    public static class DialogSceneData {
        public String title;
        public String[] body;
        public DialogButtonData[] buttons;
        public boolean pauseGame;
        public boolean canCloseWithEscape;
        public String background;
    }

    public static class DialogButtonData {
        public String text;
        public String action;
        public String style;
    }
}