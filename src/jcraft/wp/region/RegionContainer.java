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

        boolean canInteract = true;

        for (Region region : regions) {
            if (region.contains(x, y, z)) {
                if (!region.canInteract(player)) {
                    canInteract = false;
                }
            }
        }

        return canInteract;
    }

    public boolean canInteract(double x, double y, double z) {
        return canInteract(RegionInteraction.NONE, x, y, z);
    }

    public boolean canInteract(RegionInteraction type, double x, double y, double z) {
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

            for (RegionPlayer rPlayer : region.getOwners()) {
                yaml.set(regionPath + ".owners." + rPlayer.getUniqueId().toString() + ".playerName", rPlayer.getPlayerName());
            }

            for (RegionPlayer rPlayer : region.getMembers()) {
                yaml.set(regionPath + ".members." + rPlayer.getUniqueId().toString() + ".playerName", rPlayer.getPlayerName());
            }

            for (Entry<RegionFlag, Object> entry : region.getFlags().entrySet()) {
                yaml.set(regionPath + ".flags." + entry.getKey().getName() + ".state", entry.getKey().stateToString(entry.getValue()));
            }

            // TODO Parent
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

            if (!flags.isEmpty()) {
                region.getFlags().putAll(flags);
            }

            this.addRegion(region);
        }
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
