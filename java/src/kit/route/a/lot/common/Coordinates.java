package kit.route.a.lot.common;

public class Coordinates {

    private float latitude;
    private float longitude;

    public Coordinates() {
    }

    public Coordinates(float lat, float lon) {
        this.latitude = lat;
        this.longitude = lon;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
    
    @Override
    public String toString() {
        return "latitude: " + latitude + " - longitude: " + longitude;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Coordinates) {
            Coordinates other = (Coordinates) obj;
            return latitude == other.latitude && longitude == other.longitude;
        } else {
            return false;
        }
    }
    
}
