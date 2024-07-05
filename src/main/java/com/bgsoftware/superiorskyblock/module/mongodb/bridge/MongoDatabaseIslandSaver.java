package com.bgsoftware.superiorskyblock.module.mongodb.bridge;

import com.bgsoftware.superiorskyblock.api.data.DatabaseFilter;
import com.bgsoftware.superiorskyblock.api.objects.Pair;
import com.bgsoftware.superiorskyblock.module.mongodb.MongoDBClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

public class MongoDatabaseIslandSaver {
    static void addMembers(Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document info = new Document();
        Document players = new Document();
        Document query = new Document();
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else if ("player".equals(key)){
                players.append("members." + key, info);
            } else {
                info.append(key, value);
            }
        }
        collection.updateOne(query, new Document("$set", players));
    }

    static void removeMembers(DatabaseFilter filter) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document del = new Document();
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else if ("player".equals(key)){
                del.append("members." + key, "");
            }
        }
        collection.updateOne(query, new Document("$unset", del));
    }

    static void saveMemberRole(DatabaseFilter filter, Pair<String, Object>[] columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document update = new Document();
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else if ("player".equals(key)){
                for (Pair<String, Object> c : columns) {
                    update.append("members." + key + "." + c.getKey(), c.getValue());
                }
            }
        }
        collection.updateOne(query, new Document("$set", update));
    }

    public static void addBannedPlayer(Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document info = new Document();
        Document players = new Document();
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else if ("player".equals(key)){
                players.append("bans." + key, info);
            } else {
                info.append(key, value);
            }
        }

        collection.updateOne(query, new Document("$set", players));
    }

    public static void removeBannedPlayer(DatabaseFilter filter) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document del = new Document();
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else if ("player".equals(key)){
                del.append("bans." + key, "");
            }
        }
        collection.updateOne(query, new Document("$unset", del));
    }

    public static void updateSettings(DatabaseFilter filter, Pair<String, Object>[] columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document update = new Document();
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
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

    public static void deleteIslandHome(DatabaseFilter filter) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document del = new Document();
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else {
                del.append("homes." + value, "");
            }
        }
        collection.updateOne(query, new Document("$unset", del));
    }

    public static void addIslandHome(Pair<String, Object>[] columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document update = new Document();
        String environment = "";
        String location = "";
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            }
            if ("environment".equals(key)) {
                environment = value.toString();
            }
            if ("location".equals(key)) {
                location = value.toString();
            }
        }
        update.append("homes." + environment, location);
        collection.updateOne(query, new Document("$set", update));
    }

    public static void saveVisitorLocation(Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document update = new Document();
        String environment = "";
        String location = "";
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            }
            if ("environment".equals(key)) {
                environment = value.toString();
            }
            if ("location".equals(key)) {
                location = value.toString();
            }
        }
        update.append("visitors." + environment, location);

        collection.updateOne(query, new Document("$set", update));
    }

    public static void removeVisitorLocation(DatabaseFilter filter) {

        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document del = new Document();
        String environment = "";
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            }
            if ("environment".equals(key)) {
                environment = value.toString();
            }
        }
        del.append("visitors." + environment, "");
        collection.updateOne(query, new Document("$unset", del));
    }

    public static void changeOwner(DatabaseFilter filter, Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");

        Document query = new Document();
        Document update = new Document();
        for (Pair<String, Object> column : filter.getFilters()) {
            Object value = column.getValue();
            query.append("owner", value);
        }
        for (Pair<String, Object> column : columns) {
            Object value = column.getValue();
            update.append("owner", value);
        }
        collection.updateOne(query, new Document("$set", update));
    }

    public static void updateIslandInfo(DatabaseFilter filter, Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
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

    public static void savePlayerPermission(Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document update = new Document();
        String permission = "";
        Object status = null;
        String player = "";
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else if ("player".equals(key)) {
                player = value.toString();
            } else if ("permission".equals(key)) {
                permission = value.toString();
            } else if ("status".equals(key)) {
                status = value;
            }
        }
        update.append("player_permission." + player, new Document(permission, status));
        collection.updateOne(query, new Document("$set", update));
    }

    public static void clearPlayerPermission(DatabaseFilter filter) {

        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document del = new Document();
        String player = "";
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            }
            if ("player".equals(key)) {
                player = value.toString();
            }
        }
        del.append("player_permissions." + player, "");
        collection.updateOne(query, new Document("$unset", del));
    }

    public static void saveRolePermission(Pair<String, Object>... columns) {

        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document update = new Document();
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else {
                update.append("role_permissions." + key, value);
            }
        }
        collection.updateOne(query, new Document("$set", update));

    }

    public static void clearRolePermissions(DatabaseFilter filter) {

        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document update = new Document();
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            }
        }
        update.append("role_permissions", "");

        collection.updateOne(query, new Document("$unset", update));
    }

    public static void saveUpgrade(Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document update = new Document();
        String upgrade = "";
        String level = "";
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else if ("upgrade".equals(key)) {
                upgrade = (String) value;
            } else if ("level".equals(key)) {
                level = (String) value;
            }
        }
        update.append("upgrades." + upgrade, level);
        collection.updateOne(query, new Document("$set", update));
    }

    public static void saveBlockLimit(Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document update = new Document();
        String block = "";
        String limit = "";
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else if ("block".equals(key)) {
                block = (String) value;
            } else if ("limit".equals(key)) {
                limit = (String) value;
            }
        }
        update.append("block_limits." + block, limit);
        collection.updateOne(query, new Document("$set", update));
    }

    public static void clearBlockLimits(DatabaseFilter filter) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document del = new Document();
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            }
        }
        del.append("block_limits", "");
        collection.updateOne(query, new Document("$unset", del));
    }

    public static void removeBlockLimit(DatabaseFilter filter) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document del = new Document();
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else {
                del.append("block_limits." + value, "");
            }
        }
        collection.updateOne(query, new Document("$unset", del));
    }

    public static void saveEntityLimit(Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document update = new Document();
        String entity = "";
        String limit = "";
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else if ("entity".equals(key)) {
                entity = (String) value;
            } else if ("limit".equals(key)) {
                limit = (String) value;
            }
        }
        update.append("entity_limits." + entity, limit);
        collection.updateOne(query, new Document("$set", update));
    }

    public static void clearEntityLimits(DatabaseFilter filter) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document del = new Document();
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            }
        }
        del.append("entity_limits", "");
        collection.updateOne(query, new Document("$unset", del));
    }

    public static void saveIslandEffect(Pair<String, Object>... columns) {

        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document update = new Document();
        String k = "";
        String v = "";
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else if ("effect_type".equals(key)) {
                k = (String) value;
            } else if ("level".equals(key)) {
                v = (String) value;
            }
        }
        update.append("effects." + k, v);
        collection.updateOne(query, new Document("$set", update));
    }

    public static void removeIslandEffect(DatabaseFilter filter) {

        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document del = new Document();
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else {
                del.append("effects." + value, "");
            }
        }
        collection.updateOne(query, new Document("$unset", del));
    }

    public static void clearIslandEffects(DatabaseFilter filter) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            }
        }
        collection.updateOne(query, new Document("$unset", new Document("effects", "")));
    }

    public static void saveRoleLimit(Pair<String, Object>... columns) {

        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document update = new Document();
        String k = "";
        String v = "";
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else if ("role".equals(key)) {
                k = (String) value;
            } else if ("limit".equals(key)) {
                v = (String) value;
            }
        }
        update.append("role_limits." + k, v);
        collection.updateOne(query, new Document("$set", update));
    }

    public static void removeRoleLimit(DatabaseFilter filter) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document del = new Document();
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else {
                del.append("role_limits." + value, "");
            }
        }
        collection.updateOne(query, new Document("$unset", del));
    }

    public static void saveWarp(Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document update = new Document();
        String name = "";
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else if ("name".equals(key)){
                name = (String) value;
            } else {
                update.append(key, value);
            }
        }
        collection.updateOne(query, new Document("$set", new Document("warps." + name, update)));
    }

    public static void updateWarpName(DatabaseFilter filter, Pair<String, Object>[] columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        String oldName = "";
        String newName = "";
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else {
                oldName = (String) value;
            }
        }
        for (Pair<String, Object> column : columns) {
            Object value = column.getValue();
            newName = (String) value;
        }
        collection.updateOne(query, new Document("$rename", new Document("info." + oldName, "info." + newName)));
    }

    public static void updateWarp(DatabaseFilter filter, Pair<String, Object>[] columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document update = new Document();
        String name = "";
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else {
                name = "warps." + value;
            }
        }
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            update.append(name + "." + key, value);
        }
        collection.updateOne(query, new Document("$set", update));
    }

    public static void removeWarp(DatabaseFilter filter) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document update = new Document();
        String name = "";
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else {
                name = "warps." + value;
            }
        }

        collection.updateOne(query, new Document("$unset", update.append(name, "")));
    }

    public static void saveRating(Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document update = new Document();
        String name = "";
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else if ("player".equals(key)){
                name = (String) value;
            } else {
                update.append(key, value);
            }
        }
        collection.updateOne(query, new Document("$set", new Document("warps." + name, update)));
    }

    public static void removeRating(DatabaseFilter filter) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document update = new Document();
        String name = "";
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else {
                name = "rattings." + value;
            }
        }

        collection.updateOne(query, new Document("$unset", update.append(name, "")));
    }

    public static void clearRatings(DatabaseFilter filter) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            }
        }

        collection.updateOne(query, new Document("$unset", new Document("ratings", "")));
    }

    public static void saveMission(Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        String k = "";
        Object v = null;
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else if ("name".equals(key)){
                k = "missions." + value;
            } else {
                v = value;
            }
        }
        collection.updateOne(query, new Document("$set", new Document(k, v)));
    }

    public static void removeMission(DatabaseFilter filter) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        String name = "";
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else {
                name = "missions." + value;
            }
        }

        collection.updateOne(query, new Document("$unset", new Document(name, "")));
    }

    public static void saveIslandFlag(Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        String k = "";
        Object v = null;
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else if ("name".equals(key)){
                k = "flags." + value;
            } else {
                v = value;
            }
        }
        collection.updateOne(query, new Document("$set", new Document(k, v)));
    }

    public static void removeIslandFlag(DatabaseFilter filter) {

        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        String name = "";
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else {
                name = "flags." + value;
            }
        }

        collection.updateOne(query, new Document("$unset", new Document(name, "")));
    }

    public static void saveGeneratorRate(Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        String k1 = "";
        String k2 = "";
        Object v = null;
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else if ("environment".equals(key)){
                k1 = key;
            }  else if ("block".equals(key)){
                k2 = key;
            } else {
                v = value;
            }
        }

        collection.updateOne(query, new Document("$set", new Document("generators." + k1 + "." + k2, v)));
    }

    public static void removeGeneratorRate(DatabaseFilter filter) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        String environment = "";
        String block = "";
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else if ("environment".equals(key)){
                environment = key;
            } else if ("block".equals(key)){
                block = key;
            }
        }
        collection.updateOne(query, new Document("$unset", new Document("generators." + environment + "." + block, "")));
    }

    public static void clearGeneratorRates(DatabaseFilter filter) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            }
        }

        collection.updateOne(query, new Document("$unset", new Document("generators", "")));
    }

    public static void saveEntityCounts(DatabaseFilter filter, Pair<String, Object>[] columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document update = new Document();
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("uuid".equals(key)) {
                query.append("_id", value);
            } else {
                query.append(key, value);
            }
        }
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            update.append(key, value);
        }
        collection.updateOne(query, new Document("$set", update));
    }

    public static void saveIslandChest(Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        String index = "";
        Object contents = null;
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else if ("index".equals(key)){
                index = String.valueOf(value);
            }  else if ("contents".equals(key)){
                contents = value;
            }
        }

        collection.updateOne(query, new Document("$set", new Document("chests." + index, contents)));
    }

    public static void updateBank(DatabaseFilter filter, Pair<String, Object>[] columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document update = new Document();
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else {
                query.append(key, value);
            }
        }
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            update.append("banks." + key, value);
        }
        collection.updateOne(query, new Document("$set", update));
    }

    public static void updateBankTransactions(DatabaseFilter filter, Pair<String, Object>[] columns) {
        //? Why?
    }

    public static void saveVisitor(Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        String player = "";
        Object visit_time = null;
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else if ("player".equals(key)){
                player = String.valueOf(value);
            }  else if ("visit_time".equals(key)){
                visit_time = value;
            }
        }
        collection.updateOne(query, new Document("$set", new Document("visitors." + player, visit_time)));
    }

    public static void saveWarpCategory(Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document update = new Document();
        String name = "";
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else if ("name".equals(key)) {
                name = "warp_categories." + value;
            } else {
                update.append(key, value);
            }
        }
        collection.updateOne(query, new Document("$set", new Document(name, update)));
    }

    public static void updateWarpCategoryName(DatabaseFilter filter, Pair<String, Object>[] columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        String oldName = "";
        String newName = "";
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else {
                oldName = (String) value;
            }
        }
        for (Pair<String, Object> column : columns) {
            Object value = column.getValue();
            newName = (String) value;
        }
        collection.updateOne(query, new Document("$rename", new Document("warp_categories." + oldName, "info." + newName)));
    }

    public static void updateWarpCategory(DatabaseFilter filter, Pair<String, Object>[] columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document update = new Document();
        String k1 = "";
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else {
                k1 = key;
            }
        }
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            update.append("warp_categories." + k1 + "." + key, value);
        }
        collection.updateOne(query, new Document("$set", update));
    }

    public static void removeWarpCategory(DatabaseFilter filter) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document update = new Document();
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else {
                update.append("warp_categories." + value, "");
            }
        }
        collection.updateOne(query, new Document("$unset", update));
    }

    //
