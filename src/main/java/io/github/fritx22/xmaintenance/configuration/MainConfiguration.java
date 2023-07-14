package io.github.fritx22.xmaintenance.configuration;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.List;

@SuppressWarnings({"CanBeFinal", "FieldCanBeLocal", "FieldMayBeFinal"})
@ConfigSerializable
public class MainConfiguration {
    @SuppressWarnings({"CanBeFinal", "FieldCanBeLocal", "FieldMayBeFinal", "unused"})
    @ConfigSerializable
    public static class ToggledValue<C> {

        public ToggledValue(C value) {
            this.value = value;
        }

        private boolean enable = true;
        private C value;

        public boolean isEnabled() {
            return this.enable;
        }

        public C getValue() {
            return this.value;
        }
    }

    @SuppressWarnings({"CanBeFinal", "FieldMayBeFinal", "unused"})
    @ConfigSerializable
    public static class PlayersEditor {
        private ToggledValue<Integer> max = new ToggledValue<>(0);
        private ToggledValue<Integer> online = new ToggledValue<>(0);

        public ToggledValue<Integer> getMax() {
            return this.max;
        }

        public ToggledValue<Integer> getOnline() {
            return this.online;
        }
    }

    @Comment("Plugin prefix for all the configurable messages")
    private String pluginPrefix = "&6[XMaintenance]&r ";
    @Comment("""
            This will be the name of the fake outdated server version
            It's red by default\
            """)
    private String pingText = "Maintenance";
    @Comment("""
            Enable bypass permission. The permission is "xmaintenance.bypass"
            "Note that emergency mode has no bypass\
            """)
    private boolean enableBypass = true;
    @Comment("""
            Whether to allow players to execute the plugin command
            The permission for the plugin command is "xmaintenance.admin"
            For security reasons the emergency mode can't be enabled by a player\
            """)
    private boolean allowPlayers = true;
    @Comment("Players will be kicked with this message if maintenance mode is enabled")
    private String kickMessage = "&cWe are under maintenance, please try again later.";
    @Comment("Kick message shown when staff enables the emergency maintenance mode")
    private String emergencyKickMessage = "&cThe staff enabled the emergency maintenance mode.";
    @Comment("All the players that have the permission \"xmaintenance.admin\" will see this messages")
    private List<String> adminAlertMessages = List.of(
            "&6&m----------------------------------------",
            "%prefix%&7The player &c%player% &7tried to join.",
            "&6&m----------------------------------------"
    );
    @Comment("""
            Message used when a player tries to enable/disable the maintenance mode, \
            but it's already enabled/disabled.\
            """)
    private String alreadyStatus = "%prefix%&7The maintenance mode is already %status%&7.";

    @Comment("""
            If enabled, this will set custom online and maximum players
            while the server is under maintenance.\
            """)
    private PlayersEditor playersEditor = new PlayersEditor();

    @Comment("""
            If enabled, this will set a custom message
            in the player list while the server is under
            maintenance\
            """)
    private ToggledValue<List<String>> playersHover = new ToggledValue<>(List.of(
            "&7XMaintenance - Default message",
            "&aYou can add multiple lines!"
    ));

    @Comment("""
            Integer number used by the client to know if the server is the same version, \
            it's outdated, or it's a newer version.
            More information: https://minecraft.gamepedia.com/Protocol_version\
            """)
    private int fakeVersionProtocolNumber = -503;

    public String getPluginPrefix() {
        return this.pluginPrefix;
    }

    public String getPingText() {
        return this.pingText;
    }

    public boolean enableBypass() {
        return this.enableBypass;
    }

    public boolean allowPlayers() {
        return this.allowPlayers;
    }

    public String getKickMessage() {
        return this.kickMessage;
    }

    public String getEmergencyKickMessage() {
        return this.emergencyKickMessage;
    }

    public List<String> getAdminAlertMessages() {
        return this.adminAlertMessages;
    }

    public String getAlreadyStatus() {
        return this.alreadyStatus;
    }

    public PlayersEditor getPlayersEditor() {
        return this.playersEditor;
    }

    public ToggledValue<List<String>> getPlayersHover() {
        return this.playersHover;
    }

    public int getFakeVersionProtocolNumber() {
        return this.fakeVersionProtocolNumber;
    }

}
