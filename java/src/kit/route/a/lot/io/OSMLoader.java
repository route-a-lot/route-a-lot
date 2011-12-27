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
    
    private ArrayList<Integer> startIds;
    private ArrayList<Integer> endIds;
    
    
    // for explanations see http://wiki.openstreetmap.org/wiki/Map_Features
    
    
    
    public static final int OFFSET_STREET_BICYCLE = 1;
    public static final int OFFSET_STEET_NO_BICYCLE = 2;
    public static final int OFFSET_STREET_ONEWAY = 3;
    public static final int OFFSET_STREET_NO_ONEWAY = 4;
    public static final int OFFSET_STREET_ONEWAY_OPPOSITE = 5;
    public static final int OFFSET_STREET_BRIDGE = 6;
    public static final int OFFSET_STREET_TUNNEL = 7;
    
    public static final int OFFSET_AREA_BUILDING = 1;
    public static final int OFFSET_AREA = 2;
    
    public static final int OFFSET_ACCESS_PRIVATE = 1;
    public static final int OFFSET_ACCESS_PERMISSIVE = 2;
    
    public static final int AEROWAY = 1000;
    public static final int AMENITY_BAR = 1050;
    public static final int AMENITY_BBQ = 1051;
    public static final int AMENITY_BIERGARTEN = 1052;
    public static final int AMENITY_CAFE = 1053;
    public static final int AMENITY_DRINKING_WATER = 1054;
    public static final int AMENITY_FAST_FOOD = 1055;
    public static final int AMENITY_FOOD_COURT = 1056;
    public static final int AMENITY_ICE_CREAM = 1057;
    public static final int AMENITY_PUB = 1058;
    public static final int AMENITY_RESTAURANT = 1059;
    public static final int AMENITY_COLLEGE = 1060;
    public static final int AMENITY_KINDERGARTEN = 1061;
    public static final int AMENITY_LIBRARY = 1062;
    public static final int AMENITY_SCHOOL = 1063;
    public static final int AMENITY_UNIVERSITY = 1064;
    public static final int AMENITY_BICYCLE_PARKING = 1065;
    public static final int AMENITY_BICYCLE_RENTAL = 1066;
    public static final int AMENITY_CAR_RENTAL = 1067;
    public static final int AMENITY_CAR_SHARING = 1068;
    public static final int AMENITY_CAR_WASH = 1069;
    public static final int AMENITY_EV_CHARGING = 1070;
    public static final int AMENITY_FERRY_TERMINAL = 1071;
    public static final int AMENITY_FUEL = 1072;
    public static final int AMENITY_GRIT_BIN = 1073;
    public static final int AMENITY_PARKING = 1074;
    public static final int AMENITY_PARKING_ENTRANCE = 1075;
    public static final int AMENITY_PARKING_SPACE = 1076;
    public static final int AMENITY_TAXI = 1077;
    public static final int AMENITY_ATM = 1078;
    public static final int AMENITY_BANK = 1079;
    public static final int AMENITY_BUREAU_DE_CHANGE = 1080;
    public static final int AMENITY_DENTIST = 1081;
    public static final int AMENITY_DOCTORS = 1082;
    public static final int AMENITY_HOSPITAL = 1083;
    public static final int AMENITY_NURSING_HOME = 1084;
    public static final int AMENITY_PHARMACY = 1085;
    public static final int AMENITY_SOCIAL_FACILITY = 1086;
    public static final int AMENITY_VETERINARY = 1087;
    public static final int AMENITY_ARTS_CENTER = 1088;
    public static final int AMENITY_CINEMA = 1089;
    public static final int AMENITY_COMMUNITY_CENTRE = 1090;
    public static final int AMENITY_FOUNTAIN = 1091;
    public static final int AMENITY_NIGHTCLUB = 1092;
    public static final int AMENITY_SOCIAL_CENTRE = 1093;
    public static final int AMENITY_STRIPCLUB = 1094;
    public static final int AMENITY_STUDIO = 1095;
    public static final int AMENITY_THEATRE = 1096;
    public static final int AMENITY_BENCH = 1097;
    public static final int AMENITY_BROTHEL = 1098;
    public static final int AMENITY_CLOCK = 1099;
    public static final int AMENITY_COURTHOUSE = 1110;
    public static final int AMENITY_CREMATORIUM = 1111;
    public static final int AMENITY_EMBASSY = 1112;
    public static final int AMENITY_FIRE_STATION = 1113;
    public static final int AMENITY_GRAVE_YARD = 1114;
    public static final int AMENITY_HUNTING_STAND = 1115;
    public static final int AMENITY_MARKETPLACE = 1116;
    public static final int AMENITY_PLACE_OF_WORSHIP = 1117;
    public static final int AMENITY_POLICE = 1118;
    public static final int AMENITY_POST_BOX = 1119;
    public static final int AMENITY_POST_OFFICE = 1120;
    public static final int AMENITY_PRISON = 1121;
    public static final int AMENITY_PUBLIC_BUILDING = 1122;
    public static final int AMENITY_RECYCLING = 1123;
    public static final int AMENITY_SAUNA = 1124;
    public static final int AMENITY_SHELTER = 1125;
    public static final int AMENITY_TELEPHONE = 1126;
    public static final int AMENITY_TOILETS = 1127;
    public static final int AMENITY_TOWNHALL = 1128;
    public static final int AMENITY_VENDING_MACHINE = 1129;
    public static final int AMENITY_WASTE_BASKET = 1130;
    public static final int AMENITY_WASTE_DISPOSAL = 1131;
    public static final int AMENITY_WATERING_PLACE = 1132;
    
    public static final int BARRIER = 1150;

    public static final int BUILDING = 1200;
    
    public static final int CRAFT = 1250;

    public static final int CYCLEWAY_LANE = 1350;
    public static final int CYCLEWAY_OPPOSITE = 1351;
    public static final int CYCLEWAY_OPPOSITE_LANE = 1352;
    public static final int CYCLEWAY_OPPOSITE_TRACK = 1353;
    public static final int CYCLEWAY_TRACK = 1354;
    
    public static final int EMERGENCY = 1400;
    
    public static final int GEOLOGICAL = 1150;
    
    public static final int HIGHWAY_BRIDLEWAY = 1200;
    public static final int HIGHWAY_BYWAY = 1201;
    public static final int HIGHWAY_CYCLEWAY = 1202;
    public static final int HIGHWAY_FOOTWAY = 1203;  // can be used with bicycle=yes
    public static final int HIGHWAY_LIVING_STREET = 1204;
    public static final int HIGHWAY_MOTORWAY = 1205; // i.e. Autobahn, ...
    public static final int HIGHWAY_MOTORWAY_LINK = 1206;
    public static final int HIGHWAY_PATH = 1207;
    public static final int HIGHWAY_PEDESTRIAN = 1208;
    public static final int HIGHWAY_PRIMARY = 1209;
    public static final int HIGHWAY_PRIMARY_LINK = 1210;
    public static final int HIGHWAY_RACEWAY = 1211;
    public static final int HIGHWAY_RESIDENTIAL = 1212;
    public static final int HIGHWAY_ROAD = 1213;
    public static final int HIGHWAY_SECONDARY = 1214;
    public static final int HIGHWAY_SECONDARY_LINK = 1215;
    public static final int HIGHWAY_SERVICE = 1216;
    public static final int HIGHWAY_STEPS = 1217;
    public static final int HIGHWAY_STEPS_LARGE = 1218;
    public static final int HIGHWAY_TERTIARY = 1219;
    public static final int HIGHWAY_TERTIARY_LINK = 1220;
    public static final int HIGHWAY_TRACK = 1221;
    public static final int HIGHWAY_TRUNK = 1222;
    public static final int HIGHWAY_TRUNK_LINK = 1223;
    public static final int HIGHWAY_UNCLASSIFIED = 1224;
    
    public static final int HIGHWAY_GIVE_WAY = 1250;
    public static final int HIGHWAY_MINI_ROUNDABOUT = 1251;
    public static final int HIGHWAY_MOTORWAY_JUNCTION = 1252;
    public static final int HIGHWAY_ROUNDABOUT = 1253;
    public static final int HIGHWAY_STOP = 1254;
    public static final int HIGHWAY_TRAFFIC_SIGNALS = 1255;
    public static final int HIGHWAY_BUS_STOP = 1256;
    public static final int HIGHWAY_CROSSING = 1257;
    public static final int HIGHWAY_EMERGENCY_ACCESS_POINT = 1258;
    public static final int HIGHWAY_FORD = 1259;
    public static final int HIGHWAY_SPEED_CAMERA = 1260;
    public static final int HIGHWAY_SERVICES = 1261;
    public static final int HIGHWAY_TURNING_CIRCLE = 1262;
    
    public static final int HIGHWAY_IGNORED = 1299;
    
    public static final int TRAFFIC_CALMING = 1300;
    
    public static final int SERVICE = 1350;
    
    public static final int HISTORIC = 1400;

    public static final int LANDUSE = 1450;
    
    public static final int LEISURE_COMMON = 1550;
    public static final int LEISURE_DANCE = 1551;
    public static final int LEISURE_DOG_PARK = 1552;
    public static final int LEISURE_FISHING = 1553;
    public static final int LEISURE_GARDEN = 1554;
    public static final int LEISURE_GOLF_COURSE = 1555;
    public static final int LEISURE_ICE_RINK = 1556;
    public static final int LEISURE_MARINA = 1557;
    public static final int LEISURE_MINIATURE_GOLF = 1558;
    public static final int LEISURE_NATURE_RESERVE = 1559;
    public static final int LEISURE_PARK = 1560;
    public static final int LEISURE_PITCH = 1561;
    public static final int LEISURE_PLAYGROUND = 1562;
    public static final int LEISURE_SLIPWAY = 1563;
    public static final int LEISURE_SPORTS_CENTRE = 1564;
    public static final int LEISURE_STADIUM = 1565;
    public static final int LEISURE_SWIMMING_POOL = 1566;
    public static final int LEISURE_TRACK = 1567;
    public static final int LEISURE_WATER_PARK = 1568;

    public static final int MAN_MADE = 1600;
    
    public static final int MILITARY = 1630;

    public static final int NATURAL = 1650;
    
    public static final int OFFICE = 1680;
    
    public static final int POWER = 1700;
    
    public static final int PUBLIC_TRANSPORT = 1750;
    
    public static final int RAILWAY = 1750;
    
    public static final int SHOP = 1800;
    
    public static final int TOURISM = 1900;
    
    public static final int TRACKTYPE = 1950;
    
    public static final int WATERWAY = 1970;
    
    public static final int NON_PHYSICAL = 2000;
    
    
    State state;
    
    public OSMLoader() {
        state = State.getInstance();
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
                int curWayType;
                
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
                                        curWayType = HIGHWAY_BRIDLEWAY;
                                    } else if (value.equalsIgnoreCase("crossing")) {
                                        curWayType = HIGHWAY_CROSSING;
                                    } else if (value.equalsIgnoreCase("cycleway")) {
                                        curWayType = HIGHWAY_CYCLEWAY;
                                    } else if (value.equalsIgnoreCase("footway")) {
                                        curWayType = HIGHWAY_FOOTWAY;
                                    } else if (value.equalsIgnoreCase("living_street")) {
                                        curWayType = HIGHWAY_LIVING_STREET;
                                    } else if (value.equalsIgnoreCase("motorway")) {
                                        curWayType = HIGHWAY_MOTORWAY;
                                    } else if (value.equalsIgnoreCase("motorway_link")) {
                                        curWayType = HIGHWAY_MOTORWAY_LINK;
                                    } else if (value.equalsIgnoreCase("path")) {
                                        curWayType = HIGHWAY_PATH;
                                    } else if (value.equalsIgnoreCase("pedestrian")) {
                                        curWayType = HIGHWAY_PEDESTRIAN;
                                    } else if (value.equalsIgnoreCase("primary")) {
                                        curWayType = HIGHWAY_PRIMARY;
                                    } else if (value.equalsIgnoreCase("primary_link")) {
                                        curWayType = HIGHWAY_PRIMARY_LINK;
                                    } else if (value.equalsIgnoreCase("residential")) {
                                        curWayType = HIGHWAY_RESIDENTIAL;
                                    } else if (value.equalsIgnoreCase("secondary")) {
                                        curWayType = HIGHWAY_SECONDARY;
                                    } else if (value.equalsIgnoreCase("secondary_link")) {
                                        curWayType = HIGHWAY_SECONDARY_LINK;
                                    } else if (value.equalsIgnoreCase("service")) {
                                        curWayType = HIGHWAY_SERVICE;
                                    } else if (value.equalsIgnoreCase("steps")) {
                                        curWayType = HIGHWAY_STEPS;
                                    } else if (value.equalsIgnoreCase("steps_large")) {
                                        curWayType = HIGHWAY_STEPS_LARGE;
                                    } else if (value.equalsIgnoreCase("tertiary")) {
                                        curWayType = HIGHWAY_TERTIARY;
                                    } else if (value.equalsIgnoreCase("track")) {
                                        curWayType = HIGHWAY_TRACK;
                                    } else if (value.equalsIgnoreCase("trunk")) {
                                        curWayType = HIGHWAY_TRUNK;
                                    } else if (value.equalsIgnoreCase("trunk_link")) {
                                        curWayType = HIGHWAY_TRUNK_LINK;
                                    } else if (value.equalsIgnoreCase("unclassified")) {
                                        curWayType = HIGHWAY_UNCLASSIFIED;
                                    } else {
                                        curWayType = HIGHWAY_IGNORED;
                                        logger.debug("Highway type ignored: " + value);
                                    }
                                } else if (key.equalsIgnoreCase("bicycle")) {
                                    if (value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("designated")
                                            || value.equalsIgnoreCase("permissive")) {
                                        curWayType += OFFSET_STREET_BICYCLE;
                                    } else if (value.equalsIgnoreCase("no") || value.equalsIgnoreCase("private")) {
                                        curWayType += OFFSET_STEET_NO_BICYCLE;
                                    } else {
                                        logger.warn("Unknown value for " + key + " key in tags: " + value);
                                    }
                                } else if (key.equalsIgnoreCase("oneway")) {
                                    if (value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("true")) {
                                        curWayType += OFFSET_STREET_ONEWAY;
                                    } else if (value.equalsIgnoreCase("no") || value.equalsIgnoreCase("false")) {
                                        curWayType += OFFSET_STREET_NO_ONEWAY;
                                    } else if (value.equalsIgnoreCase("-1")) {
                                        curWayType += OFFSET_STREET_ONEWAY_OPPOSITE;
                                    } else {
                                        logger.warn("Unknown value for " + key + " key in tags: " + value);
                                    }
                                } else if (key.equalsIgnoreCase("amenity")) {
                                    if (value.equalsIgnoreCase("cafe")) {
                                        curWayType = AMENITY_CAFE;
                                    } else if (value.equalsIgnoreCase("car_sharing")) {
                                        curWayType = AMENITY_CAR_SHARING;
                                    } else if (value.equalsIgnoreCase("courthouse")) {
                                        curWayType = AMENITY_COURTHOUSE;
                                    } else if (value.equalsIgnoreCase("fountain")) {
                                        curWayType = AMENITY_FOUNTAIN;
                                    } else if (value.equalsIgnoreCase("hospital")) {
                                        curWayType = AMENITY_HOSPITAL;
                                    } else if (value.equalsIgnoreCase("kindergarten")) {
                                        curWayType = AMENITY_KINDERGARTEN;
                                    } else if (value.equalsIgnoreCase("library")) {
                                        curWayType = AMENITY_LIBRARY;
                                    } else if (value.equalsIgnoreCase("parking")) {
                                        curWayType = AMENITY_PARKING;
                                    } else if (value.equalsIgnoreCase("place_of_worship")) {
                                        curWayType = AMENITY_PLACE_OF_WORSHIP;
                                    } else if (value.equalsIgnoreCase("police")) {
                                        curWayType = AMENITY_POLICE;
                                    } else if (value.equalsIgnoreCase("public_building")) {
                                        curWayType = AMENITY_PUBLIC_BUILDING;
                                    } else if (value.equalsIgnoreCase("restaurant")) {
                                        curWayType = AMENITY_RESTAURANT;
                                    } else if (value.equalsIgnoreCase("school")) {
                                        curWayType = AMENITY_SCHOOL;
                                    } else if (value.equalsIgnoreCase("theatre")) {
                                        curWayType = AMENITY_THEATRE;
                                    } else if (value.equalsIgnoreCase("university")) {
                                        curWayType = AMENITY_UNIVERSITY;
                                    } else {
                                        logger.warn("Unknown value for " + key + " key in tags: " + value);
                                    }
                                } else if (key.equalsIgnoreCase("leisure")) {
                                    if (value.equalsIgnoreCase("garden")) {
                                        curWayType = LEISURE_GARDEN;
                                    } else if (value.equalsIgnoreCase("pitch")) {
                                        curWayType = LEISURE_PITCH;
                                    } else if (value.equalsIgnoreCase("park")) {
                                        curWayType = LEISURE_PARK;
                                    } else if (value.equalsIgnoreCase("playground")) {
                                        curWayType = LEISURE_PLAYGROUND;
                                    } else if (value.equalsIgnoreCase("sports_centre")) {
                                        curWayType = LEISURE_SPORTS_CENTRE;
                                    } else if (value.equalsIgnoreCase("stadium")) {
                                        curWayType = LEISURE_STADIUM;
                                    } else if (value.equalsIgnoreCase("track")) {
                                        curWayType = LEISURE_TRACK;
                                    } else {
                                        logger.warn("Unknown value for " + key + " key in tags: " + value);
                                    }
                                } else if (key.startsWith("addr:")) {
                                    /*Address address = new Address();
                                    if (key.equalsIgnoreCase("addr:housenumber") || key.equalsIgnoreCase("addr:housename")) {
                                        address.setHousenumber(value);
                                    } else if (key.equalsIgnoreCase("addr:street")) {
                                        address.setStreet(value);
                                    } else if (key.equalsIgnoreCase("addr:state")) {
                                        address.setState(value);
                                    } else if (key.equalsIgnoreCase("addr:postcode")) {
                                        address.setPostcode(value);
                                    } else if (key.equalsIgnoreCase("addr:city")) {
                                        address.setCity(value);
                                    } else if (key.equalsIgnoreCase("addr:country")) {
                                        address.setCountry(value);
                                    } else if (key.equalsIgnoreCase("addr:full")) {
                                        address.setFullAddress(value);
                                    } else if (key.equalsIgnoreCase("addr:interpolation")) {
                                        address.setInterpolation(value);
                                    } else {
                                        logger.warn("Unknown addr:* tag: " + key + ", value: " + value);
                                    }*/
                                } else if (key.equalsIgnoreCase("building")) {
                                    curWayType += OFFSET_AREA_BUILDING; // TODO here and with the following: check if value == yes or something more specific
                                } else if (key.equalsIgnoreCase("bridge")) {
                                    curWayType += OFFSET_STREET_BRIDGE;
                                } else if (key.equalsIgnoreCase("tunnel")) {
                                    curWayType += OFFSET_STREET_TUNNEL;
                                } else if (key.equalsIgnoreCase("area")) {
                                    if (value.equalsIgnoreCase("yes")) {
                                        curWayType += OFFSET_AREA;
                                    } else {
                                        logger.warn("Unknown value for " + key + " key in tags: " + value);
                                    }
                                } else if (key.equalsIgnoreCase("access")) {
                                    if (value.equalsIgnoreCase("private")) {
                                        curWayType += OFFSET_ACCESS_PRIVATE;
                                    } else if (value.equalsIgnoreCase("permissive")) {
                                        curWayType += OFFSET_ACCESS_PERMISSIVE;
                                    } else {
                                        logger.warn("Unknown value for " + key + " key in tags: " + value);
                                    }
                                } else if (key.equalsIgnoreCase("note") || key.equalsIgnoreCase("maxspeed")
                                        || key.equalsIgnoreCase("created_by")
                                        || key.equalsIgnoreCase("landuse") /* TODO really ignore that? */
                                        || key.startsWith("building:") /* " */) {
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
                                curPolylineNode = idMap.get(Long.parseLong(attributes   .getValue("ref")));
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
                            
                            state.getLoadedMapInfo().addNode(coordinates, newId);
                            
                            // TODO tags (for POI's)
                        } else if (qName.equalsIgnoreCase("way")) {
                            inWay = true;
                            curWayIds = new ArrayList<Integer>();
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
                        inWay = false;
                        inPolyline = false;
                        
                        state.getLoadedMapInfo().addWay(curWayIds, curWayName, curWayType);
                        
                        curWayIds = null;
                        curWayName = null;
                        curWayType = 0;
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
            
            state.getLoadedGraph().buildGraph(startIDs, endIDs, weights);
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
    }
}
