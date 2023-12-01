/*
This file is part of XMaintenance.
XMaintenance is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
XMaintenance is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with XMaintenance. If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.fritx22.xmaintenance.commands;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

import io.github.fritx22.xmaintenance.XMaintenance;
import io.github.fritx22.xmaintenance.configuration.MainConfiguration;
import io.github.fritx22.xmaintenance.maintenance.MaintenanceManager;
import io.github.fritx22.xmaintenance.maintenance.MaintenanceType;
import io.github.fritx22.xmaintenance.service.MessagingProviderService;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class MaintenanceCommand extends Command {

  private final XMaintenance plugin;
  private final MessagingProviderService messagingProviderService;
  private final MaintenanceManager maintenanceManager;
  private final String helpMessage;


  public MaintenanceCommand(
      XMaintenance plugin,
      MessagingProviderService messagingProviderService,
      MaintenanceManager maintenanceManager
  ) {
    super("maintenance", "xmaintenance.admin", "xmaintenance", "xm");
    this.plugin = plugin;
    this.messagingProviderService = messagingProviderService;
    this.maintenanceManager = maintenanceManager;

    this.helpMessage = "<gold>          XMaintenance <gray>- Plugin made by Fritx22</gray></gold><br>"
        + "<br>"
        + "<gray>Status: </gray><status><br>"
        + "<gray>/maintenance enable <type> - Enables the maintenance mode</gray><br>"
        + "<br>"
        + "<gray>Maintenance types:</gray><br>"
        + "<green>ALL:</green> <gray>Block connections to all servers</gray><br>"
        + "<green>JOIN:</green> <gray>Block only new connections to the proxy</gray><br>"
        + "<green>SERVER:</green> <gray>Block only server changes so default & fallback server is accessible</gray><br>"
        + "<red>EMERGENCY:</red> <gray>ALL mode + No bypass + Kick-all</gray><br>"
        + "<br>"
        + "<green>/maintenance disable</green> <gray>- Disable maintenance mode</gray><br>"
        + "<green>/maintenance reload</green> <gray>- Reload plugin configuration</gray><br>";
  }

  private void sendHelpMessage(Audience audience) {
    String status;
    if (this.maintenanceManager.isEnabled()) {
      status = "<green>enabled [" + this.maintenanceManager.getType() + "]</green><br>";
    } else {
      status = "<red>disabled</red><br>";
    }
    audience.sendMessage(miniMessage().deserialize(
        this.helpMessage,
        Placeholder.parsed("status", status)
    ));

  }

  public void execute(CommandSender sender, String[] args) {
    MainConfiguration mainConfig = this.plugin.getMainConfigContainer().getConfig();

    Audience senderAudience = this.messagingProviderService.sender(sender);

    if (sender instanceof ProxiedPlayer && !mainConfig.allowPlayers()) {
      senderAudience.sendMessage(Component.text("This command is disabled for players."));
      return;
    }

    if (args.length != 1 && args.length != 2) {

      this.sendHelpMessage(senderAudience);
      return;
    }

    switch (args[0]) {
      case "enable" -> {
        if (args.length != 2) {
          this.sendHelpMessage(senderAudience);
          return;
        }

        if (this.maintenanceManager.isEnabled()) {
          senderAudience.sendMessage(
              mainConfig.getAlreadyStatus(this.maintenanceManager.getStatus()));
          return;
        }
        if (args[1].equalsIgnoreCase("EMERGENCY") && (sender instanceof ProxiedPlayer)) {
          senderAudience.sendMessage(mainConfig.getPluginPrefix().append(Component.text(
                  "The emergency mode can only be enabled through the console."
              ))
          );
          return;
        }
        for (MaintenanceType type : MaintenanceType.values()) {
          if (type.name().equalsIgnoreCase(args[1])) {
            this.maintenanceManager.setType(type);
            this.maintenanceManager.setEnabled(true);

            this.maintenanceManager.saveStatusConfiguration()
                .handleAsync((Void result, Throwable exception) -> {
                  if (exception == null) {
                    return result;
                  }
                  senderAudience.sendMessage(mainConfig.getPluginPrefix().append(
                      miniMessage().deserialize(
                          "<red>An error occurred while saving the status, check the console.</red>"
                      ))
                  );
                  return result;
                });

            this.plugin.getLogger().info("Enabling " + type + " mode...");

            senderAudience.sendMessage(
                mainConfig.getPluginPrefix().append(
                    miniMessage().deserialize(
                        "<gray>The maintenance mode has been <green>enabled</green> [<name>]</gray>",
                        Placeholder.unparsed("name", type.toString())
                    )
                )
            );

            return;
          }
        }
        this.sendHelpMessage(senderAudience);
      }

      case "disable" -> {

        if (!this.maintenanceManager.isEnabled()) {
          senderAudience.sendMessage(
              mainConfig.getAlreadyStatus(this.maintenanceManager.getStatus()));
          return;
        }
        this.maintenanceManager.setEnabled(false);
        this.maintenanceManager.saveStatusConfiguration().handleAsync(
            (Void result, Throwable exception) -> {
              if (exception != null) {
                senderAudience.sendMessage(
                    mainConfig.getPluginPrefix().append(
                        Component.text(
                            "An error occurred while saving the status, check the console.",
                            NamedTextColor.RED))
                );
              } else {
                senderAudience.sendMessage(mainConfig.getPluginPrefix().append(
                    miniMessage().deserialize(
                        "<gray>The maintenance mode has been <red>disabled</red>.</gray>"
                    )
                ));
              }
              return result;
            });
      }

      case "reload" -> plugin.getMainConfigContainer().reload().thenAcceptAsync((Void result) ->
          this.plugin.getPingResponseManager().updateConfiguration()
      ).handleAsync((Void result, Throwable exception) -> {
        if (exception != null) {
          senderAudience.sendMessage(mainConfig.getPluginPrefix().append(
              miniMessage().deserialize(
                  "<red>An error occurred while reloading the plugin configuration, check the console.</red>"
              )
          ));
        } else {
          senderAudience.sendMessage(mainConfig.getPluginPrefix().append(
              miniMessage().deserialize(
                  "<green>The plugin configuration has been reloaded successfully.</green>"
              )
          ));
        }
        return result;
      });

      default -> this.sendHelpMessage(senderAudience);
    }

  }
}
