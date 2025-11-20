package com.infiniteflameteam.umoift.dialog;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DialogManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DialogManager.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // 存储所有对话框定义
    private static final Map<String, DialogDefinition> DIALOGS = new ConcurrentHashMap<>();

    // 存储玩家当前的对话框状态
    private static final Map<UUID, PlayerDialogState> PLAYER_STATES = new ConcurrentHashMap<>();

    public static class DialogDefinition {
        public String id;
        public String type; // "notice", "confirmation", "dialog_list"
        public Map<String, DialogScene> scenes = new HashMap<>();
        public String defaultScene;
        public JsonObject metadata;

        // 对话框类型常量
        public static final String TYPE_NOTICE = "notice";
        public static final String TYPE_CONFIRMATION = "confirmation";
        public static final String TYPE_DIALOG_LIST = "dialog_list";
    }

    public static class DialogScene {
        public String title;
        public List<DialogText> body = new ArrayList<>();
        public List<DialogButton> buttons = new ArrayList<>();
        public Map<String, String> actions = new HashMap<>(); // 按钮点击后的动作
        public String background;
        public boolean pauseGame = false;
        public boolean canCloseWithEscape = true;
    }

    public static class DialogText {
        public String type; // "plain_message", "translatable_message"
        public String contents;
        public String translate;
        public List<String> with;
    }

    public static class DialogButton {
        public String text;
        public String action;
        public String style; // "primary", "secondary", "danger"
    }

    public static class PlayerDialogState {
        public UUID playerId;
        public String currentDialog;
        public String currentScene;
        public long openTime;
        public Map<String, Object> variables = new HashMap<>();
    }

    public static void loadDialogs(ResourceManager resourceManager) {
        DIALOGS.clear();

        try {
            // 从多个命名空间加载对话框
            loadFromNamespace(resourceManager, "umoift");
            loadFromNamespace(resourceManager, "minecraft");

            LOGGER.info("已加载 {} 个对话框定义", DIALOGS.size());

            // 如果没有找到任何对话框，加载默认的
            if (DIALOGS.isEmpty()) {
                loadDefaultDialogs();
                LOGGER.info("已加载默认对话框定义");
            }
        } catch (Exception e) {
            LOGGER.error("加载对话框时发生错误", e);
            loadDefaultDialogs(); // 出错时加载默认对话框
        }
    }

    private static void loadFromNamespace(ResourceManager resourceManager, String namespace) {
        try {
            // 查找所有对话框JSON文件
            var resources = resourceManager.listResources("dialogs",
                    location -> location.getPath().endsWith(".json")
            );

            for (var resource : resources.entrySet()) {
                try {
                    var resourceLocation = resource.getKey();
                    if (!resourceLocation.getNamespace().equals(namespace)) {
                        continue;
                    }

                    var resourceOpt = resourceManager.getResource(resourceLocation);
                    if (resourceOpt.isPresent()) {
                        try (var stream = resourceOpt.get().open()) {
                            JsonObject dialogJson = GSON.fromJson(new InputStreamReader(stream), JsonObject.class);
                            parseDialogDefinition(dialogJson, resourceLocation);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("加载对话框文件失败: {}", resource.getKey(), e);
                }
            }
        } catch (Exception e) {
            LOGGER.error("从命名空间加载对话框失败: {}", namespace, e);
        }
    }

    private static void parseDialogDefinition(JsonObject json, ResourceLocation location) {
        try {
            DialogDefinition definition = new DialogDefinition();
            definition.id = location.getPath().replace("dialogs/", "").replace(".json", "");
            definition.type = json.get("type").getAsString();

            if (json.has("metadata")) {
                definition.metadata = json.getAsJsonObject("metadata");
            }

            // 解析场景
            JsonObject scenesJson = json.getAsJsonObject("scenes");
            for (String sceneKey : scenesJson.keySet()) {
                DialogScene scene = parseScene(scenesJson.getAsJsonObject(sceneKey));
                definition.scenes.put(sceneKey, scene);
            }

            definition.defaultScene = json.get("default_scene").getAsString();
            DIALOGS.put(definition.id, definition);

        } catch (Exception e) {
            LOGGER.error("解析对话框定义失败: {}", location, e);
        }
    }

    private static DialogScene parseScene(JsonObject sceneJson) {
        DialogScene scene = new DialogScene();
        scene.title = sceneJson.get("title").getAsString();

        // 解析正文内容
        JsonArray bodyArray = sceneJson.getAsJsonArray("body");
        for (JsonElement element : bodyArray) {
            DialogText text = new DialogText();
            JsonObject textObj = element.getAsJsonObject();
            text.type = textObj.get("type").getAsString();
            if (text.type.equals("plain_message")) {
                text.contents = textObj.get("contents").getAsString();
            } else if (text.type.equals("translatable_message")) {
                text.translate = textObj.get("translate").getAsString();
                if (textObj.has("with")) {
                    text.with = new ArrayList<>();
                    for (JsonElement withElement : textObj.getAsJsonArray("with")) {
                        text.with.add(withElement.getAsString());
                    }
                }
            }
            scene.body.add(text);
        }

        // 解析按钮
        if (sceneJson.has("buttons")) {
            JsonArray buttonsArray = sceneJson.getAsJsonArray("buttons");
            for (JsonElement element : buttonsArray) {
                DialogButton button = new DialogButton();
                JsonObject buttonObj = element.getAsJsonObject();
                button.text = buttonObj.get("text").getAsString();
                button.action = buttonObj.get("action").getAsString();
                button.style = buttonObj.has("style") ? buttonObj.get("style").getAsString() : "primary";
                scene.buttons.add(button);
            }
        }

        // 解析动作
        if (sceneJson.has("actions")) {
            JsonObject actionsObj = sceneJson.getAsJsonObject("actions");
            for (String actionKey : actionsObj.keySet()) {
                scene.actions.put(actionKey, actionsObj.get(actionKey).getAsString());
            }
        }

        if (sceneJson.has("background")) {
            scene.background = sceneJson.get("background").getAsString();
        }

        scene.pauseGame = sceneJson.has("pause") && sceneJson.get("pause").getAsBoolean();
        scene.canCloseWithEscape = !sceneJson.has("can_close_with_escape") ||
                sceneJson.get("can_close_with_escape").getAsBoolean();

        return scene;
    }

    public static int showDialog(Collection<ServerPlayer> targets, String dialogId, String sceneName) {
        DialogDefinition dialog = DIALOGS.get(dialogId);
        if (dialog == null) {
            LOGGER.warn("对话框不存在: {}", dialogId);
            return 0;
        }

        String actualScene = sceneName != null ? sceneName : dialog.defaultScene;
        DialogScene scene = dialog.scenes.get(actualScene);
        if (scene == null) {
            LOGGER.warn("场景不存在: {} 在对话框 {}", actualScene, dialogId);
            return 0;
        }

        int successCount = 0;
        for (ServerPlayer player : targets) {
            if (showDialogToPlayer(player, dialog, scene)) {
                successCount++;
            }
        }

        return successCount;
    }

    private static boolean showDialogToPlayer(ServerPlayer player, DialogDefinition dialog, DialogScene scene) {
        try {
            // 创建玩家状态
            PlayerDialogState state = new PlayerDialogState();
            state.playerId = player.getUUID();
            state.currentDialog = dialog.id;
            state.currentScene = scene.title;
            state.openTime = System.currentTimeMillis();

            PLAYER_STATES.put(player.getUUID(), state);

            // 发送网络数据包显示对话框
            com.infiniteflameteam.umoift.network.DialogNetworkHandler.sendDialogOpen(player, dialog, scene);

            return true;
        } catch (Exception e) {
            LOGGER.error("向玩家显示对话框失败: {}", player.getScoreboardName(), e);
            return false;
        }
    }

    public static void clearDialog(Collection<ServerPlayer> targets) {
        for (ServerPlayer player : targets) {
            PLAYER_STATES.remove(player.getUUID());
            com.infiniteflameteam.umoift.network.DialogNetworkHandler.sendDialogClear(player);
        }
    }

    public static void handleButtonClick(ServerPlayer player, String dialogId, String buttonAction) {
        PlayerDialogState state = PLAYER_STATES.get(player.getUUID());
        if (state == null || !state.currentDialog.equals(dialogId)) {
            return;
        }

        DialogDefinition dialog = DIALOGS.get(dialogId);
        if (dialog == null) {
            return;
        }

        // 执行按钮动作
        executeDialogAction(player, buttonAction, dialog, state);

        // 清除对话框
        clearDialog(Collections.singleton(player));
    }

    private static void executeDialogAction(ServerPlayer player, String action,
                                            DialogDefinition dialog, PlayerDialogState state) {
        // 解析并执行动作
        if (action.startsWith("command:")) {
            String command = action.substring(8);
            player.getServer().getCommands().performPrefixedCommand(
                    player.createCommandSourceStack(), command
            );
        } else if (action.startsWith("scene:")) {
            String nextScene = action.substring(6);
            // 切换到下一个场景
            showDialog(Collections.singleton(player), dialog.id, nextScene);
        } else if (action.equals("close")) {
            // 关闭对话框
            clearDialog(Collections.singleton(player));
        }
    }

    public static void reloadDialogs() {
        // 在实际实现中，这里应该重新加载资源管理器
        // 目前我们只是清空缓存，下次使用时重新加载
        DIALOGS.clear();
        PLAYER_STATES.clear();
        loadDefaultDialogs();
    }

    public static List<String> getAvailableDialogs() {
        return new ArrayList<>(DIALOGS.keySet());
    }

    private static void loadDefaultDialogs() {
        try {
            // 示例：欢迎对话框
            JsonObject welcomeDialog = new JsonObject();
            welcomeDialog.addProperty("type", "notice");
            welcomeDialog.addProperty("default_scene", "main");

            JsonObject scenes = new JsonObject();
            JsonObject mainScene = new JsonObject();
            mainScene.addProperty("title", "欢迎使用UMOIFT");

            JsonArray body = new JsonArray();
            JsonObject text1 = new JsonObject();
            text1.addProperty("type", "plain_message");
            text1.addProperty("contents", "欢迎使用无限火队通用模组！");
            body.add(text1);

            mainScene.add("body", body);

            JsonArray buttons = new JsonArray();
            JsonObject okButton = new JsonObject();
            okButton.addProperty("text", "确定");
            okButton.addProperty("action", "close");
            okButton.addProperty("style", "primary");
            buttons.add(okButton);

            mainScene.add("buttons", buttons);

            JsonObject actions = new JsonObject();
            actions.addProperty("close", "close");
            mainScene.add("actions", actions);

            scenes.add("main", mainScene);
            welcomeDialog.add("scenes", scenes);

            parseDialogDefinition(welcomeDialog, new ResourceLocation("umoift", "welcome"));

        } catch (Exception e) {
            LOGGER.error("加载默认对话框失败", e);
        }
    }

    public static PlayerDialogState getPlayerState(UUID playerId) {
        return PLAYER_STATES.get(playerId);
    }
}