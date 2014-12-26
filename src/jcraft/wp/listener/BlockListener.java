package jcraft.wp.listener;

import jcraft.wp.ProtectorPlugin;
import jcraft.wp.WorldInstance;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;

public class BlockListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {
        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(event.getBlock().getWorld().getName());

        if (config == null) {
            return;
        }

        final IgniteCause cause = event.getCause();
        final boolean isFireSpread = cause == IgniteCause.SPREAD;

        if (config.getWorldSettings().disableLightningFire && cause == IgniteCause.LIGHTNING) {
            event.setCancelled(true);
            return;
        }

        if (config.getWorldSettings().disableLavaFireSpread && cause == IgniteCause.LAVA) {
            event.setCancelled(true);
            return;
        }

        if (config.getWorldSettings().disableAllFireSpread && isFireSpread) {
            event.setCancelled(true);
            return;
        }

        if (config.getWorldSettings().disableLighter && (cause == IgniteCause.FLINT_AND_STEEL || cause == IgniteCause.FIREBALL)
                && event.getPlayer() != null && !event.getPlayer().hasPermission("worldprotector.allow.lighter")) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) {
        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(event.getBlock().getWorld().getName());

        if (config == null) {
            return;
        }

        if (config.getWorldSettings().disableFireBlockBreak) {
            event.setCancelled(true);
            return;
        }
    }

}
