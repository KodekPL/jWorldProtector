package jcraft.wp.util;

import org.bukkit.Material;

public class MaterialUtils {

    public static boolean isInteractiveMaterialItem(Material material) {
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

    public static boolean isInteractiveMaterialBlock(Material material) {
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
