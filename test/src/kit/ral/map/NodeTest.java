
/**
Copyright (c) 2012, Malte Wolff, Josua Stabenow
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

import static org.junit.Assert.*;
import kit.ral.common.Bounds;
import kit.ral.common.Coordinates;
import kit.ral.common.description.POIDescription;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class NodeTest {
    
    Node node;
    POINode poiNode;
    
    @BeforeClass
    public static void setUpClass() {
        PropertyConfigurator.configure("config/log4j.conf");
    }

    @Before
    public void setUp() throws Exception {
        node = new Node(new Coordinates(5.0f, 5.0f));
        poiNode = new POINode(new Coordinates(3.0f, 7.0f),
                new POIDescription("homer", 0, "simpson"), null);
        node.setID(-1);
        poiNode.setID(-2);
    }

    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testIsInBounce() {
        assertTrue(node.isInBounds(new Bounds(0, 10, 0, 10)));
        assertTrue(poiNode.isInBounds(new Bounds(0, 10, 0, 10)));
        assertEquals(new POIDescription("homer", 0, "simpson"), poiNode.getInfo());
        assertFalse(node.isInBounds(new Bounds(0, 10, 0, 1)));
        assertFalse(poiNode.isInBounds(new Bounds(0, 1, 0, 9)));
    }
    
    @Test
    public void testGetReduce() {
        assertEquals(node, node.getReduced(1, 0));
        assertEquals(null, node.getReduced(5, 0));
        assertEquals(poiNode, poiNode.getReduced(0, 0));
        assertEquals(null, poiNode.getReduced(10, 0));
    }
    
    @Test
    public void testEquals() {
        assertTrue(node.equals(node));
        assertTrue(node.equals(new Node(new Coordinates(5.0f, 5.0f))));
        assertFalse(node.equals(new Node(new Coordinates(0.3f, 0.4f))));
        assertFalse(node.equals("Hi"));
        assertTrue(poiNode.equals(poiNode));
        assertTrue(poiNode.equals(new POINode(new Coordinates(3.0f,7.0f),
                new POIDescription("homer", 0, "simpson"), null)));
        assertFalse(poiNode.equals(new Node(new Coordinates(10.3f, 10.4f))));
        assertFalse(poiNode.equals("boo"));
    }
    
}
