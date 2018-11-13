import dbUtil.SQLDatabase;
import dbUtil.Vehicle;

import java.util.List;

public class Main
{

    public static void main(String[] args) {
        SQLDatabase sqlDatabaseNew = new SQLDatabase("newlotdatabase");
//        sqlDatabaseNew.xlsxToSQL("CDJR NEW 8.31.18.xlsx");
//        sqlDatabaseNew.difference("CDJR NEW 9.24.18.xlsx");

        SQLDatabase sqlDatabaseUsed = new SQLDatabase("usedlotdatabase");
//        sqlDatabaseUsed.xlsxToSQL("CDJR USED 8.31.xlsx");
//        sqlDatabaseUsed.difference("CDJR USED 9.24.xlsx");


        // Below is just testing to make sure everything is working as expected
        List<Vehicle> test = sqlDatabaseNew.getAllVehicles();

        if (sqlDatabaseNew.search("AD6295")){
            System.out.println("FOUND");
        } else {
            System.out.println(" NOT FOUND");
        }

        for (Vehicle veh : test){
            System.out.println(veh.getVehicleID());
        }

        sqlDatabaseNew.updateVehiclePosition("AD6295", "5", "3");
        sqlDatabaseNew.updateVehicleSize("AD6295", "10", "20");

        List<Vehicle> temp = sqlDatabaseNew.getSingleVehicle("AD6295");
        for (Vehicle veh : temp){
            System.out.println(veh.toString());
        }
    }
}