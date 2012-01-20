package kit.route.a.lot.heightinfo;
import java.util.Iterator;
import java.util.HashSet;
import kit.route.a.lot.common.Coordinates;


public class Heightmap implements IHeightmap {

    /** Associations */
    private HashSet<HeightTile> tiles;
    private HashSet<HeightTile> map;
    private Iterator<HeightTile> iterator;
    /*Konstruktor*/
	public Heightmap(){
		this.tiles = new HashSet<HeightTile>();
		this.map = new HashSet<HeightTile>();
	}

    @Override
    public HashSet<HeightTile> 
		getTiles(Coordinates upLeft, Coordinates bottomRight) {
	
        this.tiles = new HashSet<HeightTile>();

        float maxlat = (float)Math.floor(upLeft.getLatitude());
        float minlon = (float)Math.floor(upLeft.getLongitude());
        float minlat = (float)Math.floor(bottomRight.getLatitude());
        float maxlon = (float)Math.floor(bottomRight.getLongitude());
        
        for(int i = (int)maxlat; i <= (int)minlat; i++){
            for(int j = (int)minlon; j <= (int)maxlon; j++){
                Coordinates origin = new Coordinates((float)i,(float)j);
                HeightTile tmpTile = new HeightTile(0,0,origin);
                iterator = map.iterator();
                while(iterator.hasNext()){
                    HeightTile tile = iterator.next();			
                    if(tile.equals(tmpTile)){
                        tiles.add(tile);
                    }//end if
                }//end while
            }//end for
        }//end for
        return tiles;
    }//end getTiles

    @Override
    public int getHeight(Coordinates pos) {
        int lat = (int)pos.getLatitude();
        int lon = (int)pos.getLongitude();
        Coordinates origin = new Coordinates((float)lat,(float)lon);
        HeightTile tmpTile = new HeightTile(0,0,origin);
        iterator = map.iterator();
        
        while(iterator.hasNext()) {
            HeightTile tile = iterator.next();
            if(tile.equals(tmpTile)){
                return tile.getHeight(pos);
            }//end if
        }//end while
        return 0;
    }//end getHeight

    @Override
    public void addHeightTile(HeightTile tile) {
        	if(tile != null){
        	    map.add(tile);
        	}
    }
}//end class
