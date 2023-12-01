package io.github.fritx22.xmaintenance.maintenance;

import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeMaintenancePlayer implements MaintenancePlayer {

  private final ProxiedPlayer player;

  public BungeeMaintenancePlayer(
      ProxiedPlayer player
  ) {
    this.player = player;
  }

  @Override
  public boolean hasBypassPermission() {
    return player.hasPermission("xmaintenance.bypass");
  }

  @Override
  public void disconnect(Component message) {
    this.player.disconnect(
        new TextComponent(LegacyComponentSerializer.legacySection().serialize(message))
    );
  }

  @Override
  public UUID getUniqueId() {
    return this.player.getUniqueId();
  }

  @Override
  public String getName() {
    return this.player.getName();
  }
}
