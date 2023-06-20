package com.birdflop.nerfstick;

import org.bukkit.plugin.java.JavaPlugin;

public final class Nerfstick extends JavaPlugin {

    public static Nerfstick nerfstick = null;

    @Override
    public void onEnable() {
        nerfstick = this;
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
