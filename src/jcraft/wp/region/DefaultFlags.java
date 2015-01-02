package jcraft.wp.region;

import jcraft.wp.region.flag.BuildRegionFlag;
import jcraft.wp.region.flag.EnderpearlRegionFlag;
import jcraft.wp.region.flag.MobSpawningRegionFlag;
import jcraft.wp.region.flag.PVPRegionFlag;

public class DefaultFlags {

    public static BuildRegionFlag BUILD_FLAG = new BuildRegionFlag("build", Boolean.class);
    public static PVPRegionFlag PVP_FLAG = new PVPRegionFlag("pvp", Boolean.class);
    public static MobSpawningRegionFlag MOBSPAWN_FLAG = new MobSpawningRegionFlag("mob-spawning", Boolean.class);
    public static EnderpearlRegionFlag ENDERPEARL_FLAG = new EnderpearlRegionFlag("enderpearl", Boolean.class);

}
