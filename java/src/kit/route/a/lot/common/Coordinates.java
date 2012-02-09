package kit.route.a.lot.common;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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
    
    public boolean isInBounds(Coordinates topLeft, Coordinates bottomRight) {
        //TODO DISCUSS: unnecessary min/max?
        float minLat = (float) Math.min(topLeft.getLatitude(), bottomRight.getLatitude());
        float maxLat = (float) Math.max(topLeft.getLatitude(), bottomRight.getLatitude());
        float minLon = (float) Math.min(topLeft.getLongitude(), bottomRight.getLongitude());
        float maxLon = (float) Math.max(topLeft.getLongitude(), bottomRight.getLongitude());
        return (latitude <= maxLat && latitude >= minLat && longitude >= minLon && longitude <= maxLon);
    }
    
    @Override
    public String toString() {
        return String.format("(LAT: %1$3.8f / LON: %2$3.8f)", latitude, longitude);
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
    
    public Coordinates add(Coordinates summand) {
        if (summand != null) {
            latitude += summand.latitude;
            longitude += summand.longitude;
        }
        return this;
    }
    
    public Coordinates add(float addLat, float addLon) {
        latitude += addLat;
        longitude += addLon;
        return this;
    }
    
    public Coordinates subtract(Coordinates subtrahend) {
        if (subtrahend != null) {
            latitude -= subtrahend.latitude;
            longitude -= subtrahend.longitude;
        }
        return this;
    }
    
    public Coordinates scale(float factor) {
        latitude *= factor;
        longitude *= factor;
        return this;
    }
    
    public Coordinates normalize() {
        float length = (float) Math.sqrt(latitude * latitude + longitude * longitude);
        latitude /= length;
        longitude /= length;
        return this;
    }
    
    public Coordinates invert() {
        latitude = -latitude;
        longitude = -longitude;
        return this;
    }
    
    public Coordinates rotate(int angle) {
        double cos = Math.cos(Math.toRadians(angle));
        double sin = Math.sin(Math.toRadians(angle));
        double oldLatitude = latitude;
        latitude = (float) Math.toDegrees(sin * longitude + cos * oldLatitude);
        longitude = (float) Math.toDegrees(cos * longitude - sin * oldLatitude);
        return this;
    }
    
    public Coordinates clone() {
        return new Coordinates(latitude, longitude);
    }
    
    public static double getDistance(Coordinates pos1, Coordinates pos2) {
        return (pos1 == null || pos2 == null) ? null :
            Math.sqrt(Math.pow(pos1.latitude - pos2.latitude, 2)
                    + Math.pow(pos1.longitude - pos2.longitude, 2));
    }
    
    public static Coordinates loadFromStream(DataInputStream stream) throws IOException {
        Coordinates result = new Coordinates();
        result.latitude = stream.readFloat();
        result.longitude = stream.readFloat();
        return result;
    }
    
    public void saveToStream(DataOutputStream stream) throws IOException {
        stream.writeFloat(this.latitude);
        stream.writeFloat(this.longitude);
    }
    
}
