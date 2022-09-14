package com.lumaa.ynyd.utils;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;

public class LifeData {
    public static int setLives(IEntityDataSaver player, int amount) {
        NbtCompound nbt = player.getPersistentData();
        int lives = MathHelper.clamp(amount, 0, 5);

        nbt.putInt("lives", lives);
        return lives;
    }

    public static boolean hasDied(IEntityDataSaver player) {
        NbtCompound nbt = player.getPersistentData();
        int lives = nbt.getInt("lives");

        if (lives <= 0) {
            return true;
        }

        return false;
    }
}