//    public static void saveIslandLeader(DatabaseFilter filter, Pair<String, Object>[] columns) {
//        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
//        Document query = new Document();
//        Document update = new Document();
//        for (Pair<String, Object> column : filter.getFilters()) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            if ("uuid".equals(key)) {
//                query.append("_id", value);
//            }
//        }
//        for (Pair<String, Object> column : columns) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            update.append(key, value);
//        }
//        collection.updateOne(query, new Document("$set", update));
//    }
//
//    public static void saveBankBalance(DatabaseFilter filter, Pair<String, Object>[] columns) {
//        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
//        Document query = new Document();
//        Document update = new Document();
//        for (Pair<String, Object> column : filter.getFilters()) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            if ("island".equals(key)) {
//                query.append("_id", value);
//            }
//        }
//        for (Pair<String, Object> column : columns) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            update.append("banks." + key, value);
//        }
//
//        collection.updateOne(query, new Document("$set", update));
//    }
//
    public static void saveBankTransaction(Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document update = new Document();
        Object v = null;
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else {
                update.append(key, value);
            }
        }
        collection.updateOne(query, new Document("$set", update));
    }

    public static void savePersistentDataContainer(Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document update = new Document();
        Object v = null;
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else {
                update.append(key, value);
            }
        }
        collection.updateOne(query, new Document("$set", update));
    }

    public static void removePersistentDataContainer(DatabaseFilter filter) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("uuid".equals(key)) {
                query.append("_id", value);
            }
        }
        collection.updateOne(query, new Document("$unset", new Document("custom_data", "")));
    }

    public static void insertIsland(Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
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
        collection.insertOne(new Document(update));
    }

    public static void insertIslandBank(Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document update = new Document();
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else {
                update.append("bank" + key, value);
            }
        }
        collection.updateOne(query, new Document("$set", update));
    }

    public static void insertIslandSettings(Pair<String, Object>... columns) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        Document query = new Document();
        Document update = new Document();
        for (Pair<String, Object> column : columns) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                query.append("_id", value);
            } else {
                update.append("settings" + key, value);
            }
        }
        collection.updateOne(query, new Document("$set", update));
    }

    public static void deleteIsland(DatabaseFilter filter) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
        for (Pair<String, Object> column : filter.getFilters()) {
            String key = column.getKey();
            Object value = column.getValue();
            if ("island".equals(key)) {
                collection.deleteOne(Filters.eq("_id", value));
            }
        }
    }


    //    public static void saveCoopLimit(DatabaseFilter filter, Pair<String, Object>[] columns) {
