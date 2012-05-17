
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

package kit.ral.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import kit.ral.common.Bounds;
import kit.ral.common.Coordinates;
import kit.ral.common.description.WayInfo;
import kit.ral.common.util.MathUtil;
import kit.ral.controller.State;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class StreetAreaTest {
    
    Area area;
    Street street;
    Street street2;
    
    @BeforeClass
    public static void setUpClass() {
        PropertyConfigurator.configure("config/log4j.conf");
        State.getInstance().resetMap();
    }

    @Before
    public void setUp() throws Exception {
        area = new Area("See", new WayInfo());
        Node[] areaNode = new Node[5];
        areaNode[0] = new Node(new Coordinates(1.0f, 1.0f));
        areaNode[1] = new Node(new Coordinates(5.0f, 1.0f));
        areaNode[2] = new Node(new Coordinates(5.0f, 5.0f));
        areaNode[3] = new Node(new Coordinates(3.0f, 5.0f));
        areaNode[4] = new Node(new Coordinates(1.0f, 5.0f));
        area.setNodes(areaNode);
        street = new Street("Parkstraße", new WayInfo());
        Node[] streetNode = new Node[4];
        streetNode[0] = new Node(new Coordinates(1.0f,1.0f));
        streetNode[1] = new Node(new Coordinates(2.0f,2.0f));
        streetNode[2] = new Node(new Coordinates(3.0f,3.0f));
        streetNode[3] = new Node(new Coordinates(4.0f,4.0f));
        street.setNodes(streetNode);
        street2 = new Street("Rundstraße", new WayInfo());
        Node[] street2Node = new Node[4];
        street2Node[0] = new Node(new Coordinates(1.0f,1.0f));
        street2Node[1] = new Node(new Coordinates(1.0f,4.0f));
        street2Node[2] = new Node(new Coordinates(4.0f,1.0f));
        street2Node[3] = new Node(new Coordinates(4.0f,4.0f));
        street2.setNodes(streetNode);
    }

    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testIsInBounce() {
        assertTrue(MathUtil.isLineInBounds(street.getNodes()[0].getPos(),
                street.getNodes()[2].getPos(), new Bounds(0, 6, 0, 6)));
               
        assertTrue(street.isInBounds(new Bounds(0, 6, 0, 6)));
        assertTrue(street.isInBounds(new Bounds(7, 9, 7, 9)));
        assertFalse(street.isInBounds(new Bounds(70, 90, 70, 90)));
        assertTrue(street.isInBounds(new Bounds(3.5f, 4.5f, 3.5f, 4.5f)));
        assertTrue(street.isInBounds(new Bounds(3.5f, 3.7f, 3.5f, 3.7f)));
        assertTrue(street2.isInBounds(new Bounds(2, 3, 2, 3)));
        assertTrue(area.isInBounds(new Bounds(1, 6, 1, 6)));
        assertFalse(area.isInBounds(new Bounds(7, 9, 7, 9)));
        assertTrue(area.isInBounds(new Bounds(4, 6, 4, 6)));
        assertTrue(area.isInBounds(new Bounds(3, 4, 5, 5)));
        assertTrue(area.isInBounds(new Bounds(2, 3, 2, 3)));
    }
    
    @Test
    public void testReduce() {
        assertEquals(2, ((Street) street.getReduced(5, 2)).getNodes().length);
        assertEquals(4, ((Area) area.getReduced(5, 2)).getNodes().length);
    }
    
    @Test
    public void testEqual() {
        assertTrue(street.equals(new Street("Parkstraße", new WayInfo())));
        assertTrue(area.equals(new Area("See", new WayInfo())));
        assertTrue(0>area.compare(area, new Area("Wiese", new WayInfo())));
    }
}
