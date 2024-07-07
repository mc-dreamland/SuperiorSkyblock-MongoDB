package com.bgsoftware.superiorskyblock.module.mongodb;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public final class MongoDBInitializer {

    public static void initDatabase(SuperiorSkyblock plugin) {
        if (!containsGrid()) {
            MongoCollection<Document> collection = MongoDBClient.getCollection("grid");
            String worldName = plugin.getSettings().getWorlds().getWorldName();
            collection.insertOne(new Document()
                    .append("last_island", worldName + ", 0, 100, 0")
                    .append("max_island_size", plugin.getSettings().getMaxIslandSize())
                    .append("world", worldName)
            );
        }

        createIndexes();
    }

    private static boolean containsGrid() {
        MongoCollection<Document> collection = MongoDBClient.getCollection("grid");
        return collection.find().cursor().hasNext();
    }

    private static void createIndexes() {
        MongoDBClient.createIndex("islands_info", "_id");
        MongoDBClient.createIndex("players_info", "_id");
    }


}
