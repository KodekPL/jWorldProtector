package jcraft.wp.region.flag;

import jcraft.wp.region.Region;

public abstract class RegionFlag {

    private final String name;
    private final Class<?> argumentType;

    public RegionFlag(String name, Class<?> argumentType) {
        this.name = name.toLowerCase();
        this.argumentType = argumentType;
    }

    public String getName() {
        return this.name;
    }

    public Class<?> getArgumentType() {
        return this.argumentType;
    }

    public abstract String stateToString(Object argument);

    public abstract Object parseState(String state);

    public abstract boolean applyToRegion(Region region, String state);

}
