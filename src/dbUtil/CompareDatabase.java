package dbUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class CompareDatabase extends SQLDatabase {
    private List<Vehicle> originalVehicles = new ArrayList<>();
    private List<Vehicle> updatedVehicles = new ArrayList<>();

    CompareDatabase(String database) {
        super(database);
    }

    public void difference(String updatedWorkbook) {
        ParseExcel parseExcel = new ParseExcel();
        List<String> updatedDB;
        List<String> formattedUpdated = new ArrayList<>();

        try {
            // Put the new/updated database into an ArrayList
            updatedDB = parseExcel.excelText(updatedWorkbook);
            // Take the existing SQL and make Vehicle objects for each
            originalVehicles = getAllVehicles();
            for (String dataPoint : updatedDB){
                // Split and format each line (all vehicle info per line [make, model, yr, etc]
                // into its own seperate List that will be used to create another List of
                // Vehicle objects. Basically jumping through hoops
                // Also the dark side has cookies
                dataPoint = dataPoint.replaceAll("Detail", "");
                List<String> temp = new ArrayList<>(Arrays.asList(dataPoint.trim().split("\\s+")));
                formattedUpdated.addAll(temp);
                updatedVehicles.add(new Vehicle(
                        formattedUpdated.get(0),
                        formattedUpdated.get(1),
                        formattedUpdated.get(2),
                        formattedUpdated.get(3),
                        formattedUpdated.get(4),
                        formattedUpdated.get(5)));
                formattedUpdated.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // After the databases are formatted correctly are identically
        // we difference and merge them
        compareAndMerge();
    }

    private void compareAndMerge() {
        // Not sure if we need these at a later date
        // If so make global
        List<String> removeThese = new ArrayList<>();
        List<String> addThese = new ArrayList<>();

        // If a new vehicle isn't in the OG list add it
        for (Vehicle newVehicle : updatedVehicles){
            if (!searchForVehicle(newVehicle.getVehicleID(), originalVehicles)){
                addThese.add(newVehicle.getVehicleID());
                insertNewVehicle(newVehicle);
            }
        }
        System.out.println("Old vehicles deleted from database");

        // If an OG vehicle isn't in the new list remove it
        for (Vehicle ogVehicle : originalVehicles){
            if (!searchForVehicle(ogVehicle.getVehicleID(), updatedVehicles)) {
                removeThese.add(ogVehicle.getVehicleID());
                delete(ogVehicle.getVehicleID());
            }
        }
        System.out.println("New vehicles inserted into database");
    }
}
