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
    
    public Coordinates() { }
    
    public Coordinates(float lat, float lon) {
        latitude = lat;
        longitude = lon;
    }
    
    //TODO why was this still here?
    public float getLatitude() {
        return (float)latitude;
    }
    
    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }
    
    public float getLongitude() {
        return (float)longitude;
    }
    
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
}
