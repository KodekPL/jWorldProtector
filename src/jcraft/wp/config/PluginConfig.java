package jcraft.wp.config;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public class PluginConfig extends YamlHandler {

    @SaveField(section = "regions")
    public String warningMessage;
    @SaveField(section = "regions")
    public String wand;

    private Material wandMaterial;

    public PluginConfig(File file) {
        super(file);

        this.warningMessage = ChatColor.RED + "Hey! " + ChatColor.GRAY + "Interactions in this region are blocked (cuboid).";

        this.wand = Material.WOOD_AXE.name();
    }

    public Material getWandMaterial() {
        if (this.wandMaterial == null) {
            this.wandMaterial = Material.getMaterial(this.wand);
        }

        return wandMaterial;
    }

}
