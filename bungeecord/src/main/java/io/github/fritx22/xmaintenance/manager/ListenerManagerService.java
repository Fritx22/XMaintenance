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
