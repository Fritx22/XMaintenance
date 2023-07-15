package io.github.fritx22.xmaintenance.util;

import java.util.logging.Logger;
import net.md_5.bungee.api.ProxyServer;

public class LoggingUtils {

  private static final Logger logger = ProxyServer.getInstance().getLogger();

  private LoggingUtils() {
    throw new IllegalStateException("This class cannot be instantiated");
  }

  public static void logError(String message) {
    logger.severe(message);
  }

  public static void logWarn(String message) {
    logger.warning(message);
  }

  public static void logInfo(String message) {
    logger.info(message);
  }

}
