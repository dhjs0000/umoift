package com.infiniteflameteam.umoift.client.gui;

import com.infiniteflameteam.umoift.client.ClientDialogHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class DialogScreen extends Screen {
    private static final ResourceLocation DIALOG_BACKGROUND = new ResourceLocation("umoift", "textures/gui/dialog.png");

    private final String dialogId;
    private final ClientDialogHandler.DialogSceneData scene;
    private int leftPos;
    private int topPos;
    private final int dialogWidth = 256;
    private final int dialogHeight = 200;

    public DialogScreen(String dialogId, ClientDialogHandler.DialogSceneData scene) {
        super(Component.literal(scene.title));
        this.dialogId = dialogId;
        this.scene = scene;
    }

    @Override
    protected void init() {
        this.leftPos = (this.width - dialogWidth) / 2;
        this.topPos = (this.height - dialogHeight) / 2;

        // 动态创建场景中的按钮
        if (scene.buttons != null) {
            for (int i = 0; i < scene.buttons.length; i++) {
                ClientDialogHandler.DialogButtonData buttonDef = scene.buttons[i];
                int buttonWidth = 156;
                int buttonHeight = 20;
                int buttonX = this.leftPos + (dialogWidth - buttonWidth) / 2;
                int buttonY = this.topPos + 150 + i * 25;

                Button button = Button.builder(Component.literal(buttonDef.text), btn -> {
                            // 发送按钮点击事件到服务端
                            ClientDialogHandler.sendButtonClick(dialogId, buttonDef.action);
                            // 关闭对话框
                            this.onClose();
                        })
                        .bounds(buttonX, buttonY, buttonWidth, buttonHeight)
                        .build();

                this.addRenderableWidget(button);
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 渲染半透明背景
        this.renderBackground(guiGraphics);

        // 渲染对话框背景
        RenderSystem.setShaderTexture(0, DIALOG_BACKGROUND);
        guiGraphics.blit(DIALOG_BACKGROUND, leftPos, topPos, 0, 0, dialogWidth, dialogHeight, 256, 256);

        // 渲染标题
        guiGraphics.drawCenteredString(this.font, this.title,
                this.width / 2, this.topPos + 15, 0xFFFFFF);

        // 渲染正文内容
        if (scene.body != null) {
            for (int i = 0; i < scene.body.length; i++) {
                String text = scene.body[i];
                guiGraphics.drawCenteredString(this.font, text,
                        this.width / 2, this.topPos + 50 + i * 12, 0xCCCCCC);
            }
        }

        // 渲染其他控件
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // ESC键关闭对话框
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && scene.canCloseWithEscape) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return scene.pauseGame;
    }

    @Override
    public void onClose() {
        super.onClose();
        // 可以在这里添加关闭对话框的额外逻辑
    }
}