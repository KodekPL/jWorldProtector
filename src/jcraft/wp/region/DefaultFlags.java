package jcraft.wp.region;

import jcraft.wp.region.flag.BuildRegionFlag;
import jcraft.wp.region.flag.PVPRegionFlag;

public class DefaultFlags {

    public static BuildRegionFlag BUILD_FLAG = new BuildRegionFlag("build", Boolean.class);
    public static PVPRegionFlag PVP_FLAG = new PVPRegionFlag("pvp", Boolean.class);

}
