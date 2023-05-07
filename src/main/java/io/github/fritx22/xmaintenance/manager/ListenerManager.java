package io.github.fritx22.xmaintenance.manager;

import io.github.fritx22.xmaintenance.XMaintenance;
import io.github.fritx22.xmaintenance.listeners.ProxyPingListener;
import io.github.fritx22.xmaintenance.listeners.ServerConnectListener;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.PluginManager;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ListenerManager {

    private final XMaintenance plugin;
    private final PluginManager pluginManager;

    private final Map<Class<? extends Listener>, Listener> listeners = new HashMap<>();


    public ListenerManager(XMaintenance plugin) {
        this.plugin = plugin;
        this.pluginManager = plugin.getProxy().getPluginManager();

        this.listeners.put(ProxyPingListener.class, new ProxyPingListener(this.plugin));
        this.listeners.put(ServerConnectListener.class, new ServerConnectListener(this.plugin));
    }

    @Nullable
    public Listener getListener(Class<? extends Listener> clazz) {
        return this.listeners.get(clazz);
    }

    public void registerListeners() {
        this.listeners.forEach(
                (Class<? extends Listener> clazz, Listener listener) -> this.registerListener(listener)
        );
    }

    public void unregisterListeners() {
        this.listeners.forEach(
                (Class<? extends Listener> clazz, Listener listener) -> this.unregisterListener(listener)
        );
    }

    public void unregisterListener(Listener listener) {
        this.pluginManager.unregisterListener(listener);
    }

    public void registerListener(Listener listener) {
        this.pluginManager.registerListener(this.plugin, listener);
        this.listeners.putIfAbsent(listener.getClass(), listener);
    }


}
