package parkingUtil;

import dbUtil.Vehicle;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;


public class ParkingSpot extends Rectangle {

    private Vehicle vehicle;

    public void setTestinfo(String testinfo) {
        this.testinfo = testinfo;
    }

    public String getTestinfo(){
        return testinfo;
    }

    private String testinfo;

    // TODO: This will be used later to display the car type icon (LAMBO, TOYOTA, ETC)
    Image vehicleIcon;

    public ParkingSpot(double x_pos, double y_pos, double width, double height){
        this.setX(x_pos);
        this.setY(y_pos);
        this.setWidth(width);
        this.setHeight(height);
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

//    @Override
//    public String toString(){
//        return super.toString() + this.getTestinfo();
//    }
}
