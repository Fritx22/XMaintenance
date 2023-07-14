package io.github.fritx22.xmaintenance.manager;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class MessagingManager {

  private final ProxyServer proxy;
  private final CommandSender console;
  private final ComponentBuilder componentBuilder;

  public MessagingManager(ProxyServer proxy) {
    this.proxy = proxy;
    this.console = proxy.getConsole();
    this.componentBuilder = new ComponentBuilder();
  }

  public void sendMessage(CommandSender sender, String... messages) {
    for (String message : messages) {
      sender.sendMessage(parse(message));
    }
  }

  public void sendConsoleMessage(String... messages) {
    sendMessage(this.console, messages);
  }

  public BaseComponent[] parse(String message) {
    BaseComponent[] result = this.componentBuilder.append(message).create();
    this.componentBuilder.getParts().clear();
    return result;
  }

}
