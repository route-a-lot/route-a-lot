package kit.ral.common;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public class Bounds implements Cloneable {

    private float left, right, top, bottom;
    
    public Bounds() {
        this.left = 0;
        this.right = 0;
        this.top = 0;
        this.bottom = 0;
    }
    
    public Bounds(float left, float right, float top, float bottom) {
        if (left > right || top > bottom) {
            throw new IllegalArgumentException();
        }
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }
    
    public Bounds(Coordinates center, float radius) {
        if (radius < 0) {
            throw new IllegalArgumentException();
        }
        this.left = center.getLongitude() - radius;
        this.right = center.getLongitude() + radius;
        this.top = center.getLatitude() - radius;
        this.bottom = center.getLatitude() + radius;
    }
    
    public Bounds(Coordinates topLeft, Coordinates bottomRight) {
        this(topLeft, bottomRight, false);
    }
    
    public Bounds(Coordinates topLeft, Coordinates bottomRight, boolean allowNegativeDimensions) {
        this.left = topLeft.getLongitude();
        this.right = bottomRight.getLongitude();
        this.top = topLeft.getLatitude();
        this.bottom = bottomRight.getLatitude();
        if (!allowNegativeDimensions && (left > right || top > bottom)) {
            throw new IllegalArgumentException();
        }
    }
    
    public float getLeft() {
        return left;
    }
    
    public float getRight() {
        return right;
    }
    
    public float getTop() {
        return top;
    }
    
    public float getBottom() {
        return bottom;
    }
    
    public float getWidth() {
        return right - left;
    }
    
    public float getHeight() {
        return bottom - top;
    }
    
    public Coordinates getTopLeft() {
        return new Coordinates(top, left);
    }
    
    public Coordinates getBottomRight() {
        return new Coordinates(bottom, right);
    }
    
    public Coordinates getCenter() {
        return new Coordinates((top + bottom) / 2, (left + right) / 2);
    }
    
    public Coordinates getDimensions() {
        return new Coordinates(getHeight(), getWidth());
    }
    
    
    
    public static Bounds loadFromInput(DataInput input) throws IOException {
        Bounds result = new Bounds();
        result.left = input.readFloat();
        result.right = input.readFloat();
        result.top = input.readFloat();
        result.bottom = input.readFloat();
        return result;
    }
    
    public void saveToOutput(DataOutput output) throws IOException {
        output.writeFloat(left);
        output.writeFloat(right);
        output.writeFloat(top);
        output.writeFloat(bottom);
    }
    
    public Bounds extend(float borderSize) {
        left -= borderSize;
        right += borderSize;
        top -= borderSize;
        bottom += borderSize;
        return this;
    }
    
    public Bounds extend(float left, float right, float top, float bottom) {
        this.left -= left;
        this.right += right;
        this.top -= top;
        this.bottom += bottom;
        return this;
    }
    
    public Bounds extend(float lat, float lon, float buffer) {
        if (lat - buffer < top) {
            top = lat - buffer;
        }
        if (lat + buffer > bottom) {
            bottom = lat + buffer;
        }
        if (lon - buffer < left) {
            left = lon - buffer;
        }
        if (lon + buffer > right) {
            right = lon + buffer;
        }
        return this;
    }
    
    public Bounds extend(float lat, float lon) {
        if (lat < top) {
            top = lat;
        }
        if (lat > bottom) {
            bottom = lat;
        }
        if (lon < left) {
            left = lon;
        }
        if (lon > right) {
            right = lon;
        }
        return this;
    }
    
    @Override
    public Bounds clone() {
        Bounds result = new Bounds(left, left, top, top);
        result.right = right;
        result.bottom = bottom;
        return result;
    }
    
    @Override
    public boolean equals(Object object) {
        if ((object == null) || !(object instanceof Bounds)) {
            return false;
        }
        Bounds other = (Bounds) object;
        return (Math.abs(left - other.left) < 0.005f)
                && (Math.abs(right - other.right) < 0.005f)
                && (Math.abs(top - other.top) < 0.005f)
                && (Math.abs(bottom - other.bottom) < 0.005f);
    }
    
}
