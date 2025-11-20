package com.infiniteflameteam.umoift.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.infiniteflameteam.umoift.dialog.DialogManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class DialogNetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("umoift", "dialog"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static final Gson GSON = new GsonBuilder().create();
    private static int packetId = 0;

    public static void register() {
        // 注册客户端到服务器的数据包
        INSTANCE.registerMessage(packetId++, ServerboundDialogButtonPacket.class,
                ServerboundDialogButtonPacket::encode,
                ServerboundDialogButtonPacket::decode,
                ServerboundDialogButtonPacket::handle);

        // 注册服务器到客户端的数据包
        INSTANCE.registerMessage(packetId++, ClientboundDialogOpenPacket.class,
                ClientboundDialogOpenPacket::encode,
                ClientboundDialogOpenPacket::decode,
                ClientboundDialogOpenPacket::handle);

        INSTANCE.registerMessage(packetId++, ClientboundDialogClearPacket.class,
                ClientboundDialogClearPacket::encode,
                ClientboundDialogClearPacket::decode,
                ClientboundDialogClearPacket::handle);
    }

    // 服务器 -> 客户端：打开对话框
    public static class ClientboundDialogOpenPacket {
        public final String dialogId;
        public final String sceneData;

        public ClientboundDialogOpenPacket(String dialogId, String sceneData) {
            this.dialogId = dialogId;
            this.sceneData = sceneData;
        }

        public static void encode(ClientboundDialogOpenPacket msg, FriendlyByteBuf buf) {
            buf.writeUtf(msg.dialogId);
            buf.writeUtf(msg.sceneData);
        }

        public static ClientboundDialogOpenPacket decode(FriendlyByteBuf buf) {
            return new ClientboundDialogOpenPacket(buf.readUtf(), buf.readUtf());
        }

        public static void handle(ClientboundDialogOpenPacket msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                // 客户端处理打开对话框
                com.infiniteflameteam.umoift.client.ClientDialogHandler.handleDialogOpen(msg.dialogId, msg.sceneData);
            });
            ctx.get().setPacketHandled(true);
        }
    }

    // 服务器 -> 客户端：清除对话框
    public static class ClientboundDialogClearPacket {
        public static void encode(ClientboundDialogClearPacket msg, FriendlyByteBuf buf) {}
        public static ClientboundDialogClearPacket decode(FriendlyByteBuf buf) { return new ClientboundDialogClearPacket(); }
        public static void handle(ClientboundDialogClearPacket msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                com.infiniteflameteam.umoift.client.ClientDialogHandler.handleDialogClear();
            });
            ctx.get().setPacketHandled(true);
        }
    }

    // 客户端 -> 服务器：按钮点击
    public static class ServerboundDialogButtonPacket {
        public final String dialogId;
        public final String buttonAction;

        public ServerboundDialogButtonPacket(String dialogId, String buttonAction) {
            this.dialogId = dialogId;
            this.buttonAction = buttonAction;
        }

        public static void encode(ServerboundDialogButtonPacket msg, FriendlyByteBuf buf) {
            buf.writeUtf(msg.dialogId);
            buf.writeUtf(msg.buttonAction);
        }

        public static ServerboundDialogButtonPacket decode(FriendlyByteBuf buf) {
            return new ServerboundDialogButtonPacket(buf.readUtf(), buf.readUtf());
        }

        public static void handle(ServerboundDialogButtonPacket msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                if (player != null) {
                    DialogManager.handleButtonClick(player, msg.dialogId, msg.buttonAction);
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }

    public static void sendDialogOpen(ServerPlayer player, DialogManager.DialogDefinition dialog, DialogManager.DialogScene scene) {
        try {
            // 将场景数据序列化为JSON
            String sceneData = GSON.toJson(scene);
            ClientboundDialogOpenPacket packet = new ClientboundDialogOpenPacket(dialog.id, sceneData);
            INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(DialogNetworkHandler.class).error("发送对话框打开数据包失败", e);
        }
    }

    public static void sendDialogClear(ServerPlayer player) {
        try {
            ClientboundDialogClearPacket packet = new ClientboundDialogClearPacket();
            INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(DialogNetworkHandler.class).error("发送对话框清除数据包失败", e);
        }
    }
}