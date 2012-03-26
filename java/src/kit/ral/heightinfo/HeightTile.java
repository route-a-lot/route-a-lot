package kit.ral.heightinfo;

import kit.ral.common.Coordinates;
import kit.ral.common.util.Splines;

public abstract class HeightTile {
    
    protected Coordinates origin;
    protected int tileWidth, tileHeight;
    
    
    // CONSTRUCTOR
    
    public HeightTile(int width, int height, Coordinates origin) {
        this.tileWidth = width;
        this.tileHeight = height;
        this.origin = origin;
    }
    

    // BASIC GETTER & SETTER
    
    public abstract int getHeight(int x, int y);

    public abstract void setHeight(int x, int y, float height);

    
    // ADVANCED GETTER & SETTER
    
    public float getHeight(Coordinates pos) {
        // position relative to the tile (values between 0 and 1 as each tile has 1° dimensions)
        float latDiff = pos.getLatitude() - origin.getLatitude();
        float lonDiff = pos.getLongitude() - origin.getLongitude();
        // convert position into next lower data pixel
        int x = (int) (lonDiff * tileWidth);
        int y = (int) (latDiff * tileHeight);
        // get ratio towards next upper data pixel (0 to 1 each)
        float ratioX = Math.abs(lonDiff * tileWidth - x);
        float ratioY = Math.abs(latDiff * tileHeight - y);           
        // interpolate linearily
        //float interpolateX1 = MathUtil.interpolate(getHeight(x,y), getHeight(x+1,y), ratioX);
        //float interpolateX2 = MathUtil.interpolate(getHeight(x,y+1), getHeight(x+1,y+1), ratioX);
        //float interpolateY = MathUtil.interpolate(interpolateX1, interpolateX2, ratioY);
        //return interpolateY;
        
        /*Berechnen von 5 Stützstellen in Y- Richtung mit, entspricht ca. 350m Luftlinie*/
        float[] xKoordinates0 = {getHeight(x-2,y-2),getHeight(x-1,y-2),getHeight(x,y-2),getHeight(x+1,y-2),getHeight(x+2,y-2)};
        float[] xKoordinates1 =  {getHeight(x-2,y-1),getHeight(x-1,y-1),getHeight(x,y-1),getHeight(x+1,y-1),getHeight(x+2,y-1)};
        float[] xKoordinates2 = {getHeight(x-2,y),getHeight(x-1,y),getHeight(x,y),getHeight(x+1,y),getHeight(x+2,y)}; 
        float[] xKoordinates3 = {getHeight(x-2,y+1),getHeight(x-1,y+1),getHeight(x,y+1),getHeight(x+1,y+1),getHeight(x+2,y+1)};
        float[] xKoordinates4 =   {getHeight(x-2,y+2),getHeight(x-1,y+2),getHeight(x,y+2),getHeight(x+1,y+2),getHeight(x+2,y+2)};     
        float[] xValues = {0f,1f,2f,3f,4f};
       
        
        Splines splineX1 = new Splines(xValues,xKoordinates0);
        Splines splineX2 = new Splines(xValues,xKoordinates1);
        Splines splineX3 = new Splines(xValues,xKoordinates2);
        Splines splineX4 = new Splines(xValues,xKoordinates3);
        Splines splineX5 = new Splines(xValues,xKoordinates4);
        
        /*Der Wert steht im 2ten Intervall, an der Stelle x-xi = ratioX*/
        float y1 = (float)splineX1.calculateValue(ratioX,2);
        float y2 = (float)splineX2.calculateValue(ratioX,2);
        float y3 = (float)splineX3.calculateValue(ratioX,2);
        float y4 = (float)splineX4.calculateValue(ratioX,2);
        float y5 = (float)splineX5.calculateValue(ratioX,2);
        float[] yKoordinates = {y1, y2, y3, y4, y5};
        Splines splineY = new Splines(xValues, yKoordinates);
        /*und in y-Richtung im 2ten Intervall, an der Stelle x-xi = ratioY*/
        return (float)splineY.calculateValue(ratioY, 2);
    }

    public void setHeight(Coordinates pos, int height) {
        // float latDiff = pos.getLatitude() - origin.getLatitude();
        // float lonDiff = pos.getLongitude() - origin.getLongitude();
        // int x = tileWidth * (int) lonDiff;
        // int y = height * (int) latDiff;    
        // setHeight(x, y, height);
    }


    // MISCELLANEAOUS
    
    public boolean equals(Object other) {        
        return (other == this) || ((other instanceof HeightTile)
                && origin.equals(((HeightTile) other).origin));
    }
    
    @Override
    public String toString(){
        return "HeightTile: " + origin;
    }
    
    public static long getSpecifier(float lat, float lon) {
        return (long) (lon * 1000 + lat * 1000000);
    }
    
    public long getSpecifier() {
        return getSpecifier(origin.getLatitude(), origin.getLongitude());
    }
}
