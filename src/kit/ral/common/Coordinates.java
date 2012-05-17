
/**
Copyright (c) 2012, Matthias Grundmann, Daniel Krauß, Yvonne Braun, Josua Stabenow
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
        * Redistributions of source code must retain the above copyright
          notice, this list of conditions and the following disclaimer.
        * Redistributions in binary form must reproduce the above copyright
          notice, this list of conditions and the following disclaimer in the
          documentation and/or other materials provided with the distribution.
        * The names of the contributors may not be used to endorse or promote products
          derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
**/

package kit.ral.common;

import java.io.DataInput;
import java.io.DataOutput;
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
        return String.format("Lat %1$3.4f / Lon %2$3.4f", latitude, longitude);
    }
    
    public boolean equals(Coordinates pos) {
        return (pos != null) && (Math.abs(latitude - pos.latitude) < 0.005f)
                && (Math.abs(longitude - pos.longitude) < 0.005f);
    }
    
    /**
     * Adds summands latitude to latitude and summands longitude to longitude and returns this.
     * @param summand
     * @return
     */
    public Coordinates add(Coordinates summand) {
        if (summand != null) {
            latitude += summand.latitude;
            longitude += summand.longitude;
        }
        return this;
    }

    /**
     * Adds addLat to latitude and addLon to the longitude and returns this.
     * 
     * @param addLat
     * @param addLon
     * @return
     */
    public Coordinates add(float addLat, float addLon) {
        latitude += addLat;
        longitude += addLon;
        return this;
    }
    
    /**
     * Subtracts subtrahends latitude from latitude and subtrahends longitude from longitude and returns this.
     * 
     * @param subtrahend
     * @return
     */
    public Coordinates subtract(Coordinates subtrahend) {
        if (subtrahend != null) {
            latitude -= subtrahend.latitude;
            longitude -= subtrahend.longitude;
        }
        return this;
    }
    
    /**
     * Multiplies latitude and longitude by factor and sets them to the results.
     * 
     * @param factor
     * @return this
     */
    public Coordinates scale(double factor) {
        latitude *= factor;
        longitude *= factor;
        return this;
    }
       
    /**
     * Normalizes the vector represented by this object.
     * @return this
     */
    public Coordinates normalize() {
        double length = getLength();
        latitude /= length;
        longitude /= length;
        return this;
    }
    
    /**
     * Sets latitude to -latitude and longitude to -longitude.
     * I.e. draws the vector by 180 degrees.
     * 
     * @return this
     */
    public Coordinates invert() {
        latitude = -latitude;
        longitude = -longitude;
        return this;
    }
    
    /**
     * Rotates the given vector by angle.
     * @param angle angle given in degrees
     * @return
     */
    public Coordinates rotate(int angle) {
        return rotate(Math.toRadians(angle));
    }
    
    /**
     * Rotates the given vector by angle.
     * @param angle angle given in radians
     * @return
     */
    public Coordinates rotate(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
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
    
    /**
     * Returns the angel between pos1 and pos2 in radians.
     * 
     * @param pos1
     * @param pos2
     * @return
     */
    public static double getAngle(Coordinates pos1, Coordinates pos2) {
        return Math.acos((pos1.latitude * pos2.latitude + pos1.longitude * pos2.longitude)
                          / (pos1.getLength() * pos2.getLength()));
    }
 
    public static Coordinates interpolate(Coordinates pos1, Coordinates pos2, float ratio) {
        return pos1.clone().add((pos2.latitude - pos1.latitude) * ratio,
                (pos2.longitude - pos1.longitude) * ratio);
    }
    
    
    public static Coordinates loadFromInput(DataInput input) throws IOException {
        Coordinates result = new Coordinates();
        result.latitude = input.readFloat();
        result.longitude = input.readFloat();
        return result;
    }
    
    public void saveToOutput(DataOutput output) throws IOException {
        output.writeFloat(this.latitude);
        output.writeFloat(this.longitude);
    }
    
}
