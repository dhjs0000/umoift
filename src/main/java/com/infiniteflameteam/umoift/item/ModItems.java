package com.infiniteflameteam.umoift.item;

import com.infiniteflameteam.umoift.Main;
import com.infiniteflameteam.umoift.blocks.ModBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Main.MODID);
    
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
            RegistryObject<Item> item = ITEMS.register(blockId,
                    () -> new BlockItem(ModBlocks.COLORED_LAMPS.get(color).get(), new Item.Properties()));
            COLORED_LAMP_ITEMS.put(color, item);
        }
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}