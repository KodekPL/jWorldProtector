package jcraft.wp.commands;

import java.util.regex.Pattern;

import jcraft.wp.ProtectorPlugin;
import jcraft.wp.WorldInstance;
import jcraft.wp.region.Region;
import jcraft.wp.region.RegionPlayer;
import jcraft.wp.region.flag.RegionFlag;
import jcraft.wp.task.AddOfflineMemberToRegionTask;
import jcraft.wp.task.AddOfflineOwnerToRegionTask;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;

public class RegionCommands extends CommandHandler {

    @Override
    public void noArgsCommand(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.YELLOW
                + "/region <define/redefine/select/remove/info/list/tp/addowner/removeowner/addmember/removemember/setperm/removeperm/flags/setflag/removeflag/setparent/removeparent/save/load> [args...]");
    }

    private static final Pattern VALID_CHARACTERS = Pattern.compile("^[A-Za-z0-9_,'\\-\\+/]{1,}$");

    @PluginCommand(args = { "define", "def", "create" }, minArgs = 2, maxArgs = 2, requiresPlayer = true, permission = "worldprotector.region.define", usage = "/region define <region_name>")
    public void onRegionDefine(CommandSender sender, String[] args) {
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

    @PluginCommand(args = { "redefine", "redef" }, minArgs = 2, maxArgs = 2, requiresPlayer = true, permission = "worldprotector.region.redefine", usage = "/region redefine <region_name>")
    public void onRegionReDefine(CommandSender sender, String[] args) {
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

    @PluginCommand(args = { "select", "sel" }, minArgs = 2, maxArgs = 2, requiresPlayer = true, permission = "worldprotector.region.select", usage = "/region select <region_name>")
    public void onRegionSelect(CommandSender sender, String[] args) {
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

    @PluginCommand(args = { "remove", "delete" }, minArgs = 2, maxArgs = 3, requiresPlayer = false, permission = "worldprotector.region.remove", usage = "/region remove <region_name> [world]")
    public void onRegionRemove(CommandSender sender, String[] args) {
        final String worldName;

        if (args.length == 3) {
            worldName = args[2];
        } else if (sender instanceof Player) {
            final Player player = (Player) sender;

            worldName = player.getWorld().getName();
        } else {
            sender.sendMessage(ChatColor.RED + "World name is missing!");
            return;
        }

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

    @PluginCommand(args = { "info" }, minArgs = 2, maxArgs = 3, requiresPlayer = false, permission = "worldprotector.region.info", usage = "/region info <region_name> [world]")
    public void onRegionInfo(CommandSender sender, String[] args) {
        final String worldName;

        if (args.length == 3) {
            worldName = args[2];
        } else if (sender instanceof Player) {
            final Player player = (Player) sender;

            worldName = player.getWorld().getName();
        } else {
            sender.sendMessage(ChatColor.RED + "World name is missing!");
            return;
        }

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

    @PluginCommand(args = { "list" }, minArgs = 1, maxArgs = 2, requiresPlayer = false, permission = "worldprotector.region.list", usage = "/region list [world]")
    public void onRegionList(CommandSender sender, String[] args) {
        final String worldName;

        if (args.length == 2) {
            worldName = args[1];
        } else if (sender instanceof Player) {
            final Player player = (Player) sender;

            worldName = player.getWorld().getName();
        } else {
            sender.sendMessage(ChatColor.RED + "World name is missing!");
            return;
        }

        final WorldInstance config = ProtectorPlugin.getWorldsManager().getWorldInstance(worldName);

        if (config == null) {
            sender.sendMessage(ChatColor.RED + "World with name '" + worldName + "' was not found.");
            return;
        }

        sender.sendMessage(config.getRegionContainer().getList());
    }

    @PluginCommand(args = { "teleport", "tp" }, minArgs = 2, maxArgs = 2, requiresPlayer = true, permission = "worldprotector.region.teleport", usage = "/region tp <region_name>")
    public void onRegionTeleport(CommandSender sender, String[] args) {
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

    @PluginCommand(args = { "addowner" }, minArgs = 3, maxArgs = 4, requiresPlayer = false, permission = "worldprotector.region.owners", usage = "/region addowner <player_name> <region_name> [world]")
    public void onRegionAddOwner(CommandSender sender, String[] args) {
        final String worldName;

        if (args.length == 4) {
            worldName = args[3];
        } else if (sender instanceof Player) {
            final Player player = (Player) sender;

            worldName = player.getWorld().getName();
        } else {
            sender.sendMessage(ChatColor.RED + "World name is missing!");
            return;
        }

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
        final Player onlinePlayer = ProtectorPlugin.getOnlinePlayer(playerName);

        if (onlinePlayer != null) {
            region.addOwner(new RegionPlayer(onlinePlayer));

            config.getRegionContainer().save();

            sender.sendMessage(ChatColor.GREEN + "Player with name '" + onlinePlayer.getName() + "' has been added to region with name '"
                    + region.getName() + "'.");
        } else {
            new AddOfflineOwnerToRegionTask(sender, playerName, config, region);

            sender.sendMessage(ChatColor.YELLOW + "Fetching UUID of player with name '" + playerName + "'...");
        }
    }

    @PluginCommand(args = { "removeowner", "deleteowner" }, minArgs = 3, maxArgs = 4, requiresPlayer = false, permission = "worldprotector.region.owners", usage = "/region removeowner <player_name> <region_name> [world]")
    public void onRegionRemoveOwner(CommandSender sender, String[] args) {
        final String worldName;

        if (args.length == 4) {
            worldName = args[3];
        } else if (sender instanceof Player) {
            final Player player = (Player) sender;

            worldName = player.getWorld().getName();
        } else {
            sender.sendMessage(ChatColor.RED + "World name is missing!");
            return;
        }

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

    @PluginCommand(args = { "addmember" }, minArgs = 3, maxArgs = 4, requiresPlayer = false, permission = "worldprotector.region.members", usage = "/region addmember <player_name> <region_name> [world_name]")
    public void onRegionAddMember(CommandSender sender, String[] args) {
        final String worldName;

        if (args.length == 4) {
            worldName = args[3];
        } else if (sender instanceof Player) {
            final Player player = (Player) sender;

            worldName = player.getWorld().getName();
        } else {
            sender.sendMessage(ChatColor.RED + "World name is missing!");
            return;
        }

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
        final Player onlinePlayer = ProtectorPlugin.getOnlinePlayer(playerName);

        if (onlinePlayer != null) {
            region.addMember(new RegionPlayer(onlinePlayer));

            config.getRegionContainer().save();

            sender.sendMessage(ChatColor.GREEN + "Player with name '" + onlinePlayer.getName() + "' has been added to region with name '"
                    + region.getName() + "'.");
        } else {
            new AddOfflineMemberToRegionTask(sender, playerName, config, region);

            sender.sendMessage(ChatColor.YELLOW + "Fetching UUID of player with name '" + playerName + "'...");
        }
    }

    @PluginCommand(args = { "removemember", "deletemember" }, minArgs = 3, maxArgs = 4, requiresPlayer = false, permission = "worldprotector.region.members", usage = "/region removemember <player_name> <region_name> [world_name]")
    public void onRegionRemoveMember(CommandSender sender, String[] args) {
        final String worldName;

        if (args.length == 4) {
            worldName = args[3];
        } else if (sender instanceof Player) {
            final Player player = (Player) sender;

            worldName = player.getWorld().getName();
        } else {
            sender.sendMessage(ChatColor.RED + "World name is missing!");
            return;
        }

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

    @PluginCommand(args = { "setperm", "setpermission" }, minArgs = 3, maxArgs = 4, requiresPlayer = false, permission = "worldprotector.region.permission", usage = "/region setperm <region_name> <permission> [world_name]")
    public void onRegionSetPermission(CommandSender sender, String[] args) {
        final String worldName;

        if (args.length == 4) {
            worldName = args[3];
        } else if (sender instanceof Player) {
            final Player player = (Player) sender;

            worldName = player.getWorld().getName();
        } else {
            sender.sendMessage(ChatColor.RED + "World name is missing!");
            return;
        }

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

    @PluginCommand(args = { "removeperm", "removepermission", "deleteperm", "deletepermission" }, minArgs = 2, maxArgs = 3, requiresPlayer = false, permission = "worldprotector.region.permission", usage = "/region removeperm <region_name> [world_name]")
    public void onRegionRemovePermission(CommandSender sender, String[] args) {
        final String worldName;

        if (args.length == 3) {
            worldName = args[2];
        } else if (sender instanceof Player) {
            final Player player = (Player) sender;

            worldName = player.getWorld().getName();
        } else {
            sender.sendMessage(ChatColor.RED + "World name is missing!");
            return;
        }

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

    @PluginCommand(args = { "flags" }, minArgs = 1, maxArgs = 1, requiresPlayer = false, permission = "worldprotector.region.flags", usage = "/region flags")
    public void onRegionFlagsList(CommandSender sender, String[] args) {
        sender.sendMessage(ProtectorPlugin.getRegionFlagManager().getList());
    }

    @PluginCommand(args = { "setflag" }, minArgs = 4, maxArgs = 5, requiresPlayer = false, permission = "worldprotector.region.flags", usage = "/region setflag <region_name> <flag_name> <flag_state> [world_name]")
    public void onRegionSetFlag(CommandSender sender, String[] args) {
        final String worldName;

        if (args.length == 5) {
            worldName = args[4];
        } else if (sender instanceof Player) {
            final Player player = (Player) sender;

            worldName = player.getWorld().getName();
        } else {
            sender.sendMessage(ChatColor.RED + "World name is missing!");
            return;
        }

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

        final String flagName = args[2];
        final RegionFlag flag = ProtectorPlugin.getRegionFlagManager().getFlag(flagName);

        if (flag == null) {
            sender.sendMessage(ChatColor.RED + "Flag with name '" + flagName + "' does not exist.");
            return;
        }

        final String flagState = args[3];

        if (!flag.applyToRegion(region, flagState)) {
            sender.sendMessage(ChatColor.RED + "Flag with name '" + flag.getName() + "' was not added to region correctly.");
            return;
        }

        config.getRegionContainer().save();

        sender.sendMessage(ChatColor.GREEN + "Flag with name '" + flag.getName() + "' was added to region with name  '" + region.getName() + "'.");
    }

    @PluginCommand(args = { "removeflag" }, minArgs = 3, maxArgs = 4, requiresPlayer = false, permission = "worldprotector.region.flags", usage = "/region removeflag <region_name> <flag_name>")
    public void onRegionRemoveFlag(CommandSender sender, String[] args) {
        final String worldName;

        if (args.length == 4) {
            worldName = args[3];
        } else if (sender instanceof Player) {
            final Player player = (Player) sender;

            worldName = player.getWorld().getName();
        } else {
            sender.sendMessage(ChatColor.RED + "World name is missing!");
            return;
        }

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

        final String flagName = args[2];
        final RegionFlag flag = ProtectorPlugin.getRegionFlagManager().getFlag(flagName);

        if (flag == null) {
            sender.sendMessage(ChatColor.RED + "Flag with name '" + flagName + "' does not exist.");
            return;
        }

        region.removeFlag(flag);

        config.getRegionContainer().save();

        sender.sendMessage(ChatColor.GREEN + "Flag with name '" + flag.getName() + "' was removed from region with name  '" + region.getName() + "'.");
    }

    @PluginCommand(args = { "setparent" }, minArgs = 3, maxArgs = 4, requiresPlayer = false, permission = "worldprotector.region.parents", usage = "/region setparent <region_name> <parent_name> [world_name]")
    public void onRegionSetParent(CommandSender sender, String[] args) {
        final String worldName;

        if (args.length == 4) {
            worldName = args[3];
        } else if (sender instanceof Player) {
            final Player player = (Player) sender;

            worldName = player.getWorld().getName();
        } else {
            sender.sendMessage(ChatColor.RED + "World name is missing!");
            return;
        }

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

        final String parentName = args[2];
        final Region parentRegion = config.getRegionContainer().getRegion(parentName);

        if (parentRegion == null) {
            sender.sendMessage(ChatColor.RED + "Parent region with name '" + parentName + "' does not exist.");
            return;
        }

        if (!region.setParent(parentRegion)) {
            sender.sendMessage(ChatColor.RED + "Region parent was not set correctly.");
            return;
        }

        config.getRegionContainer().save();

        sender.sendMessage(ChatColor.GREEN + "Region with name '" + region.getName() + "' has now parent of region with name  '"
                + parentRegion.getName() + "'.");
    }

    @PluginCommand(args = { "removeparent" }, minArgs = 2, maxArgs = 3, requiresPlayer = false, permission = "worldprotector.region.parents", usage = "/region removeparent <region_name> [world_name]")
    public void onRegionRemoveParent(CommandSender sender, String[] args) {
        final String worldName;

        if (args.length == 3) {
            worldName = args[2];
        } else if (sender instanceof Player) {
            final Player player = (Player) sender;

            worldName = player.getWorld().getName();
        } else {
            sender.sendMessage(ChatColor.RED + "World name is missing!");
            return;
        }

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

        region.setParent(null);

        config.getRegionContainer().save();

        sender.sendMessage(ChatColor.GREEN + "Removed parent from region with name '" + region.getName() + "'.");
    }

    @PluginCommand(args = { "save" }, minArgs = 2, maxArgs = 2, requiresPlayer = false, permission = "worldprotector.region.save", usage = "/region save <world>")
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

    @PluginCommand(args = { "load" }, minArgs = 2, maxArgs = 2, requiresPlayer = false, permission = "worldprotector.region.load", usage = "/region load <world>")
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
