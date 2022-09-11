/*
This file is part of XMaintenance.
XMaintenance is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
XMaintenance is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with XMaintenance. If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.fritx22.xmaintenance;

import io.github.fritx22.xmaintenance.commands.MaintenanceCommand;
import io.github.fritx22.xmaintenance.listeners.ProxyPingListener;

import io.github.fritx22.xmaintenance.listeners.ServerConnectListener;
import io.github.fritx22.xmaintenance.manager.MessagingManager;
import io.github.fritx22.xmaintenance.util.ConfigurationUtil;
import net.md_5.bungee.api.plugin.Plugin;

public class XMaintenance extends Plugin {

    private final ConfigurationUtil config = new ConfigurationUtil(this, "config.yml");
    private final ConfigurationUtil statusConfig = new ConfigurationUtil(this, "status.yml");
    private final MessagingManager messagingManager = new MessagingManager(this.getProxy());

    private ProxyPingListener pingListener;

    @Override
    public void onEnable() {

        this.config.createConfiguration();
        this.config.loadConfiguration();

        this.statusConfig.createConfiguration();
        this.statusConfig.loadConfiguration();

        if(this.statusConfig.get().getBoolean("maintenance-enabled"))
            if(!this.statusConfig.get().contains("maintenance-type"))
                this.getProxy().getLogger().severe(
                        "The plugin internal configuration is broken! Please stop the proxy and delete the status.yml file."
                );

        this.pingListener = new ProxyPingListener(this);
        ServerConnectListener connectListener = new ServerConnectListener(this);

        this.getProxy().getPluginManager().registerCommand(this, new MaintenanceCommand(this, messagingManager));
        this.getProxy().getPluginManager().registerListener(this, pingListener);
        this.getProxy().getPluginManager().registerListener(this, connectListener);

        this.messagingManager.sendConsoleMessage(
                "",
                "§6XMaintenance §f[v" + this.getDescription().getVersion() + "] has been enabled.",
                "§7Developed by " + this.getDescription().getAuthor(),
                ""
        );
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
