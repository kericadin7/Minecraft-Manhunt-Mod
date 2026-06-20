package com.minecraft.manhunt;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.level.Level;

import java.util.Optional;

public final class ManhuntCompass {
	private static final String TRACKING_TAG = "ManhuntTracking";

	private ManhuntCompass() {
	}

	public static boolean isTrackingCompass(ItemStack stack) {
		if (!stack.is(Items.COMPASS)) {
			return false;
		}
		CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
		if (customData == null) {
			return false;
		}
		return customData.copyTag().getBoolean(TRACKING_TAG);
	}

	public static void markAsTracking(ItemStack stack) {
		CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		tag.putBoolean(TRACKING_TAG, true);
		stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
	}

	public static ItemStack createTrackingCompass() {
		ItemStack stack = new ItemStack(Items.COMPASS);
		markAsTracking(stack);
		stack.set(DataComponents.CUSTOM_NAME, Component.literal("Tracking Compass"));
		return stack;
	}

	public static boolean hasTrackingCompass(ServerPlayer player) {
		for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
			if (isTrackingCompass(player.getInventory().getItem(slot))) {
				return true;
			}
		}
		return false;
	}

	public static void giveToPlayer(ServerPlayer player) {
		if (!ManhuntGameManager.isHunter(player)) {
			return;
		}
		if (hasTrackingCompass(player)) {
			return;
		}
		ItemStack compass = createTrackingCompass();
		if (!player.getInventory().add(compass)) {
			for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
				if (player.getInventory().getItem(slot).isEmpty()) {
					player.getInventory().setItem(slot, compass);
					return;
				}
			}
		}
	}

	public static void stripFromInventory(ServerPlayer player) {
		for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
			ItemStack stack = player.getInventory().getItem(slot);
			if (isTrackingCompass(stack)) {
				player.getInventory().setItem(slot, ItemStack.EMPTY);
			}
		}
	}

	public static void pointTo(ServerPlayer hunter, ItemStack compass, ResourceKey<Level> dimension, BlockPos target) {
		markAsTracking(compass);
		compass.set(
				DataComponents.LODESTONE_TRACKER,
				new LodestoneTracker(Optional.of(GlobalPos.of(dimension, target)), true)
		);
	}
}
