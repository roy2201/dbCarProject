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

import static com.microsoft.sqlserver.jdbc.StringUtils.isNumeric;

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
        int errorCode;
        resetLabel(labelInfo);
        if (txtCardNumber.getText().isEmpty() || !isNumeric(txtCardNumber.getText())) {
            labelInfo.setText("Empty or Invalid Input");
        } else {
            resetLabel(labelInfo);
            String query = "exec spCheckCardIdAvailability ?,?";
            try (CallableStatement cs = con.prepareCall(query)) {
                cs.setString(1, txtCardNumber.getText());
                cs.registerOutParameter(2, Types.INTEGER);
                cs.execute();
                errorCode = cs.getInt(2);
                if (errorCode == 1) {
                    labelInfo.setText("Card Found");
                    String query1 = "select * from fnViewCardById (?)";
                    CallableStatement cs2 = con.prepareCall(query1);
                    cs2.setString(1, txtCardNumber.getText());
                    ResultSet rs = cs2.executeQuery();
                    drawTable(rs, tableInfo);
                } else if (errorCode == -1) {
                    labelInfo.setText("Invalid Card Number");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void ViewPendingRequest() {
        int errorCode;
        String query = "exec spCheckPendingRequestsAvailability ?";
        resetLabel(labelInfo);
        try (CallableStatement cs = con.prepareCall(query)){
            cs.registerOutParameter(1, Types.INTEGER);
            cs.execute();
            errorCode = cs.getInt(1);
            if (errorCode == 1) {
                labelInfo.setText("Some Requests Still Unchecked");
                String query1 = "select * from vwPendingRequests";
                CallableStatement cs2 = con.prepareCall(query1);
                ResultSet rs = cs2.executeQuery();
                drawTable(rs,tableInfo);
            } else if (errorCode == -1) {
                labelInfo.setText("All Requests Checked");
            }
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
            if(isNumeric(txtRentInfoId.getText()) && isNumeric(txtPercentage.getText())) {
                try ( CallableStatement cs = con.prepareCall(query)) {
                    cs.setString(1, txtRentInfoId.getText());
                    cs.registerOutParameter(2, Types.INTEGER);
                    cs.execute();
                    AtomicInteger errorCode = new AtomicInteger(cs.getInt(2));
                    if (errorCode.get() == 1) {
                        labelInfo.setText("Processing..");
                        String query1 = "exec spRefund ?,?,?";
                        CallableStatement cs2 = con.prepareCall(query1);
                        cs2.setString(1, txtRentInfoId.getText());
                        cs2.setString(2, txtPercentage.getText());
                        cs2.registerOutParameter(3, Types.INTEGER);
                        cs2.execute();
                        errorCode.set(cs2.getInt(3));
                        if (errorCode.get() == -1) {
                            labelInfo.setText("No enough balance to make this transaction");
                        } else if (errorCode.get() == 1) {
                            labelInfo.setText("Transaction done");
                        }
                    } else if (errorCode.get() == -1) {
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
    }

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
