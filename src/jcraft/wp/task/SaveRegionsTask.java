package jcraft.wp.task;

import jcraft.wp.ProtectorPlugin;
import jcraft.wp.region.RegionContainer;

import org.bukkit.scheduler.BukkitRunnable;

public class SaveRegionsTask extends BukkitRunnable {

    private final RegionContainer regionContainer;

    public SaveRegionsTask(RegionContainer regionContainer) {
        this.regionContainer = regionContainer;

        this.runTask(ProtectorPlugin.getPlugin());
    }

    @Override
    public void run() {
        regionContainer.save();
    }

}
