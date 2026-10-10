package com.edusphere.config;

public final class DatabaseConfig {
    private DatabaseConfig(){}

    public static final String URL=System.getenv("POSTGRES_URL");
    public static final String USER=System.getenv("POSTGRES_USER");
    public static final String PASSWORD=System.getenv("POSTGRES_PASSWORD");
    public static final String connectionString=System.getenv("MONGO_DB_CONN_STRING");


}
