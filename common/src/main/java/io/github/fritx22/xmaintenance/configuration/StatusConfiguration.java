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
