package com.infiniteflameteam.umoift.item;

import com.infiniteflameteam.umoift.Main;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final String UMOIFT_TAB_STRING = "creativetab.umoift_tab";
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Main.MODID);

    public static final RegistryObject<CreativeModeTab> UMOIFT_TAB = CREATIVE_MODE_TABS.register("umoift_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.COLORED_LAMP_ITEMS.get("white").get()))
                    .title(Component.translatable(UMOIFT_TAB_STRING))
                    .displayItems((pParameters, pOutput) -> {
                        // 添加所有彩色灯具
                        for (RegistryObject<Item> item : ModItems.COLORED_LAMP_ITEMS.values()) {
                            pOutput.accept(item.get());
                        }
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}