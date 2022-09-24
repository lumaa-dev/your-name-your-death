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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTask;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
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

    @Shadow protected abstract void dropInventory();

    @Shadow protected abstract void dropXp();

    @Shadow public abstract ItemStack getStackInHand(Hand hand);

    public MinecraftServer server = getServer();

    // works like a totem
    @Inject(at = @At("TAIL"), method = "tryUseTotem", cancellable = true)
    public void useLife(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (source.isOutOfWorld()) {
            dropInventory();
            dropXp();

            cir.setReturnValue(false);
        } else {
            if (this.isPlayer()) {
                PlayerEntity player = (PlayerEntity) getServer().getPlayerManager().getPlayer(this.uuid);
                int slot = player.getInventory().getSlotWithStack(ModItems.LIFE.getDefaultStack());;
                ItemStack itemStack = player.getInventory().getStack(slot);

                if (slot != -1 && !isHolding(Items.TOTEM_OF_UNDYING)) {
                    setHealth(Preferences.healthAfterTotem);
                    itemStack.decrement(1);

                    player.setVelocity(0d, 0d, 0d);
                    player.clearStatusEffects();
                    this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
                    this.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0));

                    teleport((ServerPlayerEntity) player, ((ServerPlayerEntity) player).getSpawnPointPosition());

                    cir.setReturnValue(true);
                    return;
                }
            }

            cir.setReturnValue(false);
        }
    }

    private ServerTask teleport(ServerPlayerEntity player, BlockPos newLoc) {
        ServerTask tp = new ServerTask((server.getTicks()) + 1, () -> player.teleport(player.getWorld(), newLoc.getX() + 0.5d, newLoc.getY(), newLoc.getZ() + 0.5d, 5.0F, 5.0F));
        server.send(tp);
        return tp;
    }

    private void drop() {
        dropInventory();
        dropXp();
    }

    private boolean isHolding(Item item) {
        Hand[] hands = Hand.values();
        int l = hands.length;

        for(int i = 0; i < l; ++i) {
            Hand hand = hands[i];
            ItemStack inHand = this.getStackInHand(hand);
            if (inHand.isOf(item)) {
                return true;
            }
        }

        return false;
    }
}
