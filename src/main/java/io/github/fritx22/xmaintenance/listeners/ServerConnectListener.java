/*
This file is part of XMaintenance.
XMaintenance is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
XMaintenance is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with XMaintenance. If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.fritx22.xmaintenance.listeners;

import io.github.fritx22.xmaintenance.XMaintenance;
import io.github.fritx22.xmaintenance.configuration.MainConfiguration;
import io.github.fritx22.xmaintenance.configuration.StatusConfiguration;
import io.github.fritx22.xmaintenance.maintenance.MaintenanceTypes;
import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class ServerConnectListener implements Listener {

  private final XMaintenance plugin;

  public ServerConnectListener(XMaintenance plugin) {
    this.plugin = plugin;
  }

  @SuppressWarnings("unused")
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onServerConnect(ServerConnectEvent e) {

    if (e.isCancelled()) {
      return;
    }

    StatusConfiguration statusConfig = this.plugin.getStatusConfigContainer().getConfig();

    if (statusConfig.isMaintenanceEnabled()) {

      MaintenanceTypes status = statusConfig.getMaintenanceType();

      MainConfiguration mainConfig = this.plugin.getMainConfigContainer().getConfig();

      if (
          mainConfig.enableBypass() &&
              e.getPlayer().hasPermission("xmaintenance.bypass") &&
              !status.equals(MaintenanceTypes.EMERGENCY)
      ) {
        return;
      }

      switch (status) {
        case ALL, EMERGENCY -> this.cancelConnection(e);
        case JOIN -> {
          if (e.getReason() == ServerConnectEvent.Reason.JOIN_PROXY) {
            this.cancelConnection(e);
          }
        }
        case SERVER -> {
          if (e.getReason() != ServerConnectEvent.Reason.JOIN_PROXY &&
              e.getReason() != ServerConnectEvent.Reason.LOBBY_FALLBACK) {
            this.cancelConnection(e);
          }
        }
      }


    }

  }


  private void cancelConnection(ServerConnectEvent e) {
    e.setCancelled(true);
    MainConfiguration mainConfig = this.plugin.getMainConfigContainer().getConfig();
    e.getPlayer().disconnect(new TextComponent(mainConfig.getKickMessage()));

    List<TextComponent> messages = new ArrayList<>();

    for (String message : mainConfig.getAdminAlertMessages()) {
      messages.add(new TextComponent(
          ChatColor.translateAlternateColorCodes('&',
              message.replace("%player%", e.getPlayer().getName())
                  .replace("%prefix%", mainConfig.getPluginPrefix()))
      ));
    }

    messages.forEach((message) -> plugin.getProxy().getConsole().sendMessage(message));

    for (ProxiedPlayer p : plugin.getProxy().getPlayers()) {
      if (p.hasPermission("xmaintenance.admin")) {
        messages.forEach(p::sendMessage);
      }
    }

  }

}
