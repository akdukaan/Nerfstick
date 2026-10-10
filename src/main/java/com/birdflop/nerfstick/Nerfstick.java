package com.birdflop.nerfstick;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class Nerfstick extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new NerfstickListener(), this);
        new Metrics(this, 26337);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
