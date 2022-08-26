package io.github.fritx22.xmaintenance.util;

import io.github.fritx22.xmaintenance.XMaintenance;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import org.jetbrains.annotations.NotNull;

public class StringUtils {

    private StringUtils() { throw new IllegalStateException("This class cannot be instantiated"); }

    @NotNull
    private static final XMaintenance plugin = (XMaintenance) ProxyServer.getInstance().
            getPluginManager().getPlugin("XMaintenance");

    @NotNull
    public static String formatString(@NotNull String original, String... strings) {
        StringBuilder builder = new StringBuilder();
        builder.append(original);
        if (strings == null) return builder.toString();

        for(String string : strings) {
            int indexToReplace = builder.indexOf("%s");
            if (indexToReplace < 0) {
                LoggingUtils.logWarn("Breaking on " + string + " because there is nothing to replace.");
                break;
            }
            builder.replace(indexToReplace, indexToReplace+3, string);
        }

        return builder.toString();
    }

    @NotNull
    public static String formatPluginName(@NotNull String original, String... additionalStrings) {
        return formatString(String.format(original, plugin.getDescription().getName()), additionalStrings);
    }

    @NotNull
    public static String parseColor(@NotNull String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
