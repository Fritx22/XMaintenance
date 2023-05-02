/*
This file is part of XMaintenance.
XMaintenance is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
XMaintenance is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with XMaintenance. If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.fritx22.xmaintenance;

import io.github.fritx22.xmaintenance.commands.MaintenanceCommand;

import io.github.fritx22.xmaintenance.manager.ListenerManager;
import io.github.fritx22.xmaintenance.manager.MessagingManager;
import io.github.fritx22.xmaintenance.manager.PingResponseManager;
import io.github.fritx22.xmaintenance.util.ConfigurationUtil;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.api.plugin.PluginManager;

public class XMaintenance extends Plugin {

    private final ProxyServer proxy;
    private final PluginDescription description;
    private final ConfigurationUtil config;
    private final ConfigurationUtil statusConfig;
    private final MessagingManager messagingManager;
    private final ListenerManager listenerManager;
    private final PingResponseManager pingResponseManager;
    private final PluginManager pluginManager = this.getProxy().getPluginManager();

    public XMaintenance() {
        super();

        this.proxy = this.getProxy();
        this.description = this.getDescription();
        this.config = new ConfigurationUtil(this, "config.yml");
        this.statusConfig = new ConfigurationUtil(this, "status.yml");
        this.messagingManager = new MessagingManager(this.proxy);
        this.listenerManager = new ListenerManager(this);
        this.pingResponseManager = new PingResponseManager(this);
    }

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


        this.pluginManager.registerCommand(this, new MaintenanceCommand(this, messagingManager));
        this.listenerManager.registerListeners();

        this.messagingManager.sendConsoleMessage(
                "",
                "§6XMaintenance §f[v" + this.description.getVersion() + "] has been enabled.",
                "§7Developed by " + this.description.getAuthor(),
                ""
        );
    }

    @Override
    public void onDisable() {
        this.listenerManager.unregisterListeners();
        this.pluginManager.unregisterCommands(this);
    }

    public ConfigurationUtil getConfig() {
        return this.config;
    }

    public ConfigurationUtil getStatusConfig() {
        return this.statusConfig;
    }

    public ListenerManager getListenerManager() {
        return this.listenerManager;
    }

    public PingResponseManager getPingResponseManager() {
        return this.pingResponseManager;
    }

}
