package kit.route.a.lot.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.BufferedWriter;
import java.io.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.io.FileWriter;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Projection;
import kit.route.a.lot.common.ProjectionFactory;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.infosupply.MapInfo;

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
        // Open file stream, abort on failure
        DataInputStream stream = new DataInputStream(new FileInputStream(file));
        State state = State.getInstance();
        
        // TODO: add multimap support
        int len = stream.readInt();
        ArrayList<Selection> navNodes = new ArrayList<Selection>(len);  
        for (int i = 0; i < len; i++) {
            navNodes.add(new Selection(stream.readInt(), stream.readInt(),
                    stream.readFloat(), Coordinates.loadFromStream(stream)));
        }
        state.setNavigationNodes(navNodes);
        
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
        // Open file stream, abort on failure
        DataOutputStream stream = new DataOutputStream(new FileOutputStream(file));
        State state = State.getInstance();
        
        List<Selection> navNodes = state.getNavigationNodes();
        stream.writeInt(navNodes.size());
        for (Selection navNode: navNodes) {
            stream.writeInt(navNode.getFrom());
            stream.writeInt(navNode.getTo());
            stream.writeFloat(navNode.getRatio());
            navNode.getPosition().saveToStream(stream);
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

            // TODO test here
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
            MapInfo mapInfo = state.getLoadedMapInfo();
            for (int i = 0; i < route.size(); i++) {
                Coordinates localCoordinates = mapInfo.getNode(route.get(i)).getPos();
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
