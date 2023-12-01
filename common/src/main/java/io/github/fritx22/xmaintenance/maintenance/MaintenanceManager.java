package io.github.fritx22.xmaintenance.maintenance;

import io.github.fritx22.xmaintenance.configuration.ConfigurationContainer;
import io.github.fritx22.xmaintenance.configuration.MainConfiguration;
import io.github.fritx22.xmaintenance.configuration.StatusConfiguration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class MaintenanceManager {

  private final ConfigurationContainer<MainConfiguration> mainConfigContainer;
  private final ConfigurationContainer<StatusConfiguration> statusConfigContainer;
  private MaintenanceType type;
  private boolean enabled;
  private final Map<UUID, MaintenancePlayer> players;

  public MaintenanceManager(
      MaintenanceType defaultType,
      boolean defaultStatus,
      ConfigurationContainer<MainConfiguration> mainConfigContainer,
      ConfigurationContainer<StatusConfiguration> statusConfigContainer
  ) {
    this.type = defaultType;
    this.enabled = defaultStatus;
    this.mainConfigContainer = mainConfigContainer;
    this.statusConfigContainer = statusConfigContainer;
    this.players = new HashMap<>();
  }

  public MaintenanceType getType() {
    return this.type;
  }

  public void setType(MaintenanceType type) {
    this.type = type;
    this.statusConfigContainer.getConfig().setMaintenanceType(type);
    // Trigger actions (e.g. kick) based on current type
    if (this.isEnabled()) {
      this.setEnabled(true);
    }
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
    this.statusConfigContainer.getConfig().setMaintenanceEnabled(enabled);

    // Kick every player when emergency mode is enabled
    if (this.getType().equals(MaintenanceType.EMERGENCY)) {
      this.forEachPlayer((id, player) -> player.disconnect(this.mainConfigContainer.getConfig()
          .getEmergencyKickMessage()));
    }
  }

  public String getStatus() {
    return this.isEnabled() ? "enabled [" + this.type.toString() + "]" : "disabled";
  }

  public CompletableFuture<Void> saveStatusConfiguration() {
    return this.statusConfigContainer.save();
  }

  public boolean isAllowed(MaintenancePlayer player, TriggerReason reason) {
    System.out.println("Checking player " + player.getName());
    if (!this.isEnabled()) {
      return true;
    }
    if (this.type.equals(MaintenanceType.EMERGENCY)) {
      return false;
    }
    if (this.mainConfigContainer.getConfig().enableBypass() && player.hasBypassPermission()) {
      return true;
    }
    return switch (this.type) {
      // All server joins are blocked
      case ALL -> false;
      // Only join is blocked
      case JOIN -> !reason.equals(TriggerReason.PROXY_CONNECT);
      // Only server changes are blocked, so joining the proxy and joining default servers is allowed
      case SERVER -> reason.equals(TriggerReason.DEFAULT_OR_FALLBACK) || reason.equals(TriggerReason.PROXY_CONNECT);
      // Shouldn't happen, required to compile
      case EMERGENCY -> false;
    };

  }

  public void registerPlayer(MaintenancePlayer player) throws IllegalStateException {
    UUID id = player.getUniqueId();
    if (this.players.putIfAbsent(id, player) != null) {
      throw new IllegalStateException("Player " + player + " already registered.");
    }
  }

  public void unregisterPlayer(UUID id) {
    this.players.remove(id);
  }

  public void unregisterPlayer(MaintenancePlayer player) {
    this.unregisterPlayer(player.getUniqueId());
  }

  public MaintenancePlayer getPlayer(UUID id) {
    return this.players.get(id);
  }

  public void forEachPlayer(BiConsumer<UUID, MaintenancePlayer> action) {
    this.players.forEach(action);
  }
}
