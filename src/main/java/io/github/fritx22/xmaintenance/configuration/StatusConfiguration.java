package io.github.fritx22.xmaintenance.configuration;

import io.github.fritx22.xmaintenance.maintenance.MaintenanceTypes;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
@ConfigSerializable
public class StatusConfiguration {
    private boolean maintenanceEnabled = false;
    @Comment("Valid values: ALL, JOIN, SERVER, EMERGENCY")
    private MaintenanceTypes maintenanceType = MaintenanceTypes.JOIN;

    public boolean isMaintenanceEnabled() {
        return this.maintenanceEnabled;
    }

    public void setMaintenanceEnabled(boolean newValue) {
        this.maintenanceEnabled = newValue;
    }

    public MaintenanceTypes getMaintenanceType() {
        return this.maintenanceType;
    }

    public void setMaintenanceType(MaintenanceTypes newType) {
        this.maintenanceType = newType;
    }
}
