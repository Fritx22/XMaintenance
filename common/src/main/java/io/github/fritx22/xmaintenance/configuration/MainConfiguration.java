/*
This file is part of XMaintenance.
XMaintenance is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
XMaintenance is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with XMaintenance. If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.fritx22.xmaintenance.configuration;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection;

import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@SuppressWarnings({"CanBeFinal", "FieldCanBeLocal", "FieldMayBeFinal"})
@ConfigSerializable
public class MainConfiguration {

  @Comment("Plugin prefix for all the configurable messages")
  private String pluginPrefix = "<gold>[XMaintenance]</gold> ";

  @Comment("""
      This will be the name of the fake outdated server version
      It's red by default\
      """)
  private String pingText = "Maintenance";

  @Comment("""
      Enable bypass permission. The permission is "xmaintenance.bypass"
      Note that emergency mode has no bypass\
      """)
  private boolean enableBypass = true;

  @Comment("""
      Whether to allow players to execute the plugin command
      The permission for the plugin command is "xmaintenance.admin"
      For security reasons the emergency mode can't be enabled by a player\
      """)
  private boolean allowPlayers = true;

  @Comment("Players will be kicked with this message if maintenance mode is enabled")
  private String kickMessage = "<red>We are under maintenance, please try again later.</red>";

  @Comment("Kick message shown when staff enables the emergency maintenance mode")
  private String emergencyKickMessage = "<red>The staff enabled the emergency maintenance mode.</red>";

  @Comment("""
      All the players that have the permission "xmaintenance.admin" will see this messages\
      You must use "<player>" placeholder to get the player name\
      """)
  private List<String> adminAlertMessages = List.of(
      "<gold><st>----------------------------------------</st></gold>",
      "<prefix><gray>The player <red><player></red> tried to join.</gray>",
      "<gold><st>----------------------------------------</st></gold>"
  );

  @Comment("""
      Message used when a player tries to enable/disable the maintenance mode, \
      but it's already enabled/disabled.\
      """)
  private String alreadyStatus = "<prefix><gray>The maintenance mode is already <status>.";

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
  private ToggledValue<List<String>> playersHover = ToggledValue.of(List.of(
      "<gray>XMaintenance - Default message</gray>",
      "<green>You can add multiple lines!</green>"
  ));

  private transient ToggledValue<List<Component>> playersHoverCache;
  private transient ToggledValue<List<String>> legacyPlayersHoverCache;

  @Comment("""
      Integer number used by the client to know if the server is the same version, \
      it's outdated, or it's a newer version.
      More information: https://minecraft.gamepedia.com/Protocol_version\
      """)
  private int fakeVersionProtocolNumber = -503;

  public Component getPluginPrefix() {
    return miniMessage().deserialize(this.pluginPrefix);
  }

  public Component getPingText() {
    return miniMessage().deserialize(this.pingText);
  }

  public String getLegacyPingText() {
    return legacySection().serialize(this.getPingText());
  }

  public boolean enableBypass() {
    return this.enableBypass;
  }

  public boolean allowPlayers() {
    return this.allowPlayers;
  }

  public Component getKickMessage() {
    return miniMessage().deserialize(this.kickMessage);
  }

  public Component getEmergencyKickMessage() {
    return miniMessage().deserialize(this.emergencyKickMessage);
  }

  public List<Component> getAdminAlertMessages(String playerName) {
    List<Component> result = new ArrayList<>();
    for (String str : this.adminAlertMessages) {
      result.add(
          miniMessage().deserialize(
              str,
              Placeholder.component("player", Component.text(playerName)),
              Placeholder.component("prefix", this.getPluginPrefix())
          )
      );
    }
    return result;
  }

  public Component getAlreadyStatus(String status) {
    return miniMessage().deserialize(
        this.alreadyStatus,
        Placeholder.component("prefix", this.getPluginPrefix()),
        Placeholder.unparsed("status", status)
    );
  }

  public PlayersEditor getPlayersEditor() {
    return this.playersEditor;
  }

  public ToggledValue<List<Component>> getPlayersHover() {
    if (this.playersHoverCache != null)
      return playersHoverCache;

    List<String> value = this.playersHover.getValue();
    List<Component> result = new ArrayList<>(value.size());
    for (String str : value) {
      result.add(miniMessage().deserialize(str));
    }

    this.playersHoverCache = ToggledValue.of(result, this.playersHover.isEnabled());
    return this.playersHoverCache;
  }

  public ToggledValue<List<String>> getLegacyPlayersHover() {
    if (this.legacyPlayersHoverCache != null) return legacyPlayersHoverCache;

    ToggledValue<List<Component>> playersHover = this.getPlayersHover();
    List<Component> value = playersHover.getValue();
    List<String> result = new ArrayList<>(value.size());
    for (Component component : value) {
      result.add(legacySection().serialize(component));
    }

    this.legacyPlayersHoverCache = ToggledValue.of(result, playersHover.isEnabled());
    return this.legacyPlayersHoverCache;
  }

  public int getFakeVersionProtocolNumber() {
    return this.fakeVersionProtocolNumber;
  }

  @SuppressWarnings({"CanBeFinal", "FieldCanBeLocal", "FieldMayBeFinal", "unused"})
  @ConfigSerializable
  public static class ToggledValue<C> {

    private boolean enable = true;
    private C value;

    public boolean isEnabled() {
      return this.enable;
    }

    public C getValue() {
      return this.value;
    }

    public static <C> ToggledValue<C> of(C value) {
      ToggledValue<C> tv = new ToggledValue<>();
      tv.value = value;
      return tv;
    }

    public static <C> ToggledValue<C> of(C value, boolean status) {
      ToggledValue<C> tv = new ToggledValue<>();
      tv.value = value;
      tv.enable = status;
      return tv;
    }
  }

  @SuppressWarnings({"CanBeFinal", "FieldMayBeFinal", "unused"})
  @ConfigSerializable
  public static class PlayersEditor {

    private ToggledValue<Integer> max = ToggledValue.of(0);
    private ToggledValue<Integer> online = ToggledValue.of(0);

    public ToggledValue<Integer> getMax() {
      return this.max;
    }

    public ToggledValue<Integer> getOnline() {
      return this.online;
    }
  }

}
