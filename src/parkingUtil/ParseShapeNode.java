package parkingUtil;

public class ParseShapeNode {

    public String[] parseNode (String rectInfo){

        // Take the raw data, remove what we don't need.
        // Then split on the ',' and place that into an String []
        rectInfo = rectInfo.replaceAll("Rectangle", "");
        rectInfo = rectInfo.replaceAll("\\[", "").replaceAll("]","");

        String[] spotInfo = rectInfo.split(",", -1);

        // Now that each parameter is split into its own cell in the array,
        // remove all the text leading up to and including the = and put that,
        // into the String[] that we will return.
        for (int i = 0; i < spotInfo.length; i++){
            //System.out.println(spotInfo[i].trim());

            // Trim so there are no outside spaces and substring the parameter.
            spotInfo[i] = spotInfo[i].trim();
            spotInfo[i] = spotInfo[i].substring(spotInfo[i].indexOf("=") + 1);
        }
        return spotInfo;
    }
}
