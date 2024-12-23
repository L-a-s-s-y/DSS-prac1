package com.dss.practica1.service;

import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Service
public class DatabaseService {

     
    public String exportDatabase() throws SQLException, IOException {
        String url = "jdbc:h2:file:C:/Users/Lassy/Desktop/Master UGR/DSS/Practicas/Materiales para la practica 1/shop/C_/Users/Usuario/testdb;DB_CLOSE_ON_EXIT=FALSE"; 
        String user = "sa";
        String password = "password"; 

        try (Connection connection = DriverManager.getConnection(url, user, password);
               FileWriter writer = new FileWriter("database.sql")) {
               Statement statement = connection.createStatement();
               String sql = "SCRIPT TO 'database.sql'";
               statement.execute(sql);
           }
        return "database.sql"; 
    }
}
