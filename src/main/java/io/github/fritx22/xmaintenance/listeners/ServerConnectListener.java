/*
This file is part of XMaintenance.
XMaintenance is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
XMaintenance is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with XMaintenance. If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.fritx22.xmaintenance.listeners;

import io.github.fritx22.xmaintenance.XMaintenance;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.ArrayList;
import java.util.List;

public class ServerConnectListener implements Listener {

    private final XMaintenance plugin;

    public ServerConnectListener(XMaintenance plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerConnect(ServerConnectEvent e) {

        if(e.isCancelled()) return;

        if(plugin.getStatusConfig().get().getBoolean("maintenance-enabled")) {

            String status = plugin.getStatusConfig().getString("maintenance-type");

            if(plugin.getConfig().get().getBoolean("enable-bypass") && e.getPlayer().hasPermission("xmaintenance.bypass") && !status.equals("EMERGENCY"))
                return;

            switch(status) {
                case "ALL":
                case "EMERGENCY":
                    this.cancelConnection(e);
                    break;
                case "JOIN":
                    if(e.getReason() == ServerConnectEvent.Reason.JOIN_PROXY)
                        this.cancelConnection(e);
                    break;
                case "SERVER":
                    if(!(e.getReason() == ServerConnectEvent.Reason.JOIN_PROXY) || !(e.getReason() == ServerConnectEvent.Reason.LOBBY_FALLBACK))
                        this.cancelConnection(e);
                    break;
            }


        }

    }


    private void cancelConnection(ServerConnectEvent e) {
        e.setCancelled(true);
        e.getPlayer().disconnect(new TextComponent(plugin.getConfig().getString("kick-message")));

        //ArrayList<TextComponent> messages = new ArrayList<TextComponent>(plugin.getConfig().get().getStringList("admin-alert-messages").forEach( (message) -> message.replace("%player%", e.getPlayer().getName())));
        //List<String> messages = plugin.getConfig().get().getStringList("admin-alert-messages");

        //messages.forEach((message) -> messages.set(messages.indexOf(message), message.replace("%player%", e.getPlayer().getName())));

        List<TextComponent> messages = new ArrayList<>();

        for(String message : plugin.getConfig().get().getStringList("admin-alert-messages")) {
            messages.add(new TextComponent(
                    ChatColor.translateAlternateColorCodes('&',
                            message.replace("%player%", e.getPlayer().getName())
                                    .replace("%prefix%", plugin.getConfig().getString("plugin-prefix")))
            ));
        }


        //plugin.getProxy().getConsole().sendMessage(messages);
        //messages.forEach((message) -> plugin.getProxy().getConsole().sendMessage(new TextComponent(message)));
        messages.forEach((message) -> plugin.getProxy().getConsole().sendMessage(message));

        for(ProxiedPlayer p : plugin.getProxy().getPlayers()) {
            if(p.hasPermission("xmaintenance.admin"))
                messages.forEach(p::sendMessage);
        }

    }

}
