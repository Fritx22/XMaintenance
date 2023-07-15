package io.github.fritx22.xmaintenance.listeners;

import io.github.fritx22.xmaintenance.manager.PingResponseManager;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerCountListener implements Listener {

  private final PingResponseManager pingResponseManager;

  public PlayerCountListener(PingResponseManager pingResponseManager) {
    this.pingResponseManager = pingResponseManager;
  }

  @EventHandler
  public void onConnect(PostLoginEvent ignored) {
    this.pingResponseManager.updatePlayerCount();
  }

  @EventHandler
  public void onDisconnect(PlayerDisconnectEvent ignored) {
    this.pingResponseManager.updatePlayerCount();
  }

}