//        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
//        Document query = new Document();
//        Document update = new Document();
//        for (Pair<String, Object> column : filter.getFilters()) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            if ("island".equals(key)) {
//                query.append("_id", value);
//            }
//        }
//        for (Pair<String, Object> column : columns) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            update.append("settings." + key, value);
//        }
//        collection.updateOne(query, new Document("$set", update));
//    }

    //    public static void saveUnlockedWorlds(DatabaseFilter filter, Pair<String, Object>... columns) {
//        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
//        Document query = new Document();
//        Document update = new Document();
//        for (Pair<String, Object> column : filter.getFilters()) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            if ("uuid".equals(key)) {
//                query.append("_id", value);
//            }
//        }
//        for (Pair<String, Object> column : columns) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            update.append(key, value);
//        }
//        collection.updateOne(query, new Document("$set", update));
//    }

    //    public static void saveName(DatabaseFilter filter, Pair<String, Object>[] columns) {
//
//        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
//        Document query = new Document();
//        Document update = new Document();
//        for (Pair<String, Object> column : filter.getFilters()) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            if ("uuid".equals(key)) {
//                query.append("_id", value);
//            } else {
//                query.append(key, value);
//            }
//        }
//        for (Pair<String, Object> column : columns) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            update.append(key, value);
//        }
//        collection.updateOne(query, new Document("$set", update));
//    }
//
//    public static void saveDescription(DatabaseFilter filter, Pair<String, Object>[] columns) {
//        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
//        Document query = new Document();
//        Document update = new Document();
//        for (Pair<String, Object> column : filter.getFilters()) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            if ("uuid".equals(key)) {
//                query.append("_id", value);
//            } else {
//                query.append(key, value);
//            }
//        }
//        for (Pair<String, Object> column : columns) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            update.append(key, value);
//        }
//        collection.updateOne(query, new Document("$set", update));
//    }
//
//    public static void saveSize(DatabaseFilter filter, Pair<String, Object>[] columns) {
//        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
//        Document query = new Document();
//        Document update = new Document();
//        for (Pair<String, Object> column : filter.getFilters()) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            if ("island".equals(key)) {
//                query.append("_id", value);
//            }
//        }
//        for (Pair<String, Object> column : columns) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            update.append("settings." + key, value);
//        }
//        collection.updateOne(query, new Document("$set", update));
//    }
//
//    public static void saveDiscord(DatabaseFilter filter, Pair<String, Object>[] columns) {
//        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
//        Document query = new Document();
//        Document update = new Document();
//        for (Pair<String, Object> column : filter.getFilters()) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            if ("uuid".equals(key)) {
//                query.append("_id", value);
//            } else {
//                query.append(key, value);
//            }
//        }
//        for (Pair<String, Object> column : columns) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            update.append(key, value);
//        }
//    }
//
//    public static void savePaypal(DatabaseFilter filter, Pair<String, Object>[] columns) {
//        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
//        Document query = new Document();
//        Document update = new Document();
//        for (Pair<String, Object> column : filter.getFilters()) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            if ("uuid".equals(key)) {
//                query.append("_id", value);
//            } else {
//                query.append(key, value);
//            }
//        }
//        for (Pair<String, Object> column : columns) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            update.append(key, value);
//        }
//        collection.updateOne(query, new Document("$set", update));
//    }
//
//    public static void saveLockedStatus(DatabaseFilter filter, Pair<String, Object>[] columns) {
//
//        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
//        Document query = new Document();
//        Document update = new Document();
//        for (Pair<String, Object> column : filter.getFilters()) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            if ("uuid".equals(key)) {
//                query.append("_id", value);
//            } else {
//                query.append(key, value);
//            }
//        }
//        for (Pair<String, Object> column : columns) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            update.append(key, value);
//        }
//        collection.updateOne(query, new Document("$set", update));
//    }
//
//    public static void saveIgnoredStatus(DatabaseFilter filter, Pair<String, Object>[] columns) {
//
//        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
//        Document query = new Document();
//        Document update = new Document();
//        for (Pair<String, Object> column : filter.getFilters()) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            if ("uuid".equals(key)) {
//                query.append("_id", value);
//            } else {
//                query.append(key, value);
//            }
//        }
//        for (Pair<String, Object> column : columns) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            update.append(key, value);
//        }
//        collection.updateOne(query, new Document("$set", update));
//    }
//
//    public static void saveLastTimeUpdate(DatabaseFilter filter, Pair<String, Object>[] columns) {
//        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
//        Document query = new Document();
//        Document update = new Document();
//        for (Pair<String, Object> column : filter.getFilters()) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            if ("uuid".equals(key)) {
//                query.append("_id", value);
//            } else {
//                query.append(key, value);
//            }
//        }
//        for (Pair<String, Object> column : columns) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            update.append(key, value);
//        }
//        collection.updateOne(query, new Document("$set", update));
//    }
//
//    public static void saveBankLimit(DatabaseFilter filter, Pair<String, Object>[] columns) {
//        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
//        Document query = new Document();
//        Document update = new Document();
//        for (Pair<String, Object> column : filter.getFilters()) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            if ("island".equals(key)) {
//                query.append("_id", value);
//            } else {
//                query.append(key, value);
//            }
//        }
//        for (Pair<String, Object> column : columns) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            update.append("settings." + key, value);
//        }
//        collection.updateOne(query, new Document("$set", update));
//    }
//
//    public static void saveBonusWorth(DatabaseFilter filter, Pair<String, Object>[] columns) {
//        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
//        Document query = new Document();
//        Document update = new Document();
//        for (Pair<String, Object> column : filter.getFilters()) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            if ("uuid".equals(key)) {
//                query.append("_id", value);
//            } else {
//                query.append(key, value);
//            }
//        }
//        for (Pair<String, Object> column : columns) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            update.append(key, value);
//        }
//        collection.updateOne(query, new Document("$set", update));
//    }
//
//    public static void saveBonusLevel(DatabaseFilter filter, Pair<String, Object>[] columns) {
//        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
//        Document query = new Document();
//        Document update = new Document();
//        for (Pair<String, Object> column : filter.getFilters()) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            if ("uuid".equals(key)) {
//                query.append("_id", value);
//            } else {
//                query.append(key, value);
//            }
//        }
//        for (Pair<String, Object> column : columns) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            update.append(key, value);
//        }
//        collection.updateOne(query, new Document("$set", update));
//    }

    //    public static void saveCropGrowth(DatabaseFilter filter, Pair<String, Object>[] columns) {
