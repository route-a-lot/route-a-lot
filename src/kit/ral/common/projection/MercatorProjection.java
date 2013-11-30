
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



public class MercatorProjection extends Projection {

    private Coordinates mTopLeft;
    private Coordinates topLeft;
    float scale;

    protected MercatorProjection(Coordinates topLeft, Coordinates bottomRight, int width) {
        this(topLeft, calculateScaleFromWidth(topLeft, bottomRight, width));
    }


    protected MercatorProjection(Coordinates topLeft, float scale) {
        this.mTopLeft = mercatorCoordinates(topLeft);
        this.topLeft = topLeft;
        this.scale = scale;
    }

    @Override
    public Coordinates getLocalCoordinates(Coordinates geoCoordinates) {
        mTopLeft = mercatorCoordinates(topLeft);
        Coordinates mercatorCoordinates = mercatorCoordinates(geoCoordinates);
        Coordinates localCoordinates = new Coordinates();
        localCoordinates.setLongitude((mercatorCoordinates.getLongitude() - mTopLeft.getLongitude()) / scale);
        localCoordinates.setLatitude((mTopLeft.getLatitude() - mercatorCoordinates.getLatitude()) / scale);
        return localCoordinates;
    }

    @Override
    public Coordinates getGeoCoordinates(Coordinates localCoordinates) {
        mTopLeft = mercatorCoordinates(topLeft);
        Coordinates newLocalCoordinates = new Coordinates();
        newLocalCoordinates.setLatitude(mTopLeft.getLatitude() - (scale * localCoordinates.getLatitude()));
        newLocalCoordinates.setLongitude((scale * localCoordinates.getLongitude()) + mTopLeft.getLongitude());
        Coordinates reverseMercatorCoordinates = reverseMercatorCoordinates(newLocalCoordinates);
        return reverseMercatorCoordinates;
    }

    private static Coordinates mercatorCoordinates(Coordinates geoCoordinates) {
        Coordinates mercatorCoordinates = new Coordinates();
        mercatorCoordinates.setLongitude((geoCoordinates.getLongitude()));
        mercatorCoordinates.setLatitude((float) (arsinh(Math
                .tan(geoCoordinates.getLatitude() * Math.PI / 180)) * 180 / Math.PI));
        return mercatorCoordinates;
    }
    
    private static Coordinates reverseMercatorCoordinates(Coordinates localCoordinates) {
        Coordinates reverseMercatorCoordinates = new Coordinates();
        reverseMercatorCoordinates.setLongitude(localCoordinates.getLongitude());
        reverseMercatorCoordinates.setLatitude((float) (Math.atan(Math.sinh(localCoordinates.getLatitude() * Math.PI
                / 180)) * 180 / Math.PI));
        return reverseMercatorCoordinates;
    }

    private static double arsinh(double x) {
        return Math.log(x + Math.sqrt(x * x + 1));
    }

    public static float calculateScaleFromWidth(Coordinates topLeft, Coordinates bottomRight, int width) {
        return Math.abs(mercatorCoordinates(bottomRight).getLongitude()
                - mercatorCoordinates(topLeft).getLongitude())
                / width;
    }

    public static float calculateScaleFromHeight(Coordinates topLeft, Coordinates bottomRight, int height) {
        return Math.abs(mercatorCoordinates(bottomRight).getLatitude()
                - mercatorCoordinates(topLeft).getLatitude())
                / height;
    }

}
