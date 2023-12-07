package com.example.taxiandrentapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SignupController implements Initializable {

    LoginModel loginModel = new LoginModel();
    @FXML
    private TextField txtFirstName;
    @FXML
    private TextField txtLastName;
    @FXML
    private TextField txtAge;
    @FXML
    private TextField txtAddress;
    @FXML
    private TextField txtEmail;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Label info;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @FXML
    void ConfirmSignup (){
        try{
            if(loginModel.validSignUp(txtEmail.getText())) {
                loginModel.addCustomer(txtFirstName.getText(), txtLastName.getText(), txtAge.getText(), txtAddress.getText(), txtEmail.getText(), txtPassword.getText());
                info.setText("You are signed up !");
            } else {
                info.setText("email duplication");
                info.setTextFill(Color.RED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    void signInPage(ActionEvent event) {
        try{
            ((Node)event.getSource()).getScene().getWindow().hide();
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Sign In");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
