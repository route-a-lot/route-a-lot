package kit.route.a.lot.heightinfo;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Util;

public abstract class HeightTile {
    
    protected Coordinates origin;
    protected int tileWidth, tileHeight;
    
    public HeightTile(int width, int height, Coordinates origin) {
        this.tileWidth = width;
        this.tileHeight = height;
        this.origin = origin;
    }
    
    public boolean equals(Object other) {
        if(other == this) {
            return true;
        }
        if(!(other instanceof HeightTile)) {
            return false;
        }
        HeightTile comparee = (HeightTile) other;
        return tileWidth == comparee.tileWidth
                && tileHeight== comparee.tileHeight
                && origin.equals(comparee.origin);
    }
    
    public abstract int getHeight(int x, int y);

    public abstract void setHeight(int x, int y, float height);

    public float getHeight(Coordinates pos) {
        // die Tiles sind im abstand von einem Grad aufgebaut. das origin ist ganzzahlig
        float latDiff = pos.getLatitude() - origin.getLatitude();
        float lonDiff = pos.getLongitude() - origin.getLongitude();
        /*Intervallänge: 1°/1201*/
        int x = (int) (lonDiff * tileWidth);
        int y = (int) (latDiff * tileHeight);
        
        float ratioX = Math.abs(lonDiff * tileWidth - x);
        float ratioY = Math.abs(latDiff * tileHeight - y);           
        
        float interpolateX1 = Util.interpolate(getHeight(x,y), getHeight(x+1,y), ratioX);
        float interpolateX2 = Util.interpolate(getHeight(x,y+1), getHeight(x+1,y+1), ratioX);
        float interpolateY = Util.interpolate(interpolateX1, interpolateX2, ratioY);
        return interpolateY;        
    }

    public void setHeight(Coordinates pos, int height) {
        float latDiff = pos.getLatitude() - origin.getLatitude();
        float lonDiff = pos.getLongitude() - origin.getLongitude();
        int x = tileWidth * (int) lonDiff;
        int y = height * (int) latDiff;    
        setHeight(x, y, height);
    }

    public boolean equals(HeightTile other) {
        return (other != null) && (origin.equals(other.origin)); 
    }
    
    @Override
    public String toString(){
        return "HeightTile: " + origin;
    }
}
