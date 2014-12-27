package jcraft.wp.commands;

import java.util.regex.Pattern;

import jcraft.wp.ProtectorPlugin;
import jcraft.wp.WorldInstance;
import jcraft.wp.region.Region;
import jcraft.wp.region.RegionPlayer;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;

public class RegionCommands extends CommandHandler {

    // TODO Add flags commands
    // TODO Add perent commands

    @Override
    public void noArgsCommand(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.YELLOW
                + "/region <define/redefine/select/remove/info/list/tp/addowner/removeowner/addmember/removemember/setperm/removeperm/flag/setparent/save/load> [args...]");
    }

    private static final Pattern VALID_CHARACTERS = Pattern.compile("^[A-Za-z0-9_,'\\-\\+/]{1,}$");

    @PluginCommand(args = { "define", "def", "create" }, argsAmount = 2, requiresPlayer = true, permission = "worldprotector.region.define", usage = "/region define <region_name>")
    public void onRegionDefine_Player(CommandSender sender, String[] args) {
        final Player player = (Player) sender;
        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(player.getWorld().getName());

        if (config == null) {
            sender.sendMessage(ChatColor.RED + "Unexpected non-existing world!");
            return;
        }

        final String regionName = args[1];

        if (!VALID_CHARACTERS.matcher(regionName).matches()) {
            sender.sendMessage(ChatColor.RED + "Region name '" + regionName + "' contains characters that are not allowed.");
            return;
        }

        if (config.getRegionContainer().hasRegion(regionName)) {
            sender.sendMessage(ChatColor.RED + "Region with name '" + regionName + "' already exist.");
            return;
        }

        final Region region = ProtectorPlugin.getWorldEdit().getRegion(regionName, player);

        if (region == null) {
            sender.sendMessage(ChatColor.RED + "Select an area to create region.");
            return;
        }

        config.getRegionContainer().addRegion(region);
        config.getRegionContainer().save();

        sender.sendMessage(ChatColor.GREEN + "Region with name '" + regionName + "' has been added to the world.");
    }

    @PluginCommand(args = { "redefine", "redef" }, argsAmount = 2, requiresPlayer = true, permission = "worldprotector.region.redefine", usage = "/region redefine <region_name>")
    public void onRegionReDefine_Player(CommandSender sender, String[] args) {
        final Player player = (Player) sender;
        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(player.getWorld().getName());

        if (config == null) {
            sender.sendMessage(ChatColor.RED + "Unexpected non-existing world!");
            return;
        }

        final String regionName = args[1];
        final Region region = config.getRegionContainer().getRegion(regionName);

        if (region == null) {
            sender.sendMessage(ChatColor.RED + "Region with name '" + regionName + "' does not exist.");
            return;
        }

        final BlockVector[] points = ProtectorPlugin.getWorldEdit().getSelectionPoints(player);

        if (points == null) {
            sender.sendMessage(ChatColor.RED + "Select an area to create region.");
            return;
        }

        region.setMinPoint(points[0]);
        region.setMaxPoint(points[1]);

        config.getRegionContainer().save();

        sender.sendMessage(ChatColor.GREEN + "Region with name '" + regionName + "' has been redefined.");
    }

    @PluginCommand(args = { "select", "sel" }, argsAmount = 2, requiresPlayer = true, permission = "worldprotector.region.select", usage = "/region select <region_name>")
    public void onRegionSelect_Player(CommandSender sender, String[] args) {
        final Player player = (Player) sender;
        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(player.getWorld().getName());

        if (config == null) {
            sender.sendMessage(ChatColor.RED + "Unexpected non-existing world!");
            return;
        }

        final String regionName = args[1];
        final Region region = config.getRegionContainer().getRegion(regionName);

        if (region == null) {
            sender.sendMessage(ChatColor.RED + "Region with name '" + regionName + "' does not exist.");
            return;
        }

        ProtectorPlugin.getWorldEdit().setSelection(player, region.getMinPoint(), region.getMaxPoint());

        sender.sendMessage(ChatColor.GREEN + "Region with name '" + regionName + "' has been selected with WorldEdit.");
    }

    @PluginCommand(args = { "remove", "delete" }, argsAmount = 2, requiresPlayer = true, permission = "worldprotector.region.remove", usage = "/region remove <region_name> [world]")
    public void onRegionRemove_Player(CommandSender sender, String[] args) {
        final Player player = (Player) sender;
        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(player.getWorld().getName());

        if (config == null) {
            sender.sendMessage(ChatColor.RED + "Unexpected non-existing world!");
            return;
        }

        final String regionName = args[1];

        if (!config.getRegionContainer().hasRegion(regionName)) {
            sender.sendMessage(ChatColor.RED + "Region with name '" + regionName + "' does not exist.");
            return;
        }

        config.getRegionContainer().removeRegion(regionName);

        config.getRegionContainer().save();

        sender.sendMessage(ChatColor.GREEN + "Region with name '" + regionName + "' has been removed.");
    }

    @PluginCommand(args = { "remove", "delete" }, argsAmount = 3, requiresPlayer = false, permission = "worldprotector.region.remove", usage = "/region remove <region_name> <world_name>")
    public void onRegionRemove_Console(CommandSender sender, String[] args) {
        final String worldName = args[2];
        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(worldName);

        if (config == null) {
            sender.sendMessage(ChatColor.RED + "World with name '" + worldName + "' was not found.");
            return;
        }

        final String regionName = args[1];

        if (!config.getRegionContainer().hasRegion(regionName)) {
            sender.sendMessage(ChatColor.RED + "Region with name '" + regionName + "' does not exist.");
            return;
        }

        config.getRegionContainer().removeRegion(regionName);

        config.getRegionContainer().save();

        sender.sendMessage(ChatColor.GREEN + "Region with name '" + regionName + "' has been removed.");
    }

    @PluginCommand(args = { "info" }, argsAmount = 2, requiresPlayer = true, permission = "worldprotector.region.info", usage = "/region info <region_name> [world]")
    public void onRegionInfo_Player(CommandSender sender, String[] args) {
        final Player player = (Player) sender;
        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(player.getWorld().getName());

        if (config == null) {
            sender.sendMessage(ChatColor.RED + "Unexpected non-existing world!");
            return;
        }

        final String regionName = args[1];
        final Region region = config.getRegionContainer().getRegion(regionName);

        if (region == null) {
            sender.sendMessage(ChatColor.RED + "Region with name '" + regionName + "' does not exist.");
            return;
        }

        sender.sendMessage(region.getInfo());
    }

    @PluginCommand(args = { "info" }, argsAmount = 3, requiresPlayer = false, permission = "worldprotector.region.info", usage = "/region info <region_name> <world_name>")
    public void onRegionInfo_Console(CommandSender sender, String[] args) {
        final String worldName = args[2];
        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(worldName);

        if (config == null) {
            sender.sendMessage(ChatColor.RED + "World with name '" + worldName + "' was not found.");
            return;
        }

        final String regionName = args[1];
        final Region region = config.getRegionContainer().getRegion(regionName);

        if (region == null) {
            sender.sendMessage(ChatColor.RED + "Region with name '" + regionName + "' does not exist.");
            return;
        }

        sender.sendMessage(region.getInfo());
    }

    @PluginCommand(args = { "list" }, argsAmount = 1, requiresPlayer = true, permission = "worldprotector.region.list", usage = "/region list [world]")
    public void onRegionList_Player(CommandSender sender, String[] args) {
        final Player player = (Player) sender;
        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(player.getWorld().getName());

        if (config == null) {
            sender.sendMessage(ChatColor.RED + "Unexpected non-existing world!");
            return;
        }

        sender.sendMessage(config.getRegionContainer().getList());
    }

    @PluginCommand(args = { "list" }, argsAmount = 2, requiresPlayer = false, permission = "worldprotector.region.list", usage = "/region list <world_name>")
    public void onRegionList_Console(CommandSender sender, String[] args) {
        final String worldName = args[1];
        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(worldName);

        if (config == null) {
            sender.sendMessage(ChatColor.RED + "World with name '" + worldName + "' was not found.");
            return;
        }

        sender.sendMessage(config.getRegionContainer().getList());
    }

    @PluginCommand(args = { "teleport", "tp" }, argsAmount = 2, requiresPlayer = true, permission = "worldprotector.region.teleport", usage = "/region tp <region_name>")
    public void onRegionTeleport_Player(CommandSender sender, String[] args) {
        final Player player = (Player) sender;
        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(player.getWorld().getName());

        if (config == null) {
            sender.sendMessage(ChatColor.RED + "Unexpected non-existing world!");
            return;
        }

        final String regionName = args[1];
        final Region region = config.getRegionContainer().getRegion(regionName);

        if (region == null) {
            sender.sendMessage(ChatColor.RED + "Region with name '" + regionName + "' does not exist.");
            return;
        }

        final Location regionCenter = new Location(player.getWorld(), region.getMinPoint().getBlockX()
                + ((region.getMaxPoint().getBlockX() - region.getMinPoint().getBlockX()) / 2), region.getMinPoint().getBlockY()
                + ((region.getMaxPoint().getBlockY() - region.getMinPoint().getBlockY()) / 2), region.getMinPoint().getBlockZ()
                + ((region.getMaxPoint().getBlockZ() - region.getMinPoint().getBlockZ()) / 2));

        player.teleport(regionCenter);
    }

    @PluginCommand(args = { "addowner" }, argsAmount = 3, requiresPlayer = true, permission = "worldprotector.region.owners", usage = "/region addowner <player_name> <region_name> [world]")
    public void onRegionAddOwner_Player(CommandSender sender, String[] args) {
        final Player player = (Player) sender;
        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(player.getWorld().getName());

        if (config == null) {
            sender.sendMessage(ChatColor.RED + "Unexpected non-existing world!");
            return;
        }

        // TODO: Allow to add offline players

        final String playerName = args[1];
        final Player onlinePlayer = ProtectorPlugin.getOnlinePlayer(playerName);

        if (onlinePlayer == null) {
            sender.sendMessage(ChatColor.RED + "Player with name '" + playerName + "' is not online.");
            sender.sendMessage(ChatColor.YELLOW + "Support for offline players will be added soon.");
            return;
        }

        final String regionName = args[2];
        final Region region = config.getRegionContainer().getRegion(regionName);

        if (region == null) {
            sender.sendMessage(ChatColor.RED + "Region with name '" + regionName + "' does not exist.");
            return;
        }

        region.addOwner(new RegionPlayer(onlinePlayer));

        config.getRegionContainer().save();

        sender.sendMessage(ChatColor.GREEN + "Player with name '" + onlinePlayer.getName() + "' has been added to region with name '"
                + region.getName() + "'.");
    }

    @PluginCommand(args = { "addowner" }, argsAmount = 4, requiresPlayer = false, permission = "worldprotector.region.owners", usage = "/region addowner <player_name> <region_name> <world_name>")
    public void onRegionAddOwner_Console(CommandSender sender, String[] args) {
        final String worldName = args[3];
        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(worldName);

        if (config == null) {
            sender.sendMessage(ChatColor.RED + "World with name '" + worldName + "' was not found.");
            return;
        }

        // TODO: Allow to add offline players

        final String playerName = args[1];
        final Player onlinePlayer = ProtectorPlugin.getOnlinePlayer(playerName);

        if (onlinePlayer == null) {
            sender.sendMessage(ChatColor.RED + "Player with name '" + playerName + "' is not online.");
            sender.sendMessage(ChatColor.YELLOW + "Support for offline players will be added soon.");
            return;
        }

        final String regionName = args[2];
        final Region region = config.getRegionContainer().getRegion(regionName);

        if (region == null) {
            sender.sendMessage(ChatColor.RED + "Region with name '" + regionName + "' does not exist.");
            return;
        }

        region.addOwner(new RegionPlayer(onlinePlayer));

        config.getRegionContainer().save();

        sender.sendMessage(ChatColor.GREEN + "Player with name '" + onlinePlayer.getName() + "' has been added to region with name '"
                + region.getName() + "'.");
    }

    @PluginCommand(args = { "removeowner", "deleteowner" }, argsAmount = 3, requiresPlayer = true, permission = "worldprotector.region.owners", usage = "/region removeowner <player_name> <region_name> [world]")
    public void onRegionRemoveOwner_Player(CommandSender sender, String[] args) {
        final Player player = (Player) sender;
        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(player.getWorld().getName());

        if (config == null) {
            sender.sendMessage(ChatColor.RED + "Unexpected non-existing world!");
            return;
        }

        final String regionName = args[2];
        final Region region = config.getRegionContainer().getRegion(regionName);

        if (region == null) {
            sender.sendMessage(ChatColor.RED + "Region with name '" + regionName + "' does not exist.");
            return;
        }

        final String playerName = args[1];

        if (!region.isOwner(playerName)) {
            sender.sendMessage(ChatColor.RED + "Player with name '" + playerName + "' is not an owner of this region.");
            return;
        }

        region.removeOwner(playerName);

        config.getRegionContainer().save();

        sender.sendMessage(ChatColor.GREEN + "Player with name '" + playerName + "' has been removed from region with name '" + region.getName()
                + "'.");
    }

    @PluginCommand(args = { "removeowner", "deleteowner" }, argsAmount = 4, requiresPlayer = false, permission = "worldprotector.region.owners", usage = "/region removeowner <player_name> <region_name> <world_name>")
    public void onRegionRemoveOwner_Console(CommandSender sender, String[] args) {
        final String worldName = args[3];
        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(worldName);

        if (config == null) {
            sender.sendMessage(ChatColor.RED + "World with name '" + worldName + "' was not found.");
            return;
        }

        final String regionName = args[2];
        final Region region = config.getRegionContainer().getRegion(regionName);

        if (region == null) {
            sender.sendMessage(ChatColor.RED + "Region with name '" + regionName + "' does not exist.");
            return;
        }

        final String playerName = args[1];

        if (!region.isOwner(playerName)) {
            sender.sendMessage(ChatColor.RED + "Player with name '" + playerName + "' is not an owner of this region.");
            return;
        }

        region.removeOwner(playerName);

        config.getRegionContainer().save();

        sender.sendMessage(ChatColor.GREEN + "Player with name '" + playerName + "' has been removed from region with name '" + region.getName()
                + "'.");
    }

    @PluginCommand(args = { "addmember" }, argsAmount = 3, requiresPlayer = true, permission = "worldprotector.region.members", usage = "/region addmember <player_name> <region_name> [world_name]")
    public void onRegionAddMember_Player(CommandSender sender, String[] args) {
        final Player player = (Player) sender;
        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(player.getWorld().getName());

        if (config == null) {
            sender.sendMessage(ChatColor.RED + "Unexpected non-existing world!");
            return;
        }

        // TODO: Allow to add offline players

        final String playerName = args[1];
        final Player onlinePlayer = ProtectorPlugin.getOnlinePlayer(playerName);

        if (onlinePlayer == null) {
            sender.sendMessage(ChatColor.RED + "Player with name '" + playerName + "' is not online.");
            sender.sendMessage(ChatColor.YELLOW + "Support for offline players will be added soon.");
            return;
        }

        final String regionName = args[2];
        final Region region = config.getRegionContainer().getRegion(regionName);

        if (region == null) {
            sender.sendMessage(ChatColor.RED + "Region with name '" + regionName + "' does not exist.");
            return;
        }

        region.addMember(new RegionPlayer(onlinePlayer));

        config.getRegionContainer().save();

        sender.sendMessage(ChatColor.GREEN + "Player with name '" + onlinePlayer.getName() + "' has been added to region with name '"
                + region.getName() + "'.");
    }

    @PluginCommand(args = { "addmember" }, argsAmount = 4, requiresPlayer = false, permission = "worldprotector.region.members", usage = "/region addmember <player_name> <region_name> <world_name>")
    public void onRegionAddMember_Console(CommandSender sender, String[] args) {
        final String worldName = args[3];
        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(worldName);

        if (config == null) {
            sender.sendMessage(ChatColor.RED + "World with name '" + worldName + "' was not found.");
            return;
        }

        // TODO: Allow to add offline players

        final String playerName = args[1];
        final Player onlinePlayer = ProtectorPlugin.getOnlinePlayer(playerName);

        if (onlinePlayer == null) {
            sender.sendMessage(ChatColor.RED + "Player with name '" + playerName + "' is not online.");
            sender.sendMessage(ChatColor.YELLOW + "Support for offline players will be added soon.");
            return;
        }

        final String regionName = args[2];
        final Region region = config.getRegionContainer().getRegion(regionName);

        if (region == null) {
            sender.sendMessage(ChatColor.RED + "Region with name '" + regionName + "' does not exist.");
            return;
        }

        region.addMember(new RegionPlayer(onlinePlayer));

        config.getRegionContainer().save();

        sender.sendMessage(ChatColor.GREEN + "Player with name '" + onlinePlayer.getName() + "' has been added to region with name '"
                + region.getName() + "'.");
    }

    @PluginCommand(args = { "removemember", "deletemember" }, argsAmount = 3, requiresPlayer = true, permission = "worldprotector.region.members", usage = "/region removemember <player_name> <region_name> [world_name]")
    public void onRegionRemoveMember_Player(CommandSender sender, String[] args) {
        final Player player = (Player) sender;
        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(player.getWorld().getName());

        if (config == null) {
            sender.sendMessage(ChatColor.RED + "Unexpected non-existing world!");
            return;
        }

        final String regionName = args[2];
        final Region region = config.getRegionContainer().getRegion(regionName);

        if (region == null) {
            sender.sendMessage(ChatColor.RED + "Region with name '" + regionName + "' does not exist.");
            return;
        }

        final String playerName = args[1];

        if (!region.isMember(playerName)) {
            sender.sendMessage(ChatColor.RED + "Player with name '" + playerName + "' is not a member of this region.");
            return;
        }

        region.removeMember(playerName);

        config.getRegionContainer().save();

        sender.sendMessage(ChatColor.GREEN + "Player with name '" + playerName + "' has been removed from region with name '" + region.getName()
                + "'.");
    }

    @PluginCommand(args = { "removemember", "deletemember" }, argsAmount = 4, requiresPlayer = false, permission = "worldprotector.region.members", usage = "/region removemember <player_name> <region_name> <world_name>")
    public void onRegionRemoveMember_Console(CommandSender sender, String[] args) {
        final String worldName = args[3];
        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(worldName);

        if (config == null) {
            sender.sendMessage(ChatColor.RED + "World with name '" + worldName + "' was not found.");
            return;
        }

        final String regionName = args[2];
        final Region region = config.getRegionContainer().getRegion(regionName);

        if (region == null) {
            sender.sendMessage(ChatColor.RED + "Region with name '" + regionName + "' does not exist.");
            return;
        }

        final String playerName = args[1];

        if (!region.isMember(playerName)) {
            sender.sendMessage(ChatColor.RED + "Player with name '" + playerName + "' is not a member of this region.");
            return;
        }

        region.removeMember(playerName);

        config.getRegionContainer().save();

        sender.sendMessage(ChatColor.GREEN + "Player with name '" + playerName + "' has been removed from region with name '" + region.getName()
                + "'.");
    }

    @PluginCommand(args = { "setperm", "setpermission" }, argsAmount = 3, requiresPlayer = true, permission = "worldprotector.region.permission", usage = "/region setperm <region_name> <permission> [world_name]")
    public void onRegionSetPermission_Player(CommandSender sender, String[] args) {
        final Player player = (Player) sender;
        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(player.getWorld().getName());

        if (config == null) {
            sender.sendMessage(ChatColor.RED + "Unexpected non-existing world!");
            return;
        }

        final String regionName = args[1];
        final Region region = config.getRegionContainer().getRegion(regionName);

        if (region == null) {
            sender.sendMessage(ChatColor.RED + "Region with name '" + regionName + "' does not exist.");
            return;
        }

        final String permission = args[2].toLowerCase();

        region.setPermission(permission);

        config.getRegionContainer().save();

        sender.sendMessage(ChatColor.GREEN + "Set permission of region with name '" + regionName + "' to '" + region.getPermission() + "'.");
    }

    @PluginCommand(args = { "setperm", "setpermission" }, argsAmount = 4, requiresPlayer = false, permission = "worldprotector.region.permission", usage = "/region setperm <region_name> <permission> <world_name>")
    public void onRegionSetPermission_Console(CommandSender sender, String[] args) {
        final String worldName = args[3];
        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(worldName);

        if (config == null) {
            sender.sendMessage(ChatColor.RED + "World with name '" + worldName + "' was not found.");
            return;
        }

        final String regionName = args[1];
        final Region region = config.getRegionContainer().getRegion(regionName);

        if (region == null) {
            sender.sendMessage(ChatColor.RED + "Region with name '" + regionName + "' does not exist.");
            return;
        }

        final String permission = args[2].toLowerCase();

        region.setPermission(permission);

        config.getRegionContainer().save();

        sender.sendMessage(ChatColor.GREEN + "Set permission of region with name '" + regionName + "' to '" + region.getPermission() + "'.");
    }

    @PluginCommand(args = { "removeperm", "removepermission", "deleteperm", "deletepermission" }, argsAmount = 2, requiresPlayer = true, permission = "worldprotector.region.permission", usage = "/region removeperm <region_name> [world_name]")
    public void onRegionRemovePermission_Player(CommandSender sender, String[] args) {
        final Player player = (Player) sender;
        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(player.getWorld().getName());

        if (config == null) {
            sender.sendMessage(ChatColor.RED + "Unexpected non-existing world!");
            return;
        }

        final String regionName = args[1];
        final Region region = config.getRegionContainer().getRegion(regionName);

        if (region == null) {
            sender.sendMessage(ChatColor.RED + "Region with name '" + regionName + "' does not exist.");
            return;
        }

        region.setPermission(null);

        config.getRegionContainer().save();

        sender.sendMessage(ChatColor.GREEN + "Removed permission of region with name '" + regionName + "'.");
    }

    @PluginCommand(args = { "removeperm", "removepermission", "deleteperm", "deletepermission" }, argsAmount = 3, requiresPlayer = false, permission = "worldprotector.region.permission", usage = "/region removeperm <region_name> <world_name>")
    public void onRegionRemovePermission_Console(CommandSender sender, String[] args) {
        final String worldName = args[2];
        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(worldName);

        if (config == null) {
            sender.sendMessage(ChatColor.RED + "World with name '" + worldName + "' was not found.");
            return;
        }

        final String regionName = args[1];
        final Region region = config.getRegionContainer().getRegion(regionName);

        if (region == null) {
            sender.sendMessage(ChatColor.RED + "Region with name '" + regionName + "' does not exist.");
            return;
        }

        region.setPermission(null);

        config.getRegionContainer().save();

        sender.sendMessage(ChatColor.GREEN + "Removed permission of region with name '" + regionName + "'.");
    }

    @PluginCommand(args = { "save" }, argsAmount = 2, requiresPlayer = false, permission = "worldprotector.region.save", usage = "/region save <world>")
    public void onRegionSave(CommandSender sender, String[] args) {
        final String worldName = args[1];
        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(worldName);

        if (config == null) {
            sender.sendMessage(ChatColor.RED + "World with name '" + worldName + "' was not found.");
            return;
        }

        config.getRegionContainer().save();

        sender.sendMessage(ChatColor.GREEN + "Saved regions of world with name '" + worldName + "'.");
    }

    @PluginCommand(args = { "load" }, argsAmount = 2, requiresPlayer = false, permission = "worldprotector.region.load", usage = "/region load <world>")
    public void onRegionLoad(CommandSender sender, String[] args) {
        final String worldName = args[1];
        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(worldName);

        if (config == null) {
            sender.sendMessage(ChatColor.RED + "World with name '" + worldName + "' was not found.");
            return;
        }

        config.getRegionContainer().load();

        sender.sendMessage(ChatColor.GREEN + "Loaded regions of world with name '" + worldName + "'.");
    }

}
