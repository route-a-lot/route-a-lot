package kit.ral.heightinfo;

import kit.ral.common.Bounds;
import kit.ral.common.Coordinates;

public abstract class Heightmap {

    protected static final float UNDEFINED_HEIGHT = 0;
    
    public abstract float getHeight(Coordinates pos);

    public abstract void addHeightTile(HeightTile tile);

    /**
     * Interpolates the height field between the given coordinates and fits it into the target array.
     * @param topLeft the north western corner of the section
     * @param bottomRight the south western corner of the section
     * @param heightdata the target array (Dimension: [lon][lat])
     */
    public void reduceSection(Bounds bounds, float[][] heightdata, int heightBorder) {
        float stepSizeX = bounds.getWidth() / (heightdata.length - 2 * heightBorder - 1);
        float stepSizeY = bounds.getHeight() / (heightdata[0].length - 2 * heightBorder - 1);
        Coordinates pos = new Coordinates();
        for (int x = 0; x < heightdata.length; x++) {
            for (int y = 0; y < heightdata[x].length; y++) {
                pos.setLatitude(bounds.getTop() + (y - heightBorder) * stepSizeY);
                pos.setLongitude(bounds.getLeft() + (x - heightBorder) * stepSizeX);
                heightdata[x][y] = getHeight(pos);
            }
        }
    }
}

