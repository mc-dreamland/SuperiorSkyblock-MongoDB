package com.bgsoftware.superiorskyblock.module.mongodb.bridge;

import com.bgsoftware.superiorskyblock.api.data.DatabaseBridge;
import com.bgsoftware.superiorskyblock.api.data.DatabaseFilter;
import com.bgsoftware.superiorskyblock.api.missions.Mission;
import com.bgsoftware.superiorskyblock.api.objects.Pair;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.module.mongodb.MongoDBClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.Collection;
import java.util.Locale;

public class MongoDatabasePlayerSaver {

    public static void updatePlayer(DatabaseFilter filter, Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("players_info");
        Document query = new Document();
        Document update = new Document();
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("uuid".equals(key)) {
                query.append("_id", value);
            }
        }
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            update.append(key, value);
        }
        collection.updateOne(query, new Document("$set", update));
    }

    public static void saveTextureValue(DatabaseFilter filter, Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("players_info");
        Document query = new Document();
        Document update = new Document();
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("uuid".equals(key)) {
                query.append("_id", value);
            }
        }
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            update.append(key, value);
        }
        collection.updateOne(query, new Document("$set", update));
    }

    public static void savePlayerName(DatabaseFilter filter, Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("players_info");
        Document query = new Document();
        Document update = new Document();
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("uuid".equals(key)) {
                query.append("_id", value);
            }
        }
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            update.append(key, value);
        }
        collection.updateOne(query, new Document("$set", update));
    }

    public static void updatePlayerSetting(DatabaseFilter filter, Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("players_info");
        Document query = new Document();
        Document update = new Document();
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("player".equals(key)) {
                query.append("_id", value);
            }
        }
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            update.append("settings." + key, value);
        }
        collection.updateOne(query, new Document("$set", update));
    }

    public static void saveUserLocale(DatabaseFilter filter, Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("players_info");
        Document query = new Document();
        Document update = new Document();
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("player".equals(key)) {
                query.append("_id", value);
            }
        }
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            update.append("settings." + key, value);
        }
        collection.updateOne(query, new Document("$set", update));
    }

    public static void saveToggledBorder(DatabaseFilter filter, Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("players_info");
        Document query = new Document();
        Document update = new Document();
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("player".equals(key)) {
                query.append("_id", value);
            }
        }
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            update.append("settings." + key, value);
        }
        collection.updateOne(query, new Document("$set", update));
    }

    public static void saveDisbands(DatabaseFilter filter, Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("players_info");
        Document query = new Document();
        Document update = new Document();
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("player".equals(key)) {
                query.append("_id", value);
            }
        }
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            update.append("settings." + key, value);
        }
        collection.updateOne(query, new Document("$set", update));
    }

    public static void saveToggledPanel(DatabaseFilter filter, Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("players_info");
        Document query = new Document();
        Document update = new Document();
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("player".equals(key)) {
                query.append("_id", value);
            }
        }
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            update.append("settings." + key, value);
        }
        collection.updateOne(query, new Document("$set", update));
    }

    public static void saveIslandFly(DatabaseFilter filter, Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("players_info");
        Document query = new Document();
        Document update = new Document();
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("player".equals(key)) {
                query.append("_id", value);
            }
        }
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            update.append("settings." + key, value);
        }
        collection.updateOne(query, new Document("$set", update));
    }

    public static void saveBorderColor(DatabaseFilter filter, Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("players_info");
        Document query = new Document();
        Document update = new Document();
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("player".equals(key)) {
                query.append("_id", value);
            }
        }
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            update.append("settings." + key, value);
        }
        collection.updateOne(query, new Document("$set", update));
    }

    public static void saveLastTimeStatus(DatabaseFilter filter, Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("players_info");
        Document query = new Document();
        Document update = new Document();
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("player".equals(key)) {
                query.append("_id", value);
            }
        }
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            update.append("settings." + key, value);
        }
        collection.updateOne(query, new Document("$set", update));
    }

    public static void saveMission(Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("players_info");
        Document query = new Document();
        String name = "";
        Object finish_count = null;
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("player".equals(key)) {
                query.append("_id", value);
            } else if ("name".equals(key)){
                name = "missions" + value;
            } else {
                finish_count = value;
            }
        }
        collection.updateOne(query, new Document("$set", new Document(name, finish_count)));
    }

    public static void removeMission(DatabaseFilter filter) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("players_info");
        Document query = new Document();
        Document del = new Document();
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("player".equals(key)) {
                query.append("_id", value);
            } else {
                del.append("missions." + value, "");
            }
        }
        collection.updateOne(query, new Document("$unset", del));
    }

    public static void savePersistentDataContainer(Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("players_info");
        Document query = new Document();
        Document data = new Document();
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("player".equals(key)) {
                query.append("_id", value);
            } else {
                data.append("custom_data", value);
            }
        }
        collection.updateOne(query, new Document("$set", data));
    }

    public static void removePersistentDataContainer(DatabaseFilter filter) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("players_info");
        Document query = new Document();
        for (Pair<String, Object> column : filter.getFilters()) {
            query.append("_id", column.getKey());
        }
        collection.updateOne(query, new Document("$unset", new Document("custom_data", "")));
    }

    public static void insertPlayer(Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("players_info");
        Document update = new Document();
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("uuid".equals(key)) {
                update.append("_id", value);
            } else {
                update.append(key, value);
            }
        }
        collection.insertOne(update);
    }

    public static void insertPlayerSetting(Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("players_info");
        Document query = new Document();
        Document update = new Document();
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("player".equals(key)) {
                query.append("_id", value);
            } else {
                update.append("bank" + key, value);
            }
        }
        collection.updateOne(query, new Document("$set", update));
    }

    //TODO 处理加入岛屿，离开岛屿等
