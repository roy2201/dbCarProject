module com.example.taxiandrentapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.microsoft.sqlserver.jdbc;
    requires javafx.graphics;

    opens com.example.taxiandrentapp to javafx.fxml;
    exports com.example.taxiandrentapp;
}