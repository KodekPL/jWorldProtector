package jcraft.wp;

import jcraft.wp.config.WorldSettings;
import jcraft.wp.region.RegionContainer;

import org.bukkit.World;

public class WorldInstance {

    private final World bukkitWorld;
    private final WorldSettings worldConfig;
    private final RegionContainer regionContainer;

    public WorldInstance(World world, WorldSettings worldConfig, RegionContainer regionContainer) {
        this.bukkitWorld = world;
        this.worldConfig = worldConfig;
        this.regionContainer = regionContainer;
    }

    public World getBukkitWorld() {
        return this.bukkitWorld;
    }

    public WorldSettings getWorldSettings() {
        return this.worldConfig;
    }

    public RegionContainer getRegionContainer() {
        return this.regionContainer;
    }

}
