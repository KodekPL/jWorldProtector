package jcraft.wp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class RegionCommands extends CommandHandler {

    @Override
    public void noArgsCommand(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.YELLOW
                + "/region <define/redefine/select/addmember/removemember/addowner/removeowner/info/flag/setparent/remove/list/tp/reload> [args...]");
    }

    @PluginCommand(args = { "define" }, argsAmount = 2, requiresPlayer = true, permission = "worldprotector.region")
    public void onRegionDefine(CommandSender sender, String[] args) {
        sender.sendMessage("Not yet implemented!");
    }

}
