package com.minecraft.manhunt;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;

public final class ManhuntEventHandlers {
	private ManhuntEventHandlers() {
	}

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				ManhuntCommands.register(dispatcher)
		);

		UseItemCallback.EVENT.register((player, world, hand) -> {
			if (world.isClientSide() || !(player instanceof ServerPlayer serverPlayer)) {
				return InteractionResultHolder.pass(player.getItemInHand(hand));
			}
			ItemStack stack = player.getItemInHand(hand);
			InteractionResult result = CompassHandler.onUseItem(serverPlayer, stack);
			if (result == InteractionResult.SUCCESS) {
				return InteractionResultHolder.success(stack);
			}
			if (result == InteractionResult.FAIL) {
				return InteractionResultHolder.fail(stack);
			}
			return InteractionResultHolder.pass(stack);
		});

		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			ManhuntCompass.giveToPlayer(newPlayer);
		});

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			ServerPlayer speedrunner = server.getPlayerList().getPlayers().stream()
					.filter(ManhuntGameManager::isSpeedrunner)
					.findFirst()
					.orElse(null);
			if (speedrunner != null) {
				ManhuntGameManager.tick(speedrunner);
			}
		});
	}
}
