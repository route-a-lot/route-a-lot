
/**
Copyright (c) 2012, Matthias Grundmann, Yvonne Braun, Daniel Krauß, Jan Jacob, Malte Wolff, Josua Stabenow
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

package kit.ral.map;

import kit.ral.common.Bounds;
import kit.ral.common.Coordinates;
import kit.ral.common.Selection;
import kit.ral.common.description.OSMType;
import kit.ral.common.description.WayInfo;
import kit.ral.common.projection.ProjectionFactory;
import kit.ral.common.util.MathUtil;
import kit.ral.controller.State;
import kit.ral.map.info.MapInfo;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static kit.ral.common.util.Util.readUTFString;

public class Street extends MapElement implements Comparable<Street> {
    
    private String name;
    private Node[] nodes;
    private WayInfo wayInfo;

    
    // CONSTRUCTORS
    
    public Street() {
        this(null, null);
    }
    
    public Street(String name, WayInfo wayInfo) {
        this.nodes = null;
        setName(name);
        this.wayInfo = wayInfo;
    }

    
    // GETTERS & SETTERS
    
    @Override
    public String getName() {
        return (this.name != null) ? this.name : "";
    }

    private void setName(String name) {
        this.name = EMPTY.equals(name) ? null : name;
    }

    @Override
    public String getFullName() { 
        if ((wayInfo.getAddress() == null
                || wayInfo.getAddress().getCity().length() == 0)) {
            return getName();
        } else {
            return getName() + ", " + wayInfo.getAddress().getCity();
        }
    }
    
    public Node[] getNodes() {
        return nodes;
    }
    
    public void setNodes(Node[] nodes) {
        if (nodes == null || nodes.length == 0) {
            throw new IllegalArgumentException();
        }
        this.nodes = nodes;
    }

    public WayInfo getWayInfo() {
        return wayInfo;
    }

    
    // GENERAL MAP ELEMENT OPERATIONS
    
    @Override
    public boolean isInBounds(Bounds bounds) {
        boolean inBounds = false;
        Bounds extendedBounds = bounds.clone().extend(getDrawingSize() / 2 + 2);
        for (int i = 1; !inBounds && i < nodes.length; i++) {
            inBounds = nodes[i - 1].isInBounds(extendedBounds) || nodes[i].isInBounds(extendedBounds)
                    || MathUtil.isLineInBounds(nodes[i - 1].getPos(), nodes[i].getPos(), extendedBounds);
        }
        return inBounds;
    }
    
    @Override
    public Selection getSelection() {
        if (nodes.length < 2) {
            return null;
        }
        /*int start = nodes[nodes.length / 2 - 1].getID();
        int end = nodes[nodes.length / 2].getID();
        return new Selection(Coordinates.interpolate(nodes[nodes.length / 2].getPos(),
                   nodes[nodes.length / 2 + 1].getPos(), 0.5f), start, end, 0.5f, (name != null) ? name : 
                       Coordinates.interpolate(nodes[nodes.length / 2].getPos(),
                               nodes[nodes.length / 2 + 1].getPos(), 0.5f).toString());*/
        Selection sel = State.getInstance().getMapInfo().select(nodes[nodes.length / 2 - 1].getPos());
        sel.setName(getFullName());
        return sel;
    }
          
    @Override
    public MapElement getReduced(int detail, float range) {
        // draw everything on detail 0
        if (detail == 0) {
            return this;
        }
        
        if (getStreetDrawingSize(wayInfo) / 2 < detail) {
            return null;
        }
        
        // determine bounding box, discard too small streets
        /*Bounds bounds = new Bounds(nodes[0].getPos(), 0);
        for (Node node : nodes) {
            bounds.extend(node.getLatitude(), node.getLongitude());
        }
        if (bounds.getWidth() + bounds.getHeight() < range) {
            return null;
        }*/
        // return simplified street
        Street result = new Street(name, wayInfo);
        result.setNodes(simplifyNodes(nodes, range / 2));
        return (result.nodes.length == nodes.length) ? this : result;
    }
    
    
    // STREET SPECIFIC OPERATIONS
    
    public Selection getSelection(Coordinates pos) {
        int start = getClosestEdgeStartPosition(pos);
        Coordinates geoPos = ProjectionFactory.getCurrentProjection().getGeoCoordinates(pos);
        return new Selection(pos, nodes[start].getID(), nodes[start + 1].getID(),
                getRatio(start, start + 1, pos),
                (name != null) ? name : geoPos.toString());
    }
    
    public float getDistanceTo(Coordinates pos) {
        int startNode = getClosestEdgeStartPosition(pos);
        return getDistanceFromNodeToEdge(nodes, startNode, startNode + 1, pos);
    }
    
      
    // I/O OPERATIONS
    
    @Override
    protected void load(DataInput input) throws IOException {
        setName(input.readUTF());
        int len = input.readInt();
        nodes = new Node[len];
        MapInfo mapInfo = State.getInstance().getMapInfo();
        for (int i = 0; i < len; i++) {
            nodes[i] = mapInfo.getNode(input.readInt());
        }
        wayInfo = WayInfo.loadFromInput(input);
    }
    
    @Override
    protected void load(MappedByteBuffer mmap) throws IOException {
        setName(readUTFString(mmap));
        int len = mmap.getInt();
        nodes = new Node[len];
        MapInfo mapInfo = State.getInstance().getMapInfo();
        for (int i = 0; i < len; i++) {
            nodes[i] = mapInfo.getNode(mmap.getInt());
        }
        wayInfo = WayInfo.loadFromInput(mmap);
    }

    @Override
    protected void save(DataOutput output) throws IOException {
        output.writeUTF(getName());
        output.writeInt(this.nodes.length);
        for (Node node : this.nodes) {
            output.writeInt(node.getID());
        }
        wayInfo.saveToOutput(output);
    }
    
      
    // MISCELLANEOUS

    @Override
    public boolean equals(Object other) {
        return (other == this) || (
               (other != null) && (other instanceof Street)
                && getName().equals(((Street) other).getName())
                && nodes.equals(((Street) other).nodes)
                && wayInfo.equals(((Street) other).wayInfo));
    }

    public int compareTo(Street other) {
        int value;
        int otherValue;
        String otherName = other.getName();
        int minlength = otherName.length();
        if (name.length() < minlength) {
            minlength = name.length();
        }

        for (int i = 0; i < minlength; i++) {
            value = Character.getNumericValue(name.charAt(i));
            if (value < 0 || value > 25) {
                value = 25;
            }
            otherValue = Character.getNumericValue(otherName.charAt(i));
            if (value < 0 || otherValue > 25) {
                otherValue = 25;
            }
            if (value > otherValue) {
                return value - otherValue;
            } else if (value < otherValue) {
                return value - otherValue;
            }
        }

        /*
         * wenn PrÃ¤fix gleich aber dieser String kÃ¼rzer steht er lexikographisch weiter vorne
         */
        return name.length() - otherName.length();
    }

    
    // HELPER FUNCTIONS
    
    

   
    /*
     * returns the position in the array nodes of the startNode of the edge of this street, which is the
     * closest to the given Coordinate
     */
    private int getClosestEdgeStartPosition(Coordinates pos) {
        float nearestEdgeDistance = Float.MAX_VALUE;
        int nearestEdgeStartNode = 0;
        for (int i = 0; i < nodes.length - 1; i++) {
            float currentDistance = getDistanceFromNodeToEdge(nodes, i, i + 1, pos);
            if (currentDistance < nearestEdgeDistance) {
                nearestEdgeDistance = currentDistance;
                nearestEdgeStartNode = i;
            }
        }
        return nearestEdgeStartNode;
    }

    /*
     * returns the distance from the given coordinate to the given edge edge is given by the position of the
     * start - and endNode in the array nodes
     */
    private static float getDistanceFromNodeToEdge(Node[] nodes, int start, int end, Coordinates pos) {
        double b = Coordinates.getDistance(nodes[start].getPos(), pos);
        double a = Coordinates.getDistance(nodes[end].getPos(), pos);
        double c = Coordinates.getDistance(nodes[start].getPos(), nodes[end].getPos());
        if (c == 0) {
            return (float) Coordinates.getDistance(nodes[start].getPos(), pos);
        }
        float distance = (float) getTriangleCHeight(a, b, c);
        if (!(distance < 0)) {
            return distance;
        }
        return (float) Math.min(Coordinates.getDistance(nodes[start].getPos(), pos),
                Coordinates.getDistance(nodes[end].getPos(), pos));
    }

    // a= side between end and pos, b = s. between pos and start, c = s. between start and end
    private static double getTriangleCHeight(double a, double b, double c) {
        double angleBC = Math.acos((b * b + c * c - a * a) / (2 * b * c));
        double angleAB = Math.acos((a * a + c * c - b * b) / (2 * a * c));
        if (angleBC > Math.PI / 2 || angleAB > Math.PI / 2) { //
            /*
             * Not everything is in lot. No, seriously the min distance isn't between pos and a point ON the
             * line
             */
            return -1; // if failure --> wrong angles, try: if(angleAB > Math.PI / 4 || angleAB < Math.PI / 8)
        }
        return Math.sin(angleBC) * b; // height of triangle (a = "hypothenuse" and h = "gegenkathete")
    }

    private float getRatio(int startNode, int endNode, Coordinates pos) {
        double b = Coordinates.getDistance(nodes[startNode].getPos(), pos);
        double a = Coordinates.getDistance(nodes[endNode].getPos(), pos);
        double c = Coordinates.getDistance(nodes[startNode].getPos(), nodes[endNode].getPos());
        if (b == 0) {
            return 0.f;
        }
        if (a == 0) {
            return 1.f;
        }
        // float h = (float)getTriangleCHeight(a, b, c);
        double angleBC = Math.acos((b * b + c * c - a * a) / (2 * b * c));
        double angleAC = Math.acos((a * a + c * c - b * b) / (2 * a * c));
        if (angleBC > Math.PI / 2 || angleAC > Math.PI / 2) {
            if (Coordinates.getDistance(nodes[startNode].getPos(), pos) < Coordinates.getDistance(
                    nodes[endNode].getPos(), pos)) {
                return 0.0f;
            } else {
                return 1.0f;
            }
        }
        double fromStartToLot = Math.cos(angleBC) * b; // cos(AngleAB) * "hypothenuse"
        return (float) (fromStartToLot / c);
    }







    public static Node[] simplifyNodes(Node[] nodes, float range) {
        if (nodes.length <= 1) {
            return nodes;
        }
        List<Node> newNodes = new ArrayList<Node>();
        newNodes.add(nodes[0]);
        adjustTube(nodes, newNodes, 0, nodes.length - 1, range);
        newNodes.add(nodes[nodes.length - 1]);
        return newNodes.toArray(new Node[newNodes.size()]);
    }

    private static void adjustTube(Node[] nodes, Collection<Node> outNodes, int start, int end, float range) {
        float maxDistance = Float.MIN_VALUE;
        int maxDistIndex = 0;
        for (int i = start + 1; i < end; i++) {
            float curDistance = getDistanceFromNodeToEdge(nodes, start, end, nodes[i].getPos());
            if (curDistance > maxDistance) {
                maxDistance = curDistance;
                maxDistIndex = i;
            }
        }
        if (maxDistance > range) {
            adjustTube(nodes, outNodes, start, maxDistIndex, range);
            outNodes.add(nodes[maxDistIndex]);
            adjustTube(nodes, outNodes, maxDistIndex, end, range);
        }
    }

    /*private float getLength() {
        float length = 0;
        for (int i = 1; i < nodes.length; i++) {
            length += Coordinates.getDistance(nodes[i - 1].getPos(), nodes[i].getPos());
        }
        return length;
    }*/

    public static int getStreetDrawingSize(WayInfo wayInfo) {
        int basicSize = 10;
        switch (wayInfo.getType()) {
            case OSMType.HIGHWAY_MOTORWAY:
            case OSMType.HIGHWAY_MOTORWAY_JUNCTION:
            case OSMType.HIGHWAY_MOTORWAY_LINK:
                basicSize = 40;
                break;
            case OSMType.HIGHWAY_PRIMARY:
            case OSMType.HIGHWAY_PRIMARY_LINK:
                basicSize = 30;
                break;
            case OSMType.HIGHWAY_SECONDARY:
            case OSMType.HIGHWAY_SECONDARY_LINK:
                basicSize = 22;
                break;
            case OSMType.HIGHWAY_TERTIARY:
            case OSMType.HIGHWAY_TERTIARY_LINK:
                basicSize = 20;
                break;
            case OSMType.HIGHWAY_RESIDENTIAL:
            case OSMType.HIGHWAY_LIVING_STREET:
            case OSMType.HIGHWAY_UNCLASSIFIED:
                basicSize = 18;
                break;
            case OSMType.HIGHWAY_CYCLEWAY:
                basicSize = 15;
                break;
        }
        return basicSize;
    }
    
    public static int getMaxDrawingSize() {
        return 40;
    }

    public int getDrawingSize() {
        return getStreetDrawingSize(wayInfo);
    }

}
