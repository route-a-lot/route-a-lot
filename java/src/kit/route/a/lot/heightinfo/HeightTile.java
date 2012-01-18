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

    public HeightTile(int width, int height, Coordinates origin) {
    this.data = new int[height][width]; 
    this.origin = origin;
    }

    @Override
    public int getHeight(int x, int y) {
        
        return data[x][y];
    }
    
    public Coordinates getOrigin(){
        return origin;
    }

    @Override
    public void setHeight(int x, int y, int height) {
        data[x][y] = height;

    }

    @Override
    public int getHeight(Coordinates pos) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setHeight(Coordinates pos, int height) {
        // TODO Auto-generated method stub

    }

    
    public boolean equals(HeightTile other){

    return ((origin.getLatitude() == other.getOrigin().getLatitude()) &&
             (origin.getLongitude() == other.getOrigin().getLongitude() ) );

    }
   
    @Override
    public String toString(){
    return "origin = lat: "+ origin.getLatitude()+" long: " +origin.getLongitude();
    }   
}