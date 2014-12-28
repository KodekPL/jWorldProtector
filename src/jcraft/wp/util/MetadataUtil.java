package jcraft.wp.util;

import java.util.List;

import jcraft.wp.ProtectorPlugin;

import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;

public class MetadataUtil {

    public static void set(Metadatable target, String key, Object value) {
        target.setMetadata(key, new FixedMetadataValue(ProtectorPlugin.getPlugin(), value));
    }

    public static Object get(Metadatable target, String key) {
        final List<MetadataValue> values = target.getMetadata(key);

        for (MetadataValue value : values) {
            if (value.getOwningPlugin().equals(ProtectorPlugin.getPlugin())) {
                return value.value();
            }
        }

        return null;
    }

}
