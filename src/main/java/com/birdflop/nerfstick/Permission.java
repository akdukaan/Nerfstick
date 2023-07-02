package com.birdflop.nerfstick;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public final class Permission {
    private Permission() { } // Prevent instantiation

    public static String BASE_GROUP = "nerfstick";

    public static String makeGroup(String base, String... args) {
        StringBuilder builder = new StringBuilder(base);
        for (String arg : args) {
            builder.append(".").append(arg);
        }
        return builder.toString();
    }

    public static boolean allowBlockState(Player player, String blockId, String stateName) {
        String[] blockIdParts = blockId.split(":");
        String namespace = blockIdParts[0];
        String id = blockIdParts[1];
        return player.hasPermission(makeGroup(BASE_GROUP, "use", namespace, id, stateName));
    }
}
