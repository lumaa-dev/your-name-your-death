package com.lumaa.ynyd.mixin;

import com.lumaa.ynyd.utils.IEntityDataSaver;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class YnydEntityDataSaverMixin implements IEntityDataSaver {
    private NbtCompound peristentData;

    @Override
    public NbtCompound getPersistentData() {
        if (this.peristentData == null) {
            this.peristentData = new NbtCompound();
        }
        return peristentData;
    }

    @Inject(method = "writeNbt", at = @At("HEAD"))
    protected void writeMethod(NbtCompound nbt, CallbackInfoReturnable info) {
        if (peristentData != null) {
            nbt.put("ynyd.data", peristentData);
        }
    }

    @Inject(method = "readNbt", at = @At("HEAD"))
    protected void readMethod(NbtCompound nbt, CallbackInfo info) {
        if (peristentData != null) {
            peristentData = nbt.getCompound("ynyd.data");
        }
    }
}
