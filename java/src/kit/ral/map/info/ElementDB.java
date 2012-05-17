
/**
Copyright (c) 2012, Daniel Krauß, Matthias Grundmann, Jan Jacob, Josua Stabenow
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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import kit.ral.common.Coordinates;
import kit.ral.common.description.POIDescription;
import kit.ral.map.Node;
import kit.ral.map.MapElement;
import kit.ral.map.POINode;

public interface ElementDB {

    
    // GETTERS
    
    public ArrayList<POINode> getFavorites();
    
    public Iterator<Node> getAllNodes();

    public Iterator<MapElement> getAllMapElements();  
    
    
    // CONSTRUCTIVE OPERATIONS
    
    /**
     * Adds a node to the elementDB.
     * @param nodeID the ID of the node
     * @param node the node
     */
    public void addNode(int nodeID, Node node);

    /**
     * Adds a element to the elementDB and gives it a unique ID.
     * @param element the element
     */
    public void addMapElement(MapElement element);    
    
    /**
     * Adds a favorite to the elementDB.
     * @param favorite the favorite which should be added
     */
    public void addFavorite(POINode favorite);    
    
    /**
     * Deletes the favorite with the given ID from the elementID.
     * @param id the ID of the favorite
     */
    public void deleteFavorite(Coordinates pos, int detailLevel, int radius); 
    
    
    // DIRECTIVE OPERATIONS
    
    public void swapNodeIDs(int id1, int id2);
    
    
    // QUERY OPERATIONS
    
    /**
     * Returns the Node with the given id.
     * @param nodeID the ID of the node
     * @return the node which relies to the given id
     */
    public Node getNode(int nodeID);

    /**
     * Returns the element with the given ID  
     * @param id the given ID
     * @return the element which relies to the given ID
     */
    public MapElement getMapElement(int id);

    public POIDescription getFavoriteDescription(Coordinates pos, int detailLevel, float radius);
    
    
    // I/O OPERATIONS
    
    /**
     * Loads all elements from the given stream to the id store. 
     * @param stream the source stream
     * @throws IOException a stream read error occurred
     */
    public void loadFromInput(DataInput input) throws IOException;

    /**
     * Saves all id stored elements to the given stream. 
     * @param stream the destination stream
     * @throws IOException a stream write error occurred
     */
    public void saveToOutput(DataOutput output) throws IOException;
    
    
    // MISCELLANEOUS

    public boolean equals (Object o);

}
