package kit.route.a.lot.heightinfo;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.WeightCalculator;
import kit.route.a.lot.map.rendering.Renderer3D;

public interface IHeightmap

{
    /**
     * Operation getTiles
     *
     * @param upLeft - 
     * @param bottomRight - 
     * @return Set<HeightTile>
     */
    public Set<HeightTile> getTiles ( Coordinates upLeft, Coordinates bottomRight );

    /**
     * Operation getHeight
     *
     * @param pos - 
     * @return float
     */
    public float getHeight ( Coordinates pos );

    /**
     * Operation addHeightTile
     *
     * @param tile - 
     * @return 
     */
    public addHeightTile ( HeightTile tile );

}

