package com.minecraft.manhunt.mixin;

import com.minecraft.manhunt.ManhuntCompass;
import com.minecraft.manhunt.ManhuntGameManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@Inject(method = "dropAllDeathLoot", at = @At("HEAD"))
	private void manhunt$stripTrackingCompassBeforeDeathDrop(CallbackInfo ci) {
		LivingEntity self = (LivingEntity) (Object) this;
		if (self instanceof ServerPlayer player && ManhuntGameManager.isHunter(player)) {
			ManhuntCompass.stripFromInventory(player);
		}
	}
}
