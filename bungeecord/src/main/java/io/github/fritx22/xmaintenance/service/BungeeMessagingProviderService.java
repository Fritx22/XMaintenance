package io.github.fritx22.xmaintenance.service;

import java.util.UUID;
import java.util.logging.Logger;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class BungeeMessagingProviderService implements MessagingProviderService {

  private final BungeeAudiences audiences;
  private final Logger logger;


  public BungeeMessagingProviderService(Plugin plugin) {
    this.audiences = BungeeAudiences.create(plugin);
    this.logger = plugin.getLogger();
  }

  @Override
  public Audience sender(Object sender) {
    return this.audiences.sender((CommandSender)sender);
  }

  @Override
  public void start() {
    this.logger.info("Starting " + this.getClass().getSimpleName() + " service");
  }

  @Override
  public void stop() {
    this.close();
  }

  @Override
  public @NotNull Audience all() {
    return this.audiences.all();
  }

  @Override
  public @NotNull Audience console() {
    return this.audiences.console();
  }

  @Override
  public @NotNull Audience players() {
    return this.audiences.players();
  }

  @Override
  public @NotNull Audience player(@NotNull UUID playerId) {
    return this.audiences.player(playerId);
  }

  @Override
  public @NotNull Audience permission(@NotNull Key permission) {
    return this.audiences.permission(permission);
  }

  @Override
  public @NotNull Audience permission(@NotNull String permission) {
    return this.audiences.permission(permission);
  }

  @Override
  public @NotNull Audience world(@NotNull Key world) {
    return this.audiences.world(world);
  }

  @Override
  public @NotNull Audience server(@NotNull String serverName) {
    return this.audiences.server(serverName);
  }

  @Override
  public @NotNull ComponentFlattener flattener() {
    return this.audiences.flattener();
  }

  @Override
  public void close() {
    if(this.audiences != null) {
      this.audiences.close();
    }
  }
}
