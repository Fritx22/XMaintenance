/*
This file is part of XMaintenance.
XMaintenance is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
XMaintenance is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with XMaintenance. If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.fritx22.xmaintenance.manager;

import io.github.fritx22.xmaintenance.XMaintenance;
import io.github.fritx22.xmaintenance.listeners.PlayerConnectListener;
import io.github.fritx22.xmaintenance.listeners.ProxyPingListener;
import io.github.fritx22.xmaintenance.listeners.ServerConnectListener;
import io.github.fritx22.xmaintenance.service.MessagingProviderService;
import io.github.fritx22.xmaintenance.service.Service;
import io.github.fritx22.xmaintenance.service.SimpleInstanceRegistry;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

public class ListenerManagerService extends SimpleInstanceRegistry<Listener> implements Service {
  private final XMaintenance plugin;
  private final PluginManager pluginManager;
  private final MessagingProviderService messagingService;

  public ListenerManagerService(
      @NotNull XMaintenance plugin,
      @NotNull PluginManager pluginManager,
      @NotNull MessagingProviderService messagingService
  ) {
    this.plugin = plugin;
    this.pluginManager = pluginManager;
    this.messagingService = messagingService;
  }

  public void registerListeners() {
    this.forEachEntry((Class<? extends Listener> clazz, Listener listener) -> {
      this.registerListenersHelper(clazz, clazz.cast(listener));
    });
  }

  private <T extends Listener> void registerListenersHelper(Class<T> clazz, Listener instance) {
     this.registerInstance(clazz, (T) instance);
  }

  public void unregisterListeners() {
    this.forEachValue(this::unregisterListener);
  }

  public void unregisterListener(Listener listener) {
    this.pluginManager.unregisterListener(listener);
  }

  @Override
  public <S extends Listener> void registerInstance(Class<S> clazz, S listener) {
    this.plugin.getLogger().info("Registering listener instance " + clazz.getSimpleName());
    this.pluginManager.registerListener(this.plugin, listener);
    super.registerInstance(clazz, listener);
  }

  @Override
  public void start() {
    this.plugin.getLogger().info("Starting ListenerManagerService...");
    this.registerInstance(ProxyPingListener.class, new ProxyPingListener(this.plugin));
    this.registerInstance(ServerConnectListener.class, new ServerConnectListener(
        this.plugin,
        this.messagingService)
    );

    this.registerInstance(
        PlayerConnectListener.class,
        new PlayerConnectListener(this.messagingService, this.plugin.getMaintenanceManager())
    );

    this.registerListeners();
  }

  @Override
  public void stop() {
    this.unregisterListeners();
  }
}
