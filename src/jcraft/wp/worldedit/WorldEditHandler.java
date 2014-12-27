package jcraft.wp.worldedit;

import jcraft.wp.region.Region;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BlockVector;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class WorldEditHandler {

    private final WorldEditPlugin plugin;

    public WorldEditHandler(Plugin plugin) {
        this.plugin = (WorldEditPlugin) plugin;
    }

    public Selection getSelection(Player player) {
        return plugin.getSelection(player);
    }

    public void setSelection(Player player, BlockVector min, BlockVector max) {
        final com.sk89q.worldedit.Vector weMin = new com.sk89q.worldedit.Vector(min.getBlockX(), min.getBlockY(), min.getBlockZ());
        final com.sk89q.worldedit.Vector weMax = new com.sk89q.worldedit.Vector(max.getBlockX(), max.getBlockY(), max.getBlockZ());
        final CuboidSelection selection = new CuboidSelection(player.getWorld(), weMin, weMax);

        plugin.setSelection(player, selection);
    }

    public BlockVector[] getSelectionPoints(Player player) {
        final Selection selection = getSelection(player);

        if (selection == null) {
            return null;
        }

        if (!selection.getWorld().equals(player.getWorld())) {
            return null;
        }

        final int minX = selection.getMinimumPoint().getBlockX();
        final int minY = selection.getMinimumPoint().getBlockY();
        final int minZ = selection.getMinimumPoint().getBlockZ();

        final int maxX = selection.getMaximumPoint().getBlockX();
        final int maxY = selection.getMaximumPoint().getBlockY();
        final int maxZ = selection.getMaximumPoint().getBlockZ();

        return new BlockVector[] { new BlockVector(minX, minY, minZ), new BlockVector(maxX, maxY, maxZ) };
    }

    public Region getRegion(String name, Player player) {
        final BlockVector[] points = getSelectionPoints(player);

        if (points == null) {
            return null;
        }

        return new Region(name, points[0], points[1]);
    }

}
