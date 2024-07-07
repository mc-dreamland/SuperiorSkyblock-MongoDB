package com.bgsoftware.superiorskyblock.module.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MongoDBClient {

    private static final Map<String, List<String>> CACHED_INDEXES = new HashMap<>();

    private static MongoClient mongoClient;
    private static MongoDatabase database;

    private MongoDBClient() {

    }

    public static void connect(String url, String databaseName) {
        mongoClient = MongoClients.create(new ConnectionString(url));
        mongoClient.startSession(); // Makes sure connection is valid.
        database = mongoClient.getDatabase(databaseName);
        System.out.println("Connected to database: " + database.getName());
    }

    public static void close() {
        mongoClient.close();
    }

    public static MongoCollection<Document> getCollection(String collectionName) {
        return database.getCollection(collectionName);
    }

    public static void createIndex(String collectionName, String... fieldNames) {
        MongoCollection<Document> collection = MongoDBClient.getCollection(collectionName);
        IndexOptions indexOptions = new IndexOptions();

        // Check if _id is in fieldNames array
        boolean isIdField = Arrays.asList(fieldNames).contains("_id");

        // Only set unique if the index is not on _id field
        if (!isIdField) {
            indexOptions.unique(true);
        }

        collection.createIndex(Indexes.ascending(fieldNames), indexOptions);
        CACHED_INDEXES.put(collectionName, Arrays.asList(fieldNames));
    }

    @Nullable
    public static List<String> getCachedIndex(String collectionName) {
        return CACHED_INDEXES.get(collectionName);
    }

}
