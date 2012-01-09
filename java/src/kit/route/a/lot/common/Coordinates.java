package kit.route.a.lot.common;


public class Coordinates {

    private double longitude;
    private double latitude;
    
    public Coordinates(double lon, double lat) {
        this.longitude = lon;
        this.latitude = lat;
    }
    
    public Coordinates() { }
    
    public Coordinates(float lat, float lon) {
        latitude = lat;
        longitude = lon;
    }
    
    public double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }
    
    public double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
}
