package jcraft.wp;

import jcraft.wp.config.WorldSettings;

import org.bukkit.World;

public class WorldInstance {

    private final World bukkitWorld;
    private final WorldSettings worldConfig;

    public WorldInstance(World world, WorldSettings worldConfig) {
        this.bukkitWorld = world;
        this.worldConfig = worldConfig;
    }

    public World getBukkitWorld() {
        return this.bukkitWorld;
    }

    public WorldSettings getWorldSettings() {
        return this.worldConfig;
    }

}
