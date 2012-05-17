
/**
Copyright (c) 2012, Matthias Grundmann, Daniel Krauß, Josua Stabenow
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

package kit.ral.routing;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;


public class AdjacentFieldsRoutingGraphSimple implements RoutingGraph {
    
    private int[] edgesPos;
    private int[] edges;
    private int[] weights;

    @Override
    public void buildGraph(int[] startID, int[] endID, int[] weight, int maxNodeID) {
        if (startID == null || endID == null || weight == null ||
            startID.length != endID.length || endID.length != weight.length) {
            return;    //not possible to build graph
        }
        
        boolean sorted = false;
        int temp;
        while (!sorted){
           sorted = true;
           for (int i = 0; i < startID.length - 1; i++) 
              if (startID[i] > startID[i+1]) {                      
                 
                 temp       = startID[i];
                 startID[i] = startID[i+1];
                 startID[i+1] = temp;
                 
                 temp = endID[i];
                 endID[i] = endID[i+1];
                 endID[i+1] = temp;
                 
                 temp = weight[i];
                 weight[i] = weight[i+1];
                 weight[i+1] = temp;
                 
                 sorted = false;
              }
         } 
        edgesPos = new int[maxNodeID + 2];
        edgesPos[0] = 0;
        int index;
        
        for (int i = 0; i <= maxNodeID; i++) {
            index = edgesPos[i];
            for (int j = index; j < startID.length && startID[j] == i; j++) {
                index++;
            }
            edgesPos[i + 1] = index;
        }
        edges = endID.clone();
        weights = weight.clone();
    }
    
    
    //following functions are tested in another way
    @Override
    public int getWeight(int from, int to) {
        for (int i = edgesPos[from]; i < edgesPos[from+1]; i++) {
            if (edges[i] == to) {
                if (weights[i] > 0) {
                    return weights[i];
                } else {
                    return 1;
                }
            }
        }
        return 1;
    }
    @Override
    public Collection<Integer> getRelevantNeighbors(int node, byte[] areas) {
        return null;
    }
    @Override
    public RoutingGraph getInverted() {
        return null;
    }

    @Override
    public byte getAreaID(int node) {
        return 0;
    }
    
    @Override
    public void setAreaID(int node, byte id) {
    }
    
    @Override
    public int getIDCount() {
        return 0;
    }
    
    @Override
    public Collection<Integer> getAllNeighbors(int node) {
        if (node >= edgesPos.length - 1 || node < 0) {
            return new ArrayList<Integer>();
        } 
        Collection<Integer> relevantEdges = new ArrayList<Integer>();
        for (int i = edgesPos[node]; i < edgesPos[node+1]; i++) {
            relevantEdges.add(edges[i]);
        }
        return relevantEdges;
    }
    @Override
    public void setArcFlag(int node, int node2, byte area) {
    }
    
    @Override
    public String getMetisRepresentation() {
        return null;
    }
    
    @Override
    public void readAreas(String areas) {
    }
    
    @Override
    public void buildGraphWithUndirectedEdges(int[] uniqueEdgeStartIDs,
            int[] uniqueEdgeEndIDs, int maxWayNodeId) {
    }

    
    @Override
    public int[] getStartIDArray() {
        return edgesPos.clone();
    }


    @Override
    public int[] getEdgesArray() {
        return edges.clone();
    }


    @Override
    public int[] getWeightsArray() {
        return weights.clone();
    }


    @Override
    public void setAllArcFlags() {
    }


    @Override
    public void loadFromInput(DataInput input) throws IOException {
    }


    @Override
    public void saveToOutput(DataOutput output) throws IOException {
    }
}
