
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

package kit.ral.map.info;

import kit.ral.common.Coordinates;
import kit.ral.common.description.POIDescription;
import kit.ral.controller.State;
import kit.ral.map.MapElement;
import kit.ral.map.Node;
import kit.ral.map.POINode;
import kit.ral.map.rendering.MapElementGenerator;
import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class TestElementDB {
    
    ElementDB elements;
    
    @BeforeClass
    public static void setUpClass() {
        State.getInstance().resetMap();
        PropertyConfigurator.configure("config/log4j.conf");
    }

    @Before
    public void setUp() throws Exception {
        elements = new ArrayElementDB();
        for(int i = 0; i < 10; i++) {
            Node node = new Node(new Coordinates(0.0f + i, 0.0f +i));
            MapElement element1 = new MapElementGenerator().generateStreet();
            MapElement element2 = new MapElementGenerator().generateArea();
            String s = "" + i;
            POINode favorite = new POINode(new Coordinates(0.0f + i, 0.0f + i),
                    new POIDescription(s, 0 ,s), null);
            elements.addFavorite(favorite);
            elements.addMapElement(element1);
            elements.addMapElement(element2);
            elements.addNode(i, node);
        }
    }

    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testAdGet() {
        assertTrue(elements.getNode(1).getPos().getLatitude() == 1.0f && elements.getNode(1).getPos().getLongitude() == 1.0f);
        assertEquals(1,elements.getFavorites().get(1).getID());
        assertEquals(6 ,elements.getMapElement(6).getID());
        assertEquals(5 ,elements.getMapElement(5).getID());
        elements.deleteFavorite(new Coordinates(3.0f, 3.0f), 0, 0);
        assertEquals(5, elements.getFavorites().get(5).getID());
    }
    
    @Test
    public void testSwapFavDescription() {
        elements.swapNodeIDs(5, 3);
        assertTrue(5.0f == elements.getNode(3).getPos().getLatitude());
        String s = "" + 3;
        assertTrue(s.equals(elements.getFavoriteDescription(new Coordinates(3.2f, 3.2f), 0, 1).getDescription()));
    }
    
}
