package kit.route.a.lot.heightinfo;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Util;



public class HeightTile implements IHeightTile {


    /** Attributes */
    /**
     * 
     */
    private Coordinates origin;
    /**
     * 
     */
    private int[][] data;
    /**
    *
    */
    private int width;
    /**
    *
    */
    private int height;

    public HeightTile(int width, int height, Coordinates origin) {
        this.data = new int[height][width]; 
        this.origin = origin;
        this.width = width;
        this.height = height;
    }

    @Override
    public int getHeight(int x, int y) {
        return data[Util.clip(y, 0, width - 1)][Util.clip(x, 0, height - 1)];
    }
    
    public Coordinates getOrigin(){
        return origin;
    }

    @Override
    public void setHeight(int x, int y, float height) {
        data[y][x] = (int) height;
    }

    @Override
    public float getHeight(Coordinates pos) {
        // die Tiles sind im abstand von einem Grad aufgebaut. das origin ist ganzzahlig
        float latDiff = pos.getLatitude() - origin.getLatitude();
        float lonDiff = pos.getLongitude() - origin.getLongitude();
        /*Intervallänge: 1°/1201*/
        int x = (int)(lonDiff*width);
        int y = (int)(latDiff*height);
        
        float ratioX = Math.abs(lonDiff * (float) width - x);
        float ratioY = Math.abs(latDiff * (float) height - y);        
        
        float interpolateX1 = Util.interpolate(getHeight(x,y), getHeight(x+1,y), ratioX);
        float interpolateX2 = Util.interpolate(getHeight(x,y+1), getHeight(x+1,y+1), ratioX);
        float interpolateY = Util.interpolate(interpolateX1, interpolateX2, ratioY);
        return interpolateY;        
    }  

    @Override
    public void setHeight(Coordinates pos, int height) {
        float lat = pos.getLatitude() - origin.getLatitude();
        float lon = pos.getLongitude() - origin.getLongitude();
        /*Intervallänge: 1°/1201 */
        int x = (int)(lat*width);
        int y = (int)(lon*width);        
        data[x][y] = height;
    }

    
    public boolean equals(HeightTile other){
        return (Math.abs(origin.getLatitude() - other.origin.getLatitude()) < 0.005) &&
               (Math.abs(origin.getLongitude() - other.origin.getLongitude()) < 0.005);
    }
   
    @Override
    public String toString(){
        return "origin = lat: "+ origin.getLatitude()+" long: " +origin.getLongitude();
    }   
}