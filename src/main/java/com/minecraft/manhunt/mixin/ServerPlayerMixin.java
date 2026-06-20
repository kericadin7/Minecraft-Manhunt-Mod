package com.minecraft.manhunt.mixin;

import com.minecraft.manhunt.ManhuntCompass;
import com.minecraft.manhunt.ManhuntGameManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
	@Inject(method = "drop(Z)Z", at = @At("HEAD"), cancellable = true)
	private void manhunt$preventTrackingCompassKeyDrop(boolean allStack, CallbackInfoReturnable<Boolean> cir) {
		ServerPlayer self = (ServerPlayer) (Object) this;
		if (!ManhuntGameManager.isHunter(self)) {
			return;
		}
		ItemStack selected = self.getInventory().getSelected();
		if (ManhuntCompass.isTrackingCompass(selected)) {
			cir.setReturnValue(false);
		}
	}
}
