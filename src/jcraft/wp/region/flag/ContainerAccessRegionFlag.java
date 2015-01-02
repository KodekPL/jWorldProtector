package jcraft.wp.region.flag;

import jcraft.wp.region.Region;

public class ContainerAccessRegionFlag extends RegionFlag {

    public ContainerAccessRegionFlag(String name, Class<?> argumentType) {
        super(name, argumentType);
    }

    @Override
    public Object parseState(String sState) {
        return Boolean.parseBoolean(sState);
    }

    @Override
    public String stateToString(Object sState) {
        final Boolean state = (Boolean) sState;

        return state ? "true" : "false";
    }

    @Override
    public boolean applyToRegion(Region region, String sState) {
        final Boolean state = Boolean.parseBoolean(sState);

        if (state == null) {
            return false;
        }

        region.addFlag(this, state);

        return true;
    }

}
