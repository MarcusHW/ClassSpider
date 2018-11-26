package com.jsb.classspider.Dao;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * 数据层
 */
public class MongoDao {
    private static String host = "localhost";

    private static int port = 27017;

    private static String dbName = "Ryze";

    private static MongoClient mongoClient;

    public MongoDao() {
        init();
    }


    public static MongoCollection<Document> getCollection(String colName) {
        return getCollection(dbName, colName);
    }

    private static void init() {
        mongoClient = new MongoClient(host, port);
    }

    private static MongoCollection<Document> getCollection(String dbName, String colName) {
        if (mongoClient == null) {
            init();
        }
        MongoDatabase database = mongoClient.getDatabase(dbName);
        MongoCollection<Document> collection = database.getCollection(colName);
        return collection;
    }
}
