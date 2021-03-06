package jcraft.wp.region;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import jcraft.wp.region.flag.RegionFlag;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;

public class Region implements Comparable<Region> {

    private BlockVector min;
    private BlockVector max;

    private final String name;
    private String permission;
    private Set<RegionPlayer> owners;
    private Set<RegionPlayer> members;
    private Map<RegionFlag, Object> flags;
    private Region parent;

    public Region(String name, BlockVector min, BlockVector max) {
        this.name = name;
        this.min = min;
        this.max = max;
    }

    public String getName() {
        return name;
    }

    public BlockVector getMinPoint() {
        return min;
    }

    public void setMinPoint(BlockVector position) {
        this.min = position;
    }

    public BlockVector getMaxPoint() {
        return max;
    }

    public void setMaxPoint(BlockVector position) {
        this.max = position;
    }

    public Set<RegionPlayer> getOwners() {
        if (this.owners == null) {
            this.owners = new HashSet<RegionPlayer>();
        }

        return owners;
    }

    public boolean addOwner(RegionPlayer player) {
        if (this.isOwner(player.getUniqueId())) {
            return false;
        }

        this.getOwners().add(player);
        return true;
    }

    public boolean removeOwner(String playerName) {
        for (RegionPlayer player : this.getOwners()) {
            if (player.getPlayerName().equalsIgnoreCase(playerName)) {
                this.getOwners().remove(player);
                return true;
            }
        }

        return false;
    }

    public boolean removeOwner(UUID playerUUID) {
        for (RegionPlayer player : this.getOwners()) {
            if (player.getUniqueId().equals(playerUUID)) {
                this.getOwners().remove(player);
                return true;
            }
        }

        return false;
    }

    public boolean hasOwners() {
        return owners != null && !owners.isEmpty();
    }

    public boolean isOwner(String playerName) {
        if (owners != null && !owners.isEmpty()) {
            for (RegionPlayer player : owners) {
                if (player.getPlayerName().equalsIgnoreCase(playerName)) {
                    return true;
                }
            }
        }

        final Region parentRegion = this.getParent();

        if (parentRegion != null) {
            if (parentRegion.isOwner(playerName)) {
                return true;
            }
        }

        return false;
    }

    public boolean isOwner(UUID playerUUID) {
        if (owners != null && !owners.isEmpty()) {
            for (RegionPlayer player : owners) {
                if (player.getUniqueId().equals(playerUUID)) {
                    return true;
                }
            }
        }

        final Region parentRegion = this.getParent();

        if (parentRegion != null) {
            if (parentRegion.isOwner(playerUUID)) {
                return true;
            }
        }

        return false;
    }

    public Set<RegionPlayer> getMembers() {
        if (this.members == null) {
            this.members = new HashSet<RegionPlayer>();
        }

        return members;
    }

    public boolean addMember(RegionPlayer player) {
        if (isMember(player.getUniqueId())) {
            return false;
        }

        this.getMembers().add(player);
        return true;
    }

    public boolean removeMember(String playerName) {
        for (RegionPlayer player : this.getMembers()) {
            if (player.getPlayerName().equalsIgnoreCase(playerName)) {
                this.getMembers().remove(player);
                return true;
            }
        }

        return false;
    }

    public boolean removeMember(UUID playerUUID) {
        for (RegionPlayer player : this.getMembers()) {
            if (player.getUniqueId().equals(playerUUID)) {
                this.getMembers().remove(player);
                return true;
            }
        }

        return false;
    }

    public boolean hasMembers() {
        return members != null && !members.isEmpty();
    }

    public boolean isMember(String playerName) {
        if (members != null && !members.isEmpty()) {
            for (RegionPlayer player : members) {
                if (player.getPlayerName().equalsIgnoreCase(playerName)) {
                    return true;
                }
            }
        }

        final Region parentRegion = this.getParent();

        if (parentRegion != null) {
            if (parentRegion.isMember(playerName)) {
                return true;
            }
        }

        return false;
    }

    public boolean isMember(UUID playerUUID) {
        if (members != null && !members.isEmpty()) {
            for (RegionPlayer player : members) {
                if (player.getUniqueId().equals(playerUUID)) {
                    return true;
                }
            }
        }

        final Region parentRegion = this.getParent();

        if (parentRegion != null) {
            if (parentRegion.isMember(playerUUID)) {
                return true;
            }
        }

        return false;
    }

    public boolean isOwnerOrMember(String playerName) {
        return isOwner(playerName) || isMember(playerName);
    }

    public boolean isOwnerOrMember(UUID playerUUID) {
        return isOwner(playerUUID) || isMember(playerUUID);
    }

    public String getPermission() {
        return this.permission;
    }

