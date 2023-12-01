package io.github.fritx22.xmaintenance.maintenance;

import java.util.UUID;
import net.kyori.adventure.text.Component;

public interface MaintenancePlayer {

  /**
   * @return whether the player has bypass permission
   */
  boolean hasBypassPermission();

  /**
   * Disconnects the player from the proxy
   * @param message the kick message Component
   */
  void disconnect(Component message);

  /**
   *
   * @return the player's unique identifier
   */
  UUID getUniqueId();

  /**
   *
   * @return the player's username
   */
  String getName();


}
