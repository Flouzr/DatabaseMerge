package parkingUtil;

import dbUtil.Vehicle;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;


public class ParkingSpot {

    private double width, height, x_pos, y_pos;

    // TODO: Possible use to color empty/full lots or even color code to make
    private String fill, stroke;

    // TODO: This will be used later to display the car type icon (LAMBO, TOYOTA, ETC)
    Image vehicleIcon;

    public ParkingSpot(double x_pos, double y_pos, double width, double height){
        this.width = width;
        this.height = height;
        this.x_pos = x_pos;
        this. y_pos = y_pos;
    }

    public ParkingSpot(Rectangle rectangle, Vehicle vehicle){

    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getX_pos() {
        return x_pos;
    }

    public void setX_pos(Double x_pos) {
        this.x_pos = x_pos;
    }

    public Double getY_pos() {
        return y_pos;
    }

    public void setY_pos(Double y_pos) {
        this.y_pos = y_pos;
    }

}
