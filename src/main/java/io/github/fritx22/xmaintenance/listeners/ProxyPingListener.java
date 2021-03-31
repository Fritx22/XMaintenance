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

        //List<ServerPing.PlayerInfo> playerInfoList = new ArrayList<>();

        //for(String message : plugin.getConfig().get().getStringList("players-hover-messages"))
            //playerInfoList.add(new ServerPing.PlayerInfo(ChatColor.translateAlternateColorCodes('&', message), ""));

        String messages = ChatColor.translateAlternateColorCodes('&', String.join("\n", plugin.getConfig().get().getStringList("players-hover-messages")));

        //serverPing.getPlayers().setSample((ServerPing.PlayerInfo[]) playerInfoArrayList.toArray());
        //this.playerInfoArray = new ServerPing.PlayerInfo[playerInfoList.size()];

        //for(byte i = 0 ; i < playerInfoList.size() ; i++) {
            //this.playerInfoArray[i] = playerInfoList.get(i);
        //}

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

            /*
            if(plugin.getConfig().get().getBoolean("enable-players-hover-message")) {
                serverPing.getPlayers().setSample(new ServerPing.PlayerInfo[]{
                        new ServerPing.PlayerInfo(plugin.getConfig().getString("players-hover-message"), "")
                });

            } else {
                serverPing.getPlayers().setSample(null);
            }
             */

            if(plugin.getConfig().get().getBoolean("enable-players-hover-message"))
                serverPing.getPlayers().setSample(this.playerInfoArray);
            else
                serverPing.getPlayers().setSample(null);

        }
    }
}
