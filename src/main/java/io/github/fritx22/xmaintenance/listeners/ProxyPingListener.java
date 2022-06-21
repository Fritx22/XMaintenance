/*
This file is part of XMaintenance.
XMaintenance is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
XMaintenance is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with XMaintenance. If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.fritx22.xmaintenance.listeners;

import io.github.fritx22.xmaintenance.XMaintenance;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ProxyPingListener implements Listener {

    private final XMaintenance plugin;
    private ServerPing.PlayerInfo[] playerInfoArray;

    public ProxyPingListener(XMaintenance plugin) {
        this.plugin = plugin;

        this.updateMessages();
    }

    public final void updateMessages() {
        String messages = ChatColor.translateAlternateColorCodes('&',
                String.join(
                        "\n", plugin.getConfig().get().getStringList("players-hover-messages")
                )
        );

        this.playerInfoArray = new ServerPing.PlayerInfo[] {new ServerPing.PlayerInfo(messages, "")};
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = 64)
    public void onProxyPing(final ProxyPingEvent e) {
        if(e.getResponse() != null && plugin.getStatusConfig().get().getBoolean("maintenance-enabled")) {
            final ServerPing serverPing = e.getResponse();

            serverPing.getVersion().setProtocol(plugin.getConfig().get().getInt("fake-version-protocol-number"));
            serverPing.getVersion().setName(plugin.getConfig().get().getString("ping-text"));

            if(plugin.getConfig().get().getBoolean("set-players-to-zero")) {
                serverPing.getPlayers().setOnline(0);
                serverPing.getPlayers().setMax(0);
            }

            if(plugin.getConfig().get().getBoolean("enable-players-hover-message"))
                serverPing.getPlayers().setSample(this.playerInfoArray);
            else
                serverPing.getPlayers().setSample(null);

        }
    }
}