    public boolean hasPermission(Player player) {
        if (this.permission != null && this.permission.length() > 0) {
            if (player.hasPermission(this.permission)) {
                return true;
            }
        }

        final Region parentRegion = this.getParent();

        if (parentRegion != null) {
            if (parentRegion.hasPermission(player)) {
                return true;
            }
        }

        return false;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public Map<RegionFlag, Object> getFlags() {
        if (this.flags == null) {
            this.flags = new HashMap<RegionFlag, Object>();
        }

        return this.flags;
    }

    public boolean hasFlag(RegionFlag flag) {
        if (this.flags == null || this.flags.isEmpty()) {
            return false;
        }

        return getFlags().containsKey(flag);
    }

    public Object getFlag(RegionFlag flag) {
        if (this.flags == null || this.flags.isEmpty()) {
            return null;
        }

        final Object regionFlag = getFlags().get(flag);

        if (regionFlag != null) {
            return regionFlag;
        }

        final Region parentRegion = this.getParent();

        if (parentRegion != null) {
            final Object parentRegionFlag = parentRegion.getFlag(flag);

            if (parentRegionFlag != null) {
                return parentRegionFlag;
            }
        }

        return null;
    }

    public void addFlag(RegionFlag flag, Object state) {
        getFlags().put(flag, state);
    }

    public boolean removeFlag(RegionFlag flag) {
        return getFlags().remove(flag) != null;
    }

    public Region getParent() {
        return this.parent;
    }

    public boolean setParent(Region region) {
        if (region == null) {
            this.parent = null;
            return true;
        }

        if (region == this) {
            return false;
        }

        Region regionParent = region.getParent();

        while (regionParent != null) {
            if (regionParent == this) {
                return false;
            }

            regionParent = regionParent.getParent();
        }

        this.parent = region;

        return true;
    }

    public Set<Long> getHash() {
        final Set<Long> regionHash = new HashSet<Long>();

        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                regionHash.add(RegionContainer.hashPosition(x, z));
            }
        }

        return regionHash;
    }

    public boolean contains(double x, double y, double z) {
        return x >= min.getBlockX() && x < max.getBlockX() + 1 && y >= min.getBlockY() && y < max.getBlockY() + 1 && z >= min.getBlockZ()
                && z < max.getBlockZ() + 1;
    }

    public boolean canInteract(Player player) {
        return canInteract(RegionInteraction.NONE, player);
    }

    public boolean canInteract(RegionInteraction type, Player player) {
        final Boolean buildFlag = (Boolean) this.getFlag(DefaultFlags.BUILD_FLAG);

        if (buildFlag != null) {
            return buildFlag;
        }

        if (this.isOwnerOrMember(player.getUniqueId())) {
            return true;
        }

        if (this.hasPermission(player) || player.hasPermission("worldprotector.region.bypass")) {
            return true;
        }

        return false;
    }

    public String getInfo() {
        final StringBuilder builder = new StringBuilder();

        builder.append(ChatColor.GRAY);
        builder.append("\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550");
        builder.append(" REGION INFO ");
        builder.append("\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550").append('\n');

        builder.append(ChatColor.BLUE).append(" Region: ").append(ChatColor.YELLOW).append(this.getName()).append('\n');
        builder.append(ChatColor.BLUE).append(" Permission: ");

        if (this.getPermission() != null && this.getPermission().length() > 0) {
            builder.append(ChatColor.YELLOW).append(this.getPermission()).append('\n');
        } else {
            builder.append(ChatColor.RED).append("None").append('\n');
        }

        builder.append(ChatColor.BLUE).append(" Flags: ");

        if (!getFlags().isEmpty()) {
            for (Entry<RegionFlag, Object> entry : getFlags().entrySet()) {
                builder.append(ChatColor.YELLOW).append(entry.getKey().getName()).append('=').append(entry.getKey().stateToString(entry.getValue()))
                        .append(", ");
            }

            builder.delete(builder.length() - 2, builder.length());
            builder.append('\n');
        } else {
            builder.append(ChatColor.RED).append("None").append('\n');
        }

        builder.append(ChatColor.BLUE).append(" Parent: ");

        if (this.getParent() != null) {
            builder.append(ChatColor.YELLOW).append(this.getParent().getName()).append('\n');
        } else {
            builder.append(ChatColor.RED).append("None").append('\n');
        }

        builder.append(ChatColor.BLUE).append(" Owners: ");

        if (!this.getOwners().isEmpty()) {
            for (RegionPlayer player : this.getOwners()) {
                builder.append(ChatColor.YELLOW).append(player.getPlayerName()).append(ChatColor.GRAY).append(" (").append(player.getUniqueId())
                        .append(')').append(ChatColor.YELLOW).append(", ");
            }

            builder.delete(builder.length() - 4, builder.length());
            builder.append('\n');
        } else {
            builder.append(ChatColor.RED).append("None").append('\n');
        }

        builder.append(ChatColor.BLUE).append(" Members: ");

        if (!this.getMembers().isEmpty()) {
            for (RegionPlayer player : this.getMembers()) {
                builder.append(ChatColor.YELLOW).append(player.getPlayerName()).append(ChatColor.GRAY).append(" (").append(player.getUniqueId())
                        .append(')').append(ChatColor.YELLOW).append(", ");
            }

            builder.delete(builder.length() - 4, builder.length());
            builder.append('\n');
        } else {
            builder.append(ChatColor.RED).append("None").append('\n');
        }

        builder.append(ChatColor.BLUE).append(" Bounds: ").append(ChatColor.YELLOW);
        builder.append('(').append(this.getMinPoint().getBlockX()).append(',').append(this.getMinPoint().getBlockY()).append(',')
                .append(this.getMinPoint().getBlockZ()).append(')');
        builder.append(" -> ");
        builder.append('(').append(this.getMaxPoint().getBlockX()).append(',').append(this.getMaxPoint().getBlockY()).append(',')
                .append(this.getMaxPoint().getBlockZ()).append(')');

        return builder.toString();
    }

    @Override
    public int compareTo(Region compareRegion) {
        if (this.getParent() != null && compareRegion.getParent() == null) {
            return -1;
        }

        if (this.getParent() == null && compareRegion.getParent() != null) {
            return 1;
        }

        return 0;
    }

}
