/*
This file is part of XMaintenance.
XMaintenance is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
XMaintenance is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with XMaintenance. If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.fritx22.xmaintenance.commands;

import io.github.fritx22.xmaintenance.XMaintenance;
import io.github.fritx22.xmaintenance.enums.MaintenanceTypes;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class MaintenanceCommand extends Command {

    private final XMaintenance plugin;

    public MaintenanceCommand(XMaintenance plugin) {
        super("maintenance", "xmaintenance.admin", "xmaintenance", "xm");
        this.plugin = plugin;
    }

    private void sendHelpMessage(CommandSender sender) {
        //sender.sendMessage(new TextComponent("§c-----------------------------------------------------"));
        sender.sendMessage(new TextComponent("§6          XMaintenance §7- Plugin made by Fritx22"));
        sender.sendMessage(new TextComponent(""));
        sender.sendMessage(new TextComponent("§7Status: " + ( (plugin.getStatusConfig().get().getBoolean("maintenance-enabled")) ? "§aenabled" + " §7[" + plugin.getStatusConfig().getString("maintenance-type") + "§7]" : "§cdisabled" )));
        sender.sendMessage(new TextComponent(""));
        sender.sendMessage(new TextComponent("§7/maintenance enable <type> §7- Enables the maintenance mode"));
        sender.sendMessage(new TextComponent(""));
        sender.sendMessage(new TextComponent("§7Maintenance types:"));
        sender.sendMessage(new TextComponent("§aALL: §7Block connections to all servers"));
        sender.sendMessage(new TextComponent("§aJOIN: §7Block only new connections to the proxy"));
        sender.sendMessage(new TextComponent("§aSERVER: §7Block only server changes so default & fallback server is accessible"));
        sender.sendMessage(new TextComponent("§cEMERGENCY: §7ALL mode + No bypass + Kick-all"));
        sender.sendMessage(new TextComponent(""));
        sender.sendMessage(new TextComponent("§a/maintenance disable §7- Disable maintenance mode"));
        sender.sendMessage(new TextComponent("§a/maintenance reload §7- Reload plugin configuration"));
        //sender.sendMessage(new TextComponent("§c-----------------------------------------------------"));

    }

    public void execute(CommandSender sender, String[] args) {

        if(sender instanceof ProxiedPlayer && !plugin.getConfig().get().getBoolean("allow-players")) {
            sender.sendMessage(new TextComponent("This command is disabled for players."));
            return;
        }

        if(args.length != 1 && args.length != 2) {

            this.sendHelpMessage(sender);
            return;
        }

        switch (args[0]) {
            case "enable" -> {
                if (args.length != 2) {
                    this.sendHelpMessage(sender);
                    return;
                }
                if (plugin.getStatusConfig().get().getBoolean("maintenance-enabled")) {
                    sender.sendMessage(new TextComponent(plugin.getConfig().getString("already-status")));
                    return;
                }
                if (args[1].equalsIgnoreCase("EMERGENCY") && (sender instanceof ProxiedPlayer)) {
                    sender.sendMessage(new TextComponent(plugin.getConfig().getString("plugin-prefix")
                            + "The emergency mode can only be enabled through the console."));
                    return;
                }
                for (MaintenanceTypes type : MaintenanceTypes.values()) {
                    if (type.name().equalsIgnoreCase(args[1])) {
                        plugin.getStatusConfig().get().set("maintenance-enabled", true);
                        plugin.getStatusConfig().get().set("maintenance-type", type.name());

                        plugin.getStatusConfig().saveConfiguration();

                        if (args[1].equalsIgnoreCase("EMERGENCY")) {
                            for (ProxiedPlayer p : plugin.getProxy().getPlayers())
                                p.disconnect(new TextComponent(plugin.getConfig().getString("emergency-kick-message")));
                        }

                        sender.sendMessage(new TextComponent(plugin.getConfig().getString("plugin-prefix")
                                + "§7The maintenance mode has been§a enabled §7[" + type.name() + "]"));

                        return;
                    }
                }
                this.sendHelpMessage(sender);
            }
            case "disable" -> {
                if (!plugin.getStatusConfig().get().getBoolean("maintenance-enabled")) {
                    sender.sendMessage(new TextComponent(plugin.getConfig().getString("already-status")));
                    return;
                }
                plugin.getStatusConfig().get().set("maintenance-enabled", false);
                plugin.getStatusConfig().get().set("maintenance-type", null);
                plugin.getStatusConfig().saveConfiguration();
                sender.sendMessage(new TextComponent(plugin.getConfig().getString("plugin-prefix")
                        + "§7The maintenance mode has been§c disabled" + "§7."));
            }
            case "reload" -> {
                plugin.getConfig().loadConfiguration();
                plugin.getPingListener().updateMessages();
                sender.sendMessage(new TextComponent(plugin.getConfig().getString("plugin-prefix"
                ) + "The configuration has been reloaded."));
            }
            default -> this.sendHelpMessage(sender);
        }

    }
}
