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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicInteger;

import static com.microsoft.sqlserver.jdbc.StringUtils.isNumeric;

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

    void resetTextFields(TextField... textFields) {
        for (TextField textField : textFields) {
            if (textField == null) continue;
            textField.clear();
        }
    }

    void resetLabel(Label l) {
        l.setText("");
    }

    //Handling Completed
    @FXML
    void AddCarBtn() {
        int errorCode;
        if (isNumeric(txtYear.getText()) && isNumeric(txtPricePerDay.getText()) && isNumeric(txtPenaltyPerDay.getText()) && isNumeric(txtLocationId.getText()) && isNumeric(txtInsuranceCost.getText()) && !txtColor.getText().isEmpty() && !txtModel.getText().isEmpty() && !txtType.getText().isEmpty()) {
            try {
                errorCode = adminModel.addCar(txtColor.getText(), txtYear.getText(), txtModel.getText(), txtType.getText(), txtPricePerDay.getText(), txtPenaltyPerDay.getText(), txtLocationId.getText(), txtInsuranceCost.getText());
                resetTextFields(txtColor, txtYear, txtModel, txtType, txtPricePerDay, txtPenaltyPerDay, txtLocationId, txtInsuranceCost);
                if (errorCode == 1) {
                    labelINfo.setText("The car was added successfully!");
                    labelINfo.setTextFill(Color.BLUE);
                }
                else if (errorCode == -1) {
                    labelINfo.setTextFill(Color.RED);
                    labelINfo.setText("Invalid Branch");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            labelINfo.setText("Missing or Invalid Input");
            labelINfo.setTextFill(Color.RED);
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

    //Handling Completed
    @FXML
    void ViewCarById() {
        int errorCode;
        String query1, query2;
        resetLabel(labelInfoRemove);
        PreparedStatement ps;
        CallableStatement cs;
        query1 = "exec spCheckCarIdAvailability ?,?";
        if (isNumeric(txtCarId1.getText())) {
            try {
                cs = adminModel.con.prepareCall(query1);
                cs.setString(1, txtCarId1.getText());
                cs.registerOutParameter(2, Types.INTEGER);
                cs.execute();
                errorCode = cs.getInt(2);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (errorCode == 1) {
                query2 = "select * from fnViewCarById(?) ";
                try {
                    ps = adminModel.con.prepareStatement(query2);
                    ps.setString(1, txtCarId1.getText());
                    ResultSet rs = ps.executeQuery();
                    drawTable(rs, tableAdmin1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (errorCode == -1) {
                labelInfoRemove.setText("Invalid ID");
            }
        }
    }

    //Handling Completed
    @FXML
    void ViewGreaterThanORLessThan() {
        PreparedStatement ps;
        if (txtLessThan.getText().isEmpty() && isNumeric(txtGreaterThan.getText())) {
            try {
                String query = "select * from fnGetGreaterThan(?) ";
                ps = adminModel.con.prepareStatement(query);
                ps.setString(1, txtGreaterThan.getText());
                ResultSet rs = ps.executeQuery();
                drawTable(rs, tableAdmin1);
                resetTextFields(txtGreaterThan);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (txtGreaterThan.getText().isEmpty() && isNumeric(txtLessThan.getText())) {
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

    //Handling Completed
    @FXML
    void RemoveCarById() {
        resetLabel(labelInfoRemove);
        int errorCode;
        CallableStatement cs;
        String query = "exec dbo.spRemoveCarById ?,?";
        if (isNumeric(txtCarId1.getText())) {
            try {
                cs = adminModel.con.prepareCall(query);
                cs.setString(1, txtCarId1.getText());
                cs.registerOutParameter(2, Types.INTEGER);
                cs.execute();
                errorCode = cs.getInt(2);
                if (errorCode == 1) {
                    labelInfoRemove.setText("The Car is Deleted");
                } else if (errorCode == -1) {
                    labelInfoRemove.setText("Invalid Car ID");
                }
                labelInfoRemove.setTextFill(Color.BLUE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //Handling Completed
    @FXML
    void UpdateInsurance() {
        int errorCode;
        CallableStatement cs;
        String query = "exec dbo.spUpdateInsurance ?,?";
        if (isNumeric(txtCarId2.getText())) {
            try {
                cs = adminModel.con.prepareCall(query);
                cs.setString(1, txtCarId2.getText());
                cs.registerOutParameter(2, Types.INTEGER);
                cs.execute();
                errorCode = cs.getInt(2);
                if (errorCode == 0) {
                    labelUpdateInsurance.setText("Insurance not Expired");
                } else if (errorCode == -1) {
                    labelUpdateInsurance.setText("Invalid ID");
                } else if (errorCode == 1) {
                    labelUpdateInsurance.setText("Updated Insurance");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //Handling Completed
    @FXML
    void ConfirmArrival() {
        int errorCode;  //OUTPUT
        float penalty; //OUTPUT
        CallableStatement cs;
        String query = "exec dbo.spConfirmArrival ?,?,?";
        if (isNumeric(txtRentId.getText())) {
            try {
                cs = adminModel.con.prepareCall(query);
                cs.setString(1, txtRentId.getText());
                cs.registerOutParameter(2, Types.FLOAT);
                cs.registerOutParameter(3, Types.INTEGER);
                cs.execute();
                penalty = cs.getFloat(2);
                errorCode = cs.getInt(3);
                if (errorCode == 0) {
                    labelInfo.setText("Car Not Rented");
                } else if (errorCode == 1) {
                    labelInfo.setText("Penalty =  " + penalty);
                } else if (errorCode == -1) {
                    labelInfo.setText("Invalid Rent ID");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //Handling Completed
    @FXML
    void ArrivalDate() {
        PreparedStatement ps;
        String query = "select * from fnViewArrivalInThisDate(?) ";
        if (isValidDate(txtArrivalDate.getText())) {
            try {
                ps = adminModel.con.prepareStatement(query);
                ps.setString(1, txtArrivalDate.getText());
                ResultSet rs = ps.executeQuery();
                drawTable(rs, tableAdmin1);
                resetTextFields(txtArrivalDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //Handling Completed
    @FXML
    void ViewBetweenTwoDates() {
        PreparedStatement ps;
        String query = "select * from fnViewBetweenTwoDates(?,?) ";
        if (isValidDate(txtDate1.getText()) && isValidDate(txtDate2.getText())) {
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
    }

    @SuppressWarnings("all")
    void drawTable(ResultSet rs, TableView tableView) {
        tableView.getItems().clear();
        tableView.getColumns().clear();
        ObservableList<ObservableList> data = FXCollections.observableArrayList();
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

    boolean isValidDate(String dateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            dateFormat.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}