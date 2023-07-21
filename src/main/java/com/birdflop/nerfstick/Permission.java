package com.birdflop.nerfstick;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import javax.annotation.Nullable;

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

    // Check with protection plugins to see if the player can edit the block
    @Nullable
    public static String getBlockProtection(Player player, Location location, Material material) {
        PluginManager pluginManager = Bukkit.getServer().getPluginManager();

        // Check with GriefPrevention
        String griefPreventionProtection = checkGriefPrevention(pluginManager, player, location, material);
        if (griefPreventionProtection != null)
            return griefPreventionProtection;

        // Check with WorldGuard
        String worldGuardProtection = checkWorldGuard(pluginManager, player, location);
        if (worldGuardProtection != null)
            return worldGuardProtection;

        return null;
    }

    @Nullable
    private static String checkGriefPrevention(PluginManager pluginManager, Player player, Location location, Material material) {
        Plugin griefPreventionPlugin = pluginManager.getPlugin("GriefPrevention");
        if (griefPreventionPlugin != null && griefPreventionPlugin.isEnabled())
            return GriefPrevention.instance.allowBuild(player, location, material);

        return null;
    }

    @Nullable
    private static String checkWorldGuard(PluginManager pluginManager, Player player, Location location) {
        Plugin worldGuardPlugin = pluginManager.getPlugin("WorldGuard");
        if (worldGuardPlugin != null && worldGuardPlugin.isEnabled()) {
            WorldGuard worldGuard = WorldGuard.getInstance();
            WorldGuardPlatform platform = worldGuard.getPlatform();

            LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
            RegionContainer container = platform.getRegionContainer();
            RegionQuery query = container.createQuery();
            com.sk89q.worldedit.util.Location worldEditLocation = BukkitAdapter.adapt(location);

            // Check if the player has bypass permission
            if (platform.getSessionManager().hasBypass(localPlayer, localPlayer.getWorld())) {
                return null;
            }

            // Check if the player has build permission
            if (!query.testBuild(worldEditLocation, localPlayer)) {
                return "You do not have permission to build here!";
            }
        }

        return null;
    }
}
