package jcraft.wp.util;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.projectiles.ProjectileSource;

public class EntityUtils {

    public static Entity getSourceEntity(Entity entity) {
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

    public static boolean isProtectedEntity(EntityType type) {
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

}
