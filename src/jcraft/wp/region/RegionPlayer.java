package jcraft.wp.region;

import java.util.UUID;

import org.bukkit.entity.Player;

public class RegionPlayer {

    private final String playerName;
    private final UUID playerUUID;

    public RegionPlayer(Player player) {
        this.playerName = player.getName();
        this.playerUUID = player.getUniqueId();
    }

    public RegionPlayer(String playerName, UUID playerUUID) {
        this.playerName = playerName;
        this.playerUUID = playerUUID;
    }

    public RegionPlayer(String playerName, String playerUUID) {
        this.playerName = playerName;
        this.playerUUID = UUID.fromString(playerUUID);
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public UUID getUniqueId() {
        return this.playerUUID;
    }

}
