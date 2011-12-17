package kit.route.a.lot.heightinfo;

import kit.route.a.lot.common.Coordinates;

public interface IHeightTile

{
    /**
     * Operation IHeightTile
     *
     * @param width - 
     * @param height - 
     * @param origin - 
     * @return 
     */
    public IHeightTile ( int width, int height, Coordinates origin );

    /**
     * Operation getHeight
     *
     * @param x - 
     * @param y - 
     * @return float
     */
    public float getHeight ( int x, int y );

    /**
     * Operation setHeight
     *
     * @param x - 
     * @param y - 
     * @param height - 
     * @return 
     */
    public setHeight ( int x, int y, float height );

    /**
     * Operation getHeight
     *
     * @param pos - 
     * @return float
     */
    public float getHeight ( Coordinates pos );

    /**
     * Operation setHeight
     *
     * @param pos - 
     * @param height - 
     * @return 
     */
    public setHeight ( Coordinates pos, float height );

}

