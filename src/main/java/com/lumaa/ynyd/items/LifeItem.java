package com.lumaa.ynyd.items;

import com.lumaa.ynyd.YnydMod.Preferences;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Rarity;

public class LifeItem extends Item {
    public LifeItem() {
        super(new FabricItemSettings().rarity(Rarity.EPIC).group(ItemGroup.COMBAT).maxCount(Preferences.maxTotemCount).fireproof());
    }
}
