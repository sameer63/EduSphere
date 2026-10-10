package com.edusphere.util;

import com.edusphere.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {

    private DBConnection(){}

    public static Connection getConnection()throws SQLException{
      try {
           Class.forName("org.postgresql.Driver");
          return DriverManager.getConnection(
                  DatabaseConfig.URL,
                  DatabaseConfig.USER,
                  DatabaseConfig.PASSWORD
          );
      }catch (Exception e){
          e.printStackTrace();
          throw new RuntimeException("Driver not found");
      }

    }
}
