package io.github.fritx22.xmaintenance;

import io.github.fritx22.xmaintenance.commands.MaintenanceCommand;
import io.github.fritx22.xmaintenance.listeners.ProxyPingListener;

import io.github.fritx22.xmaintenance.listeners.ServerConnectListener;
import io.github.fritx22.xmaintenance.util.ConfigurationUtil;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.chat.TextComponent;

public class XMaintenance extends Plugin {

    private final ConfigurationUtil config = new ConfigurationUtil(this, "config.yml");
    private final ConfigurationUtil statusConfig = new ConfigurationUtil(this, "status.yml");

    private ProxyPingListener pingListener;

    @Override
    public void onEnable() {

        this.config.createConfiguration();
        this.config.loadConfiguration();

        this.statusConfig.createConfiguration();
        this.statusConfig.loadConfiguration();

        if(this.statusConfig.get().getBoolean("maintenance-enabled"))
            if(!this.statusConfig.get().contains("maintenance-type"))
                for(byte i = 0 ; i < 30 ; i++) {
                    this.getProxy().getLogger().severe("The plugin internal configuration is broken! Please stop the proxy and delete the status.yml file.");
                }

        this.pingListener = new ProxyPingListener(this);
        ServerConnectListener connectListener = new ServerConnectListener(this);

        this.getProxy().getPluginManager().registerCommand(this, new MaintenanceCommand(this));
        this.getProxy().getPluginManager().registerListener(this, pingListener);
        this.getProxy().getPluginManager().registerListener(this, connectListener);

        this.getProxy().getConsole().sendMessage(new TextComponent(""));
        this.getProxy().getConsole().sendMessage(new TextComponent("§6XMaintenance §f[v" + this.getDescription().getVersion() + "] has been enabled."));
        this.getProxy().getConsole().sendMessage(new TextComponent("§7Developed by " + this.getDescription().getAuthor()));
        this.getProxy().getConsole().sendMessage(new TextComponent(""));
    }

    @Override
    public void onDisable() {
        this.getProxy().getPluginManager().unregisterListeners(this);
        this.getProxy().getPluginManager().unregisterCommands(this);
    }

    public ConfigurationUtil getConfig() {
        return this.config;
    }

    public ConfigurationUtil getStatusConfig() {
        return this.statusConfig;
    }

    public ProxyPingListener getPingListener() {
        return this.pingListener;
    }

}
