package com.example.taxiandrentapp;

import java.sql.*;

public class LoginModel {

    Database db;
    Connection con;

    private static int cid;

    public LoginModel() {

        try {
            db = Database.getInstance();
            con = db.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getLogged() {
        return cid;
    }

    public static void setLogged(int c) {
        cid = c;
    }

    boolean validLogin(String user, String pass) throws SQLException {
        String query = "exec spIsValidLogin ?, ?, ?";
        boolean isValid;
        CallableStatement cst = con.prepareCall(query);
        cst.setString(1, user);
        cst.setString(2, pass);
        cst.registerOutParameter(3, Types.BOOLEAN);
        cst.execute();
        isValid = cst.getBoolean(3);
        return isValid;
    }

    void addCustomer(String fName, String lName, String age, String address, String email, String password) throws Exception {
        String query = "exec spAddCustomer ?,?,?,?,?,?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, fName);
        ps.setString(2, lName);
        ps.setString(3, age);
        ps.setString(4, address);
        ps.setString(5, email);
        ps.setString(6, password);
        if(ps.execute()) throw new Exception();
    }

    boolean validSignUp(String email) {
        //true means valid , i.e: not duplicated email
        String query = "exec spValidSignUp ?, ?";
        try {
            CallableStatement cst = con.prepareCall(query);
            cst.setString(1, email);
            cst.registerOutParameter(2, Types.BOOLEAN);
            cst.execute();
            return cst.getBoolean(2);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

}