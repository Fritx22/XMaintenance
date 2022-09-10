package io.github.fritx22.xmaintenance.manager;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
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
        this.componentBuilder.getParts().clear();
        this.console.sendMessage(
                this.componentBuilder.append(message).create()
        );
    }

    public void sendPlayerMessage(ProxiedPlayer player, String message) {
        this.componentBuilder.getParts().clear();
        player.sendMessage(
                componentBuilder.append(message).create()
        );
    }

}
