/*
This file is part of XMaintenance.
XMaintenance is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
XMaintenance is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with XMaintenance. If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.fritx22.xmaintenance.configuration;

import io.github.fritx22.xmaintenance.maintenance.MaintenanceType;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class StatusConfiguration {

  private boolean maintenanceEnabled = false;

  @Comment("Valid values: ALL, JOIN, SERVER, EMERGENCY")
  private MaintenanceType maintenanceType = MaintenanceType.JOIN;

  public boolean isMaintenanceEnabled() {
    return this.maintenanceEnabled;
  }

  public void setMaintenanceEnabled(boolean newValue) {
    this.maintenanceEnabled = newValue;
  }

  public MaintenanceType getMaintenanceType() {
    return this.maintenanceType;
  }

  public void setMaintenanceType(MaintenanceType newType) {
    this.maintenanceType = newType;
  }
}
