package jcraft.wp.listener;

import jcraft.wp.ProtectorPlugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldListener implements Listener {

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        ProtectorPlugin.getWorldsManager().initWorld(event.getWorld());
    }

}
