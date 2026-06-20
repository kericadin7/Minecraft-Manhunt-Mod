package com.minecraft.manhunt;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class ManhuntGameManager {
	private static UUID speedrunnerId;
	private static final Set<UUID> hunterIds = new HashSet<>();
	private static final Map<ResourceKey<Level>, Vec3> lastKnownPositions = new HashMap<>();

	private ManhuntGameManager() {
	}

	public static void setSpeedrunner(ServerPlayer player) {
		speedrunnerId = player.getUUID();
		updateLastKnownPosition(player);
	}

	public static void addHunter(ServerPlayer player) {
		hunterIds.add(player.getUUID());
	}

	public static void removeHunter(ServerPlayer player) {
		hunterIds.remove(player.getUUID());
	}

	public static boolean isSpeedrunner(ServerPlayer player) {
		return speedrunnerId != null && speedrunnerId.equals(player.getUUID());
	}

	public static boolean isHunter(ServerPlayer player) {
		return hunterIds.contains(player.getUUID());
	}

	public static ServerPlayer getSpeedrunner(ServerPlayer context) {
		if (speedrunnerId == null) {
			return null;
		}
		return context.server.getPlayerList().getPlayer(speedrunnerId);
	}

	public static Set<UUID> getHunterIds() {
		return Collections.unmodifiableSet(hunterIds);
	}

	public static void updateLastKnownPosition(ServerPlayer speedrunner) {
		if (!isSpeedrunner(speedrunner)) {
			return;
		}
		lastKnownPositions.put(speedrunner.level().dimension(), speedrunner.position());
	}

	public static Vec3 getLastKnownPosition(ResourceKey<Level> dimension) {
		return lastKnownPositions.get(dimension);
	}

	public static BlockPos getTrackingTarget(ServerPlayer hunter, ServerPlayer speedrunner) {
		ResourceKey<Level> hunterDimension = hunter.level().dimension();

		if (speedrunner.level().dimension().equals(hunterDimension)) {
			return speedrunner.blockPosition();
		}

		Vec3 lastKnown = getLastKnownPosition(hunterDimension);
		if (lastKnown == null) {
			return null;
		}
		return BlockPos.containing(lastKnown);
	}

	public static boolean isSameDimension(ServerPlayer hunter, ServerPlayer speedrunner) {
		return hunter.level().dimension().equals(speedrunner.level().dimension());
	}

	public static void tick(ServerPlayer speedrunner) {
		updateLastKnownPosition(speedrunner);
	}
}
