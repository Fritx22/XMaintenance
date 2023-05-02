package io.github.fritx22.xmaintenance.manager;

import io.github.fritx22.xmaintenance.XMaintenance;
import io.github.fritx22.xmaintenance.listeners.PlayerCountListener;
import io.github.fritx22.xmaintenance.util.ConfigurationUtil;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;

public class PingResponseManager {

    private final XMaintenance plugin;
    private final ConfigurationUtil config;
    private final ConfigurationUtil statusConfig;
    private final ListenerManager listenerManager;
    private final Listener playerCountListener;

    private final Favicon favicon;
    private final ServerPing.Players players;
    private final ServerPing.Protocol protocol;
    private final ServerPing pingResult;

    public PingResponseManager(XMaintenance plugin) {
        this.plugin = plugin;
        this.config = this.plugin.getConfig();
        this.statusConfig = this.plugin.getStatusConfig();
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
        Configuration config = this.config.get();

        if(config.getBoolean("players-editor.max.enable")) {
            this.players.setMax(config.getInt("players-editor.max.value"));
        }

        if(config.getBoolean("players-editor.online.enable")) {
            this.listenerManager.unregisterListener(this.playerCountListener);
            this.players.setOnline(config.getInt("players-editor.online.value"));
        } else {
            this.updatePlayerCount();
            this.listenerManager.registerListener(this.playerCountListener);
        }

        if(config.getBoolean("players-hover.enable")) {
            String message = String.join("\n", config.getStringList("players-hover.messages"));
            if(this.players.getSample()[0] == null) {
                this.players.getSample()[0] = new ServerPing.PlayerInfo("", "");
            }
            this.players.getSample()[0].setName(message);
            
        } else {
            this.players.getSample()[0] = null;
        }

        this.protocol.setName(config.getString("ping-text"));

        Configuration statusConfig = this.statusConfig.get();
        this.protocol.setProtocol(statusConfig.getInt("fake-version-protocol-number"));
    }

    public void updatePlayerCount() {
        this.players.setOnline(this.plugin.getProxy().getOnlineCount());
    }

    public ServerPing handlePing(ProxyPingEvent event) {
        Configuration config = this.config.get();
        Configuration statusConfig = this.statusConfig.get();

        if(!statusConfig.getBoolean("maintenance-enabled")) {
            return event.getResponse();
        }

        if(!config.getBoolean("players-hover.enable")) {
            this.players.getSample()[0] = event.getResponse().getPlayers().getSample()[0];
        }

        return this.pingResult;
    }


}
