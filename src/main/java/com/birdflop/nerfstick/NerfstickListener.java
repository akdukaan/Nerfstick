package com.birdflop.nerfstick;

import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Collection;

public class NerfstickListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Check if the player is holding a debug stick
        ItemStack itemStack = event.getItem();
        if (itemStack == null || itemStack.getType() != Material.DEBUG_STICK)
            return;

        event.setCancelled(true);

        // Get the block the player is looking at
        Block block = event.getClickedBlock();
        if (block == null)
            return;

        // Get NMS data
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        BlockState blockState = ((CraftBlock) block).getNMS();
        net.minecraft.world.level.block.Block nmsBlock = blockState.getBlock();
        String blockId = BuiltInRegistries.BLOCK.getKey(nmsBlock).toString();

        // Get state definition
        StateDefinition<net.minecraft.world.level.block.Block, BlockState> stateDefinition = nmsBlock.getStateDefinition();
        Collection<Property<?>> properties = stateDefinition.getProperties();
        if (properties.isEmpty())
            return;

        // Get the property
        CompoundTag nbtTagCompound = nmsItemStack.getOrCreateTagElement("DebugProperty");
        String propertyName = nbtTagCompound.getString(blockId);
        Property<?> property = stateDefinition.getProperty(propertyName);
        if (property == null)
            return;
        cycleState(blockState, property, false);
    }

    // Weird workaround for Java generics (I hate Java)
    private static <T extends Comparable<T>> BlockState cycleState(BlockState state, Property<T> property, boolean inverse) {
        return state.setValue(property, cycleState(property.getPossibleValues(), state.getValue(property), inverse));
    }

    private static <T> T cycleState(Iterable<T> elements, @Nullable T current, boolean inverse) {
        return inverse ? Util.findPreviousInIterable(elements, current) : Util.findNextInIterable(elements, current);
    }
}
