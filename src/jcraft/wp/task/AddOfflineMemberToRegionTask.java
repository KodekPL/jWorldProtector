package jcraft.wp.task;

import jcraft.wp.WorldInstance;
import jcraft.wp.region.Region;
import jcraft.wp.region.RegionPlayer;

import org.bukkit.command.CommandSender;

public class AddOfflineMemberToRegionTask extends AddOfflinePlayerToRegionTask {

    public AddOfflineMemberToRegionTask(CommandSender creator, String playerName, WorldInstance config, Region region) {
        super(creator, playerName, config, region);
    }

    @Override
    public void addPlayerToRegion(RegionPlayer regionPlayer, Region region) {
        region.addMember(regionPlayer);
    }

}