//        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
//        Document query = new Document();
//        Document update = new Document();
//        for (Pair<String, Object> column : filter.getFilters()) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            if ("island".equals(key)) {
//                query.append("_id", value);
//            } else {
//                query.append(key, value);
//            }
//        }
//        for (Pair<String, Object> column : columns) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            update.append("settings." + key, value);
//        }
//        collection.updateOne(query, new Document("$set", update));
//
//    }
//
//    public static void saveSpawnerRates(DatabaseFilter filter, Pair<String, Object>[] columns) {
//        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
//        Document query = new Document();
//        Document update = new Document();
//        for (Pair<String, Object> column : filter.getFilters()) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            if ("island".equals(key)) {
//                query.append("_id", value);
//            } else {
//                query.append(key, value);
//            }
//        }
//        for (Pair<String, Object> column : columns) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            update.append("settings." + key, value);
//        }
//        collection.updateOne(query, new Document("$set", update));
//    }
//
//    public static void saveMobDrops(DatabaseFilter filter, Pair<String, Object>[] columns) {
//        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
//        Document query = new Document();
//        Document update = new Document();
//        for (Pair<String, Object> column : filter.getFilters()) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            if ("island".equals(key)) {
//                query.append("_id", value);
//            } else {
//                query.append(key, value);
//            }
//        }
//        for (Pair<String, Object> column : columns) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            update.append("settings." + key, value);
//        }
//        collection.updateOne(query, new Document("$set", update));
//    }

    //    public static void saveTeamLimit(DatabaseFilter filter, Pair<String, Object>[] columns) {
