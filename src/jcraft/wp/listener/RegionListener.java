package jcraft.wp.listener;

import jcraft.wp.ProtectorPlugin;
import jcraft.wp.WorldInstance;
import jcraft.wp.region.RegionInteraction;
import jcraft.wp.util.EntityUtils;
import jcraft.wp.util.MaterialUtils;
import jcraft.wp.util.MetadataUtil;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.ItemStack;

public class RegionListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        final World world = player.getWorld();
        final Block block = event.getBlock();

        if (!canInteract(RegionInteraction.BLOCK_PLACE, world, player, block.getX(), block.getY(), block.getZ())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        final Player player = event.getPlayer();
        final World world = player.getWorld();
        final Block block = event.getBlockClicked().getRelative(event.getBlockFace());

        if (!canInteract(RegionInteraction.BLOCK_PLACE, world, player, block.getX(), block.getY(), block.getZ())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final World world = player.getWorld();
        final Block block = event.getBlock();

        if (!canInteract(RegionInteraction.BLOCK_BREAK, world, player, block.getX(), block.getY(), block.getZ())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBucketFill(PlayerBucketFillEvent event) {
        final Player player = event.getPlayer();
        final World world = player.getWorld();
        final Block block = event.getBlockClicked().getRelative(event.getBlockFace());

        if (!canInteract(RegionInteraction.BLOCK_BREAK, world, player, block.getX(), block.getY(), block.getZ())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteractBlock(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final World world = player.getWorld();
        final Block block = event.getClickedBlock();

        if (MaterialUtils.isInteractiveMaterialBlock(block.getType()) || MaterialUtils.isInteractiveMaterialItem(player.getItemInHand().getType())) {
            if (!canInteract(RegionInteraction.BLOCK_INTERACT, world, player, block.getX(), block.getY(), block.getZ())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityChangeToBlock(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof FallingBlock && event.getTo() != Material.AIR) {
            final FallingBlock entity = (FallingBlock) event.getEntity();
            final World world = entity.getWorld();
            final Location location = entity.getLocation();

            if (!hasRegion(world, location.getX(), location.getY(), location.getZ())) {
                @SuppressWarnings("deprecation")
                final ItemStack dropItem = MaterialUtils.getItemStackFromBlock(entity.getMaterial(), entity.getBlockData(), 1);

                location.getWorld().dropItem(location, dropItem);

                event.getEntity().remove();
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        if (event.getAttacker() == null || event.getVehicle() == null) {
            return;
        }

        final Entity attacker = EntityUtils.getSourceEntity(event.getAttacker());
        final World world = attacker.getWorld();
        final Location location = event.getVehicle().getLocation();

        if (attacker instanceof Player) {
            final Player player = (Player) attacker;

            if (!canInteract(RegionInteraction.VEHICLE_DESTROY, world, player, location.getX(), location.getY(), location.getZ())) {
                event.setCancelled(true);
            }
        } else {
            if (!hasRegion(world, location.getX(), location.getY(), location.getZ())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onHangingPlace(HangingPlaceEvent event) {
        final Player player = event.getPlayer();
        final World world = player.getWorld();
        final Location location = event.getEntity().getLocation();

        if (!canInteract(RegionInteraction.HANGING_PLACE, world, player, location.getX(), location.getY(), location.getZ())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onHangningBreak(HangingBreakEvent event) {
        if (event.getCause() == RemoveCause.ENTITY) {
            // Handle by HangingBreakByEntityEvent
            return;
        }

        final World world = event.getEntity().getWorld();
        final Location location = event.getEntity().getLocation();

        if (!hasRegion(world, location.getX(), location.getY(), location.getZ())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (event.getRemover() == null) {
            return;
        }

        final Entity remover = EntityUtils.getSourceEntity(event.getEntity());
        final World world = remover.getWorld();
        final Location location = event.getEntity().getLocation();

        if (remover instanceof Player) {
            final Player player = (Player) remover;

            if (!canInteract(RegionInteraction.HANGING_BREAK, world, player, location.getX(), location.getY(), location.getZ())) {
                event.setCancelled(true);
            }
        } else {
            if (!hasRegion(world, location.getX(), location.getY(), location.getZ())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        final Player player = event.getPlayer();
        final World world = player.getWorld();
        final Entity entity = event.getRightClicked();
        final Location location = entity.getLocation();

        if (EntityUtils.isProtectedEntity(entity.getType())) {
            if (!canInteract(RegionInteraction.ENTITY_INTERACT, world, player, location.getX(), location.getY(), location.getZ())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        onInteractEntity(event);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();

        if (EntityUtils.isProtectedEntity(entity.getType())) {
            final Entity remover = EntityUtils.getSourceEntity(event.getDamager());
            final World world = entity.getWorld();
            final Location location = entity.getLocation();

            if (remover instanceof Player) {
                final Player player = (Player) remover;

                if (!canInteract(RegionInteraction.ENTITY_DAMAGE, world, player, location.getX(), location.getY(), location.getZ())) {
                    event.setCancelled(true);
                }
            } else {
                if (!hasRegion(world, location.getX(), location.getY(), location.getZ())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPVPDamage(EntityDamageByEntityEvent event) {
        final Player target = (Player) event.getEntity();
        final World world = target.getWorld();

        if (!world.getPVP()) {
            return;
        }

        if (target instanceof Player) {
            final Entity damager = EntityUtils.getSourceEntity(event.getDamager());

            if (damager instanceof Player) {
                final Location location = target.getLocation();

                if (!canInteract(RegionInteraction.PVP, world, (Player) damager, location.getX(), location.getY(), location.getZ())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    public boolean canInteract(RegionInteraction type, World world, Player player, double x, double y, double z) {
        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(world.getName());

        if (config == null) {
            return true;
        }

        final boolean result = config.getRegionContainer().canInteract(type, player, x, y, z);

        if (!result) {
            sendWarningMessage(player, type);
        }

        return result;
    }

    public boolean hasRegion(World world, double x, double y, double z) {
        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(world.getName());

        if (config == null) {
            return true;
        }

        return config.getRegionContainer().hasRegion(x, y, z);
    }

    public void sendWarningMessage(Player player, RegionInteraction type) {
        final Long lastTime = (Long) MetadataUtil.get(player, "lastWarningMessage");

        if (lastTime == null || System.currentTimeMillis() - lastTime.longValue() >= 500L) {
            final String message;

            switch (type) {
            case PVP:
                message = ProtectorPlugin.getPluginConfig().pvpWarningMessage;
                break;
            default:
                message = ProtectorPlugin.getPluginConfig().warningMessage;
                break;
            }

            player.sendMessage(message);

            MetadataUtil.set(player, "lastWarningMessage", Long.valueOf(System.currentTimeMillis()));
        }
    }

}
