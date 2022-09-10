package io.github.fritx22.xmaintenance.manager;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class MessagingManager {

    private final ProxyServer proxy;
    private final CommandSender console;
    private final ComponentBuilder componentBuilder;

    public MessagingManager(ProxyServer proxy) {
        this.proxy = proxy;
        this.console = proxy.getConsole();
        this.componentBuilder = new ComponentBuilder();
    }

    public void sendConsoleMessage(String message) {
        this.console.sendMessage(parse(message));
    }

    public void sendPlayerMessage(ProxiedPlayer player, String message) {
        player.sendMessage(parse(message));
    }

    public BaseComponent[] parse(String message) {
        this.componentBuilder.getParts().clear();
        return this.componentBuilder.append(message).create();
    }

}
