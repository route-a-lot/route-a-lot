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
    
    @Override
    public String toString() {
        return String.format("(LAT: %1$3.2f / LON: %2$3.2f)", latitude, longitude);
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
