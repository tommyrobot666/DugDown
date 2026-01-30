package lommie.dugdown.mixin;

import lommie.dugdown.CommonClass;
import lommie.dugdown.notamixin.IMixinPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(Player.class)
public class MixinPlayer implements IMixinPlayer {
    @Unique
    private static final EntityDataAccessor<Integer> DATA_BLOCKS_DUG_DOWN = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);

    @Inject(method = "addAdditionalSaveData", at=@At("TAIL"))
    public void addAdditionalSaveData(CompoundTag pCompound, CallbackInfo ci) {
        SynchedEntityData entityData = dugDown$getSynchedEntityDataWithReflection();
        pCompound.putInt("blocksDugDown", entityData.get(DATA_BLOCKS_DUG_DOWN));
    }

    @Inject(method = "readAdditionalSaveData", at=@At("TAIL"))
    public void readAdditionalSaveData(CompoundTag pCompound, CallbackInfo ci){
        SynchedEntityData entityData = dugDown$getSynchedEntityDataWithReflection();
        entityData.set(DATA_BLOCKS_DUG_DOWN, pCompound.getInt("blocksDugDown"));
    }

    @Inject(method = "defineSynchedData", at=@At("TAIL"))
    public void defineSynchedData(SynchedEntityData.Builder pBuilder, CallbackInfo ci){
        pBuilder.define(DATA_BLOCKS_DUG_DOWN,0);
    }

    @Unique
     public int dugDown$getBlocksDugDown(){
        SynchedEntityData entityData = dugDown$getSynchedEntityDataWithReflection();
        return entityData.get(DATA_BLOCKS_DUG_DOWN);
    }

    @Unique
    public void dugDown$setBlocksDugDown(int val){
        SynchedEntityData entityData = dugDown$getSynchedEntityDataWithReflection();
        entityData.set(DATA_BLOCKS_DUG_DOWN,val);
    }

    @Unique
    SynchedEntityData dugDown$getSynchedEntityDataWithReflection(){
        Field entityDataField;
        SynchedEntityData entityData;
        try {
            entityDataField = Entity.class.getDeclaredField("entityData");
        } catch (NoSuchFieldException e) {
            try {
                // https://wagyourtail.xyz/Projects/MinecraftMappingViewer/App
                // https://wagyourtail.xyz/Projects/MinecraftMappingViewer/App?version=1.21&mapping=MOJMAP,INTERMEDIARY&search=net/minecraft/world/entity/Entity
                // https://github.com/wagyourtail/wagyourtail.xyz/tree/master/views/sections/Projects/MinecraftMappingViewer/App
                //noinspection JavaReflectionMemberAccess
                entityDataField = Entity.class.getDeclaredField("field_6011");
            } catch (NoSuchFieldException ex) {
                throw new RuntimeException(ex);
            }
        }
        entityDataField.setAccessible(true);
        try {

            entityData = (SynchedEntityData) entityDataField.get(this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return entityData;
    }
    
    @Inject(method = "die", at = @At("TAIL"))
    public void die(DamageSource pCause, CallbackInfo ci){
        CommonClass.onDeathOrRespawn(((Player) ((Object) this)));
    }
}
