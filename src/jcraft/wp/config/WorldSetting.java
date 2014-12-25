package jcraft.wp.config;

import java.io.File;

public class WorldSetting extends YamlHandler {

    @SaveField(section = "")
    private boolean enabled;

    public WorldSetting(File file) {
        super(file);

        this.enabled = true;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

}
