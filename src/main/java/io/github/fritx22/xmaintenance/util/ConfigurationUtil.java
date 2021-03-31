package io.github.fritx22.xmaintenance.util;

import io.github.fritx22.xmaintenance.XMaintenance;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;

public class ConfigurationUtil {

    private Configuration configuration;
    private final XMaintenance plugin;
    private final String fileName;

    public ConfigurationUtil(final XMaintenance plugin, final String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
    }

    public Configuration get() {
        return this.configuration;
    }

    public String getString(String path) {
        return ChatColor.translateAlternateColorCodes('&', this.configuration.getString(path)
                .replace("%prefix%", this.configuration.getString("plugin-prefix"))
                .replace("%status%", (plugin.getStatusConfig().get().getBoolean("maintenance-enabled")) ? "&aenabled" : "&cdisabled"));
    }

    public void loadConfiguration() {
        try{
            this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(plugin.getDataFolder(), fileName));
        } catch(IOException ex) {
            plugin.getProxy().getLogger().log(Level.SEVERE, ("[%plugin%] Unable to load the configuration file " + fileName + ".")
                    .replace("%plugin%", plugin.getDescription().getName()));
        }
    }

    public void saveConfiguration() {
        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            try {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, new File(plugin.getDataFolder(), fileName));
            } catch(IOException ex) {
                plugin.getProxy().getLogger().log(Level.SEVERE, "[%plugin%] Unable to save the configuration."
                        .replace("%plugin%", plugin.getDescription().getName()));
            }
        });
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void createConfiguration() {
        try {
            final File file = new File(plugin.getDataFolder(), this.fileName);

            if(!file.exists()) {
                if(!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                InputStream inputStream = plugin.getClass().getClassLoader().getResourceAsStream(this.fileName);

                if(inputStream == null) {
                    plugin.getProxy().getLogger().severe(("[%plugin%] The configuration file " + this.fileName + " isn't in the plugin jar.")
                            .replace("%plugin%", plugin.getDescription().getName()));
                    return;
                }

                Files.copy(inputStream, file.toPath());

                plugin.getProxy().getLogger().info(("[%plugin%] The configuration file " + this.fileName + " has been created.")
                        .replace("%plugin%", plugin.getDescription().getName()));

            }
        } catch(Exception ex) {
            plugin.getProxy().getLogger().severe(("[%plugin%] Unable to create the configuration file " + this.fileName + ".")
                    .replace("%plugin%", plugin.getDescription().getName()));
            ex.printStackTrace();
        }
    }


}
