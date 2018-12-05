package parkingUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;

public class SaveLoadLayout {

    private ArrayList<String[]> temp = new ArrayList<>();

    // TODO: Input parameter needs to be name of lot loaded to load correct save
    public ArrayList<String[]> LoadLayout(String filename){
        temp.clear();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(filename));
            String line = reader.readLine();
            while (line != null) {
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

    public void SaveLayout(String lotInfo, String filename) {
        // TODO: Save based on the name of the image
        File layoutSave = new File(filename);
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
