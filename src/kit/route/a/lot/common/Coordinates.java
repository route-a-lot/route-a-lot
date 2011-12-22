package kit.route.a.lot.common;


public class Coordinates {

    private double longitude;
    private double latitude;
    
    public Coordinates(double lon, double lat) {
        this.longitude = lon;
        this.latitude = lat;
    }
    
    public double getLon() {
        return this.longitude;
    }
    
    public double getLat() {
        return this.latitude;
    }
}
