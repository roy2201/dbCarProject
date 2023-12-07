package com.example.taxiandrentapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AdminSecurity implements Initializable {

    Connection con;

    @FXML
    private ComboBox<String> databasesComboBox;
    @FXML
    private ComboBox<String> tablesComboBox;
    @FXML
    private ComboBox<String> userRolesComboBox;
    @FXML
    private TextField txtLogName;
    @FXML
    private TextField txtLogPass;
    @FXML
    private TextField txtRoleName;
    @FXML
    private TextField txtUserNameForRole;
    @FXML
    private TextField nameToGrant;
    @FXML
    private CheckBox insertBox;
    @FXML
    private CheckBox updateBox;
    @FXML
    private CheckBox deleteBox;
    @FXML
    private CheckBox dbCreatorBox;
    @FXML
    private CheckBox bulkAdminBox;
    @FXML
    private CheckBox diskAdminBox;
    @FXML
    private CheckBox processAdminBox;
    @FXML
    private CheckBox securityAdminBox;
    @FXML
    private CheckBox serverAdminBox;
    @FXML
    private CheckBox setUpAdminBox;
    @FXML
    private CheckBox sysAdminBox;
    @FXML
    private CheckBox publicBox;

    public AdminSecurity() {
        Database db = null;
        try {
            db = Database.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        assert null != db;
        con = db.connect();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadDatabases();
        loadRoles();
        publicBox.setDisable(true);
    }

    public void loadDatabases() {
        String query = "exec spLoadDBNames";
        ArrayList<String> databases = new ArrayList<>();
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                databases.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        databasesComboBox.getItems().addAll(databases);
    }

    @FXML
    void loadTables() {
        if(databasesComboBox.getSelectionModel().isEmpty()) return ;
        ArrayList<String> tables = new ArrayList<>();
        String dbName = databasesComboBox.getValue();
        String query = "exec spGetTableNames ?";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, dbName);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                tables.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tablesComboBox.getItems().clear();
        tablesComboBox.getItems().addAll(tables);
    }

    void loadRoles() {
        ArrayList<String> roles = new ArrayList<>();
        String query = "exec spLoadRoles";
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                roles.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        userRolesComboBox.getItems().addAll(roles);
    }

    @FXML
    void secondAdminPage(ActionEvent event) {
        try {
            ((Node) event.getSource()).getScene().getWindow().hide();
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("admin-view2.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("admin view 2");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void addLogin() {
        String query = "exec spCreateLogin ?,?,?,?";
        CallableStatement cst;
        try {
            cst = con.prepareCall(query);
            cst.setString(1, txtLogName.getText());
            cst.setString(2, txtLogPass.getText());
            cst.setString(3, databasesComboBox.getValue());
            cst.registerOutParameter(4, Types.VARCHAR);
            cst.execute();
            System.out.println(cst.getString(4));
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void addRole() {
        String query = "exec spAddRole ?";
        CallableStatement cst;
        try {
            cst = con.prepareCall(query);
            cst.setString(1, txtRoleName.getText());
            cst.execute();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void giveRole() {
        String query = "exec spGiveRole ?,?,?,?";
        CallableStatement cst;
        try {
            cst = con.prepareCall(query);
            cst.setString(1, txtUserNameForRole.getText());
            cst.setString(2, userRolesComboBox.getValue());
            cst.setString(3, String.valueOf('U'));
            cst.registerOutParameter(4, Types.VARCHAR);
            cst.execute();
            //for debugging
            System.out.println(cst.getString(4));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void controlPermission(int p) {
        String query = "spGrantTables ?,?,?,?,?,?,?";
        String tableName = tablesComboBox.getValue();
        String name = nameToGrant.getText();
        try {
            CallableStatement cst = con.prepareCall(query);
            cst.setString(1, name);
            cst.setString(2, tableName);
            cst.setInt(
                    3, (insertBox.isSelected())? 1:0
            );
            cst.setInt(
                    4, (updateBox.isSelected())? 1:0
            );
            cst.setInt(
                    5, (deleteBox.isSelected())? 1:0
            );
            cst.registerOutParameter(6, Types.VARCHAR);
            cst.setInt(
                    7, (p == 1)? 1:0
            );
            cst.execute();
            //for debugging
            System.out.println(cst.getString(6));
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void grantPermissions() {
        controlPermission(1);
    }

    @FXML
    void revokePermissions() {
        controlPermission(0);
    }

    void giveServerRoleHelper(String roleName) {
        String query = "exec spGiveRole ?,?,?,?";
        CallableStatement cst;
        try {
            cst = con.prepareCall(query);
            cst.setString(1, txtUserNameForRole.getText());
            cst.setString(2, roleName);
            cst.setString(3, String.valueOf('S'));
            cst.registerOutParameter(4, Types.VARCHAR);
            cst.execute();
            System.out.println(cst.getString(4));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void giveServerRole() {
        if(bulkAdminBox.isSelected()){
            giveServerRoleHelper("bulkAdmin");
        }
        if(dbCreatorBox.isSelected()){
            giveServerRoleHelper("dbCreator");
        }
        if(diskAdminBox.isSelected()){
            giveServerRoleHelper("diskAdmin");
        }
        if(processAdminBox.isSelected()){
            giveServerRoleHelper("processAdmin");
        }
        if(securityAdminBox.isSelected()){
            giveServerRoleHelper("securityAdmin");
        }
        if(serverAdminBox.isSelected()){
            giveServerRoleHelper("serverAdmin");
        }
        if(setUpAdminBox.isSelected()){
            giveServerRoleHelper("setUpAdmin");
        }
        if(sysAdminBox.isSelected()){
            giveServerRoleHelper("sysAdmin");
        }
    }
}

