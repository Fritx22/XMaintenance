/*
This file is part of XMaintenance.
XMaintenance is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
XMaintenance is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with XMaintenance. If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.fritx22.xmaintenance.listeners;

import io.github.fritx22.xmaintenance.XMaintenance;
import io.github.fritx22.xmaintenance.configuration.MainConfiguration;
import io.github.fritx22.xmaintenance.maintenance.MaintenanceManager;
import io.github.fritx22.xmaintenance.maintenance.MaintenancePlayer;
import io.github.fritx22.xmaintenance.maintenance.TriggerReason;
import io.github.fritx22.xmaintenance.service.MessagingProviderService;
import java.util.List;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class ServerConnectListener implements Listener {

  private final XMaintenance plugin;
  private final MessagingProviderService messagingProviderService;
  private final MaintenanceManager maintenanceManager;

  public ServerConnectListener(
      XMaintenance plugin,
      MessagingProviderService messagingProviderService
  ) {
    this.plugin = plugin;
    this.messagingProviderService = messagingProviderService;
    this.maintenanceManager = this.plugin.getMaintenanceManager();
  }

  @SuppressWarnings("unused")
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onServerConnect(ServerConnectEvent event) {

    if (event.isCancelled()) {
      return;
    }

    MaintenancePlayer player = this.maintenanceManager.getPlayer(event.getPlayer().getUniqueId());
    TriggerReason reason = switch(event.getReason()) {
      case JOIN_PROXY -> TriggerReason.PROXY_CONNECT;
      case LOBBY_FALLBACK -> TriggerReason.DEFAULT_OR_FALLBACK;
      default -> TriggerReason.UNKNOWN;
    };
    boolean allowed = this.maintenanceManager.isAllowed(player, reason);
    if (!allowed) {
      this.plugin.getLogger().info("Cancelling connection for player " + player.getName());
      this.cancelConnection(event);
    }

  }

  private void cancelConnection(ServerConnectEvent event) {
    event.setCancelled(true);
    MainConfiguration mainConfig = this.plugin.getMainConfigContainer().getConfig();
    MaintenancePlayer player = this.maintenanceManager.getPlayer(event.getPlayer().getUniqueId());
    player.disconnect(mainConfig.getKickMessage());

    List<Component> messages = mainConfig.getAdminAlertMessages(player.getName());

    // TODO: Should we save this Audience?
    Audience admins = this.messagingProviderService.permission("xmaintenance.admin");
    messages.forEach(admins::sendMessage);

  }

}
