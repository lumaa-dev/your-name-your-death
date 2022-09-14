package com.lumaa.ynyd.mixin;

import com.lumaa.ynyd.YnydMod;
import com.lumaa.ynyd.utils.IEntityDataSaver;
import com.lumaa.ynyd.utils.LifeData;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class PlayerDeathMixin extends PlayerEntity {
    public Inventory savedInventory;

    public PlayerDeathMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile, @Nullable PlayerPublicKey publicKey) {
        super(world, pos, yaw, gameProfile, publicKey);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        this.refreshPosition();
        if (!this.isSpectator() && LifeData.hasDied((IEntityDataSaver) this)) {
            this.drop(damageSource);
        } else {
            saveInventory();
        }

        if (damageSource != null) {
            this.setVelocity((double)(-MathHelper.cos((this.knockbackVelocity + this.getYaw()) * 0.017453292F) * 0.1F), 0.10000000149011612D, (double)(-MathHelper.sin((this.knockbackVelocity + this.getYaw()) * 0.017453292F) * 0.1F));
        } else {
            this.setVelocity(0.0D, 0.1D, 0.0D);
        }

        this.incrementStat(Stats.DEATHS);
        this.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_DEATH));
        this.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST));
        this.extinguish();
        this.setOnFire(false);
        this.setLastDeathPos(Optional.of(GlobalPos.create(this.world.getRegistryKey(), this.getBlockPos())));
    }

    @Override
    public void requestRespawn() {
        super.requestRespawn();
        if (LifeData.hasDied((IEntityDataSaver) this)) {
            if (!isSpectator()) {
                this.sendMessage(Text.literal("Dead"));
                setSpectator();
            }
        } else {
            loadInventory();
        }
    }

    private void saveInventory() {
        this.savedInventory = this.getInventory();
    }

    private void loadInventory() {
        if (this.savedInventory == null) {
            YnydMod.LOGGER.info("Tried to load empty inventory");
        } else {
            for (int i = 0; i < this.savedInventory.size(); i++) {
                this.getInventory().setStack(i, this.savedInventory.getStack(i));
            }
            YnydMod.LOGGER.info("Loaded inventory");
        }
    }

    private void setSpectator() {
        if (!this.isSpectator() && this.world.isClient()) {
            this.getServer().getPlayerManager().getPlayer(this.uuid);
        }
    }
}
