package jcraft.wp.listener;

import jcraft.wp.ProtectorPlugin;
import jcraft.wp.WorldInstance;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;

public class EntityListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityInteract(EntityInteractEvent event) {
        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(event.getEntity().getWorld().getName());

        if (config == null) {
            return;
        }

        final Block block = event.getBlock();

        if (block.getType() == Material.SOIL) {
            if (config.getWorldSettings().disableMobsFarmlandGrief) {
                event.setCancelled(true);
            }
        }
    }

}
