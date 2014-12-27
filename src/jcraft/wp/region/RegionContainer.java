package jcraft.wp.region;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import jcraft.wp.config.YamlHandler;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.BlockVector;

public class RegionContainer extends YamlHandler {

    public RegionContainer(File file) {
        super(file);
    }

    // TODO: Redo the way regions are get
    private final Set<Region> regions = new HashSet<Region>();

    public boolean hasRegion(String name) {
        for (Region region : regions) {
            if (region.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    public Region getRegion(String name) {
        for (Region region : regions) {
            if (region.getName().equalsIgnoreCase(name)) {
                return region;
            }
        }

        return null;
    }

    public void addRegion(Region region) {
        regions.add(region);
    }

    public boolean removeRegion(String name) {
        for (Region region : regions) {
            if (region.getName().equalsIgnoreCase(name)) {
                regions.remove(region);
                return true;
            }
        }

        return false;
    }

    public boolean removeRegion(Region region) {
        return regions.remove(region);
    }

    public int size() {
        return regions.size();
    }

    public String getList() {
        final StringBuilder builder = new StringBuilder();

        builder.append(ChatColor.BLUE).append("Regions: ").append(ChatColor.YELLOW);

        if (!regions.isEmpty()) {
            for (Region region : regions) {
                builder.append(region.getName()).append(", ");
            }

            builder.delete(builder.length() - 2, builder.length());
        } else {
            builder.append(ChatColor.RED).append("None");
        }

        return builder.toString();
    }

    @Override
    public void save() {
        final YamlConfiguration yaml = new YamlConfiguration();

        for (Region region : regions) {
            String regionPath = "regions." + region.getName();

            yaml.set(regionPath + ".min.x", region.getMinPoint().getBlockX());
            yaml.set(regionPath + ".min.y", region.getMinPoint().getBlockY());
            yaml.set(regionPath + ".min.z", region.getMinPoint().getBlockZ());

            yaml.set(regionPath + ".max.x", region.getMaxPoint().getBlockX());
            yaml.set(regionPath + ".max.y", region.getMaxPoint().getBlockY());
            yaml.set(regionPath + ".max.z", region.getMaxPoint().getBlockZ());

            yaml.set(regionPath + ".permission", region.getPermission());

            for (RegionPlayer rPlayer : region.getOwners()) {
                yaml.set(regionPath + ".owners." + rPlayer.getUniqueId().toString() + ".playerName", rPlayer.getPlayerName());
            }

            for (RegionPlayer rPlayer : region.getMembers()) {
                yaml.set(regionPath + ".members." + rPlayer.getUniqueId().toString() + ".playerName", rPlayer.getPlayerName());
            }

            // TODO Parent
            // TODO Flags
        }

        try {
            yaml.save(this.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load() {
        regions.clear();

        final YamlConfiguration yaml = YamlConfiguration.loadConfiguration(this.getFile());

        if (!yaml.contains("regions")) {
            return;
        }

        final Set<String> regionNames = yaml.getConfigurationSection("regions").getKeys(false);

        for (String regionName : regionNames) {
            String regionPath = "regions." + regionName;

            int minX = yaml.getInt(regionPath + ".min.x");
            int minY = yaml.getInt(regionPath + ".min.y");
            int minZ = yaml.getInt(regionPath + ".min.z");

            BlockVector min = new BlockVector(minX, minY, minZ);

            int maxX = yaml.getInt(regionPath + ".max.x");
            int maxY = yaml.getInt(regionPath + ".max.y");
            int maxZ = yaml.getInt(regionPath + ".max.z");

            BlockVector max = new BlockVector(maxX, maxY, maxZ);

            String permission = yaml.getString(regionPath + ".permission");

            Set<RegionPlayer> owners = new HashSet<RegionPlayer>();

            Set<String> ownersList = (yaml.contains(regionPath + ".owners") ? yaml.getConfigurationSection(regionPath + ".owners").getKeys(false)
                    : null);

            if (ownersList != null) {
                for (String ownerUUID : ownersList) {
                    String ownerName = yaml.getString(regionPath + ".owners." + ownerUUID + ".playerName");

                    owners.add(new RegionPlayer(ownerName, ownerUUID));
                }
            }

            Set<RegionPlayer> members = new HashSet<RegionPlayer>();
            Set<String> membersList = (yaml.contains(regionPath + ".members") ? yaml.getConfigurationSection(regionPath + ".members").getKeys(false)
                    : null);

            if (membersList != null) {
                for (String memberUUID : membersList) {
                    String memberName = yaml.getString(regionPath + ".members." + memberUUID + ".playerName");

                    members.add(new RegionPlayer(memberName, memberUUID));
                }
            }

            Region region = new Region(regionName, min, max);

            if (permission != null && permission.length() > 0) {
                region.setPermission(permission);
            }

            if (!owners.isEmpty()) {
                region.getOwners().addAll(owners);
            }

            if (!members.isEmpty()) {
                region.getMembers().addAll(members);
            }

            regions.add(region);
        }
    }

}
