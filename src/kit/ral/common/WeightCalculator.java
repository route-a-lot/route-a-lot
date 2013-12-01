
/**
Copyright (c) 2012, Matthias Grundmann, Jan Jacob, Daniel Krauß, Yvonne Braun, Josua Stabenow
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

package kit.ral.common;

import kit.ral.common.description.OSMType;
import kit.ral.common.projection.Projection;
import kit.ral.controller.State;
import kit.ral.heightinfo.Heightmap;

public class WeightCalculator {

    private Projection projection = null;
    private State state;
    
    public WeightCalculator(State state) {
        this.state = state;
    }
    
    public void setProjection(Projection projection) {
        this.projection = projection;
    }
    
    public int calcWeightWithHeightAndHighwayMalus(int fromID, int toID, int wayType) {
        int weightWithHeight = calcWeightWithHeight(fromID, toID);
        return (int) (weightWithHeight * (getMalusFactor(wayType, state.getHighwayMalus())));
    }
    
    private static double getMalusFactor(int wayType, int malus) {
        if (malus == 0) {
            return 1;
        }
        float malusFactor = 0.03f;
        switch (wayType) {
            case OSMType.HIGHWAY_MOTORWAY:
            case OSMType.HIGHWAY_MOTORWAY_JUNCTION:
            case OSMType.HIGHWAY_MOTORWAY_LINK:
                malusFactor = 2.f;
                break;
            case OSMType.HIGHWAY_PRIMARY:
            case OSMType.HIGHWAY_PRIMARY_LINK:
                malusFactor = 1f;
                break;
            case OSMType.HIGHWAY_SECONDARY:
            case OSMType.HIGHWAY_SECONDARY_LINK:
                malusFactor = 0.5f;
                break;
            case OSMType.HIGHWAY_TERTIARY:
            case OSMType.HIGHWAY_TERTIARY_LINK:
                malusFactor = 0.25f;
                break;
            case OSMType.HIGHWAY_RESIDENTIAL:
            case OSMType.HIGHWAY_LIVING_STREET:
            case OSMType.HIGHWAY_UNCLASSIFIED:
                malusFactor = 0.02f;
                break;
            case OSMType.HIGHWAY_CYCLEWAY:
                malusFactor = 0;
                break;
        }
        return 1 + malusFactor / 150 * Math.pow(7, malus);
    }

    protected int calcWeightWithHeight(int fromID, int toID) {
        Coordinates from = projection.getGeoCoordinates(state.getMapInfo().getNodePosition(fromID));
        Coordinates to = projection.getGeoCoordinates(state.getMapInfo().getNodePosition(toID));
        Heightmap heightmap = state.getLoadedHeightmap();

        int flatWeight = calcWeight(fromID, toID);
        float fromHeight = heightmap.getHeight(from) * 100;
        float toHeight = heightmap.getHeight(to) * 100;

        double heightDifference = Math.pow(fromHeight - toHeight, 2)
                                    * getHeightMalusFactor(state.getHeightMalus());
        if (toHeight < fromHeight) {
            heightDifference = 0;
        }

        int weight = (int) Math.sqrt(Math.pow(flatWeight, 2) + Math.pow(heightDifference, 2));
//        print("Way from " + fromID + " to " + toID);
//        print("flatWeight: " + flatWeight);
//        print("weight with height: " + weight);
        return weight;
    }// end calcHeightWeight
    
    private double getHeightMalusFactor(int heightMalus) {
        if (heightMalus == 0) {
            return 0;
        }
        return Math.pow(2, heightMalus / 2);
    }


    protected int calcWeight(int fromID, int toID) {
        Coordinates from = state.getMapInfo().getNodePosition(fromID);
        Coordinates to = state.getMapInfo().getNodePosition(toID);

        Coordinates geoFrom = projection.getGeoCoordinates(from);
        Coordinates geoTo = projection.getGeoCoordinates(to);

        double geoFromLonRad = Math.abs(geoFrom.getLongitude() / 180 * Math.PI);
        double geoFromLalRad = Math.abs(geoFrom.getLatitude() / 180 * Math.PI);
        double geoToLonRad = Math.abs(geoTo.getLongitude() / 180 * Math.PI);
        double geoToLalRad = Math.abs(geoTo.getLatitude() / 180 * Math.PI);

        // Kugelkoordinaten berechnet distance in km
        double distanceRad =
                Math.acos(Math.sin(geoFromLalRad) * Math.sin(geoToLalRad)
                        + Math.cos(geoFromLalRad) * Math.cos(geoToLalRad)
                        * Math.cos(geoFromLonRad - geoToLonRad));
        return (int) (100 * 6371000.785 * distanceRad); // 6371000 is earthRadius in meter, so result will be
                                                        // given in cm
    }




}
