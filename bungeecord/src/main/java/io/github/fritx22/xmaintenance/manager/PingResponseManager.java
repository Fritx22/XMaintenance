package io.github.fritx22.xmaintenance.manager;

import io.github.fritx22.xmaintenance.XMaintenance;
import io.github.fritx22.xmaintenance.configuration.ConfigurationContainer;
import io.github.fritx22.xmaintenance.configuration.MainConfiguration;
import io.github.fritx22.xmaintenance.configuration.StatusConfiguration;
import io.github.fritx22.xmaintenance.listeners.PlayerCountListener;
import java.util.List;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;

public class PingResponseManager {

  private final XMaintenance plugin;
  private final ConfigurationContainer<MainConfiguration> mainConfigContainer;
  private final ConfigurationContainer<StatusConfiguration> statusConfigContainer;
  private final ListenerManager listenerManager;
  private final Listener playerCountListener;

  private final Favicon favicon;
  private final ServerPing.Players players;
  private final ServerPing.Protocol protocol;
  private final ServerPing pingResult;

  public PingResponseManager(XMaintenance plugin) {
    this.plugin = plugin;
    this.mainConfigContainer = this.plugin.getMainConfigContainer();
    this.statusConfigContainer = this.plugin.getStatusConfigContainer();
    this.listenerManager = this.plugin.getListenerManager();
    this.playerCountListener = new PlayerCountListener(this);

    this.favicon = this.plugin.getProxy().getConfig().getFaviconObject();
    this.players = new ServerPing.Players(0, 0, new ServerPing.PlayerInfo[1]);
    this.protocol = new ServerPing.Protocol("", -1);

    BaseComponent description = new ComponentBuilder("").getComponent(0);
    this.pingResult = new ServerPing(this.protocol, this.players, description, this.favicon);

    this.updateConfiguration();
  }

  public void updateConfiguration() {
    MainConfiguration mainConfig = this.mainConfigContainer.getConfig();

    MainConfiguration.ToggledValue<Integer> max = mainConfig.getPlayersEditor().getMax();
    if (max.isEnabled()) {
      this.players.setMax(max.getValue());
    }

    MainConfiguration.ToggledValue<Integer> online = mainConfig.getPlayersEditor().getOnline();
    if (online.isEnabled()) {
      this.listenerManager.unregisterListener(this.playerCountListener);
      this.players.setOnline(online.getValue());
    } else {
      this.updatePlayerCount();
      this.listenerManager.registerListener(this.playerCountListener);
    }

    MainConfiguration.ToggledValue<List<String>> playersHover = mainConfig.getPlayersHover();
    if (playersHover.isEnabled()) {
      String message = String.join("\n", playersHover.getValue());
      if (this.players.getSample()[0] == null) {
        this.players.getSample()[0] = new ServerPing.PlayerInfo("", "");
      }
      this.players.getSample()[0].setName(message);

    } else {
      this.players.getSample()[0] = null;
    }

    this.protocol.setName(mainConfig.getPingText());

    this.protocol.setProtocol(mainConfig.getFakeVersionProtocolNumber());
  }

  public void updatePlayerCount() {
    this.players.setOnline(this.plugin.getProxy().getOnlineCount());
  }

  public ServerPing handlePing(ProxyPingEvent event) {
    MainConfiguration mainConfig = this.mainConfigContainer.getConfig();
    StatusConfiguration statusConfig = this.statusConfigContainer.getConfig();

    if (!statusConfig.isMaintenanceEnabled()) {
      return event.getResponse();
    }

    if (!mainConfig.getPlayersHover().isEnabled()) {
      this.players.getSample()[0] = event.getResponse().getPlayers().getSample()[0];
    }

    return this.pingResult;
  }


}
