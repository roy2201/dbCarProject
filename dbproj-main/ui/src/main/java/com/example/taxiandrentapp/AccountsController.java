package com.example.taxiandrentapp;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class AccountsController {

    @FXML
    private TextField cardNumberTextField;

    @FXML
    private TextField cvvTextField;

    @FXML
    private TextField expiryDateTextField;

    @FXML
    private TextField nameTextField;
    
    @FXML
    private void handleWindowClose(Event event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("customer-view.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    void handleAddCard(ActionEvent event) {

    }
}
