
/**
Copyright (c) 2012, Matthias Grundmann, Jan Jacob, Daniel Krauß, Josua Stabenow
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

package kit.ral.map.info;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import org.apache.log4j.Logger;

import kit.ral.common.Bounds;
import kit.ral.common.Coordinates;
import kit.ral.common.RandomReadStream;
import kit.ral.common.RandomWriteStream;
import kit.ral.common.description.POIDescription;
import kit.ral.common.projection.Projection;
import kit.ral.map.info.ElementDB;
import kit.ral.map.MapElement;
import kit.ral.map.Node;
import kit.ral.map.POINode;

public class ArrayElementDB implements ElementDB {

    private static Logger logger = Logger.getLogger(ArrayElementDB.class);   
    
    private ArrayList<Node> nodes = new ArrayList<Node>();   
    private ArrayList<MapElement> mapElements = new ArrayList<MapElement>();    
    private ArrayList<POINode> favorites = new ArrayList<POINode>();
 
    
    // GETTERS
    
    @Override
    public ArrayList<POINode> getFavorites() {
        return favorites;
    }

    @Override
    public Iterator<Node> getAllNodes() {
        return nodes.iterator();
    }

    @Override
    public Iterator<MapElement> getAllMapElements() {
        return mapElements.iterator();
    }

    
    // CONSTRUCTIVE OPERATIONS
    
    @Override
    public void addNode(int nodeID, Node node) {
        if (nodeID != nodes.size()) {
            throw new IllegalArgumentException();
        }
        nodes.add(nodeID, node);
    }
    
    @Override
    public void addMapElement(MapElement element) throws IllegalArgumentException {
        if (element instanceof Node && !(element instanceof POINode)) {
            throw new IllegalArgumentException("Cannot save regular nodes in the map elements database.");
        }
        
        if (mapElements.add(element)) {
            element.setID(mapElements.size() - 1);
        } else {
            logger.error("MapElement could not be added.");
        }
    }
    
    @Override
    public void addFavorite(POINode favorite) {
        favorites.add(favorite);
        favorite.setID(favorites.size() - 1);     
    }
    
    @Override
    public void deleteFavorite(Coordinates pos, int detailLevel, int radius) {
        Bounds bounds = new Bounds(pos, (detailLevel + 1) * 2 * radius);
        for (int i = 0; i < favorites.size(); i++) {
            if(favorites.get(i).isInBounds(bounds)) {
                favorites.remove(i);
            }
        }
    }
    
    
    // QUERY OPERATIONS
    
    @Override
    public Node getNode(int id) {
        if (logger.isTraceEnabled()) {
            logger.trace("Get node " + id);
        }
        if (id < 0 || id >= nodes.size()) { 
            throw new IllegalArgumentException("Illegal Node ID: " + id);
        }
        return nodes.get(id);
    }
    
    @Override
    public MapElement getMapElement(int id) {
        if (logger.isTraceEnabled()) {
            logger.trace("Get element " + id);
        }
        if (id < 0 || id >= mapElements.size()) { 
            throw new IllegalArgumentException("Illegal Map Element ID: " + id);
        }
        MapElement element = mapElements.get(id);
        if (element.getID() != id) {
            logger.error("Not expected id of map element.");
        }
        return element;
    }
   
    @Override
    public POIDescription getFavoriteDescription(Coordinates pos, int detailLevel, float radius) {
        Bounds bounds = new Bounds(pos, (Projection.getZoomFactor(detailLevel) + 1) * radius); 
        for (POINode fav : favorites) {
            if(fav.isInBounds(bounds)) {
                return fav.getInfo();
            }
        }
        return null;
    }
    
    
    // DIRECTIVE OPERATIONS
    
    @Override
    public void swapNodeIDs(int id1, int id2) {  
        nodes.get(id1).setID(id2);
        nodes.get(id2).setID(id1);
        Collections.swap(nodes, id1, id2);
    }
    

    // I/O OPERATIONS
    
    @Override
    public void loadFromInput(DataInput input) throws IOException {
        logger.debug("load node array...");
        input.skipBytes(32); // pointer to index table, nodesCount, elementsCount, favoritesCount
        int nodesCount = input.readInt();
        Node[] nodesArray = new Node[nodesCount];
        for (int i = 0; i < nodesCount; i++) {
            Node node = (Node) MapElement.loadFromInput(input);
            nodesArray[node.getID()] = node;
        }
        nodes = new ArrayList<Node>(Arrays.asList(nodesArray));
        logger.debug("load map element array...");
        int elementsCount = input.readInt();
        mapElements = new ArrayList<MapElement>(elementsCount);
        for (int i = 0; i < elementsCount; i++) {
            MapElement element = MapElement.loadFromInput(input);
            mapElements.add(element);
            element.setID(i);
        }
        logger.debug("load favorite array...");
        int favoritesCount = input.readInt();
        favorites = new ArrayList<POINode>(favoritesCount);
        for (int i = 0; i < favoritesCount; i++) {
            POINode favorite = (POINode) MapElement.loadFromInput(input);
            nodes.add(favorite);
            favorite.setID(i); // TODO: favorite IDs necessary?
        }
        input.skipBytes(nodesCount * 8 + elementsCount * 8);
//        System.out.println(((RandomReadStream) input).getPosition());
    }

    @Override
    public void saveToOutput(DataOutput output) throws IOException {  
        if (!(output instanceof RandomWriteStream)) {
            throw new IllegalArgumentException("Output has to be a RandomWriteStream.");
        }
        RandomWriteStream randomWriteStream = (RandomWriteStream) output;
        
        File nodePosFile = File.createTempFile("nodePositions", ".tmp");
        DataOutputStream nodePosStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(nodePosFile)));
        File elementPosFile = File.createTempFile("elementPositions", ".tmp");
        DataOutputStream elementPosStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(elementPosFile)));
        
        long basePointer = randomWriteStream.getPosition();
        output.writeLong(0);
        output.writeLong(0);
        output.writeLong(0);
        output.writeLong(0);
        logger.info("save node array...");
        long nodesCountPointer = randomWriteStream.getPosition();
        output.writeInt(nodes.size());
        for (Node node: nodes) {
            nodePosStream.writeLong(randomWriteStream.getPosition() - basePointer);
            MapElement.saveToOutput(output, node, false);
        }
        nodePosStream.close();
        logger.info("save map element array...");
        long elementsCountPointer = randomWriteStream.getPosition();
        output.writeInt(mapElements.size());
        for (MapElement element: mapElements) {
            elementPosStream.writeLong(randomWriteStream.getPosition());
            MapElement.saveToOutput(output, element, false);
        }
        elementPosStream.close();
        logger.info("save favorite array...");
        long favoritesCountPointer = randomWriteStream.getPosition();
        output.writeInt(favorites.size());
        for (POINode favorite: favorites) {
            MapElement.saveToOutput(output, favorite, false);
        }
        long indexTablePointer = randomWriteStream.getPosition();
        DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(nodePosFile)));
        byte[] buf = new byte[2048];
        int len = inputStream.read(buf);
        while (len > 0) {
            output.write(buf, 0, len);
            len = inputStream.read(buf);
        }
        inputStream.close();
        inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(elementPosFile)));
        len = inputStream.read(buf);
        while (len > 0) {
            output.write(buf, 0, len);
            len = inputStream.read(buf);
        }
        inputStream.close();
        nodePosFile.delete();
        elementPosFile.delete();
        long endPos = randomWriteStream.getPosition();
        randomWriteStream.setPosition(basePointer);
        output.writeLong(indexTablePointer - basePointer);
        output.writeLong(nodesCountPointer - basePointer);
        output.writeLong(elementsCountPointer - basePointer);
        output.writeLong(favoritesCountPointer - basePointer);
        randomWriteStream.setPosition(endPos);
    }


    // MISCELLANEOUS
    
    public boolean equals(Object other) {
        if(other == this) {
            return true;
        }
        if(!(other instanceof ArrayElementDB)) {
            return false;
        }
        ArrayElementDB arrayElementDB = (ArrayElementDB) other;
        return nodes.equals(arrayElementDB.nodes)
                && mapElements.equals(arrayElementDB.mapElements)
                && favorites.equals(arrayElementDB.favorites);
    }

}
