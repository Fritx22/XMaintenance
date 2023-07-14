/*
This file is part of XMaintenance.
XMaintenance is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
XMaintenance is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with XMaintenance. If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.fritx22.xmaintenance.commands;

import io.github.fritx22.xmaintenance.XMaintenance;
import io.github.fritx22.xmaintenance.configuration.MainConfiguration;
import io.github.fritx22.xmaintenance.configuration.StatusConfiguration;
import io.github.fritx22.xmaintenance.maintenance.MaintenanceTypes;
import io.github.fritx22.xmaintenance.manager.MessagingManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class MaintenanceCommand extends Command {

  private final XMaintenance plugin;

  private final MessagingManager messagingManager;

  public MaintenanceCommand(XMaintenance plugin, MessagingManager messagingManager) {
    super("maintenance", "xmaintenance.admin", "xmaintenance", "xm");
    this.plugin = plugin;
    this.messagingManager = messagingManager;
  }

  private void sendHelpMessage(CommandSender sender) {
    StatusConfiguration config = this.plugin.getStatusConfigContainer().getConfig();
    messagingManager.sendMessage(
        sender,
        "§6          XMaintenance §7- Plugin made by Fritx22",
        "",
        "§7Status: " + (config.isMaintenanceEnabled()
            ? "§aenabled" + " §7[" + config.getMaintenanceType() + "§7]"
            : "§cdisabled"),
        "",
        "§7/maintenance enable <type> §7- Enables the maintenance mode",
        "",
        "§7Maintenance types:",
        "§aALL: §7Block connections to all servers",
        "§aJOIN: §7Block only new connections to the proxy",
        "§aSERVER: §7Block only server changes so default & fallback server is accessible",
        "§cEMERGENCY: §7ALL mode + No bypass + Kick-all",
        "",
        "§a/maintenance disable §7- Disable maintenance mode",
        "§a/maintenance reload §7- Reload plugin configuration"
    );

  }

  public void execute(CommandSender sender, String[] args) {
    MainConfiguration mainConfig = this.plugin.getMainConfigContainer().getConfig();

    if (sender instanceof ProxiedPlayer && !mainConfig.allowPlayers()) {
      sender.sendMessage(new TextComponent("This command is disabled for players."));
      return;
    }

    if (args.length != 1 && args.length != 2) {

      this.sendHelpMessage(sender);
      return;
    }

    switch (args[0]) {
      case "enable" -> {
        if (args.length != 2) {
          this.sendHelpMessage(sender);
          return;
        }

        StatusConfiguration statusConfig = this.plugin.getStatusConfigContainer().getConfig();

        if (statusConfig.isMaintenanceEnabled()) {
          sender.sendMessage(new TextComponent(mainConfig.getAlreadyStatus()));
          return;
        }
        if (args[1].equalsIgnoreCase("EMERGENCY") && (sender instanceof ProxiedPlayer)) {
          sender.sendMessage(new TextComponent(mainConfig.getPluginPrefix()
              + "The emergency mode can only be enabled through the console."));
          return;
        }
        for (MaintenanceTypes type : MaintenanceTypes.values()) {
          if (type.name().equalsIgnoreCase(args[1])) {
            statusConfig.setMaintenanceEnabled(true);
            statusConfig.setMaintenanceType(type);

            plugin.getStatusConfigContainer().save()
                .handleAsync((Void result, Throwable exception) -> {
                  if (exception != null) {
                    sender.sendMessage(new TextComponent(
                        mainConfig.getPluginPrefix() +
                            "§cAn error occurred while saving the status, check the console."
                    ));
                  }
                  return result;
                });

            if (args[1].equalsIgnoreCase("EMERGENCY")) {
              for (ProxiedPlayer p : plugin.getProxy().getPlayers()) {
                p.disconnect(new TextComponent(mainConfig.getEmergencyKickMessage()));
              }
            }

            sender.sendMessage(new TextComponent(mainConfig.getPluginPrefix()
                + "§7The maintenance mode has been§a enabled §7[" + type.name() + "]"));

            return;
          }
        }
        this.sendHelpMessage(sender);
      }

      case "disable" -> {
        StatusConfiguration statusConfig = this.plugin.getStatusConfigContainer().getConfig();

        if (!statusConfig.isMaintenanceEnabled()) {
          sender.sendMessage(new TextComponent(mainConfig.getAlreadyStatus()));
          return;
        }
        statusConfig.setMaintenanceEnabled(false);
        plugin.getStatusConfigContainer().save().handleAsync((Void result, Throwable exception) -> {
          if (exception != null) {
            sender.sendMessage(new TextComponent(mainConfig.getPluginPrefix() +
                "§cAn error occurred while saving the status, check the console."
            ));
          } else {
            sender.sendMessage(new TextComponent(mainConfig.getPluginPrefix() +
                "§7The maintenance mode has been§c disabled" + "§7."
            ));
          }
          return result;
        });
      }

      case "reload" -> plugin.getMainConfigContainer().reload().thenAcceptAsync((Void result) ->
          this.plugin.getPingResponseManager().updateConfiguration()
      ).handleAsync((Void result, Throwable exception) -> {
        if (exception != null) {
          sender.sendMessage(new TextComponent(
              mainConfig.getPluginPrefix() +
                  "§cAn error occurred while reloading the plugin configuration, check the console."
          ));
        } else {
          plugin.getPingResponseManager().updateConfiguration();
          sender.sendMessage(new TextComponent(
              mainConfig.getPluginPrefix() +
                  "§aThe plugin configuration has been reloaded successfully."
          ));
        }
        return result;
      });

      default -> this.sendHelpMessage(sender);
    }

  }
}