//
//    public static void replacePlayer(SuperiorPlayer originalPlayer, SuperiorPlayer newPlayer) {
//        DatabaseBridge playersReplacer = getGlobalPlayersBridge();
//
//        Pair<String, Object> uuidColumn = new Pair<>("uuid", newPlayer.getUniqueId().toString());
//        DatabaseFilter uuidFilter = createFilter("uuid", originalPlayer);
//
//        Pair<String, Object> playerColumn = new Pair<>("player", newPlayer.getUniqueId().toString());
//        DatabaseFilter playerFilter = createFilter("player", originalPlayer);
//
//        // We go through all possible tables (both island and players) and replace the player uuids.
//        playersReplacer.updateObject("bank_transactions", playerFilter, playerColumn);
//        playersReplacer.updateObject("islands", createFilter("owner", originalPlayer), new Pair<>("owner", newPlayer.getUniqueId().toString()));
//        playersReplacer.updateObject("islands_bans", playerFilter, playerColumn);
//        playersReplacer.updateObject("islands_bans", createFilter("banned_by", originalPlayer), new Pair<>("banned_by", newPlayer.getUniqueId().toString()));
//        playersReplacer.updateObject("islands_members", playerFilter, playerColumn);
//        playersReplacer.updateObject("islands_player_permissions", playerFilter, playerColumn);
//        playersReplacer.updateObject("islands_ratings", playerFilter, playerColumn);
//        playersReplacer.updateObject("islands_visitors", playerFilter, playerColumn);
//        playersReplacer.updateObject("players", uuidFilter, uuidColumn);
//        playersReplacer.updateObject("players_custom_data", playerFilter, playerColumn);
//        playersReplacer.updateObject("players_settings", playerFilter, playerColumn);
//        playersReplacer.updateObject("players_missions", playerFilter, playerColumn);
//    }
//
//    public static void deletePlayer(SuperiorPlayer superiorPlayer) {
//        DatabaseBridge playersReplacer = getGlobalPlayersBridge();
//
//        DatabaseFilter uuidFilter = createFilter("uuid", superiorPlayer);
//        DatabaseFilter playerFilter = createFilter("player", superiorPlayer);
//
//        // We go through all possible tables (both island and players) and replace the player uuids.
//        playersReplacer.deleteObject("bank_transactions", playerFilter);
//        playersReplacer.deleteObject("islands", createFilter("owner", superiorPlayer));
//        playersReplacer.deleteObject("islands_bans", playerFilter);
//        playersReplacer.deleteObject("islands_bans", createFilter("banned_by", superiorPlayer));
//        playersReplacer.deleteObject("islands_members", playerFilter);
//        playersReplacer.deleteObject("islands_player_permissions", playerFilter);
//        playersReplacer.deleteObject("islands_ratings", playerFilter);
//        playersReplacer.deleteObject("islands_visitors", playerFilter);
//        playersReplacer.deleteObject("players", uuidFilter);
//        playersReplacer.deleteObject("players_custom_data", playerFilter);
//        playersReplacer.deleteObject("players_settings", playerFilter);
//        playersReplacer.deleteObject("players_missions", playerFilter);
//    }
}
