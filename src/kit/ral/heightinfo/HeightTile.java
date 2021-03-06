
/**
Copyright (c) 2012, Matthias Grundmann, Jan Jacob, Yvonne Braun, Josua Stabenow
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
        * Redistributions of source code must retain the above copyright
          notice, this list of conditions and the following disclaimer.
        * Redistributions in binary form must reproduce the above copyright
          notice, this list of conditions and the following disclaimer in the
          documentation and/or other materials provided with the distribution.
        * The names of the contributors may not be used to endorse or promote products
          derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
**/

package kit.ral.heightinfo;

import kit.ral.common.Coordinates;
import kit.ral.common.util.MathUtil;

public abstract class HeightTile {
    
    protected Coordinates origin;
    protected int tileWidth, tileHeight;
    
    
    // CONSTRUCTOR
    
    public HeightTile(int width, int height, Coordinates origin) {
        this.tileWidth = width;
        this.tileHeight = height;
        this.origin = origin;
    }
    

    // BASIC GETTER & SETTER
    
    public abstract int getHeight(int x, int y);

    public abstract void setHeight(int x, int y, float height);

    
    // ADVANCED GETTER & SETTER
    
    
    
    public float getHeight(Coordinates pos) {
        // position relative to the tile (values between 0 and 1 as each tile has 1° dimensions)
        float latDiff = pos.getLatitude() - origin.getLatitude();
        float lonDiff = pos.getLongitude() - origin.getLongitude();
        // convert position into next lower data pixel
        int x = (int) (lonDiff * tileWidth);
        int y = (int) (latDiff * tileHeight);
        // get ratio towards next upper data pixel (0 to 1 each)
        float ratioX = Math.abs(lonDiff * tileWidth - x);
        float ratioY = Math.abs(latDiff * tileHeight - y);           
        // interpolate linearily
        //float interpolateX1 = MathUtil.interpolate(getHeight(x,y), getHeight(x+1,y), ratioX);
        //float interpolateX2 = MathUtil.interpolate(getHeight(x,y+1), getHeight(x+1,y+1), ratioX);
        //float interpolateY = MathUtil.interpolate(interpolateX1, interpolateX2, ratioY);
        //return interpolateY;
        
        /*Berechnen von 5 Stützstellen in Y- Richtung mit, entspricht ca. 350m Luftlinie*/
        float[][] grid = new float[5][5];
        for (int gy = 0; gy < 5; gy++) {
            for (int gx = 0; gx < 5; gx++) {
                grid[gy][gx] = getHeight(x + gx - 2, y + gy - 2);
            }
        }

        /*Der Wert steht im 2ten Intervall, an der Stelle x-xi = ratioX*/
        float y1 = MathUtil.getSplineValue(grid[0], ratioX);
        float y2 = MathUtil.getSplineValue(grid[1], ratioX);
        float y3 = MathUtil.getSplineValue(grid[2], ratioX);
        float y4 = MathUtil.getSplineValue(grid[3], ratioX);
        float y5 = MathUtil.getSplineValue(grid[4], ratioX);
        
        /*und in y-Richtung im 2ten Intervall, an der Stelle x-xi = ratioY*/
        return MathUtil.getSplineValue(new float[] {y1, y2, y3, y4, y5}, ratioY);
    }


    // MISCELLANEAOUS
    
    public boolean equals(Object other) {        
        return (other == this) || ((other instanceof HeightTile)
                && origin.equals(((HeightTile) other).origin));
    }
    
    @Override
    public String toString(){
        return "HeightTile: " + origin;
    }

    // specifier encodes the tiles lower left corner
    public static long getSpecifier(float lat, float lon) {
        double cornerLatitude = Math.floor(lat) * 100;
        double cornerLongitude = Math.floor(lon) * 100;
        return (long) (cornerLongitude + cornerLatitude * 10000);
    }
    
    public long getSpecifier() {
        return getSpecifier(origin.getLatitude(), origin.getLongitude());
    }
}
