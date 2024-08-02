package com.birdflop.nerfstick;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.block.Block;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.DebugStickState;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import org.bukkit.Material;
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
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Check if the player is holding a debug stick
        ItemStack itemStack = event.getItem();
        if (itemStack == null || itemStack.getType() != Material.DEBUG_STICK)
            return;

        event.setCancelled(true);

        // Get the block the player is looking at
        org.bukkit.block.Block block = event.getClickedBlock();
        if (block == null)
            return;

        // Check if the modification is allowed
        String denyReason = Permission.getBlockProtection(event.getPlayer(), block.getLocation(), block.getType());
        if (denyReason != null && !denyReason.isEmpty()) {
            // Tell player about the error
            event.getPlayer().sendActionBar(
                    Component.empty()
                            .append(Component.text("Interaction denied! Reason: ", TextColor.color(0xFFAA00)))
                            .append(Component.text(denyReason, TextColor.color(0xFF5555)))
            );
            return;
        }

        // Get block id
        String blockId = block.getType().getKey().toString();

        // Get NMS data
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        BlockState blockState = ((CraftBlock) block).getNMS();
        net.minecraft.world.level.block.Block nmsBlock = blockState.getBlock();

        // Get state definition
        StateDefinition<net.minecraft.world.level.block.Block, BlockState> stateDefinition = nmsBlock.getStateDefinition();
        Collection<Property<?>> properties = stateDefinition.getProperties();

        // Filter properties
        List<Property<?>> propertyList = properties.stream().filter(property -> {
            String propertyName = property.getName();
            return Permission.allowBlockState(event.getPlayer(), blockId, propertyName);
        }).toList();

        // Get the player
        Player player = event.getPlayer();

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
        DebugStickState debugStickState = nmsItemStack.get(DataComponents.DEBUG_STICK_STATE);
        Property<?> property = debugStickState.properties().getOrDefault(Holder.direct(nmsBlock), propertyList.getFirst());

        Action action = event.getAction();
        if (action != Action.LEFT_CLICK_BLOCK && action != Action.RIGHT_CLICK_BLOCK)
            return;

        boolean inverse = player.isSneaking();
        boolean setState = action == Action.LEFT_CLICK_BLOCK;

        if (setState) {
            // Update property name
            property = getRelative(propertyList, property, inverse);

//            debugStickState = debugStickState.withProperty(Holder.direct(nmsBlock), property);
//            System.out.println("trying to set the component");
//            nmsItemStack.set(DataComponents.DEBUG_STICK_STATE, debugStickState);

            // Get the bukkit copy
//            System.out.println("trying to get bukkit copy");
//            ItemStack bukkitItemStack = CraftItemStack.asBukkitCopy(nmsItemStack);

//            System.out.println("Will try to set item in hand");
            // Update the item in the player's hand
//            player.getInventory().setItemInMainHand(bukkitItemStack);

            // Tell player about new selected state
            player.sendActionBar(
                    Component.empty()
                            .append(Component.text("Selected state ", TextColor.color(0xFFAA00)))
                            .append(Component.text(property.getName(), TextColor.color(0xFF5555)))
                            .append(Component.text(" for ", TextColor.color(0xFFAA00)))
                            .append(Component.text(blockId, TextColor.color(0xFF5555)))
            );
            event.setCancelled(false);
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

    // Weird workaround for Java generics (I hate Java)
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
