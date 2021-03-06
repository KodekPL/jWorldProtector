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
        this.registerFlag(DefaultFlags.PVP_FLAG);
        this.registerFlag(DefaultFlags.MOBSPAWN_FLAG);
        this.registerFlag(DefaultFlags.ENDERPEARL_FLAG);
        this.registerFlag(DefaultFlags.CONTAINER_ACCESS_FLAG);
        this.registerFlag(DefaultFlags.BLOCK_INTERACT_FLAG);

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
            for (RegionFlag flag : flags.values()) {
                builder.append(flag.getName()).append(" (").append(flag.getArgumentType().getSimpleName().toLowerCase()).append(')').append(", ");
            }

            builder.delete(builder.length() - 2, builder.length());
        } else {
            builder.append(ChatColor.RED).append("None");
        }

        return builder.toString();
    }

}
