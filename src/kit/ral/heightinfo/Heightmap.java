
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

import kit.ral.common.Bounds;
import kit.ral.common.Coordinates;

public abstract class Heightmap {

    protected static final float UNDEFINED_HEIGHT = 0;
    
    public abstract float getHeight(Coordinates pos);

    public abstract void addHeightTile(HeightTile tile);

    /**
     * Interpolates the height field between the given coordinates and fits it into the target array.
     * @param bounds the bounds of the section
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

