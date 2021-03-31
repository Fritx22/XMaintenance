package io.github.fritx22.xmaintenance.settings;

import io.github.fritx22.xmaintenance.XMaintenance;
import io.github.fritx22.xmaintenance.util.ConfigurationUtil;


//
// Para que esta clase tenga mejor sentido se tiene que usar arraylist para llevar a memoria los valores
// de esta manera la clase se puede reutilizar para diferentes archivos de configuración.
//


public class Settings {

    private final XMaintenance plugin;
    private final ConfigurationUtil config;

    private boolean maintenanceEnabled;
    private String pingText;
    private boolean playersToZero;
    private String playersHoverMessage;

    public boolean isMaintenanceEnabled() {
        return maintenanceEnabled;
    }

    public void setMaintenanceEnabled(boolean status) {
        this.maintenanceEnabled = status;
    }

    public String getPingText() {
        return pingText;
    }

    public boolean isPlayersToZero() {
        return playersToZero;
    }

    public String getPlayersHoverMessage() {
        return playersHoverMessage;
    }

    public Settings(XMaintenance plugin, String fileName) {
        this.plugin = plugin;
        this.config = new ConfigurationUtil(plugin, fileName);

        config.createConfiguration();
        this.loadSettings();
    }

    public ConfigurationUtil getConfiguration() {
        return config;
    }

    public void loadSettings() {
        config.loadConfiguration();

        maintenanceEnabled = this.config.get().getBoolean("maintenance-enabled");
        pingText = this.config.get().getString("ping-text");
        playersToZero = this.config.get().getBoolean("set-players-to-zero");
        playersHoverMessage = this.config.get().getString("player-hover-message");
    }

    public void saveSettings() {
        this.config.get().set("maintenance-enabled", maintenanceEnabled);
        this.config.get().set("pingText", pingText);
        this.config.get().set("set-players-to-zero", playersToZero);
        this.config.get().set("players-hover-message", playersHoverMessage);

        this.config.saveConfiguration();
    }
}
