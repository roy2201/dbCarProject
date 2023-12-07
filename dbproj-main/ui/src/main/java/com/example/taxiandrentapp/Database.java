package com.example.taxiandrentapp;

import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TableView;

import java.sql.*;

import static java.lang.Class.forName;

public class Database {

    public static Database databaseInstance;
    public static Connection connection;

    private Database() throws SQLException {
    }

    public static final String USERNAME = "sa";
    public static final String PASSWORD = "root123123";
    public static final String CONN_STR = "jdbc:sqlserver://localhost\\sqlexpress:1433;databaseName=carrentaldb;"+
            "encrypt=true;trustServerCertificate=true";

    public static Database getInstance() throws SQLException {
        if (databaseInstance == null) {
            System.out.println("Getting your instance");
            databaseInstance = new Database();
        }
        return databaseInstance;
    }

    public Connection connect() {
        try {
            System.out.println("Connecting");
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(CONN_STR, USERNAME,PASSWORD);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            connection = null;
        }
        if (connection == null) {
            System.out.println("Null connection");
            System.exit(1);
        }
        else System.out.println("Connected");
        return connection;
    }

}
