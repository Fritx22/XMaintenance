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
import io.github.fritx22.xmaintenance.manager.ListenerManager;
import io.github.fritx22.xmaintenance.manager.MessagingManager;
import io.github.fritx22.xmaintenance.manager.PingResponseManager;
import java.nio.file.Path;
import java.util.Objects;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.api.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

public class XMaintenance extends Plugin {

  @SuppressWarnings("FieldCanBeLocal")
  private final ProxyServer proxy;
  private final PluginDescription description;
  private final ConfigurationContainer<MainConfiguration> mainConfigContainer;
  private final ConfigurationContainer<StatusConfiguration> statusConfigContainer;
  private final MessagingManager messagingManager;
  private final ListenerManager listenerManager;
  private final PingResponseManager pingResponseManager;
  private final PluginManager pluginManager = this.getProxy().getPluginManager();
  private BungeeAudiences audiences;

  public XMaintenance() {
    super();

    this.proxy = this.getProxy();
    this.description = this.getDescription();
    this.mainConfigContainer = ConfigurationContainer.load(
        MainConfiguration.class,
        this.getLogger(),
        Path.of("config.conf"),
        "XMaintenance plugin configuration\nCopyright (C) 2020 Fritx22"
    );
    this.statusConfigContainer = ConfigurationContainer.load(
        StatusConfiguration.class,
        this.getLogger(),
        Path.of("status.conf"),
        "Don't edit this file! " +
            "This is for saving the maintenance status when the server is restarted"
    );
    this.messagingManager = new MessagingManager(this.proxy);
    this.listenerManager = new ListenerManager(this);
    this.pingResponseManager = new PingResponseManager(this);
  }

  @Override
  public void onEnable() {
    this.audiences = BungeeAudiences.create(this);
    this.pluginManager.registerCommand(
        this,
        new MaintenanceCommand(this, messagingManager)
    );
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

    if (this.audiences != null) {
      this.audiences.close();
      this.audiences = null;
    }
  }

  public ConfigurationContainer<MainConfiguration> getMainConfigContainer() {
    return this.mainConfigContainer;
  }

  public ConfigurationContainer<StatusConfiguration> getStatusConfigContainer() {
    return this.statusConfigContainer;
  }

  public ListenerManager getListenerManager() {
    return this.listenerManager;
  }

  public PingResponseManager getPingResponseManager() {
    return this.pingResponseManager;
  }

  public @NotNull BungeeAudiences getAudiences() {
    return Objects.requireNonNull(this.audiences, "BungeeAudiences instance is null");
  }

}
