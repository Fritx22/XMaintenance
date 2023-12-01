/*
This file is part of XMaintenance.
XMaintenance is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
XMaintenance is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with XMaintenance. If not, see <https://www.gnu.org/licenses/>.
 */
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
