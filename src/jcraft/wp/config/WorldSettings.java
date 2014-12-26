package jcraft.wp.config;

import java.io.File;

public class WorldSettings extends YamlHandler {

    @SaveField(section = "")
    private boolean enabled;

    @SaveField(section = "ignition")
    public boolean disableLighter;
    @SaveField(section = "ignition")
    public boolean disableTntExplosion, disableTntBlockDamage;

    @SaveField(section = "fire")
    public boolean disableLightningFire;
    @SaveField(section = "fire")
    public boolean disableLavaFireSpread, disableAllFireSpread;
    @SaveField(section = "fire")
    public boolean disableFireBlockBreak;

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
    public boolean disableMobsFarmlandGrief;

    @SaveField(section = "player")
    public boolean disablePlayerFarmlandGrief;

    public WorldSettings(File file) {
        super(file);

        this.enabled = true;

        this.disableLighter = false; // TODO
        this.disableTntExplosion = true; // TODO
        this.disableTntBlockDamage = true; // TODO

        this.disableLightningFire = true;
        this.disableLavaFireSpread = true;
        this.disableAllFireSpread = true;
        this.disableFireBlockBreak = true;

        this.disableCreeperExplosion = false; // TODO
        this.disableCreeperBlockDamage = true; // TODO

        this.disableWitherExplosion = false; // TODO
        this.disableWitherBlockDamage = true; // TODO

        this.disableEnderDragonBlockDamage = true; // TODO

        this.disableEndermanBlockGrief = true; // TODO

        this.disableZombieDoorBreaking = true; // TODO

        this.disablePaintingBreaking = true; // TODO
        this.disableItemFrameBreaking = true; // TODO
        this.disableArmorStandBreaking = true; // TODO

        this.disableMobsFarmlandGrief = true;

        this.disablePlayerFarmlandGrief = true;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

}
