
/**
Copyright (c) 2012, Matthias Grundmann, Malte Wolff, Josua Stabenow
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

package kit.ral.common.projection;

import kit.ral.common.Coordinates;
import kit.ral.controller.State;


public class ProjectionFactory {
    private static final float SCALE = 5E-6f;
    
    public static Projection getNewProjection(Coordinates topLeft, Coordinates bottomRight) {
        return new MercatorProjection(topLeft, SCALE);
        // return new SimpleProjection(topLeft, bottomRight, 21000, 12000);
    }
    
    /**
     * Returns the projection for the map that is currently loaded in
     * State.getInstance().getLoadedMapInfo().
     * @return the projection for the current map
     */
    public static Projection getCurrentProjection() {
        Coordinates topLeft = State.getInstance().getMapInfo().getGeoTopLeft();
        return getNewProjection(topLeft, null);
        // Coordinates bottomRight = State.getInstance().getLoadedMapInfo().getGeoBottomRight();
        // return getNewProjection(topLeft, bottomRight);
    }
    
}
