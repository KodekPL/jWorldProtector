package jcraft.wp.listener;

import jcraft.wp.ProtectorPlugin;
import jcraft.wp.WorldInstance;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(player.getWorld().getName());

        if (config == null) {
            return;
        }

        if (event.getAction() == Action.PHYSICAL && !event.isCancelled()) {
            final Block block = event.getClickedBlock();

            if (block.getType() == Material.SOIL) {
                if (config.getWorldSettings().disablePlayerFarmlandGrief) {
                    event.setCancelled(true);
                }
            }
        }
    }

}
