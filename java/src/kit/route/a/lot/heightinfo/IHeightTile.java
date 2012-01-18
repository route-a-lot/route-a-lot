package kit.route.a.lot.heightinfo;

import kit.route.a.lot.common.Coordinates;

public interface IHeightTile {

    /**
     * Operation getHeight
     * 
     * @param x
     *            -
     * @param y
     *            -
     * @return float
     */
    public int getHeight(int x, int y);

    /**
     * Operation setHeight
     * 
     * @param x
     *            -
     * @param y
     *            -
     * @param height
     *            -
     * @return
     * @return
     */
    public void setHeight(int x, int y, int height);

    /**
     * Operation getHeight
     * 
     * @param pos
     *            -
     * @return float
     */
    public int getHeight(Coordinates pos);

    /**
     * Operation setHeight
     * 
     * @param pos
     *            -
     * @param height
     *            -
     * @return
     * @return
     */
    public void setHeight(Coordinates pos, int height);

}
