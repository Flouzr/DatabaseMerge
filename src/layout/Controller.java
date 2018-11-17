package layout;

import dbUtil.SQLDatabase;
import dbUtil.Vehicle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import parkingUtil.ParkingSpot;
import parkingUtil.SaveLoadLayout;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable {

    @FXML
    public AnchorPane locator_anchor_pane;

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
    private Label editor_total_label;

    @FXML
    public AnchorPane editor_anchor_pane;

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
    public Button layout_load_button;

    @FXML
    public TabPane main_tabpane;

    private Stage stage;

    private Image image;

    static class Wrapper<T> { T value ; }

    // TODO: Make the scrollpane zoomable
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SaveLoadLayout saveLoadLayout = new SaveLoadLayout();
        ArrayList<ParkingSpot> parkingSpots = new ArrayList<>();
        image = new Image("/assets/skm.jpg");
        ImageView iv = new ImageView(image);
        iv.fitWidthProperty().bind(layout_scrollpane.widthProperty());
        iv.fitHeightProperty().bind(layout_scrollpane.heightProperty());
        iv.setPreserveRatio(false);
        final double maxX = iv.getImage().getWidth();
        final double maxY = iv.getImage().getHeight();

        layout_anchor_pane.getChildren().add(iv);
        editor_anchor_pane.getChildren().add(iv);
        layout_anchor_pane.getChildren().add(iv);

        /*
        Begin Locator Specific Code
         */
        SQLDatabase sqlDatabaseNew = new SQLDatabase("newlotdatabase");
//        sqlDatabaseNew.xlsxToSQL("CDJR NEW 8.31.18.xlsx");
//        sqlDatabaseNew.difference("CDJR NEW 9.24.18.xlsx");
        SQLDatabase sqlDatabaseUsed = new SQLDatabase("usedlotdatabase");
//        sqlDatabaseUsed.xlsxToSQL("CDJR USED 8.31.xlsx");
//        sqlDatabaseUsed.difference("CDJR USED 9.24.xlsx");

        locator_search_button.setOnMouseClicked(e -> {
            // Make sure input is upper case since search is case-sensitive
            locator_vehicle_information.getItems().clear();
            if (sqlDatabaseNew.search(locator_search_field.getText().toUpperCase())){
                addVehicleListInfo(sqlDatabaseNew.getSingleVehicle(locator_search_field.getText().toUpperCase()));
            } else if (sqlDatabaseUsed.search(locator_search_field.getText().toUpperCase())) {
                locator_vehicle_information.getItems().add(sqlDatabaseUsed.getSingleVehicle(locator_search_field.getText().toUpperCase()));
            } else {
                locator_vehicle_information.getItems().add("No vehicles with stock number " + locator_search_field.getText());
            }
        });
        /*
        End Locator Specific Code

        Begin Editor Specific Code
         */
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
        /*
        End Editor Specific Code

        Begin Layout Specific Code
         */
        // TODO: Just place near center of screen
        layout_add_button.setOnMouseClicked(e -> {
            if (e.getSceneX() < maxX && e.getSceneY() < maxY) {
                int r = (int) (Math.random() * (500 - 10)) + 10;
                int t = (int) (Math.random() * (500 - 10)) + 10;

                layout_anchor_pane.getChildren().add(createDraggableRectangle(r, t, 30, 15));
            }
        });

        layout_browse_button.setOnMouseClicked(e -> {
            // TODO: Add back ability to load layout
            //openBrowseMenu();
        });

        layout_save_button.setOnMouseClicked(e -> {
            parkingSpots.clear();
            List<Node> temp = getAllNodes(layout_scrollpane);
            for (Node n : temp){
                if (n.toString().contains("Rectangle")){
                    // Save the raw node data so we can just parse it later.
                    // Yes this is probably more expensive than just saving the values and
                    // reading it line by line... Maybe I'll fix that later.
                    try {
                        saveLoadLayout.SaveLayout(n.toString());
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        layout_load_button.setOnMouseClicked(e -> {
            layout_load_button.setDisable(true);
            ArrayList<String[]> spots = saveLoadLayout.LoadLayout();

            // TODO: Add checks to see if the layout is empty
            for (String[] info : spots){
                    parkingSpots.add(parkingSpots.size() , new ParkingSpot(Double.parseDouble(info[0]), Double.parseDouble(info[1]), Double.parseDouble(info[2]), Double.parseDouble(info[3])));
            }
            for (ParkingSpot spot : parkingSpots){
                layout_anchor_pane.getChildren().add(createDraggableRectangle(spot.getX_pos(), spot.getY_pos(), spot.getWidth(), spot.getHeight()));
            }
        });
    }

    // Create a new rectangle and add points to both resize and move it
    private Rectangle createDraggableRectangle(double x, double y, double width, double height) {
        final double handleRadius = 5 ;

        Rectangle vehicleSpot = new Rectangle(x, y, width, height);

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



    public String openBrowseMenu(){
        stage = (Stage) layout_scrollpane.getScene().getWindow();
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter png = new FileChooser.ExtensionFilter("png", "*.png");
        FileChooser.ExtensionFilter jpg = new FileChooser.ExtensionFilter("jpg", "*.jpg");
        fc.getExtensionFilters().addAll(png,jpg);

        // TODO: If no file is chosen nullpointed needs to be fixed
        return fc.showOpenDialog(stage).getAbsolutePath();
    }

    public void addVehicleListInfo(List vehicle){

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
