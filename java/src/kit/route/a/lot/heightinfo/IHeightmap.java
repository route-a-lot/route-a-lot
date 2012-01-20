package kit.route.a.lot.heightinfo;

import java.util.Set;

import kit.route.a.lot.common.Coordinates;

public interface IHeightmap {

    /**
     * Operation getTiles
     * 
     * @param upLeft
     *            -
     * @param bottomRight
     *            -
     * @return Set<HeightTile>
     */
    public Set<HeightTile>
            getTiles(Coordinates upLeft, Coordinates bottomRight);

    /**
     * Operation getHeight
     * 
     * @param pos
     *            -
     * @return float
     */
    public int getHeight(Coordinates pos);

    /**
     * Operation addHeightTile
     * 
     * @param tile
     *            -
     * @return
     * @return
     */
    public void addHeightTile(HeightTile tile);

}
