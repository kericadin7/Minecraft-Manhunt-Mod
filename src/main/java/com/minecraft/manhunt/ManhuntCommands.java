package com.minecraft.manhunt;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class ManhuntCommands {
	private ManhuntCommands() {
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
				Commands.literal("manhunt")
						.requires(source -> source.hasPermission(2))
						.then(
								Commands.literal("setrunner")
										.then(
												Commands.argument("playerName", EntityArgument.player())
														.executes(ManhuntCommands::setRunner)
										)
						)
						.then(
								Commands.literal("addhunter")
										.then(
												Commands.argument("playerName", EntityArgument.player())
														.executes(ManhuntCommands::addHunter)
										)
						)
		);
	}

	private static int setRunner(CommandContext<CommandSourceStack> context) {
		ServerPlayer player;
		try {
			player = EntityArgument.getPlayer(context, "playerName");
		} catch (com.mojang.brigadier.exceptions.CommandSyntaxException exception) {
			context.getSource().sendFailure(Component.literal("Player not found."));
			return 0;
		}

		ManhuntGameManager.setSpeedrunner(player);
		context.getSource().sendSuccess(
				() -> Component.literal(player.getName().getString() + " is now the speedrunner."),
				true
		);
		return 1;
	}

	private static int addHunter(CommandContext<CommandSourceStack> context) {
		ServerPlayer player;
		try {
			player = EntityArgument.getPlayer(context, "playerName");
		} catch (com.mojang.brigadier.exceptions.CommandSyntaxException exception) {
			context.getSource().sendFailure(Component.literal("Player not found."));
			return 0;
		}

		ManhuntGameManager.addHunter(player);
		ManhuntCompass.giveToPlayer(player);
		context.getSource().sendSuccess(
				() -> Component.literal(player.getName().getString() + " is now a hunter."),
				true
		);
		return 1;
	}
}
