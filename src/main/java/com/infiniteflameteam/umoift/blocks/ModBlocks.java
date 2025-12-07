package com.infiniteflameteam.umoift.blocks;

import com.infiniteflameteam.umoift.Main;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Main.MODID);
    
    public static final Map<String, RegistryObject<Block>> COLORED_LAMPS = new LinkedHashMap<>();
    
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
            COLORED_LAMPS.put(color, block);
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

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}