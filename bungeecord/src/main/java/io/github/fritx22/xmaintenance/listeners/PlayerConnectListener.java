package io.github.fritx22.xmaintenance.listeners;

import io.github.fritx22.xmaintenance.maintenance.BungeeMaintenancePlayer;
import io.github.fritx22.xmaintenance.maintenance.MaintenanceManager;
import io.github.fritx22.xmaintenance.service.MessagingProviderService;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerConnectListener implements Listener {
  private final MessagingProviderService messagingService;
  private final MaintenanceManager maintenanceManager;

  public PlayerConnectListener(
      MessagingProviderService messagingService,
      MaintenanceManager maintenanceManager
  ) {
    this.messagingService = messagingService;
    this.maintenanceManager = maintenanceManager;
  }

  @EventHandler
  public void onConnect(PostLoginEvent event) {
    messagingService.console().sendMessage(Component.text("Registering player " + event.getPlayer().getName()));
    this.maintenanceManager.registerPlayer(new BungeeMaintenancePlayer(event.getPlayer()));
  }

  @EventHandler
  public void onDisconnect(PlayerDisconnectEvent event) {
    messagingService.console().sendMessage(Component.text("Unregistering player " + event.getPlayer().getName()));
    this.maintenanceManager.unregisterPlayer(event.getPlayer().getUniqueId());
  }
}
