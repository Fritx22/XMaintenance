/*
This file is part of XMaintenance.
XMaintenance is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
XMaintenance is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with XMaintenance. If not, see <https://www.gnu.org/licenses/>.
 */
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
