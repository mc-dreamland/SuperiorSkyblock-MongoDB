package com.bgsoftware.superiorskyblock.module.mongodb.bridge;

import com.bgsoftware.superiorskyblock.api.data.DatabaseBridge;
import com.bgsoftware.superiorskyblock.api.data.DatabaseBridgeMode;
import com.bgsoftware.superiorskyblock.api.data.DatabaseFilter;
import com.bgsoftware.superiorskyblock.api.objects.Pair;
import com.bgsoftware.superiorskyblock.module.mongodb.MongoDBClient;
import com.bgsoftware.superiorskyblock.module.mongodb.threading.DatabaseExecutor;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.eq;

public final class MongoDatabaseBridge implements DatabaseBridge {

    private DatabaseBridgeMode databaseBridgeMode = DatabaseBridgeMode.IDLE;
    private Map<MongoCollection<Document>, List<WriteModel<Document>>> batchOperations;

    public MongoDatabaseBridge() {
    }

    @Override
    public void loadAllObjects(String collectionName, Consumer<Map<String, Object>> resultConsumer) {
        MongoCollection<Document> collection = MongoDBClient.getCollection(collectionName);
        try (MongoCursor<Document> cursor = collection.find().cursor()) {
            while (cursor.hasNext()) {
                resultConsumer.accept(cursor.next());
            }
        }
    }

    @Override
    public void batchOperations(boolean batchOperations) {
        if (batchOperations) {
            this.batchOperations = new HashMap<>();
        } else if (this.batchOperations != null) {
            Map<MongoCollection<Document>, List<WriteModel<Document>>> batchOperationsCopy = this.batchOperations;
            this.batchOperations = null;
            DatabaseExecutor.execute(() -> batchOperationsCopy.forEach(MongoCollection::bulkWrite));
        }
    }

    @Override
    public void updateObject(String collectionName, DatabaseFilter filter, Pair<String, Object>[] columns) {
        if (databaseBridgeMode != DatabaseBridgeMode.SAVE_DATA)
            return;

        DatabaseExecutor.execute(() -> {
            switch (collectionName) {
                case "islands_members" -> {
                    MongoDatabaseIslandSaver.saveMemberRole(filter, columns);
                }
                case "islands_settings" -> {
                    MongoDatabaseIslandSaver.updateSettings(filter, columns);
                }
                case "islands" -> {
                    MongoDatabaseIslandSaver.updateIslandInfo(filter, columns);
                }
                case "islands_warps" -> {
                    if (columns[0].getKey().equals("name")) {
                        MongoDatabaseIslandSaver.updateWarpName(filter, columns);
                    } else {
                        MongoDatabaseIslandSaver.updateWarp(filter, columns);
                    }
                }
                case "islands_banks" -> {
                    MongoDatabaseIslandSaver.updateBank(filter, columns);
                }
                case "islands_warp_categories" -> {
                    if (columns[0].getKey().equals("name")) {
                        MongoDatabaseIslandSaver.updateWarpCategoryName(filter, columns);
                    } else {
                        MongoDatabaseIslandSaver.updateWarpCategory(filter, columns);
                    }
                }
                case "players" -> {
                    MongoDatabasePlayerSaver.updatePlayer(filter, columns);
                }
                case "players_settings" -> {
                    MongoDatabasePlayerSaver.updatePlayerSetting(filter, columns);
                }
            }
        });
    }

    @SafeVarargs
    @Override
    public final void insertObject(String collectionName, Pair<String, Object>... columns) {
        if (databaseBridgeMode != DatabaseBridgeMode.SAVE_DATA)
            return;

        DatabaseExecutor.execute(() -> {
            switch (collectionName) {
                case "islands_members" -> {
                    MongoDatabaseIslandSaver.addMembers(columns);
                }
                case "islands_bans" -> {
                    MongoDatabaseIslandSaver.addBannedPlayer(columns);
                }
                case "islands_homes" -> {
                    MongoDatabaseIslandSaver.addIslandHome(columns);
                }
                case "islands_visitor_homes" -> {
                    MongoDatabaseIslandSaver.saveVisitorLocation(columns);
                }
                case "islands_player_permissions" -> {
                    MongoDatabaseIslandSaver.savePlayerPermission(columns);
                }
                case "islands_role_permissions" -> {
                    MongoDatabaseIslandSaver.saveRolePermission(columns);
                }
                case "islands_upgrades" -> {
                    MongoDatabaseIslandSaver.saveUpgrade(columns);
                }
                case "islands_block_limits" -> {
                    MongoDatabaseIslandSaver.saveBlockLimit(columns);
                }
                case "islands_entity_limits" -> {
                    MongoDatabaseIslandSaver.saveEntityLimit(columns);
                }
                case "islands_effects" -> {
                    MongoDatabaseIslandSaver.saveIslandEffect(columns);
                }
                case "islands_role_limits" -> {
                    MongoDatabaseIslandSaver.saveRoleLimit(columns);
                }
                case "islands_warps" -> {
                    MongoDatabaseIslandSaver.saveWarp(columns);
                }
                case "islands_ratings" -> {
                    MongoDatabaseIslandSaver.saveRating(columns);
                }
                case "islands_missions" -> {
                    MongoDatabaseIslandSaver.saveMission(columns);
                }
                case "islands_flags" -> {
                    MongoDatabaseIslandSaver.saveIslandFlag(columns);
                }
                case "islands_generators" -> {
                    MongoDatabaseIslandSaver.saveGeneratorRate(columns);
                }
                case "islands_chests" -> {
                    MongoDatabaseIslandSaver.saveIslandChest(columns);
                }
                case "islands_visitors" -> {
                    MongoDatabaseIslandSaver.saveVisitor(columns);
                }
                case "islands_warp_categories" -> {
                    MongoDatabaseIslandSaver.saveWarpCategory(columns);
                }
                case "bank_transactions" -> {
                    MongoDatabaseIslandSaver.saveBankTransaction(columns);
                }
                case "savePersistentDataContainer" -> {
                    MongoDatabaseIslandSaver.savePersistentDataContainer(columns);
                }
                case "islands" -> {
                    MongoDatabaseIslandSaver.insertIsland(columns);
                }
                case "islands_banks" -> {
                    MongoDatabaseIslandSaver.insertIslandBank(columns);
                }
                case "islands_settings" -> {
                    MongoDatabaseIslandSaver.insertIslandSettings(columns);
                }
                case "players_missions" -> {
                    MongoDatabasePlayerSaver.saveMission(columns);
                }
                case "players_custom_data" -> {
                    MongoDatabasePlayerSaver.savePersistentDataContainer(columns);
                }
                case "players" -> {
                    MongoDatabasePlayerSaver.insertPlayer(columns);
                }
                case "players_settings" -> {
                    MongoDatabasePlayerSaver.insertPlayerSetting(columns);
                }
            }


        });
    }

