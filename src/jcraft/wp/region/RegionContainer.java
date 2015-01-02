package jcraft.wp.region;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jcraft.wp.ProtectorPlugin;
import jcraft.wp.config.YamlHandler;
import jcraft.wp.region.flag.RegionFlag;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;

public class RegionContainer extends YamlHandler {

    public RegionContainer(File file) {
        super(file);
    }

    private final Set<Region> regionsList = new HashSet<Region>();
    private final Map<Long, Set<Region>> regionsFragments = new HashMap<Long, Set<Region>>();

    public boolean hasRegion(String name) {
        for (Region region : regionsList) {
            if (region.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    public Region getRegion(String name) {
        for (Region region : regionsList) {
            if (region.getName().equalsIgnoreCase(name)) {
                return region;
            }
        }

        return null;
    }

    public Set<Region> getRegions(double x, double y, double z) {
        final Set<Region> foundRegions = new HashSet<Region>();
        final long regionHash = hashPosition((int) x, (int) z);
        final Set<Region> regions = regionsFragments.get(regionHash);

        if (regions == null || regions.isEmpty()) {
            return foundRegions;
        }

        for (Region region : regions) {
            if (region.contains(x, y, z)) {
                foundRegions.add(region);
            }
        }

        return foundRegions;
    }

    public void addRegion(Region region) {
        final Set<Long> regionHash = region.getHash();

        for (Long hash : regionHash) {
            if (!regionsFragments.containsKey(hash)) {
                regionsFragments.put(hash, new HashSet<Region>());
            }

            regionsFragments.get(hash).add(region);
        }

        regionsList.add(region);
    }

    public boolean removeRegion(String name) {
        Region removeRegion = null;

        for (Region region : regionsList) {
            if (region.getName().equalsIgnoreCase(name)) {
                removeRegion = region;
                break;
            }
        }

        if (removeRegion == null) {
            return false;
        }

        final long regionHash = hashPosition(removeRegion.getMinPoint().getBlockX() + 1, removeRegion.getMinPoint().getBlockZ() + 1);
        final Set<Region> regions = regionsFragments.get(regionHash);

        if (regions == null || regions.isEmpty()) {
            return false;
        }

        regions.remove(removeRegion);
        regionsFragments.put(regionHash, regions);

        for (Region region : regionsList) {
            if (region.getParent() != null && region.getParent().equals(removeRegion)) {
                region.setParent(null);
            }
        }

        regionsList.remove(removeRegion);

        return true;
    }

    public boolean removeRegion(Region removeRegion) {
        final long regionHash = hashPosition(removeRegion.getMinPoint().getBlockX() + 1, removeRegion.getMinPoint().getBlockZ() + 1);
        final Set<Region> regions = regionsFragments.get(regionHash);

        if (regions == null || regions.isEmpty()) {
            return false;
        }

        regions.remove(removeRegion);
        regionsFragments.put(regionHash, regions);

        for (Region region : regionsList) {
            if (region.getParent() != null && region.getParent().equals(removeRegion)) {
                region.setParent(null);
            }
        }

        regionsList.remove(removeRegion);

        return true;
    }

    public int size() {
        return regionsList.size();
    }

    public boolean canInteract(Player player, double x, double y, double z) {
        return canInteract(player, x, y, z);
    }

    public boolean canInteract(RegionInteraction type, Player player, double x, double y, double z) {
        final long regionHash = hashPosition((int) x, (int) z);
        final Set<Region> regions = regionsFragments.get(regionHash);

        if (regions == null || regions.isEmpty()) {
            return true;
        }

        for (Region region : regions) {
            if (region.contains(x, y, z)) {
                if (!region.canInteract(type, player)) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean hasRegion(double x, double y, double z) {
        final long regionHash = hashPosition((int) x, (int) z);
        final Set<Region> regions = regionsFragments.get(regionHash);

        if (regions == null || regions.isEmpty()) {
            return true;
        }

        for (Region region : regions) {
            if (region.contains(x, y, z)) {
                return false;
            }
        }

        return true;
    }

    public String getList() {
        final StringBuilder builder = new StringBuilder();

        builder.append(ChatColor.BLUE).append("Regions: ").append(ChatColor.YELLOW);

        if (!regionsList.isEmpty()) {
            for (Region region : regionsList) {
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

        for (Region region : regionsList) {
            String regionPath = "regions." + region.getName();

            yaml.set(regionPath + ".min.x", region.getMinPoint().getBlockX());
            yaml.set(regionPath + ".min.y", region.getMinPoint().getBlockY());
            yaml.set(regionPath + ".min.z", region.getMinPoint().getBlockZ());

            yaml.set(regionPath + ".max.x", region.getMaxPoint().getBlockX());
            yaml.set(regionPath + ".max.y", region.getMaxPoint().getBlockY());
            yaml.set(regionPath + ".max.z", region.getMaxPoint().getBlockZ());

            yaml.set(regionPath + ".permission", region.getPermission());

            yaml.set(regionPath + ".parent", ((region.getParent() != null) ? region.getParent().getName() : null));

            for (RegionPlayer rPlayer : region.getOwners()) {
                yaml.set(regionPath + ".owners." + rPlayer.getUniqueId().toString() + ".playerName", rPlayer.getPlayerName());
            }

            for (RegionPlayer rPlayer : region.getMembers()) {
                yaml.set(regionPath + ".members." + rPlayer.getUniqueId().toString() + ".playerName", rPlayer.getPlayerName());
            }

            for (Entry<RegionFlag, Object> entry : region.getFlags().entrySet()) {
                yaml.set(regionPath + ".flags." + entry.getKey().getName() + ".state", entry.getKey().stateToString(entry.getValue()));
            }

        }

        try {
            yaml.save(this.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load() {
        regionsList.clear();
        regionsFragments.clear();

        final YamlConfiguration yaml = YamlConfiguration.loadConfiguration(this.getFile());

        if (!yaml.contains("regions")) {
            return;
        }

        final Set<String> regionNames = yaml.getConfigurationSection("regions").getKeys(false);

        for (String regionName : regionNames) {
            Region region = loadRegion(regionName, yaml);

            if (region == null) {
                continue;
            }

            if (this.hasRegion(region.getName())) {
                // If region is already loaded, skip it
                continue;
            }

            this.addRegion(region);
        }
    }

    private Region loadRegion(String regionName, YamlConfiguration yaml) {
        final String regionPath = "regions." + regionName;

        if (!yaml.contains(regionPath)) {
            return null;
        }

        // Min point

        int minX = yaml.getInt(regionPath + ".min.x");
        int minY = yaml.getInt(regionPath + ".min.y");
        int minZ = yaml.getInt(regionPath + ".min.z");

        BlockVector min = new BlockVector(minX, minY, minZ);

        // Max point

        int maxX = yaml.getInt(regionPath + ".max.x");
        int maxY = yaml.getInt(regionPath + ".max.y");
        int maxZ = yaml.getInt(regionPath + ".max.z");

        BlockVector max = new BlockVector(maxX, maxY, maxZ);

        // Permission

        String permission = yaml.getString(regionPath + ".permission");

        // Parent

        String parentName = yaml.getString(regionPath + ".parent");
        Region parentRegion = null;

        if (parentName != null) {
            parentRegion = this.getRegion(parentName); // Try to get parent region

            if (parentRegion == null) { // Region is not loaded
                parentRegion = loadRegion(parentName, yaml); // Try loading
            }

            if (parentRegion != null) {
                this.addRegion(parentRegion); // If loaded parent region, add it
            }
        }

        // Owners

        Set<RegionPlayer> owners = new HashSet<RegionPlayer>();

        Set<String> ownersList = (yaml.contains(regionPath + ".owners") ? yaml.getConfigurationSection(regionPath + ".owners").getKeys(false) : null);

        if (ownersList != null) {
            for (String ownerUUID : ownersList) {
                String ownerName = yaml.getString(regionPath + ".owners." + ownerUUID + ".playerName");

                owners.add(new RegionPlayer(ownerName, ownerUUID));
            }
        }

        // Members

        Set<RegionPlayer> members = new HashSet<RegionPlayer>();
        Set<String> membersList = (yaml.contains(regionPath + ".members") ? yaml.getConfigurationSection(regionPath + ".members").getKeys(false)
                : null);

        if (membersList != null) {
            for (String memberUUID : membersList) {
                String memberName = yaml.getString(regionPath + ".members." + memberUUID + ".playerName");

                members.add(new RegionPlayer(memberName, memberUUID));
            }
        }

        // Flags

        Map<RegionFlag, Object> flags = new HashMap<RegionFlag, Object>();
        Set<String> flagsList = (yaml.contains(regionPath + ".flags") ? yaml.getConfigurationSection(regionPath + ".flags").getKeys(false) : null);

        if (flagsList != null) {
            for (String flagName : flagsList) {
                RegionFlag flag = ProtectorPlugin.getRegionFlagManager().getFlag(flagName);

                if (flag == null) {
                    continue;
                }

                String sState = yaml.getString(regionPath + ".flags." + flagName + ".state");

                Object flagState = flag.parseState(sState);

                if (flagState == null) {
                    continue;
                }

                flags.put(flag, flagState);
            }
        }

        // Setup

        Region region = new Region(regionName, min, max);

        if (permission != null && permission.length() > 0) {
            region.setPermission(permission);
        }

        if (parentRegion != null) {
            region.setParent(parentRegion);
        }

        if (!owners.isEmpty()) {
            region.getOwners().addAll(owners);
        }

        if (!members.isEmpty()) {
            region.getMembers().addAll(members);
        }

        if (!flags.isEmpty()) {
            region.getFlags().putAll(flags);
        }

        return region;
    }

    public static long hashPosition(int x, int z) {
        int regionX = x >> 8; // x / 256
        int regionZ = z >> 8; // z / 256

        long hash = 0xBB40E64DA205B064L;
        final long modifier = 7664345821815920749L;

        hash = (hash * modifier) ^ (regionX & 0xff);
        hash = (hash * modifier) ^ (regionZ & 0xff);

        return hash;
    }

}
