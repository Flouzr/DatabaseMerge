package parkingUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class SaveLoadLayout {

    private ArrayList<String[]> temp = new ArrayList<>();

    // TODO: Input parameter needs to be name of lot loaded to load correct save
    public ArrayList<String[]> LoadLayout(){
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(
                    "save_location.txt"));
            String line = reader.readLine();
            while (line != null) {
                //System.out.println(line);
                temp.add(new ParseShapeNode().parseNode(line));
                // read next line
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return temp;
    }

    public void SaveLayout(String lotInfo) throws FileNotFoundException {
        // TODO: Save based on the name of the image
        File layoutSave = new File("save_location.txt");
        try{
            if(!layoutSave.exists()){
                layoutSave.createNewFile();
            }
            PrintWriter out = new PrintWriter(new FileWriter(layoutSave, true));
            out.write(lotInfo + System.getProperty("line.separator"));
            out.close();
        }catch(IOException e){
            System.out.println("COULD NOT LOG!!");
        }
    }
}
