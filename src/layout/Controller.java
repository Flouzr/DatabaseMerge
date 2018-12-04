package layout;

import dbUtil.SQLDatabase;
import dbUtil.Vehicle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import parkingUtil.ParkingSpot;
import parkingUtil.SaveLoadLayout;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class Controller implements Initializable {

    @FXML
    public AnchorPane locator_anchor_pane;

    @FXML
    public ScrollPane locator_scrollpane;

    @FXML
    private Button locator_search_button;

    @FXML
    private TextField locator_search_field;

    @FXML
    private ListView locator_vehicle_information;

    @FXML
    private Button editor_refresh_button;

    @FXML
    private ListView editor_add_list;

    @FXML
    public Button editor_save_button;

    @FXML
    private Label editor_total_label;

    @FXML
    public AnchorPane editor_anchor_pane;

    @FXML
    public ScrollPane editor_scrollpane;

    @FXML
    private AnchorPane layout_anchor_pane;

    @FXML
    private ScrollPane layout_scrollpane;

    @FXML
    private Button layout_save_button;

    @FXML
    private Button layout_add_button;

    @FXML
    private Button layout_browse_button;

    @FXML
    public Button utilities_load_database_button;

    @FXML
    public Button utilities_merge_database_button;

    @FXML
    public Button utilities_clear_database_button;

    @FXML
    public TabPane main_tabpane;

    private Stage stage;

    private Image image;

    private ParkingSpot lastSpotSearched;

    static class Wrapper<T> { T value ; }

    String layoutSaveName = null;

    // TODO: Make the scrollpane zoomable
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SaveLoadLayout saveLoadLayout = new SaveLoadLayout();
        ArrayList<ParkingSpot> parkingSpots = new ArrayList<>();

        /*
        Begin Locator Specific Code
         */

        //TODO: Add button to manually sync/clear database.
        //TODO: Also needs to select database names.
        SQLDatabase sqlDatabaseNew = new SQLDatabase("newlotdatabase");
//        sqlDatabaseNew.xlsxToSQL("CDJR NEW 8.31.18.xlsx");
//        sqlDatabaseNew.difference("CDJR NEW 9.24.18.xlsx");
        SQLDatabase sqlDatabaseUsed = new SQLDatabase("usedlotdatabase");
//        sqlDatabaseUsed.xlsxToSQL("CDJR USED 8.31.xlsx");
//        sqlDatabaseUsed.difference("CDJR USED 9.24.xlsx");

        //TODO: Add ability to select initial and second(merge) spreadsheet rather than hardcode
        utilities_load_database_button.setOnMouseClicked(e -> {
            sqlDatabaseNew.xlsxToSQL("CDJR NEW 8.31.18.xlsx");
            sqlDatabaseUsed.xlsxToSQL("CDJR USED 8.31.xlsx");
            System.out.println("Initial Workbook Loaded");
        });

        utilities_merge_database_button.setOnMouseClicked(e ->{
            sqlDatabaseNew.difference("CDJR NEW 9.24.18.xlsx");
            sqlDatabaseUsed.difference("CDJR USED 9.24.xlsx");
            System.out.println("New Workbook Merged");
        });

        utilities_clear_database_button.setOnMouseClicked(e -> {
            sqlDatabaseNew.deleteAllEntries();
            sqlDatabaseUsed.deleteAllEntries();
            System.out.println("All Databases Cleared");
        });

        locator_search_button.setOnMouseClicked(e -> {
            // Make sure input is upper case since search is case-sensitive
            locator_vehicle_information.getItems().clear();
            if (sqlDatabaseNew.search(locator_search_field.getText().toUpperCase())){
                addVehicleListInfo(sqlDatabaseNew.getSingleVehicle(locator_search_field.getText().toUpperCase()));
                HightlightSpot(parkingSpots);
            } else if (sqlDatabaseUsed.search(locator_search_field.getText().toUpperCase())) {
                locator_vehicle_information.getItems().add(sqlDatabaseUsed.getSingleVehicle(locator_search_field.getText().toUpperCase()));
                for (ParkingSpot spot : parkingSpots){
                    if (spot.getFill() == Color.BLUE){
                        HightlightSpot(parkingSpots);
                    }
                }
            } else {
                locator_vehicle_information.getItems().add("No vehicles with stock number " + locator_search_field.getText());
                if (lastSpotSearched != null){
                    for (ParkingSpot spot : parkingSpots){
                        spot.setStroke(Color.BLACK);
                        spot.setStrokeWidth(2);
                        spot.setFill(Color.GREY);
                    }
                }
            }
        });
        /*
        End Locator Specific Code

        Begin Editor Specific Code
         */

        //TODO: Check last edited save file and load that (modified time)
        //loadParkingSpots(saveLoadLayout, parkingSpots, editor_anchor_pane, layoutSaveName + "_savelocations.txt", false);


        //TODO: Check if blue and if so save.
        editor_save_button.setOnMouseClicked(e -> {
                    List<Node> temp = getAllNodes(editor_scrollpane);
                    for (Node n : temp){
                        if (n instanceof ParkingSpot) {
                            if(((ParkingSpot) n).getFill() == Color.BLUE){
                                for (ParkingSpot spot : parkingSpots){
                                    if (spot.getTestinfo() != null ){
                                        if (spot.getX() == ((ParkingSpot) n).getX() && spot.getY() == ((ParkingSpot) n).getY() && spot.getFill() == ((ParkingSpot) n).getFill()) {
                                            new SaveLoadLayout().SaveLayout(n + "===" + spot.getTestinfo(), layoutSaveName + "_savespots.txt");
                                            System.out.println(n);
                                            System.out.println(spot.getTestinfo());
                                        }
                                    }
                                }
                            }
                        }
                    }
        });

        editor_refresh_button.setOnMouseClicked(e -> {
            List<Vehicle> newAllVehicles = sqlDatabaseNew.getAllVehicles();
            List<Vehicle> usedAllVehicles = sqlDatabaseUsed.getAllVehicles();
            int total = 0;

            editor_add_list.getItems().clear();

            for (Vehicle veh : newAllVehicles){
                editor_add_list.getItems().add(veh.toString());
                total++;
            }
            for (Vehicle veh : usedAllVehicles){
                editor_add_list.getItems().add(veh.toString());
                total++;
            }

            editor_total_label.setText("Total Unadded Vehicles: " + total);
        });

        editor_add_list.setCellFactory(param -> {
            ListCell<String> listCell = new ListCell<String>() {
                @Override
                public void updateItem(String item , boolean empty) {
                    super.updateItem(item, empty);
                    setText(item);
                }
            };

            // Setup the transfer mode for drag and drop
            listCell.setOnDragDetected(event -> {
                /* drag was detected, start a drag-and-drop gesture*/
                /* allow any transfer mode */
                Dragboard db = listCell.startDragAndDrop(TransferMode.COPY);

                /* Put a string on a dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(listCell.getText());
                db.setContent(content);

                event.consume();
            });

            //When the drag is successful execute below
            listCell.setOnDragDone(event -> {
                /* the drag and drop gesture ended */
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.COPY) {
                    //listCell.setText("DROPPED ONTO TARGET!");
                    editor_add_list.getItems().remove(listCell.getItem());
                    //TODO: add another box and change that text
                    int tempnum = Integer.parseInt(editor_total_label.getText().substring(editor_total_label.getText().indexOf(':') + 1).trim()) - 1;
                    editor_total_label.setText("Total Unadded Vehicles: " + tempnum);
                }
                event.consume();
            });

            return listCell;
        });
        /*
        End Editor Specific Code

        Begin Layout Specific Code
         */

        // TODO: Just place near center of screen
        layout_add_button.setOnMouseClicked(e -> {
//            if (e.getSceneX() < maxX_layout && e.getSceneY() < maxY_layout) {
                int r = (int) (Math.random() * (500 - 10)) + 10;
                int t = (int) (Math.random() * (500 - 10)) + 10;

                layout_anchor_pane.getChildren().add(createDraggableRectangle(r, t, 50, 30));
//            }
        });

        layout_save_button.setOnMouseClicked(e -> {
            parkingSpots.clear();
            List<Node> temp = getAllNodes(layout_scrollpane);
            for (Node n : temp){
                if (n.toString().contains("Rectangle")){
                    // Save the raw node data so we can just parse it later.
                    // Yes this is probably more expensive than just saving the values and
                    // reading it line by line... Maybe I'll fix that later.

                    //TODO: Get name from the image selected
                    saveLoadLayout.SaveLayout(n.toString(), layoutSaveName + "_savelocations.txt");
                }
            }
        });

        layout_browse_button.setOnMouseClicked(e -> {
            // TODO: Add back ability to load layout and check if cancel (NULL)
            File file = new File(openBrowseMenu());
            image = new Image(file.toURI().toString());
            updateAllLayouts(image);
            loadParkingSpots(saveLoadLayout, parkingSpots, layout_anchor_pane, layoutSaveName + "_savelocations.txt",true);
            loadParkingSpots(saveLoadLayout, parkingSpots, editor_anchor_pane, layoutSaveName + "_savelocations.txt", false);
            loadParkingSpots(saveLoadLayout, parkingSpots, locator_anchor_pane, layoutSaveName + "_savelocations.txt", false);
        });
    }

    private void HightlightSpot(ArrayList<ParkingSpot> parkingSpots) {
        for (ParkingSpot spot : parkingSpots){
            if (spot.getTestinfo() != null ){
                if (spot.getTestinfo().substring(0, spot.getTestinfo().indexOf("|")).trim().equals(locator_search_field.getText().toUpperCase())){
                    if (lastSpotSearched != null){
                        lastSpotSearched.setStroke(Color.BLACK);
                        lastSpotSearched.setStrokeWidth(2);
                        lastSpotSearched.setFill(Color.GREY);
                    }
                    lastSpotSearched = spot;
                    locator_anchor_pane.getChildren().remove(spot);
                    spot.setStroke(Color.RED);
                    spot.setFill(Color.BLACK);
                    spot.setStrokeWidth(4);
                    locator_anchor_pane.getChildren().add(spot);
                }
            }
        }
    }

    // Load the saved rectangles based on the tab (draggable and not draggable for layout and editor respectively).
    private void loadParkingSpots(SaveLoadLayout saveLoadLayout, ArrayList<ParkingSpot> parkingSpots, AnchorPane pane, String saveFile, Boolean draggable) {
        ArrayList<String[]> spots = saveLoadLayout.LoadLayout(saveFile);


        parkingSpots.clear();
        // TODO: Add checks to see if the layout is empty
        for (String[] info : spots){
                parkingSpots.add(parkingSpots.size() , new ParkingSpot(Double.parseDouble(info[0]), Double.parseDouble(info[1]), Double.parseDouble(info[2]), Double.parseDouble(info[3])));
        }

        if (draggable){
            for (ParkingSpot spot : parkingSpots){
                pane.getChildren().add(createDraggableRectangle(spot.getX(), spot.getY(), spot.getWidth(), spot.getHeight()));
            }
        } else {
            for (ParkingSpot spot : parkingSpots){
                pane.getChildren().add(createRectangle(parkingSpots, spot.getX(), spot.getY(), spot.getWidth(), spot.getHeight()));
            }
        }
//        parkingSpots.clear();
    }

    // Create a new rectangle
    private ParkingSpot createRectangle(ArrayList<ParkingSpot> parkingspot,double x, double y, double width, double height){
        ParkingSpot vehicleSpot = new ParkingSpot(x, y, width, height);
        vehicleSpot.setStroke(Color.BLACK);
        vehicleSpot.setStrokeWidth(2);
        vehicleSpot.setFill(Color.GREY);

        vehicleSpot.setOnDragDropped(event -> {
            /* data dropped */
            /* if there is a string data on dragboard, read it and use it */
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                for (ParkingSpot spot : parkingspot){
                    if (spot.getX() == vehicleSpot.getX() && spot.getY() == vehicleSpot.getY()){
                        spot.setTestinfo(db.getString());
                        spot.setFill(Color.BLUE);
                        vehicleSpot.setFill(Color.BLUE);
                    }
                }
                success = true;
            }
            /* let the source know whether the string was successfully
             * transferred and used */
            event.setDropCompleted(success);
            event.consume();
        });

        vehicleSpot.setOnDragEntered(event -> {
            /* the drag-and-drop gesture entered the target */
            /* show to the user that it is an actual gesture target */
            if (event.getGestureSource() != vehicleSpot &&
                    event.getDragboard().hasString()) {
                if (vehicleSpot.getFill() == Color.BLUE){
                    event.acceptTransferModes(TransferMode.NONE);
                } else {
                    vehicleSpot.setFill(Color.GREEN);
                }
            }

            event.consume();
        });

        vehicleSpot.setOnDragExited(event -> {
            /* mouse moved away, remove the graphical cues */
            if (vehicleSpot.getFill() == Color.GREEN){
                vehicleSpot.setFill(Color.GREY);
            }
            event.consume();
        });

        vehicleSpot.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.COPY);
            if (vehicleSpot.getFill() == Color.BLUE){
                event.acceptTransferModes(TransferMode.NONE);
            } else {
                vehicleSpot.setFill(Color.GREEN);
            }
            event.consume();
        });

        return vehicleSpot;
    }

    // Create a new draggable rectangle and add points to both resize and move it
    private ParkingSpot createDraggableRectangle(double x, double y, double width, double height) {
        final double handleRadius = 5 ;

        ParkingSpot vehicleSpot = new ParkingSpot(x, y, width, height);

        vehicleSpot.setStroke(Color.BLACK);
        vehicleSpot.setStrokeWidth(2);
        vehicleSpot.setFill(Color.GREY);

        // top left resize handle:
        Circle resizeHandleNW = new Circle(handleRadius, Color.FIREBRICK);
        // bind to top left corner of Rectangle:
        resizeHandleNW.centerXProperty().bind(vehicleSpot.xProperty());
        resizeHandleNW.centerYProperty().bind(vehicleSpot.yProperty());

        // bottom right resize handle:
        Circle resizeHandleSE = new Circle(handleRadius, Color.FIREBRICK);
        // bind to bottom right corner of Rectangle:
        resizeHandleSE.centerXProperty().bind(vehicleSpot.xProperty().add(vehicleSpot.widthProperty()));
        resizeHandleSE.centerYProperty().bind(vehicleSpot.yProperty().add(vehicleSpot.heightProperty()));

//        vehicleSpot.setOnMouseClicked(e ->{
//            if (e.getButton() == MouseButton.SECONDARY){
//                layout_anchor_pane.getChildren().remove(vehicleSpot);
//            }
//        });

        // force circles to live in same parent as rectangle:
        vehicleSpot.parentProperty().addListener((obs, oldParent, newParent) -> {
            for (Circle c : Arrays.asList(resizeHandleNW, resizeHandleSE)) {
                Pane currentParent = (Pane)c.getParent();
                if (currentParent != null) {
                    currentParent.getChildren().remove(c);
                }
                ((Pane)newParent).getChildren().add(c);
            }
        });

        Wrapper<Point2D> mouseLocation = new Wrapper<>();

        setUpDragging(resizeHandleNW, mouseLocation) ;
        setUpDragging(resizeHandleSE, mouseLocation) ;

        resizeHandleNW.setOnMouseDragged(event -> {
            if (mouseLocation.value != null) {
                double deltaX = event.getSceneX() - mouseLocation.value.getX();
                double deltaY = event.getSceneY() - mouseLocation.value.getY();
                double newX = vehicleSpot.getX() + deltaX ;
                if (newX >= handleRadius
                        && newX <= vehicleSpot.getX() + vehicleSpot.getWidth() - handleRadius) {
                    vehicleSpot.setX(newX);
                    vehicleSpot.setWidth(vehicleSpot.getWidth() - deltaX);
                }
                double newY = vehicleSpot.getY() + deltaY ;
                if (newY >= handleRadius
                        && newY <= vehicleSpot.getY() + vehicleSpot.getHeight() - handleRadius) {
                    vehicleSpot.setY(newY);
                    vehicleSpot.setHeight(vehicleSpot.getHeight() - deltaY);
                }
                mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
            }
        });

        vehicleSpot.setOnMouseDragged((MouseEvent me) ->{
            double diffX = me.getX() - vehicleSpot.getWidth() / 2;
            double diffY = me.getY() - vehicleSpot.getHeight() / 2;
            vehicleSpot.setX(diffX);
            vehicleSpot.setY(diffY);
        });

        resizeHandleSE.setOnMouseDragged(event -> {
            if (mouseLocation.value != null) {
                double deltaX = event.getSceneX() - mouseLocation.value.getX();
                double deltaY = event.getSceneY() - mouseLocation.value.getY();
                double newMaxX = vehicleSpot.getX() + vehicleSpot.getWidth() + deltaX ;
                if (newMaxX >= vehicleSpot.getX()
                        && newMaxX <= vehicleSpot.getParent().getBoundsInLocal().getWidth() - handleRadius) {
                    vehicleSpot.setWidth(vehicleSpot.getWidth() + deltaX);
                }
                double newMaxY = vehicleSpot.getY() + vehicleSpot.getHeight() + deltaY ;
                if (newMaxY >= vehicleSpot.getY()
                        && newMaxY <= vehicleSpot.getParent().getBoundsInLocal().getHeight() - handleRadius) {
                    vehicleSpot.setHeight(vehicleSpot.getHeight() + deltaY);
                }
                mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
            }

            // TODO: Use the code below to apply rotation (points don't stick so we need to
            // TODO: put them in a container (Pane/Group) and apply a Rotate transform on that Node instead
            // TODO: Use Node.getRotate() in ParseShapeNode
//            if (mouseLocation.value != null) {
//                double deltaX = event.getSceneX() - mouseLocation.value.getX();
//                double deltaY = event.getSceneY() - mouseLocation.value.getY();
//                double radAngle = Math.atan2(deltaY, deltaX);
//                double degAngle = radAngle * 180 / Math.PI;
//                vehicleSpot.setRotate(degAngle);
//            }
        });

        return vehicleSpot ;
    }

    private void setUpDragging(Circle circle, Wrapper<Point2D> mouseLocation) {

        circle.setOnDragDetected(event -> {
            circle.getParent().setCursor(Cursor.CLOSED_HAND);
            mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
        });

        circle.setOnMouseReleased(event -> {
            circle.getParent().setCursor(Cursor.DEFAULT);
            mouseLocation.value = null ;
        });
    }

    public static ArrayList<Node> getAllNodes(Parent root) {
        ArrayList<Node> nodes = new ArrayList<>();
        addAllDescendents(root, nodes);
        return nodes;
    }

    private static void addAllDescendents(Parent parent, ArrayList<Node> nodes) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            nodes.add(node);
            if (node instanceof Parent)
                addAllDescendents((Parent)node, nodes);
        }
    }

    public String updateAllLayouts(Image image){
        ImageView locator_imageview = new ImageView(image);
        locator_imageview.fitWidthProperty().bind(layout_scrollpane.widthProperty());
        locator_imageview.fitHeightProperty().bind(layout_scrollpane.heightProperty());
        locator_imageview.setPreserveRatio(false);

        ImageView editor_imageview = new ImageView(image);
        editor_imageview.fitWidthProperty().bind(layout_scrollpane.widthProperty());
        editor_imageview.fitHeightProperty().bind(layout_scrollpane.heightProperty());
        editor_imageview.setPreserveRatio(false);

        ImageView layout_imageview = new ImageView(image);
        layout_imageview.fitWidthProperty().bind(layout_scrollpane.widthProperty());
        layout_imageview.fitHeightProperty().bind(layout_scrollpane.heightProperty());
        layout_imageview.setPreserveRatio(false);

        locator_anchor_pane.getChildren().add(locator_imageview);
        editor_anchor_pane.getChildren().add(editor_imageview);
        layout_anchor_pane.getChildren().add(layout_imageview);

        return image.getUrl().substring(image.getUrl().lastIndexOf("/")+1);
    }

    public String openBrowseMenu(){
        stage = (Stage) layout_scrollpane.getScene().getWindow();
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter png = new FileChooser.ExtensionFilter("png", "*.png");
        FileChooser.ExtensionFilter jpg = new FileChooser.ExtensionFilter("jpg", "*.jpg");
        fc.getExtensionFilters().addAll(jpg, png);

        // TODO: If no file is chosen nullpointed needs to be fixed
        return fc.showOpenDialog(stage).getAbsolutePath();
    }

    private void addVehicleListInfo(List vehicle){
        for (Vehicle veh : (List<Vehicle>) vehicle){
            locator_vehicle_information.getItems().add("Stock #: " + veh.getVehicleID());
            locator_vehicle_information.getItems().add("Make: " + veh.getMake());
            locator_vehicle_information.getItems().add("Days: " + veh.getDays());
            locator_vehicle_information.getItems().add("Model: " + veh.getModel());
            locator_vehicle_information.getItems().add("Serial: " + veh.getSerial());
            locator_vehicle_information.getItems().add("Year: " + veh.getYear());
        }
    }
}