package dbUtil;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLDatabase {

    private String SQLCONN = "jdbc:sqlite:database.sqlite";
    private String WRKBK;
    private String workingDatabase;
    private Connection CONN;

    public SQLDatabase(String database) {
        workingDatabase = database;
        try {
            CONN = getConnection(CONN);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void difference(String updatedWorkbook) {
        CompareDatabase compare = new CompareDatabase(workingDatabase);
        compare.difference(updatedWorkbook);
    }

    public void xlsxToSQL(String workbook) {
        WRKBK = workbook;
        ParseExcel parseExcel = new ParseExcel();
        ArrayList <String> excelLotInfo;
        try {
            excelLotInfo = parseExcel.excelText(WRKBK);

            for (String dataPoint : excelLotInfo){
                dataPoint = dataPoint.replaceAll("Detail", "");
                String[] temp = dataPoint.trim().split("\\s+");
                Vehicle newVehicle = new Vehicle(temp[0],temp[1],temp[2],temp[3],temp[4],temp[5]);
                insertNewVehicle(newVehicle);
            }

            System.out.println("Datapoints added");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void insertNewVehicle(Vehicle vehicle) {
        String sql = "INSERT INTO " + workingDatabase+ " (vehicle_id, days, year, make, model, serial) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = CONN.prepareStatement(sql)) {
            ps.setString(1, vehicle.getVehicleID());
            ps.setString(2, vehicle.getDays());
            ps.setString(3, vehicle.getYear());
            ps.setString(4, vehicle.getMake());
            ps.setString(5, vehicle.getModel());
            ps.setString(6, vehicle.getSerial());

            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Basically a .contains for a List but since we cannot do that because
    // of the vehicle object we make our own search function
    public Boolean searchForVehicle(String searchItem, List<Vehicle> listOfVehicles){
        Boolean found = false;
        for (Vehicle vehicle: listOfVehicles) {
            if (vehicle.getVehicleID().equals(searchItem)){
                found =true;
            }
        }
        return found;
    }

    public Boolean search (String searchValue) {
        String sql = "SELECT * FROM " + workingDatabase + " WHERE (vehicle_id) = (?)";
        PreparedStatement ps = null;
        try {
            ps = CONN.prepareStatement(sql);
            ps.setString(1, searchValue);

            ResultSet resultSet = ps.executeQuery();

            //The following if statement checks if the ResultSet is empty
            //Be sure to close the CONNs after use
            if (!resultSet.next()){
                resultSet.close();
                ps.close();
                return false;
            }else{
                resultSet.close();
                ps.close();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void updateVehiclePosition(String vehicleID, String x_pos, String y_pos) {
        String sql = "UPDATE " + workingDatabase + " SET x_pos = (?), y_pos = (?) WHERE (vehicle_id) = (?)";

        updateVehicle(vehicleID, y_pos, x_pos, sql);
    }

    public void updateVehicleSize (String vehicleID, String width, String height) {
        String sql = "UPDATE " + workingDatabase + " SET width = (?), height = (?) WHERE (vehicle_id) = (?)";

        updateVehicle(vehicleID, width, height, sql);
    }

    private void updateVehicle(String vehicleID, String arg1, String arg2, String sql) {
        try (PreparedStatement ps = CONN.prepareStatement(sql)){

            ps.setString(1, arg2);
            ps.setString(2, arg1);
            ps.setString(3, vehicleID);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List getSingleVehicle(String vehicleID) {
        String sql = "SELECT * FROM " + workingDatabase + " WHERE (vehicle_id) = '" + vehicleID + "'";

        List<Vehicle> temp = new ArrayList<>();
        getVehicle(sql, temp);
        return temp;
    }

    public List getAllVehicles() {
        String sql = "SELECT * FROM " + workingDatabase;

        List<Vehicle> temp = new ArrayList<>();
        getVehicle(sql, temp);
        return temp;
    }

    private void getVehicle(String sql, List<Vehicle> temp) {
        try (PreparedStatement ps = CONN.prepareStatement(sql)) {

            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                temp.add(new Vehicle(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        resultSet.getString(6),
                        resultSet.getString(7),
                        resultSet.getString(8),
                        resultSet.getString(9),
                        resultSet.getString(10)));
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete (String removeValue) {
        String sql = "DELETE FROM " + workingDatabase + " WHERE (vehicle_id) = (?)";

        try (PreparedStatement ps = CONN.prepareStatement(sql)) {
            ps.setString(1, removeValue);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection(Connection connection) throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(SQLCONN);
            System.out.println("Database connected");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
