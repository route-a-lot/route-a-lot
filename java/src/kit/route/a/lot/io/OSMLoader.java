package kit.route.a.lot.io;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import kit.route.a.lot.common.Address;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.WayInfo;
import kit.route.a.lot.common.WeightCalculator;
import kit.route.a.lot.controller.State;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class OSMLoader {
    
    private static Logger logger = Logger.getLogger(OSMLoader.class);
    
    private ArrayList<Integer> startIds;
    private ArrayList<Integer> endIds;
    
    
    State state;
    WeightCalculator weightCalculator;
    
    public OSMLoader() {
        state = State.getInstance();
        weightCalculator = WeightCalculator.getInstance();
        startIds = new ArrayList<Integer>();
        endIds = new ArrayList<Integer>();
    }

    /**
     * 
     * @param file
     *            -
     * @return
     * @return
     */
    public void importMap(File file) {
        
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            
            DefaultHandler handler = new DefaultHandler() {
                
                Map<Long, Integer> idMap = new HashMap<Long, Integer>(); // key is an OSM-id and value is the new id
                
                boolean inWay;
                boolean inPolyline;
                Integer curPolylineNode;
                List<Integer> curWayIds;
                String curWayName;
                WayInfo curWayInfo;
                Address curAddress;
                
                long ignoredKeys = 0;
                
                public void startElement(String uri, String localName, String qName, Attributes attributes)
                    throws SAXException {
                    
                    if (inWay) {
                        if (inPolyline) {
                            if (qName.equalsIgnoreCase("nd")) {
                                Integer newPolylineNode = idMap.get(Long.parseLong(attributes.getValue("ref")));
                                if (newPolylineNode == null) {
                                    logger.error("Node id is not known: id = " + attributes.getValue("ref"));
                                }
                                startIds.add(curPolylineNode);
                                endIds.add(newPolylineNode);
                                curPolylineNode = newPolylineNode;
                                curWayIds.add(curPolylineNode);
                            } else if (qName.equalsIgnoreCase("tag")) {
                                String key = attributes.getValue("k");
                                String value = attributes.getValue("v");
                                if (key.equalsIgnoreCase("name")) {
                                    curWayName = value;
                                } else if (key.equalsIgnoreCase("highway")) {
                                    if (value.equalsIgnoreCase("bridleway")) {
                                        curWayInfo.setType(WayInfo.HIGHWAY_BRIDLEWAY);
                                    } else if (value.equalsIgnoreCase("crossing")) {
                                        curWayInfo.setType(WayInfo.HIGHWAY_CROSSING);
                                    } else if (value.equalsIgnoreCase("cycleway")) {
                                        curWayInfo.setType(WayInfo.HIGHWAY_CYCLEWAY);
                                    } else if (value.equalsIgnoreCase("footway")) {
                                        curWayInfo.setType(WayInfo.HIGHWAY_FOOTWAY);
                                    } else if (value.equalsIgnoreCase("living_street")) {
                                        curWayInfo.setType(WayInfo.HIGHWAY_LIVING_STREET);
                                    } else if (value.equalsIgnoreCase("motorway")) {
                                        curWayInfo.setType(WayInfo.HIGHWAY_MOTORWAY);
                                    } else if (value.equalsIgnoreCase("motorway_link")) {
                                        curWayInfo.setType(WayInfo.HIGHWAY_MOTORWAY_LINK);
                                    } else if (value.equalsIgnoreCase("path")) {
                                        curWayInfo.setType(WayInfo.HIGHWAY_PATH);
                                    } else if (value.equalsIgnoreCase("pedestrian")) {
                                        curWayInfo.setType(WayInfo.HIGHWAY_PEDESTRIAN);
                                    } else if (value.equalsIgnoreCase("primary")) {
                                        curWayInfo.setType(WayInfo.HIGHWAY_PRIMARY);
                                    } else if (value.equalsIgnoreCase("primary_link")) {
                                        curWayInfo.setType(WayInfo.HIGHWAY_PRIMARY_LINK);
                                    } else if (value.equalsIgnoreCase("residential")) {
                                        curWayInfo.setType(WayInfo.HIGHWAY_RESIDENTIAL);
                                    } else if (value.equalsIgnoreCase("secondary")) {
                                        curWayInfo.setType(WayInfo.HIGHWAY_SECONDARY);
                                    } else if (value.equalsIgnoreCase("secondary_link")) {
                                        curWayInfo.setType(WayInfo.HIGHWAY_SECONDARY_LINK);
                                    } else if (value.equalsIgnoreCase("service")) {
                                        curWayInfo.setType(WayInfo.HIGHWAY_SERVICE);
                                    } else if (value.equalsIgnoreCase("steps")) {
                                        curWayInfo.setType(WayInfo.HIGHWAY_STEPS);
                                    } else if (value.equalsIgnoreCase("steps_large")) {
                                        curWayInfo.setType(WayInfo.HIGHWAY_STEPS_LARGE);
                                    } else if (value.equalsIgnoreCase("tertiary")) {
                                        curWayInfo.setType(WayInfo.HIGHWAY_TERTIARY);
                                    } else if (value.equalsIgnoreCase("track")) {
                                        curWayInfo.setType(WayInfo.HIGHWAY_TRACK);
                                    } else if (value.equalsIgnoreCase("trunk")) {
                                        curWayInfo.setType(WayInfo.HIGHWAY_TRUNK);
                                    } else if (value.equalsIgnoreCase("trunk_link")) {
                                        curWayInfo.setType(WayInfo.HIGHWAY_TRUNK_LINK);
                                    } else if (value.equalsIgnoreCase("unclassified")) {
                                        curWayInfo.setType(WayInfo.HIGHWAY_UNCLASSIFIED);
                                    } else {
                                        curWayInfo.setType(WayInfo.HIGHWAY_IGNORED);
                                        logger.debug("Highway type ignored: " + value);
                                    }
                                } else if (key.equalsIgnoreCase("bicycle")) {
                                    if (value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("designated")
                                            || value.equalsIgnoreCase("permissive")) {
                                        curWayInfo.setBicycle(WayInfo.BICYCLE);
                                    } else if (value.equalsIgnoreCase("no") || value.equalsIgnoreCase("private")) {
                                        curWayInfo.setBicycle(WayInfo.NO_BICYCLE);
                                    } else {
                                        logger.warn("Unknown value for " + key + " key in tags: " + value);
                                    }
                                } else if (key.equalsIgnoreCase("oneway")) {
                                    if (value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("true")) {
                                        curWayInfo.setOneway(WayInfo.ONEWAY);
                                    } else if (value.equalsIgnoreCase("no") || value.equalsIgnoreCase("false")) {
                                        curWayInfo.setOneway(WayInfo.NO_ONEWAY);
                                    } else if (value.equalsIgnoreCase("-1")) {
                                        curWayInfo.setOneway(WayInfo.ONEWAY_OPPOSITE);
                                    } else {
                                        logger.warn("Unknown value for " + key + " key in tags: " + value);
                                    }
                                } else if (key.equalsIgnoreCase("amenity")) {
                                    if (value.equalsIgnoreCase("cafe")) {
                                        curWayInfo.setType(WayInfo.AMENITY_CAFE);
                                    } else if (value.equalsIgnoreCase("car_sharing")) {
                                        curWayInfo.setType(WayInfo.AMENITY_CAR_SHARING);
                                    } else if (value.equalsIgnoreCase("courthouse")) {
                                        curWayInfo.setType(WayInfo.AMENITY_COURTHOUSE);
                                    } else if (value.equalsIgnoreCase("fountain")) {
                                        curWayInfo.setType(WayInfo.AMENITY_FOUNTAIN);
                                    } else if (value.equalsIgnoreCase("hospital")) {
                                        curWayInfo.setType(WayInfo.AMENITY_HOSPITAL);
                                    } else if (value.equalsIgnoreCase("kindergarten")) {
                                        curWayInfo.setType(WayInfo.AMENITY_KINDERGARTEN);
                                    } else if (value.equalsIgnoreCase("library")) {
                                        curWayInfo.setType(WayInfo.AMENITY_LIBRARY);
                                    } else if (value.equalsIgnoreCase("parking")) {
                                        curWayInfo.setType(WayInfo.AMENITY_PARKING);
                                    } else if (value.equalsIgnoreCase("place_of_worship")) {
                                        curWayInfo.setType(WayInfo.AMENITY_PLACE_OF_WORSHIP);
                                    } else if (value.equalsIgnoreCase("police")) {
                                        curWayInfo.setType(WayInfo.AMENITY_POLICE);
                                    } else if (value.equalsIgnoreCase("public_building")) {
                                        curWayInfo.setType(WayInfo.AMENITY_PUBLIC_BUILDING);
                                    } else if (value.equalsIgnoreCase("restaurant")) {
                                        curWayInfo.setType(WayInfo.AMENITY_RESTAURANT);
                                    } else if (value.equalsIgnoreCase("school")) {
                                        curWayInfo.setType(WayInfo.AMENITY_SCHOOL);
                                    } else if (value.equalsIgnoreCase("theatre")) {
                                        curWayInfo.setType(WayInfo.AMENITY_THEATRE);
                                    } else if (value.equalsIgnoreCase("university")) {
                                        curWayInfo.setType(WayInfo.AMENITY_UNIVERSITY);
                                    } else {
                                        logger.warn("Unknown value for " + key + " key in tags: " + value);
                                    }
                                } else if (key.equalsIgnoreCase("leisure")) {
                                    if (value.equalsIgnoreCase("garden")) {
                                        curWayInfo.setType(WayInfo.LEISURE_GARDEN);
                                    } else if (value.equalsIgnoreCase("pitch")) {
                                        curWayInfo.setType(WayInfo.LEISURE_PITCH);
                                    } else if (value.equalsIgnoreCase("park")) {
                                        curWayInfo.setType(WayInfo.LEISURE_PARK);
                                    } else if (value.equalsIgnoreCase("playground")) {
                                        curWayInfo.setType(WayInfo.LEISURE_PLAYGROUND);
                                    } else if (value.equalsIgnoreCase("sports_centre")) {
                                        curWayInfo.setType(WayInfo.LEISURE_SPORTS_CENTRE);
                                    } else if (value.equalsIgnoreCase("stadium")) {
                                        curWayInfo.setType(WayInfo.LEISURE_STADIUM);
                                    } else if (value.equalsIgnoreCase("track")) {
                                        curWayInfo.setType(WayInfo.LEISURE_TRACK);
                                    } else {
                                        logger.warn("Unknown value for " + key + " key in tags: " + value);
                                    }
                                } else if (key.equalsIgnoreCase("natural")) {
                                    if (value.equalsIgnoreCase("bay")) {
                                        curWayInfo.setType(WayInfo.NATURAL_BAY);
                                    } else if (value.equalsIgnoreCase("beach")) {
                                        curWayInfo.setType(WayInfo.NATURAL_BEACH);
                                    } else if (value.equalsIgnoreCase("cliff")) {
                                        curWayInfo.setType(WayInfo.NATURAL_CLIFF);
                                    } else if (value.equalsIgnoreCase("coastline")) {
                                        curWayInfo.setType(WayInfo.NATURAL_COASTLINE);
                                    } else if (value.equalsIgnoreCase("glacier")) {
                                        curWayInfo.setType(WayInfo.NATURAL_GLACIER);
                                    } else if (value.equalsIgnoreCase("heath")) {
                                        curWayInfo.setType(WayInfo.NATURAL_HEATH);
                                    } else if (value.equalsIgnoreCase("land")) {
                                        curWayInfo.setType(WayInfo.NATURAL_LAND);
                                    } else if (value.equalsIgnoreCase("marsh")) {
                                        curWayInfo.setType(WayInfo.NATURAL_MARSH);
                                    } else if (value.equalsIgnoreCase("peak")) {
                                        curWayInfo.setType(WayInfo.NATURAL_PEAK);
                                    } else if (value.equalsIgnoreCase("sand")) {
                                        curWayInfo.setType(WayInfo.NATURAL_SAND);
                                    } else if (value.equalsIgnoreCase("scrub")) {
                                        curWayInfo.setType(WayInfo.NATURAL_SCRUB);
                                    } else if (value.equalsIgnoreCase("spring")) {
                                        curWayInfo.setType(WayInfo.NATURAL_SPRING);
                                    } else if (value.equalsIgnoreCase("stone")) {
                                        curWayInfo.setType(WayInfo.NATURAL_STONE);
                                    } else if (value.equalsIgnoreCase("tree")) {
                                        curWayInfo.setType(WayInfo.NATURAL_TREE);
                                    } else if (value.equalsIgnoreCase("volcano")) {
                                        curWayInfo.setType(WayInfo.NATURAL_VOLCANO);
                                    } else if (value.equalsIgnoreCase("water")) {
                                        curWayInfo.setType(WayInfo.NATURAL_WATER);
                                    } else if (value.equalsIgnoreCase("wetland")) {
                                        curWayInfo.setType(WayInfo.NATURAL_WETLAND);
                                    } else if (value.equalsIgnoreCase("wood")) {
                                        curWayInfo.setType(WayInfo.NATURAL_WOOD);
                                    } else {
                                        logger.warn("Unknown value for " + key + " key in tags: " + value);
                                    }
                                } else if (key.equalsIgnoreCase("waterway")) {
                                    if (value.equalsIgnoreCase("canal")) {
                                        curWayInfo.setType(WayInfo.WATERWAY_CANAL);
                                    } else if (value.equalsIgnoreCase("dam")) {
                                        curWayInfo.setType(WayInfo.WATERWAY_DAM);
                                    } else if (value.equalsIgnoreCase("ditch")) {
                                        curWayInfo.setType(WayInfo.WATERWAY_DITCH);
                                    } else if (value.equalsIgnoreCase("dock")) {
                                        curWayInfo.setType(WayInfo.WATERWAY_DOCK);
                                    } else if (value.equalsIgnoreCase("drain")) {
                                        curWayInfo.setType(WayInfo.WATERWAY_DRAIN);
                                    } else if (value.equalsIgnoreCase("river")) {
                                        curWayInfo.setType(WayInfo.WATERWAY_RIVER);
                                    } else if (value.equalsIgnoreCase("riverbank")) {
                                        curWayInfo.setType(WayInfo.WATERWAY_RIVERBANK);
                                    } else if (value.equalsIgnoreCase("stream")) {
                                        curWayInfo.setType(WayInfo.WATERWAY_STREAM);
                                    } else if (value.equalsIgnoreCase("waterfall")) {
                                        curWayInfo.setType(WayInfo.WATERWAY_WATERFALL);
                                    } else {
                                        logger.warn("Unknown value for " + key + " key in tags: " + value);
                                    }
                                } else if (key.equalsIgnoreCase("cycleway")) {
                                    if (value.equalsIgnoreCase("track")) {
                                        curWayInfo.setType(WayInfo.CYCLEWAY_TRACK);
                                    } else if (value.equalsIgnoreCase("lane")) {
                                        curWayInfo.setType(WayInfo.CYCLEWAY_LANE);
                                    } else if (value.equalsIgnoreCase("opposite")) {
                                        curWayInfo.setType(WayInfo.CYCLEWAY_OPPOSITE);
                                    } else if (value.equalsIgnoreCase("opposite_lane")) {
                                        curWayInfo.setType(WayInfo.CYCLEWAY_OPPOSITE_LANE);
                                    } else if (value.equalsIgnoreCase("opposite_track")) {
                                        curWayInfo.setType(WayInfo.CYCLEWAY_OPPOSITE_TRACK);
                                    } else {
                                        logger.warn("Unknown value for " + key + " key in tags: " + value);
                                    }
                                } else if (key.equalsIgnoreCase("railway")) {
                                    if (value.equalsIgnoreCase("rail")) {
                                        curWayInfo.setOther(true);
                                        curWayInfo.setType(WayInfo.RAILWAY_RAIL);
                                    } else if (value.equalsIgnoreCase("subway")) {
                                        curWayInfo.setOther(true);
                                        curWayInfo.setType(WayInfo.RAILWAY_SUBWAY);
                                    } else if (value.equalsIgnoreCase("tram")) {
                                        curWayInfo.setOther(true);
                                        curWayInfo.setType(WayInfo.RAILWAY_TRAM);
                                    } else {
                                        logger.warn("Unknown value for " + key + " key in tags: " + value);
                                    }
                                } else if (key.startsWith("addr:")) {
                                    if (key.equalsIgnoreCase("addr:housenumber") || key.equalsIgnoreCase("addr:housename")) {
                                        curAddress.setHousenumber(value);
                                    } else if (key.equalsIgnoreCase("addr:street")) {
                                        curAddress.setStreet(value);
                                    } else if (key.equalsIgnoreCase("addr:state")) {
                                        curAddress.setState(value);
                                    } else if (key.equalsIgnoreCase("addr:postcode")) {
                                        curAddress.setPostcode(value);
                                    } else if (key.equalsIgnoreCase("addr:city")) {
                                        curAddress.setCity(value);
                                    } else if (key.equalsIgnoreCase("addr:country")) {
                                        curAddress.setCountry(value);
                                    } else if (key.equalsIgnoreCase("addr:full")) {
                                        curAddress.setFullAddress(value);
                                    } else if (key.equalsIgnoreCase("addr:interpolation")) {
                                        curAddress.setInterpolation(value);
                                    } else {
                                        logger.warn("Unknown addr:* tag: " + key + ", value: " + value);
                                    }
                                } else if (key.equalsIgnoreCase("building")) {
                                    curWayInfo.setBuilding(true); // TODO here and with the following: check if value == yes or something more specific
                                } else if (key.equalsIgnoreCase("bridge")) {
                                    curWayInfo.setBridge(WayInfo.BRIDGE);
                                } else if (key.equalsIgnoreCase("tunnel")) {
                                    curWayInfo.setTunnel(WayInfo.TUNNEL);
                                } else if (key.equalsIgnoreCase("area")) {
                                    if (value.equalsIgnoreCase("yes")) {
                                        curWayInfo.setArea(true);
                                    } else {
                                        logger.warn("Unknown value for " + key + " key in tags: " + value);
                                    }
                                } else if (key.equalsIgnoreCase("barrier")) {
                                    curWayInfo.setOther(true);
                                } else if (key.equalsIgnoreCase("access")) {
                                    if (value.equalsIgnoreCase("private")) {
                                         curWayInfo.setAccess(WayInfo.ACCESS_PRIVATE);
                                    } else if (value.equalsIgnoreCase("permissive")) {
                                        curWayInfo.setAccess(WayInfo.ACCESS_PERMISSIVE);
                                    } else if (value.equalsIgnoreCase("destination")) {
                                        curWayInfo.setAccess(WayInfo.ACCESS_DESTINATION);
                                    } else {
                                        logger.warn("Unknown value for " + key + " key in tags: " + value);
                                    }
                                } else if (key.equalsIgnoreCase("surface")) {
                                   if (value.equalsIgnoreCase("paved")) {
                                        curWayInfo.setSurface(WayInfo.SURFACE_PAVED);
                                   } else if (value.equalsIgnoreCase("unpaved")) {
                                       curWayInfo.setAccess(WayInfo.SURFACE_UNPAVED);
                                   } else if (value.equalsIgnoreCase("asphalt")) {
                                       curWayInfo.setAccess(WayInfo.SURFACE_ASPHALT);
                                   } else if (value.equalsIgnoreCase("gravel")) {
                                       curWayInfo.setAccess(WayInfo.SURFACE_GRAVEL);
                                   } else if (value.equalsIgnoreCase("ground")) {
                                       curWayInfo.setAccess(WayInfo.SURFACE_GROUND);
                                   } else if (value.equalsIgnoreCase("grass")) {
                                       curWayInfo.setAccess(WayInfo.SURFACE_GRASS);
                                   } else if (value.equalsIgnoreCase("dirt")) {
                                       curWayInfo.setAccess(WayInfo.SURFACE_DIRT);
                                   } else if (value.equalsIgnoreCase("cobblestone")) {
                                       curWayInfo.setAccess(WayInfo.SURFACE_COBBLESTONE);
                                   } else if (value.equalsIgnoreCase("paving_stones")) {
                                       curWayInfo.setAccess(WayInfo.SURFACE_PAVING_STONES);
                                   } else if (value.equalsIgnoreCase("concrete")) {
                                       curWayInfo.setAccess(WayInfo.SURFACE_CONCRETE);
                                   } else if (value.equalsIgnoreCase("sand")) {
                                       curWayInfo.setAccess(WayInfo.SURFACE_SAND);
                                   } else {
                                       logger.warn("Unknown value for " + key + " key in tags: " + value);
                                   }
                               } else if (key.equalsIgnoreCase("segregated")) {
                                    if (value.equalsIgnoreCase("yes")) {
                                        curWayInfo.setSegregated(WayInfo.SEGREGATED);
                                   } else if (value.equalsIgnoreCase("no")) {
                                       curWayInfo.setSegregated(WayInfo.NO_SEGREGATED);
                                   } else {
                                       logger.warn("Unknown value for " + key + " key in tags: " + value);
                                   }
                               } else if (key.equalsIgnoreCase("postal_code")) {
                                    curAddress.setPostcode(value);
                               } else if (key.equalsIgnoreCase("layer")) {
                                    curWayInfo.setLayer(Integer.parseInt(value));
                               } else if (key.equalsIgnoreCase("lanes")) {
                                   curWayInfo.setLanes(Integer.parseInt(value));
                              } else if (key.equalsIgnoreCase("note") || key.equalsIgnoreCase("maxspeed")
                                        || key.equalsIgnoreCase("created_by")
                                        || key.equalsIgnoreCase("foot")
                                        || key.equalsIgnoreCase("source")
                                        || key.equalsIgnoreCase("opening_date")
                                        || key.equalsIgnoreCase("landuse") /* TODO really ignore that? */
                                        || key.startsWith("building:") /* " */
                                        || key.equalsIgnoreCase("ref")
                                        || key.equalsIgnoreCase("planned")
                                        || key.equalsIgnoreCase("construction")) {
                                    // ignore
                                } else {
                                    ignoredKeys++;
                                    logger.debug("Key ignored: " + key + ", value = " + value + " : " + ignoredKeys);
                                }
                            } else {
                                logger.warn("Element ignored in polyline: " + qName);
                            }
                        } else {
                            if (qName.equalsIgnoreCase("nd")) {
                                curPolylineNode = idMap.get(Long.parseLong(attributes.getValue("ref")));
                                if (curPolylineNode == null) {
                                    logger.error("Node id is not known: id = " + attributes.getValue("ref"));
                                }
                                inPolyline = true;
                                curWayIds.add(curPolylineNode);
                            }
                        }
                    } else {
                        if (qName.equalsIgnoreCase("node")) {
                            Coordinates coordinates = new Coordinates();
                            
                            coordinates.setLatitude(Float.parseFloat(attributes.getValue("lat")));
                            coordinates.setLongitude(Float.parseFloat(attributes.getValue("lon")));
                            
                            int newId = idMap.size();
                            if (newId >= Integer.MAX_VALUE) {
                                logger.error("Tried to import more than " + newId + " nodes!");
                                // TODO throw exception
                            }
                            idMap.put(Long.parseLong(attributes.getValue("id")), newId);
                            
                            // TODO curAddress = new Address();
                            
                            state.getLoadedMapInfo().addNode(coordinates, newId, curAddress);
                            
                            // TODO tags (for POI's)
                        } else if (qName.equalsIgnoreCase("way")) {
                            inWay = true;
                            curWayIds = new ArrayList<Integer>();
                            curWayInfo = new WayInfo();
                            curAddress = new Address();
                        } else if (qName.equalsIgnoreCase("relation")) {
                            // TODO
                        } else if (qName.equalsIgnoreCase("bounds")) {
                            Coordinates upLeft = new Coordinates();
                            Coordinates bottomRight = new Coordinates();;
                            
                            upLeft.setLatitude(Float.parseFloat(attributes.getValue("minlat")));
                            upLeft.setLongitude(Float.parseFloat(attributes.getValue("minlon")));
                            
                            bottomRight.setLatitude(Float.parseFloat(attributes.getValue("minlat")));
                            bottomRight.setLongitude(Float.parseFloat(attributes.getValue("minlon")));
                            
                            state.getLoadedMapInfo().setBounds(upLeft, bottomRight);
                            
                        } else if (qName.equalsIgnoreCase("osm")) {
                            String version = attributes.getValue("version");
                            if (!version.equals("0.6")) {
                                logger.warn("OSM-Version is " + version);
                            }
                        } else {
                            logger.trace("Element start ignored: " + qName);
                        }
                    }
                }
                    
                public void endElement(String uri, String localName, String qName)
                        throws SAXException {
                    if (inWay && qName.equalsIgnoreCase("way")) {
                        
                        curWayInfo.setAddress(curAddress);
                        if (curWayInfo.isStreet()) {
                            curAddress.setStreet(curWayName);
                        }
                        if (curWayInfo.isStreet() || curWayInfo.isArea() || curWayInfo.isBuilding()) {
                            state.getLoadedMapInfo().addWay(curWayIds, curWayName, curWayInfo);
                        }
                        
                        inWay = false;
                        inPolyline = false;
                        curWayIds = null;
                        curWayName = null;
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
                weights[i] = weightCalculator.calcWeight(startIDs[i], endIDs[i]);
            }
            
            state.getLoadedGraph().buildGraph(startIDs, endIDs, weights);
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
    }
}
