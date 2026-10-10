
package com.edusphere.util;

import com.edusphere.config.DatabaseConfig;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.util.concurrent.TimeUnit;

public final class MongoDBConfig {

    private static final String CONNECTION_STRING = DatabaseConfig.connectionString;

    private static final String DATABASE_NAME =
            "edusphere_activity";

    private static final MongoClient CLIENT = createClient();

    private MongoDBConfig() {
    }


    private static MongoClient createClient() {

        ConnectionString connectionString =
                new ConnectionString(CONNECTION_STRING);

        MongoClientSettings settings =
                MongoClientSettings.builder()
                        .applyConnectionString(connectionString)
                        .applyToSocketSettings(builder ->
                                builder.connectTimeout(
                                        5, TimeUnit.SECONDS))
                        .build();

        return MongoClients.create(settings);
    }

    public static MongoDatabase getDatabase() {
        return CLIENT.getDatabase(DATABASE_NAME);
    }

    public static void close() {
        CLIENT.close();
    }
}
