package com.bgsoftware.superiorskyblock.module.mongodb;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.data.DatabaseBridge;
import com.bgsoftware.superiorskyblock.api.data.DatabaseFilter;
import com.bgsoftware.superiorskyblock.api.enums.BorderColor;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.missions.Mission;
import com.bgsoftware.superiorskyblock.api.player.container.PlayersContainer;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.module.mongodb.bridge.MongoDatabaseBridge;
import com.bgsoftware.superiorskyblock.module.mongodb.container.MongoPlayerContainer;
import com.bgsoftware.superiorskyblock.module.mongodb.tools.DatabaseResult;
import com.bgsoftware.superiorskyblock.player.PlayerLocales;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MongoPlayerDataLoader {

    public static void deserializeMissions(DatabaseResult databaseResult, SuperiorPlayer.Builder builder) {
        Optional<Map<String, Object>> playerMissions = databaseResult.getMap("missions");
        if (playerMissions.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Object> allMissions : playerMissions.get().entrySet()) {
            String name = allMissions.getKey();
            int finishCount = (int) allMissions.getValue();
            Mission<?> mission = SuperiorSkyblockAPI.getMissions().getMission(name);
            if (mission != null) {
                builder.setCompletedMission(mission, finishCount);
            }
        }
    }

    public static void deserializePlayerSettings(DatabaseResult playerInfo, SuperiorPlayer.Builder builder) {
        Optional<Map<String, Object>> playerSettingsMap = playerInfo.getMap("settings");
        if (playerSettingsMap.isEmpty()) {
            return;
        }
        DatabaseResult settings = new DatabaseResult(playerSettingsMap.get());

        settings.getBoolean("toggled_panel").ifPresent(builder::setToggledPanel);
        settings.getBoolean("island_fly").ifPresent(builder::setIslandFly);
        settings.getEnum("border_color", BorderColor.class).ifPresent(builder::setBorderColor);
        settings.getString("language").map(PlayerLocales::getLocale).ifPresent(builder::setLocale);
        settings.getBoolean("toggled_border").ifPresent(builder::setWorldBorderEnabled);
    }

    public static void deserializePersistentDataContainer(DatabaseResult playerInfo, SuperiorPlayer.Builder builder) {
        Optional<Map<String, Object>> custom_data = playerInfo.getMap("custom_data");
        if (custom_data.isEmpty()) {
            return;
        }
        DatabaseResult customData = new DatabaseResult(custom_data.get());
        byte[] persistentData = customData.getBlob("data").orElse(new byte[0]);
        if (persistentData.length == 0) {
            return;
        }
        builder.setPersistentData(persistentData);
    }

    public static void loadPlayer(PlayersContainer mongoPlayerContainer, UUID playerUUID, DatabaseBridge mongoDatabaseBridge) {
        mongoDatabaseBridge.loadObject("players_info",
                DatabaseFilter.fromFilter("_id", playerUUID.toString()),
                resultSetRaw -> {
                    if (resultSetRaw == null) {
                        return;
                    }
                    DatabaseResult databaseResult = new DatabaseResult(resultSetRaw);
                    SuperiorPlayer.Builder builder = SuperiorPlayer.newBuilder();
                    deserializeMissions(databaseResult, builder);
                    deserializePlayerSettings(databaseResult, builder);
                    deserializePersistentDataContainer(databaseResult, builder);
                    mongoPlayerContainer.addPlayer(builder
                            .setUniqueId(playerUUID)
                            .setName(databaseResult.getString("last_used_name").orElse("null"))
                            .setDisbands(databaseResult.getInt("disbands").orElse(0))
                            .setTextureValue(databaseResult.getString("last_used_skin").orElse(""))
                            .setLastTimeUpdated(databaseResult.getLong("last_time_updated").orElse(System.currentTimeMillis() / 1000))
                            .build());

                    Optional<UUID> islandUUID = databaseResult.getUUID("island");
                    if (islandUUID.isPresent()) {
                        DatabaseBridge islandLoader = SuperiorSkyblockPlugin.getPlugin().getFactory().createDatabaseBridge((Island) null);
                        Island island = SuperiorSkyblockPlugin.getPlugin().getGrid().getIslandsContainer().getIslandByUUID(islandUUID.get());
                        if (island != null) {
                            return;
                        }
                        MongoIslandDataLoader.loadIsland(islandUUID.get(), islandLoader);
                    }
                });
    }
}
