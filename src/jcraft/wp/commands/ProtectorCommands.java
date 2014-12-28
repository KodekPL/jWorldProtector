package jcraft.wp.commands;

import jcraft.wp.ProtectorPlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ProtectorCommands extends CommandHandler {

    @Override
    public void noArgsCommand(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.YELLOW + "jWorldProtector v" + ProtectorPlugin.getPlugin().getDescription().getVersion() + " by Kodek");
    }

    @PluginCommand(args = { "reload" }, maxArgs = 1, minArgs = 1, requiresPlayer = false, permission = "worldprotector.reload", usage = "/wp reload")
    public void onRegionDefine(CommandSender sender, String[] args) {
        // Worlds reload
        ProtectorPlugin.getWorldsManager().reset();
        ProtectorPlugin.getWorldsManager().loadWorlds();

        sender.sendMessage(ChatColor.GREEN + "jWorldProtector reloaded.");
    }

}
