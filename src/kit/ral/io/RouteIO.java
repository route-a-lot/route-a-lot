
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

package kit.ral.io;

import kit.ral.common.Coordinates;
import kit.ral.common.Selection;
import kit.ral.common.projection.Projection;
import kit.ral.common.projection.ProjectionFactory;
import kit.ral.controller.State;
import kit.ral.map.info.MapInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RouteIO {

    /**
     * Loads the current route (i.e. the navigation node selection) from a file.
     * @param file the route file
     * @throws IOException 
     */
    public static void loadCurrentRoute(File file) throws IOException {
        // Verify requirements
        if (file == null) {
            throw new IllegalArgumentException();
        }
        File mapFile = State.getInstance().getLoadedMapFile();
        if (mapFile == null) {
            throw new IllegalStateException();
        }
        Projection projection = ProjectionFactory.getCurrentProjection();
        MapInfo mapInfo = State.getInstance().getMapInfo();
        
        // read from stream, use either selections or geo coordinates, depending on the current map
        DataInputStream stream = new DataInputStream(new FileInputStream(file));
        boolean isSameMap = stream.readUTF().equals(mapFile.getName());
        int len = stream.readInt();
        ArrayList<Selection> navNodes = new ArrayList<Selection>(len);  
        for (int i = 0; i < len; i++) {
            Coordinates pos = projection.getLocalCoordinates(Coordinates.loadFromInput(stream));
            Selection selection = Selection.loadFromInput(stream);
            navNodes.add((isSameMap) ? selection : mapInfo.select(pos));
        }
        State.getInstance().setNavigationNodes(navNodes);
        stream.close();
    }

    /**
     * Saves the current route (i.e. the navigation node selection) to a file.
     * @param file the route file
     * @throws IOException
     */
    public static void saveCurrentRoute(File file) throws IOException {
        // Verify requirements
        if (file == null) {
            throw new IllegalArgumentException();
        }
        File mapFile = State.getInstance().getLoadedMapFile();
        if (mapFile == null) {
            throw new IllegalStateException();
        }
        Projection projection = ProjectionFactory.getCurrentProjection();
        List<Selection> navNodes = State.getInstance().getNavigationNodes();
        
        // save to stream, save each navnode as selection and as geo coordinates
        DataOutputStream stream = new DataOutputStream(new FileOutputStream(file));
        stream.writeUTF(mapFile.getName());
        stream.writeInt(navNodes.size());
        for (Selection navNode: navNodes) {
            projection.getGeoCoordinates(navNode.getPosition()).saveToOutput(stream);
            navNode.saveToOutput(stream);
        }
        stream.close();
    }

    /**
     * Operation exportCurrentRouteToKML
     * @param file the kml file
     */
    public static void exportCurrentRouteToKML(File file) {
        try {
            State state = State.getInstance();
            List<Integer> route = state.getCurrentRoute();
            List<Selection> navNodes = state.getNavigationNodes();

            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

            document.createProcessingInstruction("version", "1.0");
            document.createProcessingInstruction("encoding", "UTF-8");

            Element kmlElement = document.createElement("kml");
            document.appendChild(kmlElement);
            kmlElement.setAttribute("xmlns", "http://earth.google.com/kml/2.1");
            Element documentElement = document.createElement("Document");
            kmlElement.appendChild(documentElement);
            
            Element styleElement = document.createElement("Style");
            documentElement.appendChild(styleElement);
            styleElement.setAttribute("id", "roadStyle");
            Element lineStyleElement = document.createElement("LineStyle");
            styleElement.appendChild(lineStyleElement);
            Element colorElement = document.createElement("color");
            lineStyleElement.appendChild(colorElement);
            colorElement.appendChild(document.createTextNode("ffffff00"));
            Element widthElement = document.createElement("width");
            lineStyleElement.appendChild(widthElement);
            widthElement.appendChild(document.createTextNode("6"));
            
            Element openElement = document.createElement("Open");
            documentElement.appendChild(openElement);
            openElement.appendChild(document.createTextNode("1"));
            Element descriptionElement = document.createElement("description");
            documentElement.appendChild(descriptionElement);
            descriptionElement.appendChild(document.createTextNode("Route calculated by 'Route A Lot'"));
            Element nameElement = document.createElement("name");
            documentElement.appendChild(nameElement);
            nameElement.appendChild(document.createTextNode("Route"));
            Element placemarkElement = document.createElement("Placemark");
            documentElement.appendChild(placemarkElement);
            nameElement = document.createElement("name");
            placemarkElement.appendChild(nameElement);
            Element styleUrlElement = document.createElement("styleUrl");
            placemarkElement.appendChild(styleUrlElement);
            styleUrlElement.appendChild(document.createTextNode("#roadStyle"));
            Element multiGeometryElement = document.createElement("MultiGeometry");
            placemarkElement.appendChild(multiGeometryElement);
            Element lineStringElement = document.createElement("LineString");
            multiGeometryElement.appendChild(lineStringElement);
            Element coordinatesElement = document.createElement("coordinates");
            lineStringElement.appendChild(coordinatesElement);
            
            StringBuilder coordinatesSB = new StringBuilder();
            Projection projection = ProjectionFactory.getCurrentProjection();
            MapInfo mapInfo = state.getMapInfo();
            for (int i = 0; i < route.size(); i++) {
                if (route.get(i) == -1) {
                    continue;
                }
                Coordinates localCoordinates = mapInfo.getNodePosition(route.get(i));
                Coordinates geoCoordinates = projection.getGeoCoordinates(localCoordinates);
                
                coordinatesSB.append(geoCoordinates.getLongitude() + "," + geoCoordinates.getLatitude());
                if (i % 3 == 2) {
                    coordinatesSB.append("\n");
                } else {
                    coordinatesSB.append(" ");
                }
            }
            coordinatesElement.appendChild(document.createTextNode(coordinatesSB.toString()));
            
            Element folderElement = document.createElement("Folder");
            documentElement.appendChild(folderElement);
            nameElement = document.createElement("name");
            folderElement.appendChild(nameElement);
            nameElement.appendChild(document.createTextNode("waypoints"));
            for (int i = 0; i < navNodes.size(); i++) {
                placemarkElement = document.createElement("Placemark");
                folderElement.appendChild(placemarkElement);
                nameElement = document.createElement("name");
                placemarkElement.appendChild(nameElement);
                String name;
                if (i == 0) {   // TODO i18n
                    name = "Start";
                } else if (i == navNodes.size() - 1) {
                    name = "Ziel";
                } else {
                    name = "Zwischenhalt";
                }
                nameElement.appendChild(document.createTextNode(name));
                Element pointElement = document.createElement("Point");
                placemarkElement.appendChild(pointElement);
                coordinatesElement = document.createElement("coordinates");
                pointElement.appendChild(coordinatesElement);
                Coordinates nodePosition = projection.getGeoCoordinates(navNodes.get(i).getPosition());
                String coordinates = nodePosition.getLongitude() + "," + nodePosition.getLatitude();
                coordinatesElement.appendChild(document.createTextNode(coordinates));
            }
            
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(document);
            transformer.transform(source, result);
            String xmlString = result.getWriter().toString();

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(xmlString);
            writer.flush();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