//        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
//        Document query = new Document();
//        Document update = new Document();
//        for (Pair<String, Object> column : filter.getFilters()) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            if ("island".equals(key)) {
//                query.append("_id", value);
//            } else {
//                query.append(key, value);
//            }
//        }
//        for (Pair<String, Object> column : columns) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            update.append("settings." + key, value);
//        }
//        collection.updateOne(query, new Document("$set", update));
//    }
//
//    public static void saveWarpsLimit(DatabaseFilter filter, Pair<String, Object>[] columns) {
//
//        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
//        Document query = new Document();
//        Document update = new Document();
//        for (Pair<String, Object> column : filter.getFilters()) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            if ("island".equals(key)) {
//                query.append("_id", value);
//            } else {
//                query.append(key, value);
//            }
//        }
//        for (Pair<String, Object> column : columns) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            update.append("settings." + key, value);
//        }
//        collection.updateOne(query, new Document("$set", update));
//    }

    //
//    public static void updateWarpLocation(DatabaseFilter filter, Pair<String, Object>[] columns) {
//        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
//        Document query = new Document();
//        Document update = new Document();
//        String name = "";
//        for (Pair<String, Object> column : filter.getFilters()) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            if ("island".equals(key)) {
//                query.append("_id", value);
//            } else {
//                name = "warps." + value;
//            }
//        }
//        for (Pair<String, Object> column : columns) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            update.append(name + "." + key, value);
//        }
//        collection.updateOne(query, new Document("$set", update));
//    }
//
//    public static void updateWarpPrivateStatus(DatabaseFilter filter, Pair<String, Object>[] columns) {
//        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
//        Document query = new Document();
//        Document update = new Document();
//        String name = "";
//        for (Pair<String, Object> column : filter.getFilters()) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            if ("island".equals(key)) {
//                query.append("_id", value);
//            } else {
//                name = "warps." + value;
//            }
//        }
//        for (Pair<String, Object> column : columns) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            update.append(name + "." + key, value);
//        }
//        collection.updateOne(query, new Document("$set", update));
//    }
//
//    public static void updateWarpIcon(DatabaseFilter filter, Pair<String, Object>[] columns) {
//        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
//        Document query = new Document();
//        Document update = new Document();
//        String name = "";
//        for (Pair<String, Object> column : filter.getFilters()) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            if ("island".equals(key)) {
//                query.append("_id", value);
//            } else {
//                name = "warps." + value;
//            }
//        }
//        for (Pair<String, Object> column : columns) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            update.append(name + "." + key, value);
//        }
//        collection.updateOne(query, new Document("$set", update));
//    }

    //
