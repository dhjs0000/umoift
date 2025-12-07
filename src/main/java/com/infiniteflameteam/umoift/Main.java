package com.infiniteflameteam.umoift;

import com.infiniteflameteam.umoift.blocks.ModBlocks;
import com.infiniteflameteam.umoift.commands.DialogCommand;
import com.infiniteflameteam.umoift.commands.ClaimCommand;
import com.infiniteflameteam.umoift.dialog.OfficialDialogManager;
import com.infiniteflameteam.umoift.network.DialogNetworkHandler;
import com.infiniteflameteam.umoift.claim.ClaimManager;
import com.infiniteflameteam.umoift.item.ModCreativeModeTabs;
import com.infiniteflameteam.umoift.item.ModItems;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.server.packs.resources.PreparableReloadListener.PreparationBarrier;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mod(Main.MODID)
public class Main {
    public static final String MODID = "umoift";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Main() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册方块、物品和创造模式物品栏
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);

        // 注册 Mod 事件总线处理器
        modEventBus.register(new ModEventHandler());

        // 注册 Forge 事件总线处理器
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandler());

        // 初始化领地管理器（会自动加载数据）
        ClaimManager.getInstance();

        // 注册网络信道
        DialogNetworkHandler.register();

        LOGGER.info("Universal Mod of Infinite Flame Team初始化完成");
    }

    // 专门处理 Mod 事件总线的类
    private static class ModEventHandler {
        @SubscribeEvent
        public void addCreative(BuildCreativeModeTabContentsEvent event) {
            if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS ||
                    event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS ||
                    event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
                for (var item : ModItems.COLORED_LAMP_ITEMS.values()) {
                    event.accept(item.get());
                }
            }
        }
    }

    // 专门处理 Forge 事件总线的类
    private static class ForgeEventHandler {
        @SubscribeEvent
        public void onRegisterCommands(RegisterCommandsEvent event) {
            DialogCommand.register(event.getDispatcher());
            ClaimCommand.register(event.getDispatcher());
            LOGGER.info("UMOIFT命令注册完成");
        }

        @SubscribeEvent
        public void onAddReloadListener(AddReloadListenerEvent event) {
            event.addListener(new DialogReloadListener());
        }

        @SubscribeEvent
        public void onServerStarting(ServerStartingEvent event) {
            // 服务器启动时初始化对话框系统
            OfficialDialogManager.loadDialogs(event.getServer().getResourceManager());

            // 确保领地系统初始化
            ClaimManager.getInstance();
            LOGGER.info("领地系统初始化完成");
        }

        @SubscribeEvent
        public void onServerStopping(net.minecraftforge.event.server.ServerStoppingEvent event) {
            // 服务器关闭时保存领地数据
            ClaimManager.getInstance().saveClaims();
            LOGGER.info("领地数据已保存");
        }
    }

    // 修复后的 DialogReloadListener
    private static class DialogReloadListener implements PreparableReloadListener {
        @Override
        public CompletableFuture<Void> reload(PreparationBarrier barrier,
                                              ResourceManager resourceManager,
                                              ProfilerFiller preparationsProfiler,
                                              ProfilerFiller reloadProfiler,
                                              Executor backgroundExecutor,
                                              Executor gameExecutor) {
            return CompletableFuture.runAsync(() -> {
                // 在后台线程中重新加载对话框
                try {
                    OfficialDialogManager.loadDialogs(resourceManager);
                    LOGGER.info("对话框配置重载完成");
                } catch (Exception e) {
                    LOGGER.error("重载对话框配置时发生错误", e);
                }
            }, backgroundExecutor).thenCompose(barrier::wait);
        }
    }
}