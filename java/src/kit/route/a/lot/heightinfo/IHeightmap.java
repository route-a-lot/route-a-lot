package kit.route.a.lot.heightinfo;

import java.util.Set;

import kit.route.a.lot.common.Coordinates;

public interface IHeightmap {


    public Set<HeightTile> getTiles(Coordinates upLeft, Coordinates bottomRight);

    public float getHeight(Coordinates pos);

    public void addHeightTile(HeightTile tile);

    /**
     * Interpolates the height field between the given coordinates and fits it into the target array.
     * @param topLeft the north western corner of the section
     * @param bottomRight the south western corner of the section
     * @param heightdata the target array (Dimension: [lon][lat])
     */
    public void reduceSection(Coordinates topLeft, Coordinates bottomRight, float[][] heightdata, int heightBorder);

}

