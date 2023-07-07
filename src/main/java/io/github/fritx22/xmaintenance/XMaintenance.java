/*
This file is part of XMaintenance.
XMaintenance is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
XMaintenance is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with XMaintenance. If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.fritx22.xmaintenance;

import io.github.fritx22.xmaintenance.commands.MaintenanceCommand;

import io.github.fritx22.xmaintenance.configuration.MainConfiguration;
import io.github.fritx22.xmaintenance.configuration.StatusConfiguration;
import io.github.fritx22.xmaintenance.manager.ListenerManager;
import io.github.fritx22.xmaintenance.manager.MessagingManager;
import io.github.fritx22.xmaintenance.manager.PingResponseManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.api.plugin.PluginManager;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.nio.file.Path;

public class XMaintenance extends Plugin {

    @SuppressWarnings("FieldCanBeLocal")
    private final ProxyServer proxy;
    private final PluginDescription description;
    private final HoconConfigurationLoader mainConfigLoader;
    private MainConfiguration mainConfig;
    private final HoconConfigurationLoader statusConfigLoader;
    private StatusConfiguration statusConfig;
    private final MessagingManager messagingManager;
    private final ListenerManager listenerManager;
    private final PingResponseManager pingResponseManager;
    private final PluginManager pluginManager = this.getProxy().getPluginManager();

    public XMaintenance() {
        super();

        this.proxy = this.getProxy();
        this.description = this.getDescription();
        this.mainConfigLoader = HoconConfigurationLoader.builder().defaultOptions(
                opts -> opts.shouldCopyDefaults(true)
                        .header("XMaintenance plugin configuration\nCopyright (C) 2020 Fritx22")
                ).path(Path.of("config.conf"))
                .build();
        this.statusConfigLoader = HoconConfigurationLoader.builder().defaultOptions(
                opts -> opts.shouldCopyDefaults(true)
                        .header(
                                "Don't edit this file! "
                                        + "This is for saving the maintenance status when the server is restarted"
                        )
                ).path(Path.of("status.conf"))
                .build();
        this.messagingManager = new MessagingManager(this.proxy);
        this.listenerManager = new ListenerManager(this);
        this.pingResponseManager = new PingResponseManager(this);
    }

    @Override
    public void onEnable() {
        this.loadConfiguration();

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

    public void loadConfiguration() {
        try {
            this.mainConfig = this.mainConfigLoader.load().get(MainConfiguration.class);
            this.statusConfig = this.statusConfigLoader.load().get(StatusConfiguration.class);
        } catch(ConfigurateException exception) {
            this.getProxy().getLogger().severe("An error occurred while loading the configuration!");
            exception.printStackTrace();
        }
    }

    public MainConfiguration getMainConfig() {
        return this.mainConfig;
    }

    public StatusConfiguration getStatusConfig() {
        return this.statusConfig;
    }

    public ListenerManager getListenerManager() {
        return this.listenerManager;
    }

    public PingResponseManager getPingResponseManager() {
        return this.pingResponseManager;
    }

}
