package jcraft.wp;

import jcraft.wp.commands.ProtectorCommands;
import jcraft.wp.commands.RegionCommands;

import org.bukkit.plugin.java.JavaPlugin;

public class ProtectorPlugin extends JavaPlugin {

    private static ProtectorPlugin plugin;

    @Override
    public void onEnable() {
        plugin = this;

        this.getDataFolder().mkdirs();

        this.getCommand("worldprotector").setExecutor(new ProtectorCommands());
        this.getCommand("region").setExecutor(new RegionCommands());
    }

    public static ProtectorPlugin getPlugin() {
        return plugin;
    }

}
