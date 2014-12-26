package jcraft.wp.config;

import java.io.File;

public class WorldSettings extends YamlHandler {

    @SaveField(section = "")
    private boolean enabled;

    @SaveField(section = "ignition")
    public boolean disableTntExplosion, disableTntBlockDamage;

    @SaveField(section = "fire")
    public boolean disableLavaFireSpread, disableAllFireSpread;

    @SaveField(section = "mobs")
    public boolean disableCreeperExplosion, disableCreeperBlockDamage;
    @SaveField(section = "mobs")
    public boolean disableWitherExplosion, disableWitherBlockDamage;
    @SaveField(section = "mobs")
    public boolean disableFireballExplosion, disableFireballBlockDamage;
    @SaveField(section = "mobs")
    public boolean disableEnderDragonBlockDamage;
    @SaveField(section = "mobs")
    public boolean disableEndermanBlockGrief;
    @SaveField(section = "mobs")
    public boolean disableZombieDoorBreaking;
    @SaveField(section = "mobs")
    public boolean disablePaintingBreaking, disableItemFrameBreaking, disableArmorStandBreaking;
    @SaveField(section = "mobs")
    public boolean disableMobsCropsGrief;

    @SaveField(section = "player")
    public boolean disablePlayerCropsGrief;

    public WorldSettings(File file) {
        super(file);

        this.enabled = true;

        this.disableTntExplosion = true;
        this.disableTntBlockDamage = true;

        this.disableLavaFireSpread = true;
        this.disableAllFireSpread = true;

        this.disableCreeperExplosion = false;
        this.disableCreeperBlockDamage = true;

        this.disableWitherExplosion = false;
        this.disableWitherBlockDamage = true;

        this.disableEnderDragonBlockDamage = true;

        this.disableEndermanBlockGrief = true;

        this.disableZombieDoorBreaking = true;

        this.disablePaintingBreaking = true;
        this.disableItemFrameBreaking = true;
        this.disableArmorStandBreaking = true;

        this.disableMobsCropsGrief = true;

        this.disablePlayerCropsGrief = true;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

}
