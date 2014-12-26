package jcraft.wp;

import java.io.File;
import java.util.logging.Level;

import jcraft.wp.commands.ProtectorCommands;
import jcraft.wp.commands.RegionCommands;
import jcraft.wp.listener.BlockListener;
import jcraft.wp.listener.WorldListener;

import org.bukkit.plugin.java.JavaPlugin;

public class ProtectorPlugin extends JavaPlugin {

    public static File WORLDS_DIR;

    private static ProtectorPlugin plugin;
    private static WorldsManager worldsManager;

    @Override
    public void onEnable() {
        plugin = this;

        WORLDS_DIR = new File(this.getDataFolder(), "worlds");

        this.getDataFolder().mkdirs();

        worldsManager = new WorldsManager();
        worldsManager.loadWorlds();

        this.getServer().getPluginManager().registerEvents(new WorldListener(), this);
        this.getServer().getPluginManager().registerEvents(new BlockListener(), this);

        this.getCommand("worldprotector").setExecutor(new ProtectorCommands());
        this.getCommand("region").setExecutor(new RegionCommands());
    }

    public static ProtectorPlugin getPlugin() {
        return plugin;
    }

    public static void log(Level level, String message) {
        plugin.getLogger().log(level, message);
    }

    public static WorldsManager getWorldsManager() {
        return worldsManager;
    }

}
