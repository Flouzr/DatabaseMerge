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

    // Take a spreadsheet and puts transforms it into a format for the database
    public void xlsxToSQL(String workbook) {
        WRKBK = workbook;
        ParseExcel parseExcel = new ParseExcel();
        ArrayList <String> excelLotInfo;
        try {
            excelLotInfo = parseExcel.excelText(WRKBK);

            // Take the array excel info and put it into Vehicle format
            for (String dataPoint : excelLotInfo){
                dataPoint = dataPoint.replaceAll("Detail", "");
                String[] temp = dataPoint.trim().split("\\s+");
                Vehicle newVehicle = new Vehicle(temp[0],temp[1],temp[2],temp[3],temp[4],temp[5]);
                insertNewVehicle(newVehicle);
            }

            System.out.println("Datapoints added to " + workingDatabase);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Appends a new vehicle to the workingDatabase
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

    // Checks to see if there is a vehicle with a unique stock number in the database
    public Boolean search (String vehicleID) {
        String sql = "SELECT * FROM " + workingDatabase + " WHERE (vehicle_id) = (?)";
        PreparedStatement ps = null;
        try {
            ps = CONN.prepareStatement(sql);
            ps.setString(1, vehicleID);

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

    public Boolean deleteAllEntries(){
        try (PreparedStatement ps = CONN.prepareStatement("DELETE FROM " + workingDatabase )){
            ps.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Returns a single vehicles in the database in Vehicle class format
    public List getSingleVehicle(String vehicleID) {
        String sql = "SELECT * FROM " + workingDatabase + " WHERE (vehicle_id) = '" + vehicleID + "'";

        List<Vehicle> temp = new ArrayList<>();
        getVehicle(sql, temp);
        return temp;
    }

    // Returns all of the vehicles in the database in Vehicle class format
    public List getAllVehicles() {
        String sql = "SELECT * FROM " + workingDatabase;

        List<Vehicle> temp = new ArrayList<>();
        getVehicle(sql, temp);
        return temp;
    }

    // Parent for getAllVehicles and getSingleVehicle
    private List getVehicle(String sql, List<Vehicle> temp) {
        try (PreparedStatement ps = CONN.prepareStatement(sql)) {

            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                temp.add(new Vehicle(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        resultSet.getString(6)));
            }
            resultSet.close();
            return temp;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return temp;
    }

    public boolean checkIfEmpty(){
        String sql = "SELECT count(*) FROM " + workingDatabase + " limit 1";

        try (PreparedStatement ps = CONN.prepareStatement(sql)) {

            ResultSet resultSet = ps.executeQuery();

            if (resultSet.getString(1).equals("0")){
                return true;
            } else {
                resultSet.close();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete an entry in the database based on vehicle ID
    public void delete (String removeValue) {
        String sql = "DELETE FROM " + workingDatabase + " WHERE (vehicle_id) = (?)";

        try (PreparedStatement ps = CONN.prepareStatement(sql)) {
            ps.setString(1, removeValue);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get a connection to the database
    private Connection getConnection(Connection connection) throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(SQLCONN);
            System.out.println("Database connected to " + workingDatabase);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
