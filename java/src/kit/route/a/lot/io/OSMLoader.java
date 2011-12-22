package kit.route.a.lot.io;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.WeightCalculator;
import kit.route.a.lot.controller.State;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class OSMLoader {
    
    private static Logger logger = Logger.getLogger(OSMLoader.class);
    
    static ArrayList<Integer> startIds = new ArrayList<Integer>();
    static ArrayList<Integer> endIds = new ArrayList<Integer>();
    
    public static final int HIGHWAY_UNCLASSIFIED = 1;
    public static final int HIGHWAY_CYCLEWAY = 2;

    /**
     * Operation importMap lok. Variable idMapping: Map<int, int>
     * 
     * @param file
     *            -
     * @return
     * @return
     */
    public static void importMap(File file) {
        
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            
            DefaultHandler handler = new DefaultHandler() {
                
                Map<Integer, Integer> idMap = new HashMap<Integer, Integer>();
                // ToDo lohnt es sich, noch eine ArrayList mitzuführen?
                
                boolean inWay = false;
                boolean inPolyline = false;
                int curPolylineNode;
                List<Integer> curWayIds;
                String curWayName;
                int curWayType;
                
                public void startElement(String uri, String localName, String qName, Attributes attributes)
                    throws SAXException {
                    
                    if (inWay) {
                        if (inPolyline) {
                            if (qName.equalsIgnoreCase("nd")) {
                                int newPolylineNode = idMap.get(Integer.parseInt(attributes.getValue("ref")));
                                startIds.add(curPolylineNode);
                                endIds.add(newPolylineNode);
                                curPolylineNode = newPolylineNode;
                                curWayIds.add(curPolylineNode);
                            } else if (qName.equalsIgnoreCase("tag")) {
                                String key = attributes.getValue("k");
                                if (key.equalsIgnoreCase("name")) {
                                    curWayName = attributes.getValue("v");
                                } else if (key.equalsIgnoreCase("highway")) {
                                    String value = attributes.getValue("v");
                                    if (value.equalsIgnoreCase("unclassified")) {
                                        curWayType = HIGHWAY_UNCLASSIFIED;
                                    } else if (value.equalsIgnoreCase("cycleway")) {
                                        curWayType = HIGHWAY_CYCLEWAY;
                                    } else {
                                        logger.debug("Highway type ignored: " + value);
                                    }
                                }
                            }
                        } else {
                            if (qName.equalsIgnoreCase("nd")) {
                                curPolylineNode = idMap.get(Integer.parseInt(attributes.getValue("ref")));
                                inPolyline = true;
                                curWayIds.add(curPolylineNode);
                            }
                        }
                    } else {
                        if (qName.equalsIgnoreCase("osm")) {
                            String version = attributes.getValue("version");
                            if (!version.equals("0.6")) {
                                logger.warn("OSM-Version is " + version);
                            }
                        } else if (qName.equalsIgnoreCase("bounds")) {
                            Coordinates upLeft = new Coordinates();
                            Coordinates bottomDown = new Coordinates();;
                            
                            upLeft.setLatitude(Float.parseFloat(attributes.getValue("minlat")));
                            upLeft.setLongitude(Float.parseFloat(attributes.getValue("minlon")));
                            
                            bottomDown.setLatitude(Float.parseFloat(attributes.getValue("minlat")));
                            bottomDown.setLongitude(Float.parseFloat(attributes.getValue("minlon")));
                            
                        } else if (qName.equalsIgnoreCase("node")) {
                            Coordinates coordinates = new Coordinates();
                            
                            coordinates.setLatitude(Float.parseFloat(attributes.getValue("lat")));
                            coordinates.setLongitude(Float.parseFloat(attributes.getValue("lon")));
                            
                            int newId = idMap.size();
                            idMap.put(Integer.parseInt(attributes.getValue("id")), newId);
                            
                            State.getInstance().getLoadedMapInfo().addNode(coordinates, newId);
                            
                            // ToDo tags
                        } else if (qName.equalsIgnoreCase("way")) {
                            inWay = true;
                        } else if (qName.equalsIgnoreCase("relation")) {
                            // ToDo
                        }
                    }
                }
                    
                public void endElement(String uri, String localName, String qName)
                        throws SAXException {
                    if (inWay) {
                        inWay = false;
                        inPolyline = false;
                        
                        State.getInstance().getLoadedMapInfo().addWay(curWayIds, curWayName, curWayType);
                    }
                }
                
                public void characters(char ch[], int start, int length) throws SAXException {
                    
                }
                
            };

            parser.parse(file, handler);
            
            int countIDs = startIds.size();
            int[] startIDs = new int[countIDs];
            int[] endIDs = new int[countIDs];
            int[] weights = new int[countIDs];
            for (int i = 0; i < countIDs; i++) {
                startIDs[i] = startIds.get(i);
                endIDs[i] = endIds.get(i);
                weights[i] = WeightCalculator.calcWeight(startIDs[i], endIDs[i]);
            }
            
            State.getInstance().getLoadedGraph().buildGraph(startIDs, endIDs, weights);
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
    }
}
