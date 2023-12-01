/*
This file is part of XMaintenance.
XMaintenance is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
XMaintenance is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with XMaintenance. If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.fritx22.xmaintenance.listeners;

import io.github.fritx22.xmaintenance.XMaintenance;
import io.github.fritx22.xmaintenance.manager.PingResponseManager;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ProxyPingListener implements Listener {

  private final PingResponseManager pingResponseManager;

  public ProxyPingListener(XMaintenance plugin) {
    this.pingResponseManager = plugin.getPingResponseManager();
  }

  @SuppressWarnings("unused")
  @EventHandler(priority = 64)
  public void onProxyPing(ProxyPingEvent e) {
    e.setResponse(this.pingResponseManager.handlePing(e));
  }
}
