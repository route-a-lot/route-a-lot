
/**
Copyright (c) 2012, Matthias Grundmann, Malte Wolff, Jan Jacob, Daniel Krauß, Josua Stabenow
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
import kit.ral.common.description.Address;
import kit.ral.common.description.POIDescription;
import kit.ral.common.description.WayInfo;
import kit.ral.map.info.MapInfo;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MapInfoTest {

    public MapInfo info;

    @BeforeClass
    public static void setUpClass() {
        PropertyConfigurator.configure("config/log4j.conf");
    }

    @Before
    public void setUp() throws Exception {
        info = new MapInfo();
        info.setBounds(new Bounds(0, 180, 0, 90));
    }

    @Test
    public void test() {
        info.addNode(new Coordinates(2f, 2f), 0, new Address());
        info.addNode(new Coordinates(10.0f, 10.0f), 1, new Address());
        info.addNode(new Coordinates(4f, 2.5f), 2, new Address());
        info.addNode(new Coordinates(4f, 3f), 3, new Address());
        info.addNode(new Coordinates(2.4f, 1f), 4, new Address());
        info.addNode(new Coordinates(5f, 2f), 5, new Address());
        info.addNode(new Coordinates(0.0f, 0.0f), 6, new Address());
        info.addNode(new Coordinates(10.0f, 11.0f), 7, new Address());
        info.addNode(new Coordinates(11.0f, 10.0f), 8, new Address());
        info.addNode(new Coordinates(11.0f, 11.0f), 9, new Address());
        info.addNode(new Coordinates(10.0f, 12.0f), 10, new Address());
        info.addNode(new Coordinates(12.0f, 20.0f), 11, new Address());

        ArrayList<Integer> ids = new ArrayList<Integer>();
        ids.add(0);
        ids.add(1);
        ids.add(2);
        ids.add(3);
        ids.add(4);
        ArrayList<Integer> aids = new ArrayList<Integer>();
        aids.add(6);
        aids.add(7);
        aids.add(8);
        aids.add(9);
        aids.add(10);
        aids.add(11);

        WayInfo street = new WayInfo();
        street.setAddress(new Address());
        street.setStreet(true);
        info.addWay(ids, "Hubert Straße", street);
        info.addWay(aids, "Fußballplatz", new WayInfo());

        Collection<MapElement> a = info.queryElements(0, new Bounds(1, 5, 5, 1), false);

        assertEquals(1, a.size());
        //POINode favorit1 = new POINode(new Coordinates(10.0f, 15.0f), new POIDescription("derp", 0, "home"));
        //POINode favorit2 = new POINode(new Coordinates(20.0f, 25.0f), new POIDescription("derpina", 0, "home"));
        info.addFavorite(new Coordinates(10.0f, 15.0f), new POIDescription("derp", 0, "home"));
        info.addFavorite(new Coordinates(20.0f, 25.0f), new POIDescription("derpina", 0, "home"));
        info.addFavorite(new Coordinates(0.0f, 0.0f), new POIDescription("foo", 0, "deletable"));
        info.deleteFavorite(new Coordinates(3.0f, 3.0f), 0, 1);
        assertEquals(1, info.queryElements(0, new Bounds(0, 30, 0, 25), true).size());
        info.compactify();
        assertEquals(1, info.queryElements(0, new Bounds(0, 30, 0, 25), true).size());
        assertTrue(info.getPOIDescription(new Coordinates(10.3f, 15.3f), 1, 0).getName().equals("derp"));
        info.printQuadTree();
        info.addPOI(new Coordinates(7.0f, 6.7f), new POIDescription("nirvana", 0, "ja das gibt es wirklich"), new Address());
        info.printQuadTree();
        assertTrue(info.getPOIDescription(new Coordinates(7.0f, 6.7f), 1.0f, 0).getName().equals("nirvana"));
//        assertTrue(info.select(new Coordinates(2.1f, 2.1f)).getName().equals("Hubert Straße"));
//        assertTrue(info.getMapElement(1).getName().equals("Fußballplatz"));
        assertTrue(info.getNodePosition(1).getLatitude() == 10.0f);
        info.swapNodeIds(0, 1);
        assertTrue(info.getNodePosition(1).getLatitude() == 2.0f);
//        assertTrue(info.getBaseLayerForPositionAndRadius(new Coordinates(1.0f, 1.0f), 0.5f, true).size() == 2);
        
        info.setGeoBounds(new Bounds(0, 75, 0.1f, 50));
        assertTrue(info.getGeoBottomRight().getLongitude() == 75.0f);
        assertTrue(info.getGeoTopLeft().getLatitude() == 0.1f);
        info.setBounds(new Bounds(1, 45.6f, 0, 12.3f));
        Bounds bounds = new Bounds(1, 45.6f, 0, 12.3f);
        bounds = info.getBounds();
        assertTrue(bounds.getLeft() == 1.0f);
    }
}
