package kit.ral.heightinfo;

import kit.ral.common.Coordinates;
import kit.ral.common.util.MathUtil;



public class RAMHeightTile extends HeightTile {

    protected int[][] data;

    public RAMHeightTile(int width, int height, Coordinates origin) {
        super(width, height, origin);
        this.data = new int[height][width];      
    }

    @Override
    public int getHeight(int x, int y) {
        if (!( (x >= 0) && (x < tileWidth) && (y >= 0) && (y < tileHeight) )) {
            return 0;
        }
        return data[MathUtil.clip(y, 0, tileWidth - 1)][MathUtil.clip(x, 0, tileHeight - 1)];
    }

    @Override
    public void setHeight(int x, int y, float height) {
        if ((x >= 0) && (x < tileWidth) && (y >= 0) && (y < tileHeight)) {
            data[y][x] = (int) height;
        }
    }

}