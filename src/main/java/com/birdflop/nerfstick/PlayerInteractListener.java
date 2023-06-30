package com.birdflop.nerfstick;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Rail;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Material material = event.getMaterial();
        if (material != Material.DEBUG_STICK) return;
        if (event.useInteractedBlock() == Event.Result.DENY) return;
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;
        BlockData blockData = clickedBlock.getBlockData();

        // Don't allow debug stick on waterloggable blocks in the nether
        World world = clickedBlock.getWorld();
        if (world.getEnvironment() == World.Environment.NETHER) {
            if (blockData instanceof Waterlogged) {
                cancelEvent(event, "in this world.");
                return;
            }
        }

        // Only allow on some blocks
        if (blockData instanceof TrapDoor) return;
        if (blockData instanceof Bell) return;
        if (blockData instanceof Comparator) return;
        if (blockData instanceof Gate) return;
        if (blockData instanceof Lectern) return;
        if (blockData instanceof LightningRod) return;
        if (blockData instanceof NoteBlock) return;
        if (blockData instanceof Observer) return;
        if (blockData instanceof RedstoneRail) return;
        if (blockData instanceof Repeater) return;
        if (blockData instanceof Switch) return;
        if (blockData instanceof Tripwire) return;
        if (blockData instanceof TripwireHook) return;
        if (blockData instanceof Ladder) return;
        if (blockData instanceof Chest) return;
        if (blockData instanceof Barrel) return;
        if (blockData instanceof Furnace) return;
        if (blockData instanceof Sign) return;
        if (blockData instanceof HangingSign) return;
        if (blockData instanceof WallSign) return;
        if (blockData instanceof EnderChest) return;
        if (blockData instanceof Rail) return;
        if (blockData instanceof Leaves) return;
        if (blockData instanceof Lantern) return;
        if (blockData instanceof Fence) return;
        if (blockData instanceof Wall) return;
        if (blockData instanceof Stairs) return;
        if (clickedBlock.getType() == Material.REDSTONE_LAMP) return;
        if (clickedBlock.getType().toString().endsWith("_GLAZED_TERRACOTTA")) return;

        BlockState blockState = clickedBlock.getState();
        if (blockState instanceof ShulkerBox) return;
        cancelEvent(event, "on that block.");

    }

    public void cancelEvent(PlayerInteractEvent event, String reason) {
        Player player = event.getPlayer();
        if (player.hasPermission("nerfstick.use." + event.getClickedBlock().getType())) return;
        event.setCancelled(true);
        player.sendMessage(ChatColor.RED + "You cannot use the debug stick " + reason);
    }
}