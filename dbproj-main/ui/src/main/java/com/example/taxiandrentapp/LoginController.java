package com.example.taxiandrentapp;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private final LoginModel loginModel = new LoginModel();
    @FXML
    private Label connectionLabel;
    @FXML
    private TextField txtUser;
    @FXML
    private PasswordField txtPass;

    Connection con;
    Database db;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void addLogin(String email) {
        try {
            String query = "EXEC spLogIn ?, ?";
            CallableStatement cs = con.prepareCall(query);
            cs.setString(1, email);
            cs.registerOutParameter(2, Types.INTEGER);
            cs.execute();
            LoginModel.setLogged(cs.getInt(2));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void Login(ActionEvent event){
        if(!txtPass.getText().isEmpty() && !txtUser.getText().isEmpty()) {
            try {
                if (loginModel.validLogin(txtUser.getText(), txtPass.getText())) {
                    try {
                        db = Database.getInstance();
                        con = db.connect();
                        addLogin(txtUser.getText());
                        ((Node) event.getSource()).getScene().getWindow().hide();
                        Stage stage = new Stage();
                        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("customer-view.fxml"));
                        Scene scene = new Scene(fxmlLoader.load());
                        stage.setTitle("Customer Mode");
                        stage.setScene(scene);
                        stage.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else
                    connectionLabel.setText("Invalid information");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            connectionLabel.setText("Invalid Input");
        }
    }

    @FXML
    public void SignUp(ActionEvent event) {
        try{
            ((Node)event.getSource()).getScene().getWindow().hide();
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("signup-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Sign Up");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void adminMode(ActionEvent event) {
        try{
            ((Node)event.getSource()).getScene().getWindow().hide();
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("admin-view1.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Admin Mode");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}
