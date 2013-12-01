
/**
Copyright (c) 2012, Matthias Grundmann, Jan Jacob, Yvonne Braun, Josua Stabenow
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

package kit.ral.map.rendering;

import kit.ral.common.Bounds;
import kit.ral.common.Context2D;
import kit.ral.common.Coordinates;
import kit.ral.common.description.OSMType;
import kit.ral.common.description.WayInfo;
import kit.ral.map.Node;
import kit.ral.map.Street;
import org.junit.Ignore;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.Assert.assertTrue;

public class RendererPerformanceTest {

    JFrame fenster;
    JPanel panel;
    JLabel label;
    ImageIcon imageB;
    BufferedImage image;
    Container c;
    Graphics g;
    RendererMock renderer;
    MapInfoMock mapInfo;
    int zoomLevel;
    Context2D context;
   
    
    
    @Ignore
    public void test() {
        
        fenster = new JFrame("RendererPerformance");
        panel = new JPanel();
        panel.setSize(300,200);
        c = fenster.getContentPane();
        image = new BufferedImage(300,200,BufferedImage.TYPE_INT_RGB);
        g = image.getGraphics();
        imageB = new ImageIcon(image);
        label = new JLabel(imageB);
        panel.add(label);
        c.add(panel);
        fenster.setSize(300,200);
        fenster.setVisible(true);
        fenster.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        renderer = new RendererMock();
        mapInfo = (MapInfoMock)renderer.state.getMapInfo();
        zoomLevel = 0;
        context = new Context2D(new Bounds(0, 40, 0, 40), zoomLevel, g);
        /*--------------------*/
        WayInfo wayInfo = new WayInfo();
        wayInfo.setStreet(true);
        //wayInfo.setType(OSMType.HIGHWAY_MOTORWAY);
        wayInfo.setType(OSMType.HIGHWAY_SECONDARY);
        Node node1 = new Node(new Coordinates(0.0f, 0.0f));
        Node node2 = new Node(new Coordinates(10.0f, 10.0f));
        Node node3 = new Node(new Coordinates(20.0f, 20.0f));
        Node node4 = new Node(new Coordinates(30.0f, 30.0f));
      
        Street street = new Street("", wayInfo);
        Node[] nodes = new Node[4];
        nodes[0] = node1;
        nodes[1] = node2;
        nodes[2] = node3;
        nodes[3] = node4;
        street.setNodes(nodes);
        mapInfo.addMapElement(street);
        mapInfo.lastElementAdded();
        long start = System.currentTimeMillis();
        renderer.render(context);
        long duration = System.currentTimeMillis() - start;
        assertTrue(duration < 1000);
       
        label.repaint();     
        
    }

}
