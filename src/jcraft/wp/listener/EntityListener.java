package jcraft.wp.listener;

import jcraft.wp.ProtectorPlugin;
import jcraft.wp.WorldInstance;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkull;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

public class EntityListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(entity.getWorld().getName());

        if (config == null) {
            return;
        }

        final Entity damager = event.getDamager();

        if (entity instanceof ItemFrame) {
            if (config.getWorldSettings().disableItemFrameBreaking && !(damager instanceof Player)) {
                event.setCancelled(true);
                return;
            }
        } else if (entity instanceof ArmorStand) {
            if (config.getWorldSettings().disableArmorStandBreaking && !(damager instanceof Player)) {
                event.setCancelled(true);
                // Set velocity downwards to minimize armor stand movement
                entity.setVelocity(new Vector(0, -100, 0));
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onHangingBreak(HangingBreakEvent event) {
        final Hanging hanging = event.getEntity();
        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(hanging.getWorld().getName());

        if (config == null) {
            return;
        }

        if (event instanceof HangingBreakByEntityEvent) {
            final HangingBreakByEntityEvent entityEvent = (HangingBreakByEntityEvent) event;

            Entity remover = entityEvent.getRemover();

            if (remover instanceof Projectile) {
                final Projectile projectile = (Projectile) remover;
                final ProjectileSource removerEntity = projectile.getShooter();

                remover = ((removerEntity instanceof LivingEntity) ? (LivingEntity) removerEntity : null);
            }

            if (!(remover instanceof Player)) {
                if (hanging instanceof Painting && config.getWorldSettings().disablePaintingBreaking) {
                    event.setCancelled(true);
                } else if (hanging instanceof ItemFrame && config.getWorldSettings().disableItemFrameBreaking) {
                    event.setCancelled(true);
                }
            }
        } else if (event.getCause() == RemoveCause.EXPLOSION) {
            if (hanging instanceof Painting && config.getWorldSettings().disablePaintingBreaking) {
                event.setCancelled(true);
            } else if (hanging instanceof ItemFrame && config.getWorldSettings().disableItemFrameBreaking) {
                event.setCancelled(true);
            }
        }
    }

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

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(event.getLocation().getWorld().getName());

        if (config == null) {
            return;
        }

        final Entity entity = event.getEntity();

        if (entity instanceof Creeper) {
            if (config.getWorldSettings().disableCreeperExplosion) {
                event.setCancelled(true);
                return;
            }

            if (config.getWorldSettings().disableCreeperBlockDamage) {
                event.blockList().clear();
                return;
            }
        } else if (entity instanceof EnderDragon) {
            if (config.getWorldSettings().disableEnderDragonBlockDamage) {
                event.blockList().clear();
                return;
            }
        } else if (entity instanceof TNTPrimed || entity instanceof ExplosiveMinecart) {
            if (config.getWorldSettings().disableTntExplosion) {
                event.setCancelled(true);
                return;
            }

            if (config.getWorldSettings().disableTntBlockDamage) {
                event.blockList().clear();
                return;
            }
        } else if (entity instanceof Fireball) {
            if (entity instanceof WitherSkull) {
                if (config.getWorldSettings().disableWitherSkullExplosion) {
                    event.setCancelled(true);
                    return;
                }

                if (config.getWorldSettings().disableWitherSkullBlockDamage) {
                    event.blockList().clear();
                    return;
                }
            } else {
                if (config.getWorldSettings().disableFireballExplosion) {
                    event.setCancelled(true);
                    return;
                }

                if (config.getWorldSettings().disableFireballBlockDamage) {
                    event.blockList().clear();
                    return;
                }
            }
        } else if (entity instanceof Wither) {
            if (config.getWorldSettings().disableWitherExplosion) {
                event.setCancelled(true);
                return;
            }

            if (config.getWorldSettings().disableWitherBlockDamage) {
                event.blockList().clear();
                return;
            }
        } else {
            if (config.getWorldSettings().disableOtherEntityExplosionDamage) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(event.getEntity().getWorld().getName());

        if (config == null) {
            return;
        }

        if (event.getEntityType() == EntityType.WITHER) {
            if (config.getWorldSettings().disableWitherExplosion) {
                event.setCancelled(true);
                return;
            }
        } else if (event.getEntityType() == EntityType.WITHER_SKULL) {
            if (config.getWorldSettings().disableWitherSkullExplosion) {
                event.setCancelled(true);
                return;
            }
        } else if (event.getEntityType() == EntityType.FIREBALL) {
            if (config.getWorldSettings().disableFireballExplosion) {
                event.setCancelled(true);
                return;
            }
        } else if (event.getEntityType() == EntityType.CREEPER) {
            if (config.getWorldSettings().disableCreeperExplosion) {
                event.setCancelled(true);
                return;
            }
        } else if (event.getEntityType() == EntityType.PRIMED_TNT || event.getEntityType() == EntityType.MINECART_TNT) {
            if (config.getWorldSettings().disableTntExplosion) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityBlockChange(EntityChangeBlockEvent event) {
        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(event.getEntity().getWorld().getName());

        if (config == null) {
            return;
        }

        if (event.getEntityType() == EntityType.WITHER) {
            if (config.getWorldSettings().disableWitherBlockDamage) {
                event.setCancelled(true);
                return;
            }
        } else if (event.getEntityType() == EntityType.SILVERFISH && event.getTo() != Material.AIR) {
            if (config.getWorldSettings().disableSilverfishHideInBlock) {
                event.setCancelled(true);
                return;
            }
        } else if (event.getEntityType() == EntityType.ENDERMAN) {
            if (config.getWorldSettings().disableEndermanBlockGrief) {
                event.setCancelled(true);
                return;
            }
        } else if (event.getEntityType() == EntityType.ZOMBIE && event instanceof EntityBreakDoorEvent) {
            if (config.getWorldSettings().disableZombieDoorBreaking) {
                event.setCancelled(true);
                return;
            }
        }
    }

}
