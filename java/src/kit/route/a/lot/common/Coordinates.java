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
        return (topLeft.latitude < latitude) && (latitude < bottomRight.latitude) 
            && (topLeft.longitude < longitude) && (longitude < bottomRight.longitude);
    }
    
    @Override
    public String toString() {
        return String.format("Lat %1$3.4f / Lon %2$3.4f", latitude, longitude);
    }
    
    public boolean equals(Coordinates pos) {
        return (pos != null) && (Math.abs(latitude - pos.latitude) < 0.005f)
                && (Math.abs(longitude - pos.longitude) < 0.005f);
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
        double length = getLength();
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
    

    public double getLength() {
        return Math.sqrt(latitude * latitude + longitude * longitude);
    }
    
    public static double getDistance(Coordinates pos1, Coordinates pos2) {
        return pos1.clone().subtract(pos2).getLength();
    }
    
    public static double getAngle(Coordinates pos1, Coordinates pos2) {
        return Math.acos((pos1.latitude * pos2.latitude + pos1.longitude * pos2.longitude)
                          / (pos1.getLength() * pos2.getLength()));
    }
 
    public static Coordinates interpolate(Coordinates pos1, Coordinates pos2, float ratio) {
        return pos1.clone().add((pos2.latitude - pos1.latitude) * ratio,
                (pos2.longitude - pos1.longitude) * ratio);
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
