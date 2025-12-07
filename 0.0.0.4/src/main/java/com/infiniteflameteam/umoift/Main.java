package com.infiniteflameteam.umoift;

import com.infiniteflameteam.umoift.blocks.*;
import com.infiniteflameteam.umoift.commands.DialogCommand;
import com.infiniteflameteam.umoift.commands.ClaimCommand;
import com.infiniteflameteam.umoift.dialog.OfficialDialogManager;
import com.infiniteflameteam.umoift.network.DialogNetworkHandler;
import com.infiniteflameteam.umoift.claim.ClaimManager;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;

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

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final Map<String, RegistryObject<Block>> COLORED_LAMPS = new LinkedHashMap<>();
    public static final Map<String, RegistryObject<Item>> COLORED_LAMP_ITEMS = new LinkedHashMap<>();

    private static final String[] COLORS = {
            "white", "orange", "magenta", "light_blue",
            "yellow", "lime", "pink", "gray",
            "light_gray", "cyan", "purple", "blue",
            "brown", "green", "red", "black"
    };

    static {
        for (String color : COLORS) {
            String blockId = color + "_lamp_next";

            RegistryObject<Block> block = createColoredLampBlock(color, blockId);
            RegistryObject<Item> item = ITEMS.register(blockId,
                    () -> new BlockItem(block.get(), new Item.Properties()));

            COLORED_LAMPS.put(color, block);
            COLORED_LAMP_ITEMS.put(color, item);
        }
    }

    private static RegistryObject<Block> createColoredLampBlock(String color, String blockId) {
        BlockBehaviour.Properties properties = BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_LIGHT_BLUE)
                .strength(0.3f)
                .lightLevel(state -> state.getValue(AbstractColoredLampNextBlock.LIT) ? 15 : 0);

        return switch (color) {
            case "white" -> BLOCKS.register(blockId, () -> new WhiteLampNextBlock(properties));
            case "orange" -> BLOCKS.register(blockId, () -> new OrangeLampNextBlock(properties));
            case "magenta" -> BLOCKS.register(blockId, () -> new MagentaLampNextBlock(properties));
            case "light_blue" -> BLOCKS.register(blockId, () -> new LightBlueLampNextBlock(properties));
            case "yellow" -> BLOCKS.register(blockId, () -> new YellowLampNextBlock(properties));
            case "lime" -> BLOCKS.register(blockId, () -> new LimeLampNextBlock(properties));
            case "pink" -> BLOCKS.register(blockId, () -> new PinkLampNextBlock(properties));
            case "gray" -> BLOCKS.register(blockId, () -> new GrayLampNextBlock(properties));
            case "light_gray" -> BLOCKS.register(blockId, () -> new LightGrayLampNextBlock(properties));
            case "cyan" -> BLOCKS.register(blockId, () -> new CyanLampNextBlock(properties));
            case "purple" -> BLOCKS.register(blockId, () -> new PurpleLampNextBlock(properties));
            case "blue" -> BLOCKS.register(blockId, () -> new BlueLampNextBlock(properties));
            case "brown" -> BLOCKS.register(blockId, () -> new BrownLampNextBlock(properties));
            case "green" -> BLOCKS.register(blockId, () -> new GreenLampNextBlock(properties));
            case "red" -> BLOCKS.register(blockId, () -> new RedLampNextBlock(properties));
            case "black" -> BLOCKS.register(blockId, () -> new BlackLampNextBlock(properties));
            default -> BLOCKS.register(blockId, () -> new WhiteLampNextBlock(properties));
        };
    }

    public Main() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册方块和物品
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);

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
                for (RegistryObject<Item> item : COLORED_LAMP_ITEMS.values()) {
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