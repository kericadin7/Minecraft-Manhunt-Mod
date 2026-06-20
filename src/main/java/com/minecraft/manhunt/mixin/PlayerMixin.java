package com.minecraft.manhunt.mixin;

import com.minecraft.manhunt.ManhuntCompass;
import com.minecraft.manhunt.ManhuntGameManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {
	@Inject(
			method = "drop(Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/world/entity/item/ItemEntity;",
			at = @At("HEAD"),
			cancellable = true
	)
	private void manhunt$preventTrackingCompassDrop(ItemStack stack, boolean bl, CallbackInfoReturnable<ItemEntity> cir) {
		Player self = (Player) (Object) this;
		if (self instanceof ServerPlayer player
				&& ManhuntGameManager.isHunter(player)
				&& ManhuntCompass.isTrackingCompass(stack)) {
			cir.setReturnValue(null);
		}
	}

	@Inject(
			method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
			at = @At("HEAD"),
			cancellable = true
	)
	private void manhunt$preventTrackingCompassDropWithOwnership(
			ItemStack stack,
			boolean bl,
			boolean retainOwnership,
			CallbackInfoReturnable<ItemEntity> cir
	) {
		Player self = (Player) (Object) this;
		if (self instanceof ServerPlayer player
				&& ManhuntGameManager.isHunter(player)
				&& ManhuntCompass.isTrackingCompass(stack)) {
			cir.setReturnValue(null);
		}
	}
}
