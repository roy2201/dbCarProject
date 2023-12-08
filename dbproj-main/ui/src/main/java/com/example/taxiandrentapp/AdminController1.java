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
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("ALL")
public class AdminController1 {

    @FXML
    private TextField txtColor;
    @FXML
    private TextField txtYear;
    @FXML
    private TextField txtModel;
    @FXML
    private TextField txtType;
    @FXML
    private TextField txtPricePerDay;
    @FXML
    private TextField txtPenaltyPerDay;
    @FXML
    private TextField txtLocationId;
    @FXML
    private TextField txtInsuranceCost;
    @FXML
    private Label labelInfo;
    @FXML
    private TableView<?> tableAdmin1;
    @FXML
    private TextField txtCarId2;
    @FXML
    private Label labelUpdateInsurance;
    @FXML
    private TextField txtDate1;
    @FXML
    private TextField txtDate2;
    @FXML
    private TextField txtGreaterThan;
    @FXML
    private TextField txtLessThan;
    @FXML
    private TextField txtCarId1;
    @FXML
    private TextField txtArrivalDate;
    @FXML
    private Label labelInfoRemove;
    @FXML
    private TextField txtRentId;
    @FXML
    private Label labelINfo;
    AdminModel adminModel = new AdminModel();

    //start of navigation functions
    @FXML
    void nextPage(ActionEvent event) {

        try {
            ((Node) event.getSource()).getScene().getWindow().hide();
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("admin-view2.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Admin View 2");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void logOutPage(ActionEvent event) {
        try {
            ((Node) event.getSource()).getScene().getWindow().hide();
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Sign in");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //end of navigation methods
    void resetTextFields(TextField... textFields) {
        //System.out.println("Start reset function");
        for (TextField textField : textFields) {
            if(textField == null) continue;
            textField.clear();
            //textField.setText("");
        }
        //System.out.println("End reset function");
    }
    void resetTextField(TextField t) {
        t.setText("");
    }
    void resetLabel(Label l) {
        l.setText("");
    }
    boolean anyEmpty() {
        return (txtColor.getText().isEmpty() || txtYear.getText().isEmpty() || txtModel.getText().isEmpty() || txtType.getText().isEmpty() || txtPricePerDay.getText().isEmpty() || txtPenaltyPerDay.getText().isEmpty() || txtLocationId.getText().isEmpty() || txtInsuranceCost.getText().isEmpty());
    }

    @FXML
    void AddCarBtn() {
        if(anyEmpty()) {
            labelInfo.setText("Missing Information");
            labelInfo.setTextFill(Color.RED);
            resetTextFields(txtColor, txtYear, txtModel, txtType, txtPricePerDay, txtPenaltyPerDay, txtLocationId, txtInsuranceCost);
        } else {
            try {
                int errorCode = adminModel.addCar(txtColor.getText(), txtYear.getText(), txtModel.getText(), txtType.getText(), txtPricePerDay.getText(), txtPenaltyPerDay.getText(), txtLocationId.getText(), txtInsuranceCost.getText());
                resetTextFields(txtColor, txtYear, txtModel, txtType, txtPricePerDay, txtPenaltyPerDay, txtLocationId, txtInsuranceCost);
                if(errorCode == 1) {
                    labelInfo.setText("The car was added successfully!");
                    labelInfo.setTextFill(Color.BLUE);
                } else if (errorCode == -1) {
                    labelInfo.setTextFill(Color.RED);
                    labelInfo.setText("Invalid Branch");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    void ViewAllCustomers() {
        String query = "select * from  vwAllCustomers";
        try {
            ResultSet rs = adminModel.con.createStatement().executeQuery(query);
            drawTable(rs, tableAdmin1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void ViewAllCars() {
        String query = "select * from  vwCarsAndBranch";
        try {
            ResultSet rs = adminModel.con.createStatement().executeQuery(query);
            drawTable(rs, tableAdmin1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void viewAllRents() {
        String query = "select * from vwCustRentInfo";
        try {
            ResultSet rs = adminModel.con.createStatement().executeQuery(query);
            drawTable(rs, tableAdmin1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void ViewExpired() {
        String query = "select * from  vwExpiredInsurance";
        try {
            ResultSet rs = adminModel.con.createStatement().executeQuery(query);
            drawTable(rs, tableAdmin1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void ViewRented() {
        String query = "select * FROM vwRentedCars";
        try {
            ResultSet rs = adminModel.con.createStatement().executeQuery(query);
            drawTable(rs, tableAdmin1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void ViewInsurance() {
        String query = "select * from vwInsuranceHistory";
        try {
            ResultSet rs = adminModel.con.createStatement().executeQuery(query);
            drawTable(rs, tableAdmin1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void ViewBetweenTwoDates() {
        PreparedStatement ps;
        String query = "select * from fnViewBetweenTwoDates(?,?) ";
        try {
            ps = adminModel.con.prepareStatement(query);
            ps.setString(1, txtDate1.getText());
            ps.setString(2, txtDate2.getText());
            ResultSet rs = ps.executeQuery();
            drawTable(rs, tableAdmin1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void ViewGreaterThanORLessThan() {
        PreparedStatement ps;
        if (!(txtGreaterThan.getText().isEmpty()) && txtLessThan.getText().isEmpty()) {
            String query = "select * from fnGetGreaterThan(?) ";
            try {
                ps = adminModel.con.prepareStatement(query);
                ps.setString(1, txtGreaterThan.getText());
                ResultSet rs = ps.executeQuery();
                drawTable(rs, tableAdmin1);
                resetTextFields(txtGreaterThan);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ((txtGreaterThan.getText().isEmpty()) && !(txtLessThan.getText().isEmpty())) {
            String query = "select * from fnGetLessThan(?) ";
            try {
                ps = adminModel.con.prepareStatement(query);
                ps.setString(1, txtLessThan.getText());
                ResultSet rs = ps.executeQuery();
                drawTable(rs, tableAdmin1);
                resetTextFields(txtLessThan);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void ViewCarById() throws SQLException {
        resetLabel(labelInfoRemove);
        PreparedStatement ps;
        CallableStatement cs;
        String query1 = "exec spCheckCarIdAvailability ?,?";
        cs = adminModel.con.prepareCall(query1);
        cs.setString(1, txtCarId1.getText());
        cs.registerOutParameter(2, Types.INTEGER);
        cs.execute();
        int errorCode = cs.getInt(2);
        if (errorCode == 1) {
            String query = "select * from fnViewCarById(?) ";
            try {
                ps = adminModel.con.prepareStatement(query);
                ps.setString(1, txtCarId1.getText());
                ResultSet rs = ps.executeQuery();
                drawTable(rs, tableAdmin1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (errorCode == -1) {
            labelInfoRemove.setText("Enter valid car ID");
        }
    }

    @FXML
    void RemoveCarById() {
        labelInfoRemove.setText("");
        int errorCode;
        String errorMessage = "";
        CallableStatement cs;
        String query = "exec dbo.spRemoveCarById ?,?";
        try {
            cs = adminModel.con.prepareCall(query);
            cs.setString(1, txtCarId1.getText());
            cs.registerOutParameter(2, Types.INTEGER);
            cs.execute();
            errorCode = cs.getInt(2);
            if (errorCode == 1) {
                errorMessage = "The car is deleted";
            } else {
                if (errorCode == -1) {
                    errorMessage = "Invalid car ID";
                }
            }
            labelInfoRemove.setText(errorMessage);
            labelInfoRemove.setTextFill(Color.BLUE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*insurance related*/
    @FXML
    void UpdateInsurance() {
        int errorCode;
        CallableStatement cs;
        String query = "exec dbo.spUpdateInsurance ?,?";
        try {
            cs = adminModel.con.prepareCall(query);
            cs.setString(1, txtCarId2.getText());
            cs.registerOutParameter(2, Types.INTEGER);
            cs.execute();
            errorCode = cs.getInt(2);
            var msg = new AtomicReference<>("");
            if (errorCode == 0) {
                msg.set("The insurance is not expired");
            } else if (errorCode == -1) {
                msg.set("Invalid car ID");
            } else if (errorCode == 1) {
                msg.set("The insurance is updated");
            }
            labelUpdateInsurance.setText(msg.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void ConfirmArrival() {
        //penalty why ?
        resetLabel(labelINfo);
        float penalty; //OUTPUT
        int errorCode;  //OUTPUT
        var msg = new AtomicReference<>("");
        CallableStatement cs;
        String query = "exec dbo.spConfirmArrival ?,?,?";
        try {
            cs = adminModel.con.prepareCall(query);
            cs.setString(1, txtRentId.getText());
            cs.registerOutParameter(2, Types.FLOAT);
            cs.registerOutParameter(3, Types.INTEGER);
            cs.execute();
            penalty = cs.getFloat(2);
            errorCode = cs.getInt(3);
            if (errorCode == 0) {
                msg.set("Car not rented");
                labelINfo.setText(msg.get());
            } else {
                if (errorCode == 1) {
                    msg.set("Penalty : "+ penalty);
                } else if (errorCode == -1) {
                    msg.set("Invalid Rent ID");
                }
                labelINfo.setText(msg.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void ArrivalDate() {
        PreparedStatement ps;
        String query = "select * from fnViewArrivalInThisDate(?) ";
        try {
            ps = adminModel.con.prepareStatement(query);
            ps.setString(1, txtArrivalDate.getText());
            ResultSet rs = ps.executeQuery();
            drawTable(rs, tableAdmin1);
            resetTextField(txtArrivalDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("all")
    void drawTable(ResultSet rs, TableView tableView) {
        tableView.getItems().clear();
        tableView.getColumns().clear();
        ObservableList<ObservableList> data = FXCollections.observableArrayList();;
        try {
            for (AtomicInteger i = new AtomicInteger(); i.get() < rs.getMetaData().getColumnCount(); i.getAndIncrement()) {
                final int j;
                j = i.get();
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i.get() + 1));
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

}
