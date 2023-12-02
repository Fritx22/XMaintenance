/*
This file is part of XMaintenance.
XMaintenance is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
XMaintenance is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with XMaintenance. If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.fritx22.xmaintenance.manager;

import io.github.fritx22.xmaintenance.XMaintenance;
import io.github.fritx22.xmaintenance.configuration.ConfigurationContainer;
import io.github.fritx22.xmaintenance.configuration.MainConfiguration;
import io.github.fritx22.xmaintenance.configuration.MainConfiguration.ToggledValue;
import io.github.fritx22.xmaintenance.listeners.PlayerCountListener;
import io.github.fritx22.xmaintenance.maintenance.MaintenanceManager;
import java.util.List;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import org.jetbrains.annotations.NotNull;

public class PingResponseManager {

  private final XMaintenance plugin;
  private final ConfigurationContainer<MainConfiguration> mainConfigContainer;
  private final ListenerManagerService listenerManager;
  private final PlayerCountListener playerCountListener;

  private final Favicon favicon;
  private final ServerPing.Players players;
  private final ServerPing.Protocol protocol;
  private final ServerPing pingResult;
  private final MaintenanceManager maintenanceManager;
  private TextComponent messageOfTheDay;

  public PingResponseManager(
      @NotNull XMaintenance plugin,
      @NotNull ListenerManagerService listenerManager
  ) {
    this.plugin = plugin;
    this.mainConfigContainer = this.plugin.getMainConfigContainer();
    this.listenerManager = listenerManager;
    this.playerCountListener = new PlayerCountListener(this);

    this.favicon = this.plugin.getProxy().getConfig().getFaviconObject();
    this.players = new ServerPing.Players(0, 0, new ServerPing.PlayerInfo[1]);
    this.protocol = new ServerPing.Protocol("", -1);

    BaseComponent description = new ComponentBuilder("").getComponent(0);
    this.pingResult = new ServerPing(this.protocol, this.players, description, this.favicon);

    this.maintenanceManager = this.plugin.getMaintenanceManager();

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
      this.listenerManager.registerInstance(PlayerCountListener.class, this.playerCountListener);
    }

    MainConfiguration.ToggledValue<List<String>> playersHover = mainConfig.getLegacyPlayersHover();
    if (playersHover.isEnabled()) {
      String message = String.join("\n", playersHover.getValue());
      if (this.players.getSample()[0] == null) {
        this.players.getSample()[0] = new ServerPing.PlayerInfo("", "");
      }
      this.players.getSample()[0].setName(message);

    } else {
      this.players.getSample()[0] = null;
    }

    this.protocol.setName(mainConfig.getLegacyPingText());

    this.protocol.setProtocol(mainConfig.getFakeVersionProtocolNumber());

    ToggledValue<List<String>> messageOfTheDay = mainConfig.getLegacyMaintenanceMessageOfTheDay();
    if (messageOfTheDay.isEnabled()) {
      this.messageOfTheDay = new TextComponent(
          String.join("\n", messageOfTheDay.getValue())
      );
    } else {
      this.messageOfTheDay = null;
    }
  }

  public void updatePlayerCount() {
    this.players.setOnline(this.plugin.getProxy().getOnlineCount());
  }

  public ServerPing handlePing(ProxyPingEvent event) {

    if (!this.maintenanceManager.isEnabled()) {
      return event.getResponse();
    }

    MainConfiguration mainConfig = this.mainConfigContainer.getConfig();

    if (!mainConfig.getPlayersHover().isEnabled()) {
      this.players.getSample()[0] = event.getResponse().getPlayers().getSample()[0];
    }

    if (this.messageOfTheDay == null) {
      ServerPing response = event.getResponse();
      this.pingResult.setDescriptionComponent(response.getDescriptionComponent());
    } else {
      this.pingResult.setDescriptionComponent(this.messageOfTheDay);
    }

    return this.pingResult;
  }


}
