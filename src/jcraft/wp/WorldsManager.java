package jcraft.wp;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import jcraft.wp.config.WorldSettings;

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
        final WorldInstance worldInst;

        if (worldFile.exists()) {
            final WorldSettings worldConfig = new WorldSettings(worldFile);

            worldConfig.load(); // Load world config to memory
            worldConfig.save(); // Save fresh copy of world config

            worldInst = new WorldInstance(world, worldConfig);

            ProtectorPlugin.log(Level.INFO, "Loaded world '" + world.getName() + "' from file.");
        } else {
            final WorldSettings worldConfig = new WorldSettings(worldFile);

            worldConfig.save();

            worldInst = new WorldInstance(world, worldConfig);

            ProtectorPlugin.log(Level.INFO, "Created new world file for world '" + world.getName() + "'.");
        }

        worlds.put(worldInst.getBukkitWorld().getName(), worldInst);
    }

    public WorldInstance getWorldInstance(String worldName) {
        return worlds.get(worldName);
    }

    public void reset() {
        worlds.clear();
    }

}
