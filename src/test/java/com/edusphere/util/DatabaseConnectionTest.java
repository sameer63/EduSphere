package com.edusphere.util;

import java.sql.Connection;
import com.edusphere.util.DBConnection;

public class DatabaseConnectionTest {
    public static void main(String []args){
        try (Connection conn=DBConnection.getConnection()){
            System.out.println("PostgreSQL Connection Successful!");
            System.out.println("Database :"+conn.getCatalog());

        }catch (Exception e){
            System.err.println("PostgreSQL Connection failed!");
            e.printStackTrace();
        }
    }
}
