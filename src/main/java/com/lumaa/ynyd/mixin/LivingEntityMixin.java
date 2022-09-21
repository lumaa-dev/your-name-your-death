package com.lumaa.ynyd.mixin;

import com.lumaa.ynyd.YnydMod.Preferences;
import com.lumaa.ynyd.items.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTask;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

     public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow public abstract void setHealth(float health);

    @Shadow public abstract boolean addStatusEffect(StatusEffectInstance effect);

    public MinecraftServer server = getServer();

    // works like a totem
    @Inject(at = @At("HEAD"), method = "tryUseTotem", cancellable = true)
    public void useLife(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (source.isOutOfWorld()) {
            cir.setReturnValue(false);
        } else {
            if (this.isPlayer()) {
                PlayerEntity player = (PlayerEntity) getServer().getPlayerManager().getPlayer(this.uuid);
                int slot = player.getInventory().getSlotWithStack(ModItems.LIFE.getDefaultStack());;
                ItemStack itemStack = player.getInventory().getStack(slot);

                if (slot != -1) {
                    if (itemStack.getCount() == 1) {
                        itemStack.setCount(0);
                        cir.setReturnValue(false);
                    } else {
                        setHealth(Preferences.healthAfterTotem);
                        itemStack.decrement(1);

                        player.clearStatusEffects();
                        this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
                        this.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0));

                        teleport((ServerPlayerEntity) player, ((ServerPlayerEntity) player).getSpawnPointPosition());

                        cir.setReturnValue(true);
                    }
                } else {
                    cir.setReturnValue(false);
                }
            } else {
                cir.setReturnValue(false);
            }
        }

        cir.setReturnValue(false);
    }

    private ServerTask teleport(ServerPlayerEntity player, BlockPos newLoc) {
        ServerTask tp = new ServerTask((server.getTicks()) + 1, () -> player.teleport(player.getWorld(), newLoc.getX(), newLoc.getY(), newLoc.getZ(), 5.0F, 5.0F));
        server.send(tp);
        return tp;
    }
}
