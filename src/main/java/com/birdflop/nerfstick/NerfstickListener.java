package com.birdflop.nerfstick;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.DebugStickState;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class NerfstickListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Check if the player is holding a debug stick
        ItemStack itemStack = event.getItem();
        if (itemStack == null || itemStack.getType() != Material.DEBUG_STICK) return;

        // Get the block the player is looking at
        Block block = event.getClickedBlock();
        if (block == null) return;

        // Get the action
        Action action = event.getAction();
        if (action != Action.LEFT_CLICK_BLOCK && action != Action.RIGHT_CLICK_BLOCK) return;

        event.setCancelled(true);

        // Get the player
        Player player = event.getPlayer();

        // Check if the modification is allowed
        String denyReason = Permission.getBlockProtection(player, block.getLocation(), block.getType());
        if (denyReason != null && !denyReason.isEmpty()) {
            // Tell player about the error
            player.sendActionBar(
                    Component.empty()
                            .append(Component.text("Interaction denied! Reason: ", TextColor.color(0xFFAA00)))
                            .append(Component.text(denyReason, TextColor.color(0xFF5555)))
            );
            return;
        }

        // Get NMS data
        CraftItemStack craftItemStack = (CraftItemStack) itemStack;
        BlockState blockState = ((CraftBlock) block).getNMS();
        net.minecraft.world.level.block.Block nmsBlock = blockState.getBlock();
        StateDefinition<net.minecraft.world.level.block.Block, BlockState> stateDefinition = nmsBlock.getStateDefinition();

        // Filter properties
        Collection<Property<?>> properties = stateDefinition.getProperties();
        String blockId = block.getType().getKey().toString();
        List<Property<?>> propertyList = properties.stream().filter(property -> {
            String propertyName = property.getName();
            return Permission.allowBlockState(player, blockId, propertyName);
        }).toList();

        // Check if the block has any block state
        if (propertyList.isEmpty()) {
            // Tell player about the error
            player.sendActionBar(
                    Component.empty()
                            .append(Component.text("Block ", TextColor.color(0xFFAA00)))
                            .append(Component.text(blockId, TextColor.color(0xFF5555)))
                            .append(Component.text(" does not have any modifiable block state!", TextColor.color(0xFFAA00)))
            );
            return;
        }

        // Get the property
        DebugStickState debugStickState = craftItemStack.handle.get(DataComponents.DEBUG_STICK_STATE);
        if (debugStickState == null) return;
        Property<?> property = debugStickState.properties().getOrDefault(blockState.getBlockHolder(), propertyList.getFirst());
        String propertyName = property.getName();
        if (propertyList.stream().noneMatch(p -> p.getName().equals(propertyName))) {
            property = propertyList.getFirst();
        }

        boolean inverse = player.isSneaking();
        if (action == Action.LEFT_CLICK_BLOCK) {
            property = getRelative(propertyList, property, inverse);

            // Update property name
            debugStickState = debugStickState.withProperty(blockState.getBlockHolder(), property);
            craftItemStack.handle.set(DataComponents.DEBUG_STICK_STATE, debugStickState);

            // Get the bukkit copy
            ItemStack bukkitItemStack = craftItemStack.handle.asBukkitCopy();

            // Update the item in the player's hand
            player.getInventory().setItemInMainHand(bukkitItemStack);

            // Tell player about new selected state
            player.sendActionBar(
                    Component.empty()
                            .append(Component.text("Selected state ", TextColor.color(0xFFAA00)))
                            .append(Component.text(property.getName(), TextColor.color(0xFF5555)))
                            .append(Component.text(" for ", TextColor.color(0xFFAA00)))
                            .append(Component.text(blockId, TextColor.color(0xFF5555)))
            );
        } else {
            // Cycle the state
            blockState = cycleState(blockState, property, inverse);

            // Set the block state
            Level nmsWorld = ((CraftWorld) block.getWorld()).getHandle();
            BlockPos blockPos = new BlockPos(block.getX(), block.getY(), block.getZ());
            nmsWorld.setBlock(blockPos, blockState, 18); // I have no idea what the 18 flag does, but it works

            // Tell player about new state
            player.sendActionBar(
                    Component.empty()
                            .append(Component.text("Set state ", TextColor.color(0xFFAA00)))
                            .append(Component.text(property.getName(), TextColor.color(0xFF5555)))
                            .append(Component.text(" to ", TextColor.color(0xFFAA00)))
                            .append(Component.text(getName(blockState, property), TextColor.color(0xFF5555)))
            );
        }
    }

    // Weird workaround for Java generics
    private static <T extends Comparable<T>> BlockState cycleState(BlockState state, Property<T> property, boolean inverse) {
        return state.setValue(property, getRelative(property.getPossibleValues(), state.getValue(property), inverse));
    }

    private static <T> T getRelative(Iterable<T> elements, @Nullable T current, boolean inverse) {
        return inverse ? Util.findPreviousInIterable(elements, current) : Util.findNextInIterable(elements, current);
    }

    private static <T extends Comparable<T>> String getName(BlockState state, Property<T> property) {
        return property.getName(state.getValue(property));
    }
}