//    public static void saveGeneratedSchematics(DatabaseFilter filter, Pair<String, Object>[] columns) {
//        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
//        Document query = new Document();
//        Document update = new Document();
//        for (Pair<String, Object> column : filter.getFilters()) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            if ("uuid".equals(key)) {
//                query.append("_id", value);
//            } else {
//                query.append(key, value);
//            }
//        }
//        for (Pair<String, Object> column : columns) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            update.append(key, value);
//        }
//        collection.updateOne(query, new Document("$set", update));
//    }
//
//    public static void saveDirtyChunks(DatabaseFilter filter, Pair<String, Object>[] columns) {
//        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
//        Document query = new Document();
//        Document update = new Document();
//        for (Pair<String, Object> column : filter.getFilters()) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            if ("uuid".equals(key)) {
//                query.append("_id", value);
//            } else {
//                query.append(key, value);
//            }
//        }
//        for (Pair<String, Object> column : columns) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            update.append(key, value);
//        }
//        collection.updateOne(query, new Document("$set", update));
//    }
//
//    public static void saveBlockCounts(DatabaseFilter filter, Pair<String, Object>[] columns) {
//        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
//        Document query = new Document();
//        Document update = new Document();
//        for (Pair<String, Object> column : filter.getFilters()) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            if ("uuid".equals(key)) {
//                query.append("_id", value);
//            } else {
//                query.append(key, value);
//            }
//        }
//        for (Pair<String, Object> column : columns) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            update.append(key, value);
//        }
//        collection.updateOne(query, new Document("$set", update));
//    }

    //    public static void saveLastInterestTime(DatabaseFilter filter, Pair<String, Object>[] columns) {
