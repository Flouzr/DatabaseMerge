package dbUtil;

public class Vehicle {

    private String vehicleID, days, year, make, model, serial, x_pos, y_pos, width, height;

    public Vehicle(String vehicleID, String days, String year, String make, String model, String serial) {
        this.vehicleID = vehicleID;
        this.days = days;
        this.year = year;
        this.make = make;
        this.model = model;
        this.serial = serial;
    }

    public Vehicle(String vehicleID, String days, String year, String make, String model, String serial, String x_pos
    , String y_pos, String width, String height) {
        this.vehicleID = vehicleID;
        this.days = days;
        this.year = year;
        this.make = make;
        this.model = model;
        this.serial = serial;
        this.x_pos = x_pos;
        this.y_pos = y_pos;
        this.width = width;
        this.height = height;
    }

    public String getX_pos() {
        return x_pos;
    }

    public void setX_pos(String x_pos) {
        this.x_pos = x_pos;
    }

    public String getY_pos() {
        return y_pos;
    }

    public void setY_pos(String y_pos) {
        this.y_pos = y_pos;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(String vehicleID) {
        this.vehicleID = vehicleID;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    @Override
    public String toString(){
        return getVehicleID() + " | " + getDays() + " | " + getMake() + " | " + getModel() + " | " +
                getSerial() + " | " + getYear() + " | " + getHeight() + " | " + getWidth() + " | " +
                getX_pos() + " | " + getY_pos();
    }

}
