package jcraft.wp.listener;

import java.util.Set;

import jcraft.wp.ProtectorPlugin;
import jcraft.wp.WorldInstance;
import jcraft.wp.region.Region;

import org.bukkit.ChatColor;
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
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // TODO: Allow to change wand in config
            if (player.getItemInHand().getType() == Material.WOOD_AXE && player.hasPermission("worldprotector.region.wand")) {
                final Block block = event.getClickedBlock();
                final Set<Region> regions = config.getRegionContainer().getRegions(block.getX(), block.getY(), block.getZ());

                final StringBuilder builder = new StringBuilder();

                if (!regions.isEmpty()) {
                    for (Region region : regions) {
                        builder.append(ChatColor.GOLD).append("Name: ").append(ChatColor.YELLOW).append(region.getName()).append('\n');

                        builder.append(ChatColor.GOLD).append("Can you build?: ")
                                .append((region.canInteract(player) ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No")).append('\n');

                        builder.append(ChatColor.GOLD).append("-----").append('\n');
                    }

                    builder.deleteCharAt(builder.length() - 1);
                } else {
                    builder.append(ChatColor.GOLD).append("No defined regions here!").append('\n').append(ChatColor.GOLD).append("Can you build?: ")
                            .append(ChatColor.GREEN).append("Yes");
                }

                player.sendMessage(builder.toString());
            }
        }

    }

}
