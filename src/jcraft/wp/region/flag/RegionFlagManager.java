package jcraft.wp.region.flag;

import java.util.HashMap;
import java.util.Map;

import jcraft.wp.ProtectorPlugin;
import jcraft.wp.region.DefaultFlags;

import org.bukkit.ChatColor;

public class RegionFlagManager {

    private final Map<String, RegionFlag> flags = new HashMap<String, RegionFlag>();

    private boolean reloadRegionsOnRegister = false;

    public RegionFlagManager() {
        this.registerFlag(DefaultFlags.BUILD_FLAG);

        this.reloadRegionsOnRegister = true;
    }

    public void registerFlag(RegionFlag flag) {
        if (flags.containsKey(flag.getName())) {
            return;
        }

        flags.put(flag.getName(), flag);

        if (reloadRegionsOnRegister) {
            ProtectorPlugin.getWorldsManager().reloadWorldRegions();
        }
    }

    public RegionFlag getFlag(String name) {
        return flags.get(name.toLowerCase());
    }

    public String getList() {
        final StringBuilder builder = new StringBuilder();

        builder.append(ChatColor.BLUE).append("Flags: ").append(ChatColor.YELLOW);

        if (!flags.isEmpty()) {
            for (String flagName : flags.keySet()) {
                builder.append(flagName).append(", ");
            }

            builder.delete(builder.length() - 2, builder.length());
        } else {
            builder.append(ChatColor.RED).append("None");
        }

        return builder.toString();
    }

}
