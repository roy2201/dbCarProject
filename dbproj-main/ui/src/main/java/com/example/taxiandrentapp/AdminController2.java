package com.example.taxiandrentapp;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.*;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("rawtypes")
public class AdminController2 {

    Database db;
    Connection con;

    @FXML
    private Label labelInfo;
    @FXML
    private TableView<?> tableInfo;
    @FXML
    private TextField txtCardNumber;
    @FXML
    private TextField txtPercentage;
    @FXML
    private TextField txtRentInfoId;

    public AdminController2() {
        try {
            db = Database.getInstance();
            con = db.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void securityPage(ActionEvent event) {
        try {
            ((Node) event.getSource()).getScene().getWindow().hide();
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("admin-view3.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Admin View 3");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void firstAdminPage(ActionEvent event) {
        try {
            ((Node) event.getSource()).getScene().getWindow().hide();
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("admin-view1.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("admin view 1");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void resetLabel(Label l) {
        l.setText("");
    }

    @FXML
    public void ViewAccountClicked() {
        resetLabel(labelInfo);
        if (txtCardNumber.getText().isEmpty()) {
            labelInfo.setText("Please input card number");
        } else {
            labelInfo.setText("");
            String msg = "";
            int errorCode;
            String query = "exec spCheckCardIdAvailability ?,?";
            try {
                CallableStatement cs = con.prepareCall(query);
                cs.setString(1, txtCardNumber.getText());
                cs.registerOutParameter(2, Types.INTEGER);
                cs.execute();
                errorCode = cs.getInt(2);
                if (errorCode == 1) {
                    msg = "Card number found";
                    String query1 = "select * from fnViewCardById (?)";
                    cs = con.prepareCall(query1);
                    cs.setString(1, txtCardNumber.getText());
                    ResultSet rs = cs.executeQuery();
                    drawTable(rs, tableInfo);
                } else if (errorCode == -1) {
                    msg = "Enter valid card number";
                }
                labelInfo.setText(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void ViewPendingRequest() {
        resetLabel(labelInfo);
        int errorCode;
        String msg = "";
        CallableStatement cs;
        String query = "exec spCheckPendingRequestsAvailability ?";
        try {
            cs = con.prepareCall(query);
            cs.registerOutParameter(1, Types.INTEGER);
            cs.execute();
            errorCode = cs.getInt(1);
            if (errorCode == 1) {
                msg = "There is requests not checked yet";
                String query1 = "select * from vwPendingRequests";
                cs = con.prepareCall(query1);
                ResultSet rs = cs.executeQuery();
                drawTable(rs,tableInfo);
            } else if (errorCode == -1) {
                msg = "No results found";
            }
            labelInfo.setText(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void RefundClicked() {
        resetLabel(labelInfo);
        if (txtRentInfoId.getText().isEmpty() || txtPercentage.getText().isEmpty()) {
            labelInfo.setText("Missing info");
        } else if (Integer.parseInt(txtPercentage.getText()) > 100 || Integer.parseInt(txtPercentage.getText()) < 0)
            labelInfo.setText("Invalid percentage");
        else {
            String query = "exec spCheckRentInfoIdAvailability ?,?";
            try {
                CallableStatement cs;
                cs = con.prepareCall(query);
                cs.setString(1, txtRentInfoId.getText());
                cs.registerOutParameter(2, Types.INTEGER);
                //System.out.println("start1 exec");
                cs.execute();
                //System.out.println("finish1 exec");
                AtomicInteger errorCode = new AtomicInteger(cs.getInt(2));
                if (errorCode.get() == 1) {
                    labelInfo.setText("Processing..");
                    String query1 = "exec spRefund ?,?,?";
                    cs = con.prepareCall(query1);
                    cs.setString(1, txtRentInfoId.getText());
                    cs.setString(2, txtPercentage.getText());
                    cs.registerOutParameter(3, Types.INTEGER);
                    cs.execute();
                    errorCode.set(cs.getInt(3));
                    if (errorCode.get() == -1){
                       labelInfo.setText("No enough balance to make this transaction");
                    } else if (errorCode.get() == 1) {
                        labelInfo.setText("Transaction done");
                    }
                } else if (errorCode.get() == -1){
                    labelInfo.setText("Enter a valid rent info id");
                } else if (errorCode.get() == 0) {
                    labelInfo.setText("This request is already checked");
                } else if (errorCode.get() == -5) {
                    labelInfo.setText("invalid refund percentage");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("all")
    void drawTable(ResultSet rs, TableView tableView) {
        tableView.getColumns().clear();
        ObservableList<ObservableList> data = FXCollections.observableArrayList();
        try {
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j).toString()));
                tableView.getColumns().addAll(col);
                System.out.println("Column [" + i + "] ");
            }
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    row.add(rs.getString(i));
                }
                System.out.println("Row [1] added " + row);
                data.add(row);
                tableView.setItems(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //end
}
