package com.lumaa.ynyd.items;

import com.lumaa.ynyd.YnydMod;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModItems {
    public static final Item LIFE = registerItem(new LifeItem(), "life");

    private static Item registerItem(Item item, String name) {
        Registry.register(Registry.ITEM, new Identifier(YnydMod.id, name), item);
        return item;
    }

    public static void registerItems() {
        YnydMod.LOGGER.info("bla bla bla i have items");
    }
}
