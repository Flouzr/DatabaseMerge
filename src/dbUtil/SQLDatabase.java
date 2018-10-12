package dbUtil;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class SQLDatabase {

    private String SQLCONN = "jdbc:sqlite:database.sqlite";
    private String WRKBK = "CDJR NEW 8.31.18.xlsx";
    private Connection CONN = null;

    public SQLDatabase(){
        try {
            CONN = getConnection(CONN);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        xlsxToSQL();
    }

    private void xlsxToSQL() {
        // Count to 6 for each row.
        int count = 0;
        ParseExcel parseExcel = new ParseExcel();
        ArrayList <String> excelLotInfo = null;
        try {
            excelLotInfo = parseExcel.excelText(WRKBK);

            for (String dataPoint : excelLotInfo){
                dataPoint = dataPoint.replaceAll("Detail", "");
                String[] temp = dataPoint.trim().split("\\s+");
                insert(CONN, temp);
                //System.out.println(Arrays.toString(temp));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.println(excelLotInfo.toString());
    }

    public boolean insert(Connection connection, String[] values){
        PreparedStatement pStatement = null;
        try {
            pStatement = connection.prepareStatement("INSERT INTO lotdatabase " +
                    "(Vehicle, Days, Year, Make, Model, Serial) " +
                    "VALUES (?, ?, ?, ?, ?, ?)");
            pStatement.setString(1, values[0]);
            pStatement.setString(2, values[1]);
            pStatement.setString(3, values[2]);
            pStatement.setString(4, values[3]);
            pStatement.setString(5, values[4]);
            pStatement.setString(6, values[5]);

            //System.out.println(pStatement);
            pStatement.execute();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            close(pStatement);
        }
        return false;
    }

    public Connection getConnection(Connection connection) throws SQLException {

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(SQLCONN);
            System.out.println("Database Connected");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void close(Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