    @Override
    public void deleteObject(String collectionName, DatabaseFilter filter) {
        if (databaseBridgeMode != DatabaseBridgeMode.SAVE_DATA)
            return;

        DatabaseExecutor.execute(() -> {
            switch (collectionName) {
                case "islands_members" -> {
                    MongoDatabaseIslandSaver.removeMembers(filter);
                }
                case "islands_bans" -> {
                    MongoDatabaseIslandSaver.removeBannedPlayer(filter);
                }
                case "islands_homes" -> {
                    MongoDatabaseIslandSaver.deleteIslandHome(filter);
                }
                case "islands_visitor_homes" -> {
                    MongoDatabaseIslandSaver.removeVisitorLocation(filter);
                }
                case "islands_player_permissions" -> {
                    MongoDatabaseIslandSaver.clearPlayerPermission(filter);
                }
                case "islands_role_permissions" -> {
                    MongoDatabaseIslandSaver.clearRolePermissions(filter);
                }
                case "islands_block_limits" -> {
                    if (filter.getFilters().size() == 1) {
                        MongoDatabaseIslandSaver.clearBlockLimits(filter);
                    } else {
                        MongoDatabaseIslandSaver.removeBlockLimit(filter);
                    }
                }
                case "islands_entity_limits" -> {
                    MongoDatabaseIslandSaver.clearEntityLimits(filter);
                }
                case "islands_effects" -> {
                    if (filter.getFilters().size() == 1) {
                        MongoDatabaseIslandSaver.clearIslandEffects(filter);
                    } else {
                        MongoDatabaseIslandSaver.removeIslandEffect(filter);
                    }
                }
                case "islands_role_limits" -> {
                    MongoDatabaseIslandSaver.removeRoleLimit(filter);
                }
                case "islands_warps" -> {
                    MongoDatabaseIslandSaver.removeWarp(filter);
                }
                case "islands_ratings" -> {
                    if (filter.getFilters().size() == 1) {
                        MongoDatabaseIslandSaver.clearRatings(filter);
                    } else {
                        MongoDatabaseIslandSaver.removeRating(filter);
                    }
                }
                case "islands_missions" -> {
                    MongoDatabaseIslandSaver.removeMission(filter);
                }
                case "islands_flags" -> {
                    MongoDatabaseIslandSaver.removeIslandFlag(filter);
                }
                case "islands_generators" -> {
                    if (filter.getFilters().size() == 2) {
                        MongoDatabaseIslandSaver.clearGeneratorRates(filter);
                    } else {
                        MongoDatabaseIslandSaver.removeGeneratorRate(filter);
                    }
                }
                case "islands_warp_categories" -> {
                    MongoDatabaseIslandSaver.removeWarpCategory(filter);
                }
                case "islands_custom_data" -> {
                    MongoDatabaseIslandSaver.removePersistentDataContainer(filter);
                }
                case "islands" -> {
                    MongoDatabaseIslandSaver.deleteIsland(filter);
                }
                case "players_missions" -> {
                    MongoDatabasePlayerSaver.removeMission(filter);
                }
                case "players_custom_data" -> {
                    MongoDatabasePlayerSaver.removePersistentDataContainer(filter);
                }
            }

        });
    }

    @Override
    public void loadObject(String collectionName, DatabaseFilter filter, Consumer<Map<String, Object>> resultConsumer) {
        MongoCollection<Document> collection = MongoDBClient.getCollection(collectionName);
        try (MongoCursor<Document> cursor = collection.find(buildFilter(filter)).cursor()) {
            while (cursor.hasNext()) {
                resultConsumer.accept(cursor.next());
            }
        }
    }

    @Override
    public void setDatabaseBridgeMode(DatabaseBridgeMode databaseBridgeMode) {
        this.databaseBridgeMode = databaseBridgeMode;
    }

    @Override
    public DatabaseBridgeMode getDatabaseBridgeMode() {
        return this.databaseBridgeMode;
    }


    private static Bson buildFilter(@Nullable DatabaseFilter filter) {
        if (filter == null || filter.getFilters().isEmpty())
            return Filters.empty();

        List<Bson> filters = new LinkedList<>();

        filter.getFilters().forEach(columnFilter ->
                filters.add(eq(columnFilter.getKey(), columnFilter.getValue())));

        return buildFiltersFromList(filters);
    }

    private static Bson buildFiltersFromList(List<Bson> filters) {
        return filters.isEmpty() ? Filters.empty() : filters.size() == 1 ? filters.get(0) : Filters.and(filters);
    }

}
