package dbUtil;

public class Vehicle {

    private String vehicleID, days, year, make, model, serial;

    public Vehicle(String vehicleID, String days, String year, String make, String model, String serial) {
        this.vehicleID = vehicleID;
        this.days = days;
        this.year = year;
        this.make = make;
        this.model = model;
        this.serial = serial;
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
        return getVehicleID() + " | " + getDays() + " | " + getYear() + " | " + getMake() + " | " + getModel() + " | " +
                getSerial();
    }

}
