package jcraft.wp;

import java.io.File;
import java.util.logging.Level;

import jcraft.wp.commands.ProtectorCommands;
import jcraft.wp.commands.RegionCommands;
import jcraft.wp.listener.BlockListener;
import jcraft.wp.listener.EntityListener;
import jcraft.wp.listener.PlayerListener;
import jcraft.wp.listener.RegionListener;
import jcraft.wp.listener.WorldListener;
import jcraft.wp.region.flag.RegionFlagManager;
import jcraft.wp.worldedit.WorldEditHandler;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ProtectorPlugin extends JavaPlugin {

    public static File WORLDS_DIR, REGIONS_DIR;

    private static ProtectorPlugin plugin;
    private static WorldsManager worldsManager;
    private static RegionFlagManager flagsManager;
    private static WorldEditHandler worldEditHandler;

    @Override
    public void onEnable() {
        plugin = this;

        WORLDS_DIR = new File(this.getDataFolder(), "worlds");
        REGIONS_DIR = new File(this.getDataFolder(), "regions");

        this.getDataFolder().mkdirs();

        WORLDS_DIR.mkdirs();
        REGIONS_DIR.mkdirs();

        flagsManager = new RegionFlagManager();

        worldsManager = new WorldsManager();
        worldsManager.loadWorlds();

        worldEditHandler = new WorldEditHandler(this.getServer().getPluginManager().getPlugin("WorldEdit"));

        this.getServer().getPluginManager().registerEvents(new WorldListener(), this);
        this.getServer().getPluginManager().registerEvents(new BlockListener(), this);
        this.getServer().getPluginManager().registerEvents(new EntityListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        this.getServer().getPluginManager().registerEvents(new RegionListener(), this);

        this.getCommand("worldprotector").setExecutor(new ProtectorCommands());
        this.getCommand("region").setExecutor(new RegionCommands());
    }

    public static ProtectorPlugin getPlugin() {
        return plugin;
    }

    public static WorldsManager getWorldsManager() {
        return worldsManager;
    }

    public static RegionFlagManager getRegionFlagManager() {
        return flagsManager;
    }

    public static WorldEditHandler getWorldEdit() {
        return worldEditHandler;
    }

    public static void log(Level level, String message) {
        plugin.getLogger().log(level, message);
    }

    public static Player getOnlinePlayer(String name) {
        for (Player player : getPlugin().getServer().getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }

        return null;
    }

}
