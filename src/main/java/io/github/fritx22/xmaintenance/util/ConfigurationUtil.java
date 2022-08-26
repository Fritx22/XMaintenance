/*
This file is part of XMaintenance.
XMaintenance is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
XMaintenance is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with XMaintenance. If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.fritx22.xmaintenance.util;

import io.github.fritx22.xmaintenance.XMaintenance;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ConfigurationUtil {

    private Configuration configuration;
    private final XMaintenance plugin;
    private final String fileName;

    public ConfigurationUtil(XMaintenance plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
    }

    public Configuration get() {
        return this.configuration;
    }

    public String getString(String path) {
        return StringUtils.parseColor(
                this.configuration.getString(path)
                .replace("%prefix%", this.configuration.getString("plugin-prefix"))
                .replace("%status%",
                        (plugin.getStatusConfig().get().getBoolean("maintenance-enabled"))
                                ? "&aenabled" : "&cdisabled"
                )
        );
    }

    public void loadConfiguration() {
        try{
            this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(plugin.getDataFolder(), fileName));
        } catch(IOException ex) {
            LoggingUtils.logError(
                    StringUtils.formatPluginName("[%s] Unable to load the configuration file %s.", this.fileName)
            );
        }
    }

    public void saveConfiguration() {
        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            try {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, new File(plugin.getDataFolder(), fileName));
            } catch(IOException ex) {
                LoggingUtils.logError(
                        StringUtils.formatPluginName("[%s] Unable to save the configuration file %s.", this.fileName)
                );
            }
        });
    }

    public void createConfiguration() {
        try {
            File file = new File(plugin.getDataFolder(), this.fileName);

            if(!file.exists()) {
                if(!file.getParentFile().exists()) {
                    if(!file.getParentFile().mkdirs()) throw
                            new IOException("Can't create parent directories");
                }

                InputStream inputStream = plugin.getClass().getClassLoader().getResourceAsStream(this.fileName);

                if(inputStream == null) {
                    LoggingUtils.logError(
                            StringUtils.formatPluginName("[%s] The configuration file %s isn't in the plugin jar.", this.fileName)
                    );
                    return;
                }

                Files.copy(inputStream, file.toPath());

                LoggingUtils.logError(
                        String.format("[%s] The configuration file %s has been created.", plugin.getDescription().getName(), this.fileName)
                );

            }
        } catch(Exception ex) {
            LoggingUtils.logError(
                    String.format("[%s] Unable to create the configuration file %s.", plugin.getDescription().getName(), this.fileName)
            );
            ex.printStackTrace();
        }
    }


}
