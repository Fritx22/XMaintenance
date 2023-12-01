/*
This file is part of XMaintenance.
XMaintenance is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
XMaintenance is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with XMaintenance. If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.fritx22.xmaintenance;

import io.github.fritx22.xmaintenance.commands.MaintenanceCommand;
import io.github.fritx22.xmaintenance.configuration.ConfigurationContainer;
import io.github.fritx22.xmaintenance.configuration.MainConfiguration;
import io.github.fritx22.xmaintenance.configuration.StatusConfiguration;
import io.github.fritx22.xmaintenance.maintenance.MaintenanceManager;
import io.github.fritx22.xmaintenance.manager.ListenerManagerService;
import io.github.fritx22.xmaintenance.manager.PingResponseManager;
import io.github.fritx22.xmaintenance.service.BungeeMessagingProviderService;
import io.github.fritx22.xmaintenance.service.MessagingProviderService;
import io.github.fritx22.xmaintenance.service.Service;
import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.api.plugin.PluginManager;

public class XMaintenance extends Plugin {

  private final PluginDescription description;
  private final ConfigurationContainer<MainConfiguration> mainConfigContainer;
  private final ConfigurationContainer<StatusConfiguration> statusConfigContainer;
  private final PingResponseManager pingResponseManager;
  private final PluginManager pluginManager = this.getProxy().getPluginManager();
  private final MaintenanceManager maintenanceManager;
  private final MessagingProviderService messagingService;
  private final Set<Service> services;

  public XMaintenance() {
    super();

    this.description = this.getDescription();

    this.services = new HashSet<>();

    this.messagingService = new BungeeMessagingProviderService(this);
    this.services.add(this.messagingService);

    ListenerManagerService listenerManagerService = new ListenerManagerService(
        this,
        this.getProxy().getPluginManager(),
        this.messagingService
    );
    this.services.add(listenerManagerService);

    File folder = this.getDataFolder();
    this.mainConfigContainer = ConfigurationContainer.load(
        MainConfiguration.class,
        this.getLogger(),
        Path.of(folder.toString(), "config.conf"),
        "XMaintenance plugin configuration\nCopyright (C) 2020 Fritx22"
    );
    this.statusConfigContainer = ConfigurationContainer.load(
        StatusConfiguration.class,
        this.getLogger(),
        Path.of(folder.toString(), "status.conf"),
        "Don't edit this file! " +
            "This is for saving the maintenance status when the server is restarted"
    );
    if (this.mainConfigContainer == null || this.statusConfigContainer == null) {
      this.getLogger().severe("Main or status config is null");
      throw new IllegalStateException();
    }

    StatusConfiguration status = this.statusConfigContainer.getConfig();
    this.maintenanceManager = new MaintenanceManager(
        status.getMaintenanceType(),
        status.isMaintenanceEnabled(),
        this.mainConfigContainer,
        this.statusConfigContainer
    );

    this.pingResponseManager = new PingResponseManager(this, listenerManagerService);
  }

  @Override
  public void onEnable() {
    this.services.forEach(Service::start);

    this.pluginManager.registerCommand(
        this,
        new MaintenanceCommand(
            this,
            this.messagingService,
            this.maintenanceManager
        )
    );

    String message = "<br><gold>XMaintenance</gold> <white>[v"
        + this.description.getVersion()
        + "] has been enabled.</white><br>"
        + "<gray>Developed by "
        + this.description.getAuthor()
        + "</gray>";

    this.messagingService.console().sendMessage(
        MiniMessage.miniMessage().deserialize(message)
    );
  }

  @Override
  public void onDisable() {
    this.pluginManager.unregisterCommands(this);

    this.services.forEach(Service::stop);
  }

  public ConfigurationContainer<MainConfiguration> getMainConfigContainer() {
    return this.mainConfigContainer;
  }

  public ConfigurationContainer<StatusConfiguration> getStatusConfigContainer() {
    return this.statusConfigContainer;
  }

  public PingResponseManager getPingResponseManager() {
    return this.pingResponseManager;
  }

  public MaintenanceManager getMaintenanceManager() {
    return this.maintenanceManager;
  }
}
