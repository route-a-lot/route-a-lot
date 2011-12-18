package kit.route.a.lot.heightinfo;

import kit.route.a.lot.common.Coordinates;

public class HeightTile
 implements IHeightTile
{
    /** Attributes */
    /**
     * 
     */
    private Coordinates origin;
    /**
     * 
     */
    private float[][] data;
    
    public HeightTile ( int width, int height, Coordinates origin ){
	}

	@Override
	public float getHeight(int x, int y) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setHeight(int x, int y, float height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getHeight(Coordinates pos) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setHeight(Coordinates pos, float height) {
		// TODO Auto-generated method stub
		
	}
}