//        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
//        Document query = new Document();
//        Document update = new Document();
//        for (Pair<String, Object> column : filter.getFilters()) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            if ("island".equals(key)) {
//                query.append("_id", value);
//            } else {
//                query.append(key, value);
//            }
//        }
//        for (Pair<String, Object> column : columns) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            update.append("banks." + key, value);
//        }
//        collection.updateOne(query, new Document("$set", update));
//    }

    //    public static void updateWarpCategory(DatabaseFilter filter, Pair<String, Object>[] columns) {
//        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
//        Document query = new Document();
//        Document update = new Document();
//        for (Pair<String, Object> column : filter.getFilters()) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            if ("island".equals(key)) {
//                query.append("_id", value);
//            } else {
//                query.append(key, value);
//            }
//        }
//        for (Pair<String, Object> column : columns) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            update.append("warps." + key, value);
//        }
//        collection.updateOne(query, new Document("$set", update));
//    }

    //
//    public static void updateWarpCategorySlot(DatabaseFilter filter, Pair<String, Object>[] columns) {
//        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
//        Document query = new Document();
//        Document update = new Document();
//        String k1 = "";
//        for (Pair<String, Object> column : filter.getFilters()) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            if ("island".equals(key)) {
//                query.append("_id", value);
//            } else {
//                k1 = key;
//            }
//        }
//        for (Pair<String, Object> column : columns) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            update.append("warp_categories." + k1 + "." + key, value);
//        }
//        collection.updateOne(query, new Document("$set", update));
//    }
//
//    public static void updateWarpCategoryIcon(DatabaseFilter filter, Pair<String, Object>[] columns) {
//        MongoCollection<Document> collection = MongoDBClient.getCollection("islands_info");
//        Document query = new Document();
//        Document update = new Document();
//        String k1 = "";
//        for (Pair<String, Object> column : filter.getFilters()) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            if ("island".equals(key)) {
//                query.append("_id", value);
//            } else {
//                k1 = key;
//            }
//        }
//        for (Pair<String, Object> column : columns) {
//            String key = column.getKey();
//            Object value = column.getValue();
//            update.append("warp_categories." + k1 + "." + key, value);
//        }
//        collection.updateOne(query, new Document("$set", update));
//    }

}
