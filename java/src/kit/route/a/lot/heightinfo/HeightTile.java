package kit.route.a.lot.heightinfo;

import kit.route.a.lot.common.Coordinates;



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
        return data[x][y];
    }
    
    public Coordinates getOrigin(){
        return origin;
    }

    @Override
    public void setHeight(int x, int y, float height) {
        data[x][y] = (int) height;
    }

    @Override
    public float getHeight(Coordinates pos) {
        // die Tiles sind im abstand von einem Grad aufgebaut. das origin ist ganzzahlig
        float latDiff = pos.getLatitude() - origin.getLatitude();
        float lonDiff = pos.getLongitude() - origin.getLongitude();
        /*Intervallänge: 1°/1201*/
        int x = (int)(latDiff*width);
        int y = (int)(lonDiff*height);
        
        float facX = Math.abs(lonDiff - x / (float) width);
        float facY = Math.abs(latDiff - y / (float) height);        
        
        if(x == width || y == height) {
            
        }
        
        float interpolateX1 = data[x][y] + (data[x+1][y] - data[x][y]) * facX;
        float interpolateX2 = data[x][y+1] + (data[x+1][y+1] - data[x][y+1]) * facX;
        float interpolateY = interpolateX1 + (interpolateX2 - interpolateX1) * facY;
        return interpolateY;
        //return data[x][y];
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

        return (Math.abs(origin.getLatitude() - other.getOrigin().getLatitude()) < 0.005) &&
               (Math.abs(origin.getLongitude() - other.getOrigin().getLongitude()) < 0.005);
    }
   
    @Override
    public String toString(){
        return "origin = lat: "+ origin.getLatitude()+" long: " +origin.getLongitude();
    }   
}