package com.example.taxiandrentapp;

import com.example.taxiandrentapp.Database;

import java.sql.*;

public class AdminModel {

    public Database db;
    public Connection con;

    public AdminModel() {

        try {
            db = Database.getInstance();
            con = db.connect();
        } catch (SQLException e) {
            e.printStackTrace();
     }
    }

    public int addCar(String color, String year, String model, String type, String price, String penalty, String location, String cost) throws Exception {
        CallableStatement cst;
        String query = "exec spAddCar ?,?,?,?,?,?,?,?,?";
        cst = con.prepareCall(query);
        cst.setString(1,color);
        cst.setString(2,year);
        cst.setString(3,model);
        cst.setString(4,type);
        cst.setFloat(5, Float.parseFloat(price));
        cst.setFloat(6, Float.parseFloat(penalty));
        cst.setInt(7, Integer.parseInt(location));
        cst.setInt(8, Integer.parseInt(cost));
        cst.registerOutParameter(9,Types.INTEGER);
        if(cst.execute()) throw new Exception();
        return cst.getInt(9);
    }

}
