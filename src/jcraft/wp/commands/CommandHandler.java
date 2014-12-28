package jcraft.wp.commands;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class CommandHandler implements CommandExecutor {

    private final Map<String, Set<Method>> commands = new HashMap<String, Set<Method>>();

    public CommandHandler() {
        this.register();
    }

    private void register() {
        for (Method method : this.getClass().getDeclaredMethods()) {
            method.setAccessible(true);

            if (method.isAnnotationPresent(PluginCommand.class)) {
                PluginCommand command = method.getAnnotation(PluginCommand.class);

                for (String alias : command.args()) {
                    Set<Method> cmds;

                    if (commands.containsKey(alias)) {
                        cmds = commands.get(alias);
                    } else {
                        cmds = new HashSet<Method>();
                    }

                    cmds.add(method);
                    commands.put(alias, cmds);
                }
            }
        }
    }

    public abstract void noArgsCommand(CommandSender sender, String[] args);

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            noArgsCommand(sender, args);
            return true;
        }

        final Set<Method> cmdMethods = commands.get(args[0]);

        if (cmdMethods != null) {
            PluginCommand command = null;

            for (Method cmdMethod : cmdMethods) {
                command = cmdMethod.getAnnotation(PluginCommand.class);

                if (command.requiresPlayer() && !(sender instanceof Player)) {
                    continue;
                }

                if (command.requiresPlayer() && !sender.hasPermission(command.permission())) {
                    sender.sendMessage(ChatColor.RED + "Permission is required - " + command.permission());
                    continue;
                }

                if (command.argsAmount() != args.length) {
                    if (!command.requiresPlayer() && sender instanceof Player) {
                        continue;
                    }

                    sender.sendMessage(ChatColor.YELLOW + " Usage: " + command.usage());
                    continue;
                }

                return invokeMethod(cmdMethod, sender, args);
            }

            if (command != null && command.requiresPlayer() && !(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command is accessible only for players.");
            }
        }

        return false;
    }

    private boolean invokeMethod(Method method, Object... args) {
        try {
            method.invoke(this, args);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
