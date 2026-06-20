package com.minecraft.manhunt;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class CompassHandler {
	private CompassHandler() {
	}

	public static InteractionResult onUseItem(ServerPlayer hunter, ItemStack stack) {
		if (!stack.is(Items.COMPASS)) {
			return InteractionResult.PASS;
		}
		if (!ManhuntGameManager.isHunter(hunter)) {
			return InteractionResult.PASS;
		}

		ServerPlayer speedrunner = ManhuntGameManager.getSpeedrunner(hunter);
		if (speedrunner == null) {
			hunter.displayClientMessage(
					Component.literal("No speedrunner has been designated yet."),
					true
			);
			return InteractionResult.FAIL;
		}

		BlockPos target = ManhuntGameManager.getTrackingTarget(hunter, speedrunner);
		if (target == null) {
			hunter.displayClientMessage(
					Component.literal("No player to track in this dimension (Pointing to last known location)"),
					true
			);
			return InteractionResult.FAIL;
		}

		ManhuntCompass.pointTo(hunter, stack, hunter.level().dimension(), target);

		if (ManhuntGameManager.isSameDimension(hunter, speedrunner)) {
			hunter.displayClientMessage(
					Component.literal("Compass is now pointing to " + speedrunner.getName().getString()),
					true
			);
		} else {
			hunter.displayClientMessage(
					Component.literal("No player to track in this dimension (Pointing to last known location)"),
					true
			);
		}

		return InteractionResult.SUCCESS;
	}
}
