package io.github.fritx22.xmaintenance.util;

import net.md_5.bungee.api.ProxyServer;

import java.util.logging.Logger;

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

}
