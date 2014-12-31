package jcraft.wp.task;

import java.net.Proxy;
import java.util.UUID;

import jcraft.wp.ProtectorPlugin;
import jcraft.wp.WorldInstance;
import jcraft.wp.region.Region;
import jcraft.wp.region.RegionPlayer;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;

public abstract class AddOfflinePlayerToRegionTask extends BukkitRunnable {

    private final CommandSender creator;
    private final String playerName;
    private final WorldInstance config;
    private final Region region;

    public AddOfflinePlayerToRegionTask(CommandSender creator, String playerName, WorldInstance config, Region region) {
        this.creator = creator;
        this.playerName = playerName;
        this.config = config;
        this.region = region;

        this.runTaskAsynchronously(ProtectorPlugin.getPlugin());
    }

    @Override
    public void run() {
        try {
            safeRun();
        } catch (Exception e) {
            sendCreatorMessage(ChatColor.RED + "An error occurred while adding offline player to region. See console for error stack trace.");
            e.printStackTrace();
        }
    }

    public void safeRun() throws Exception {
        final YggdrasilAuthenticationService authService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
        final GameProfileRepository profileRepo = authService.createProfileRepository();
        final GameProfileLookup gameProfileLookup = new GameProfileLookup();

        profileRepo.findProfilesByNames(new String[] { this.playerName }, Agent.MINECRAFT, gameProfileLookup);

        if (!gameProfileLookup.hasProfile()) {
            sendCreatorMessage(new String[] { ChatColor.RED + "Player with name '" + this.playerName + "' does not exist.",
                    ChatColor.RED + "Player was not added to region." });
            return;
        }

        final GameProfile gameProfile = gameProfileLookup.getGameProfile();
        final RegionPlayer regionPlayer = new RegionPlayer(gameProfile.getName(), gameProfile.getId());

        this.addPlayerToRegion(regionPlayer, this.region);

        new SaveRegionsTask(this.config.getRegionContainer());

        sendCreatorMessage(ChatColor.GREEN + "Player with name '" + regionPlayer.getPlayerName() + "' has been added to region with name '"
                + this.region.getName() + "'.");
    }

    public abstract void addPlayerToRegion(RegionPlayer regionPlayer, Region region);

    private void sendCreatorMessage(String... message) {
        if (this.creator != null) {
            this.creator.sendMessage(message);
        }
    }

    private final static class GameProfileLookup implements ProfileLookupCallback {

        private GameProfile profile;

        public void onProfileLookupSucceeded(GameProfile gameProfile) {
            this.profile = gameProfile;
        }

        public void onProfileLookupFailed(GameProfile gameProfile, Exception exception) {
            this.profile = null;
        }

        public boolean hasProfile() {
            return this.profile != null;
        }

        public GameProfile getGameProfile() {
            return this.profile;
        }

    }

}
