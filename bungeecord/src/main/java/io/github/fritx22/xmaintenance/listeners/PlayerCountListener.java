/*
This file is part of XMaintenance.
XMaintenance is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
XMaintenance is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with XMaintenance. If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.fritx22.xmaintenance.listeners;

import io.github.fritx22.xmaintenance.manager.PingResponseManager;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerCountListener implements Listener {

  private final PingResponseManager pingResponseManager;

  public PlayerCountListener(
      PingResponseManager pingResponseManager
  ) {
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
