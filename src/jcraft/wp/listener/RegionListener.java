package jcraft.wp.listener;

import jcraft.wp.ProtectorPlugin;
import jcraft.wp.WorldInstance;
import jcraft.wp.region.RegionInteraction;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
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
import org.bukkit.projectiles.ProjectileSource;

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
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        if (event.getAttacker() == null || event.getVehicle() == null) {
            return;
        }

        final Entity attacker = getSourceEntity(event.getAttacker());
        final World world = attacker.getWorld();
        final Location location = event.getVehicle().getLocation();

        if (attacker instanceof Player) {
            final Player player = (Player) attacker;

            if (!canInteract(RegionInteraction.VEHICLE_DESTROY, world, player, location.getX(), location.getY(), location.getZ())) {
                event.setCancelled(true);
            }
        } else {
            if (!canInteract(RegionInteraction.VEHICLE_DESTROY, world, location.getX(), location.getY(), location.getZ())) {
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

        if (!canInteract(RegionInteraction.HANGING_BREAK, world, location.getX(), location.getY(), location.getZ())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (event.getRemover() == null) {
            return;
        }

        final Entity remover = getSourceEntity(event.getEntity());
        final World world = remover.getWorld();
        final Location location = event.getEntity().getLocation();

        if (remover instanceof Player) {
            final Player player = (Player) remover;

            if (!canInteract(RegionInteraction.HANGING_BREAK, world, player, location.getX(), location.getY(), location.getZ())) {
                event.setCancelled(true);
            }
        } else {
            if (!canInteract(RegionInteraction.HANGING_BREAK, world, location.getX(), location.getY(), location.getZ())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteractBlock(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final World world = player.getWorld();
        final Block block = event.getClickedBlock();

        if (isInteractiveMaterialBlock(block.getType()) || isInteractiveMaterialItem(player.getItemInHand().getType())) {
            if (!canInteract(RegionInteraction.BLOCK_INTERACT, world, player, block.getX(), block.getY(), block.getZ())) {
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

        if (isProtectedEntity(entity.getType())) {
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

        if (isProtectedEntity(entity.getType())) {
            final Entity remover = getSourceEntity(event.getDamager());
            final World world = entity.getWorld();
            final Location location = entity.getLocation();

            if (remover instanceof Player) {
                final Player player = (Player) remover;

                if (!canInteract(RegionInteraction.ENTITY_DAMAGE, world, player, location.getX(), location.getY(), location.getZ())) {
                    event.setCancelled(true);
                }
            } else {
                if (!canInteract(RegionInteraction.ENTITY_DAMAGE, world, location.getX(), location.getY(), location.getZ())) {
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
            // TODO: Allow to change message in config
            // TODO: Add delay to message print to avoid message spam and double messages
            player.sendMessage(ChatColor.RED + "Hey! " + ChatColor.GRAY + "Interactions in this region are blocked (cuboid).");
        }

        return result;
    }

    public boolean canInteract(RegionInteraction type, World world, double x, double y, double z) {
        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(world.getName());

        if (config == null) {
            return true;
        }

        return config.getRegionContainer().canInteract(type, x, y, z);
    }

    public Entity getSourceEntity(Entity entity) {
        if (entity instanceof Projectile && ((Projectile) entity).getShooter() != null) {
            final ProjectileSource shooter = ((Projectile) entity).getShooter();

            return (Entity) shooter;
        } else if (entity instanceof TNTPrimed && ((TNTPrimed) entity).getSource() != null) {
            final Entity source = ((TNTPrimed) entity).getSource();

            if (source instanceof Projectile && ((Projectile) entity).getShooter() != null) {
                return getSourceEntity(source);
            }

            return source;
        }

        return entity;
    }

    public boolean isProtectedEntity(EntityType type) {
        switch (type) {
        case ARMOR_STAND:
        case BOAT:
        case CHICKEN:
        case COW:
        case HORSE:
        case ITEM_FRAME:
        case LEASH_HITCH:
        case MINECART:
        case MINECART_CHEST:
        case MINECART_COMMAND:
        case MINECART_FURNACE:
        case MINECART_HOPPER:
        case MINECART_MOB_SPAWNER:
        case MINECART_TNT:
        case MUSHROOM_COW:
        case OCELOT:
        case PAINTING:
        case PIG:
        case RABBIT:
        case SHEEP:
        case SNOWMAN:
        case VILLAGER:
        case WOLF:
            return true;
        default:
            return false;
        }
    }

    public boolean isInteractiveMaterialItem(Material material) {
        switch (material) {
        case MINECART:
        case POWERED_MINECART:
        case HOPPER_MINECART:
        case EXPLOSIVE_MINECART:
        case COMMAND_MINECART:
        case BOAT:
        case FLINT_AND_STEEL:
        case WOOD_HOE:
        case STONE_HOE:
        case GOLD_HOE:
        case IRON_HOE:
        case DIAMOND_HOE:
        case BOWL:
        case BUCKET:
        case SADDLE:
        case INK_SACK:
        case ENDER_PEARL:
        case MONSTER_EGG:
        case FIREBALL:
        case ARMOR_STAND:
        case LEASH:
            return true;
        default:
            return false;
        }
    }

    public boolean isInteractiveMaterialBlock(Material material) {
        switch (material) {
        case DISPENSER:
        case DROPPER:
        case NOTE_BLOCK:
        case TNT:
        case CHEST:
        case TRAPPED_CHEST:
        case WORKBENCH:
        case SOIL:
        case FURNACE:
        case BURNING_FURNACE:
        case LEVER:
        case REDSTONE_ORE:
        case STONE_BUTTON:
        case WOOD_BUTTON:
        case JUKEBOX:
        case TRAP_DOOR:
        case FENCE_GATE:
        case DARK_OAK_FENCE_GATE:
        case SPRUCE_FENCE_GATE:
        case BIRCH_FENCE_GATE:
        case ACACIA_FENCE_GATE:
        case JUNGLE_FENCE_GATE:
        case ENCHANTMENT_TABLE:
        case ENDER_PORTAL_FRAME:
        case DRAGON_EGG:
        case ENDER_CHEST:
        case COMMAND:
        case BEACON:
        case ANVIL:
        case DAYLIGHT_DETECTOR:
        case DAYLIGHT_DETECTOR_INVERTED:
        case HOPPER:
        case BED_BLOCK:
        case BREWING_STAND:
        case CAKE_BLOCK:
        case DIODE_BLOCK_ON:
        case DIODE_BLOCK_OFF:
        case REDSTONE_COMPARATOR_ON:
        case REDSTONE_COMPARATOR_OFF:
        case WOODEN_DOOR:
        case DARK_OAK_DOOR:
        case ACACIA_DOOR:
        case SPRUCE_DOOR:
        case BIRCH_DOOR:
        case JUNGLE_DOOR:
        case WOOD_PLATE:
        case STONE_PLATE:
        case GOLD_PLATE:
        case IRON_PLATE:
            return true;
        default:
            return false;
        }
    }

}
