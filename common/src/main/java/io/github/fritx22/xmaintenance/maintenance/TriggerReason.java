package io.github.fritx22.xmaintenance.maintenance;

/**
 * Represents the reason the maintenance mode check has been triggered
 */
public enum TriggerReason {
  PROXY_CONNECT,
  DEFAULT_OR_FALLBACK,
  UNKNOWN
}
