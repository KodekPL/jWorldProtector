package jcraft.wp;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import jcraft.wp.config.WorldSettings;
import jcraft.wp.region.RegionContainer;

import org.bukkit.World;

public class WorldsManager {

    private final Map<String, WorldInstance> worlds = new HashMap<String, WorldInstance>();

    public void loadWorlds() {
        for (World world : ProtectorPlugin.getPlugin().getServer().getWorlds()) {
            initWorld(world);
        }
    }

    public void initWorld(World world) {
        ProtectorPlugin.log(Level.INFO, "Initialization of world '" + world.getName() + "'.");

        final File worldFile = new File(ProtectorPlugin.WORLDS_DIR, world.getName() + ".yml");
        final File regionFile = new File(ProtectorPlugin.REGIONS_DIR, world.getName() + ".yml");

        final WorldSettings worldConfig = new WorldSettings(worldFile);
        final RegionContainer regionContainer = new RegionContainer(regionFile);

        if (worldFile.exists()) {
            worldConfig.load(); // Load world config to memory
            worldConfig.save(); // Save fresh copy of world config

            ProtectorPlugin.log(Level.INFO, "Loaded world '" + world.getName() + "' from file.");
        } else {
            worldConfig.save();

            ProtectorPlugin.log(Level.INFO, "Created new world file for world '" + world.getName() + "'.");
        }

        if (regionFile.exists()) {
            regionContainer.load();

            ProtectorPlugin.log(Level.INFO, "Loaded world '" + world.getName() + "' " + regionContainer.size() + " regions from file.");
        } else {
            regionContainer.save();

            ProtectorPlugin.log(Level.INFO, "Created new world regions file for world '" + world.getName() + "'.");
        }

        final WorldInstance worldInst = new WorldInstance(world, worldConfig, regionContainer);

        worlds.put(worldInst.getBukkitWorld().getName(), worldInst);
    }

    public void reloadWorldConfigs() {
        for (WorldInstance world : worlds.values()) {
            world.getWorldSettings().load();
        }
    }

    public void reloadWorldRegions() {
        for (WorldInstance world : worlds.values()) {
            world.getRegionContainer().load();
        }
    }

    public WorldInstance getWorldInstance(String worldName) {
        return worlds.get(worldName);
    }

    public void reset() {
        worlds.clear();
    }

}
